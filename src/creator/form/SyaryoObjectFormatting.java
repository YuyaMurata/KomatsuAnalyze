/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.form.item.FormAllSurpport;
import creator.form.item.FormDead;
import creator.form.item.FormDeploy;
import creator.form.item.FormKomtrax;
import creator.form.item.FormNew;
import creator.form.item.FormOrder;
import creator.form.item.FormOwner;
import creator.form.item.FormParts;
import creator.form.item.FormProduct;
import creator.form.item.FormSMR;
import creator.form.item.FormUsed;
import creator.form.item.FormWork;
import java.text.DecimalFormat;
import param.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import file.MapToJSON;
import file.SyaryoToCompress;
import java.io.PrintWriter;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author zz17390
 */
public class SyaryoObjectFormatting {

    private static String KISY = "PC200";
    private static String HONSY_INDEXPATH = KomatsuDataParameter.HONSYA_INDEX_PATH;
    private static String PRODUCT_INDEXPATH = KomatsuDataParameter.PRODUCT_INDEXPATH;
    private static DecimalFormat df = new DecimalFormat("0000");
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    
    public static List<String> w = new ArrayList<>();
    public static List<String> p = new ArrayList<>();
    
    public static String currentKey = "";

    public static void main(String[] args) {
        form(KISY);
    }

    private static void form(String kisy) {
        LOADER.setFile(kisy);
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        //本社コード
        Map<String, String> honsyIndex = new MapToJSON().toMap(HONSY_INDEXPATH);

        //生産日情報
        Map<String, String> productIndex = new MapToJSON().toMap(PRODUCT_INDEXPATH);

        int n = 0;
        for (String key : syaryoMap.keySet()) {
            //System.out.println(key);
            currentKey = key;

            SyaryoObject syaryo = syaryoMap.get(key);
            syaryo.startHighPerformaceAccess();

            //整形時のデータ削除ルールを設定
            DataRejectRule rule = new DataRejectRule();

            //整形後出力するMap
            Map newMap;

            //日付ズレの補正 閾値以降のデータを削除
            formDate(syaryo, LOADER.indexes("受注"), 20170501);

            //生産の整形
            newMap = FormProduct.form(syaryo.get("生産"), productIndex, syaryo.getName());
            syaryo.put("生産", newMap);
            
            //出荷情報の整形
            newMap = FormDeploy.form(syaryo.get("出荷"), syaryo.get("生産").keySet().stream().findFirst().get(), syaryo.getName());
            syaryo.put("出荷", newMap);
            
            //顧客の整形
            newMap = FormOwner.form(syaryo.get("顧客"), LOADER.indexes("顧客"), honsyIndex, syaryo.get("経歴"), LOADER.indexes("経歴"), rule);
            syaryo.put("顧客", newMap);

            //新車の整形
            if (KomatsuDataParameter.PC_KR_SMASTER.get(syaryo.getName().split("-")[0] + "-" + syaryo.getName().split("-")[2]) != null) {
                syaryo.put("新車", null);
            }
            newMap = FormNew.form(syaryo.get("新車"), syaryo.get("生産"), syaryo.get("出荷"), LOADER.indexes("新車"));
            syaryo.put("新車", newMap);
            rule.addNew(newMap.keySet().stream().findFirst().get().toString());

            //中古車の整形
            newMap = FormUsed.form(syaryo.get("中古車"), LOADER.indexes("中古車"), rule.getNew(), rule.getKUEC());
            syaryo.put("中古車", newMap);

            //受注
            newMap = FormOrder.form(syaryo.get("受注"), LOADER.indexes("受注"), rule);
            syaryo.put("受注", newMap);
            List sbnList = null;
            if (syaryo.get("受注") != null) {
                sbnList = new ArrayList(syaryo.get("受注").keySet());
            }

            //廃車
            newMap = FormDead.form(syaryo.get("廃車"), rule.currentDate, LOADER.indexes("廃車"));
            syaryo.put("廃車", newMap);

            //作業
            newMap = FormWork.form(syaryo.get("作業"), sbnList, LOADER.indexes("作業"), rule.getWORKID());
            syaryo.put("作業", newMap);

            //部品
            newMap = FormParts.form(syaryo.get("部品"), sbnList, LOADER.indexes("部品"), rule.getPARTSID());
            syaryo.put("部品", newMap);

            //SMR
            newMap = FormSMR.form(syaryo.get("SMR"), LOADER.indexes("SMR"));
            syaryo.put("SMR", newMap);

            //AS
            newMap = FormAllSurpport.form(syaryo.get("オールサポート"), LOADER.indexes("オールサポート"));
            syaryo.put("オールサポート", newMap);

            //Komtrax
            FormKomtrax.form(syaryo, syaryo.get("出荷"));

            //余計な情報を削除
            formExtra(syaryo, new String[]{"最終更新日", "国"});
            removeEmptyObject(syaryo);

            syaryo.stopHighPerformaceAccess();
            n++;

            if (n % 1000 == 0) {
                System.out.println(n + "台処理");
            }
        }

        //R
        //R.close();
        LOADER.close();
        new SyaryoToCompress().write(LOADER.getFilePath().replace(".bz2", "_form.bz2"), syaryoMap);
        
        /*try(PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_作番と紐づかない部品.csv")){
            p.stream().forEach(pw::println);
        }
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_作番と紐づかない作業.csv")){
            w.stream().forEach(pw::println);
        }*/
    }

    public static Map reduce(Map<String, List<String>> r, List<Integer> exp) {
        Map map = r.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(),
                        e -> exp.stream().map(i -> e.getValue().get(i)).collect(Collectors.toList())));

        return map;
    }

    public static Map combine(Map c1, Map c2) {
        if (c2 == null) {
            return c1;
        } else if (c1 == null) {
            return c2;
        }

        Map map = new TreeMap(c1);

        for (Object key : c2.keySet()) {
            map.put(dup(key.toString(), c1), c2.get(key));
        }

        return map;
    }

    private static void formExtra(SyaryoObject syaryo, String[] removeInfo) {
        for (String remove : removeInfo) {
            syaryo.remove(remove);
        }
    }

    //日付に混じっているごみを削除 (日付が無い or 日付がおかしい)
    private static void formDate(SyaryoObject syaryo, List indexList, Integer date) {
        for (Object key : syaryo.getMap().keySet()) {
            Map<String, List<String>> map = syaryo.get(key.toString());
            if (map == null) {
                continue;
            }

            List remove;

            //日付情報をキーにもたない場合は無視
            try {
                if (key.toString().equals("受注")) {
                    int date_idx = indexList.indexOf("ODDAY"); //kom_orderが紐づく場合ODDAY
                    remove = map.entrySet().stream()
                            .filter(v -> !v.getValue().get(date_idx).toString().contains("None"))
                            .filter(v -> Integer.valueOf(v.getValue().get(date_idx).toString().split("#")[0].replace("/", "")) >= date)
                            .map(v -> v.getKey())
                            .collect(Collectors.toList());
                } else {
                    remove = map.keySet().stream()
                            .filter(v -> !v.contains("None"))
                            .filter(v -> Integer.valueOf(v.split("#")[0].split(" ")[0].replace("/", "").substring(0, 8)) >= date)
                            .collect(Collectors.toList());
                }
                if (remove.isEmpty()) {
                    continue;
                }
            } catch (NumberFormatException | StringIndexOutOfBoundsException se) {
                continue;
            }

            //System.out.println(currentKey + ":" + key + " - " + remove);
            for (Object removeDate : remove) {
                map.remove(removeDate.toString());
            }

            syaryo.put(key.toString(), map);
        }
    }

    

    //重複して並んでいるIDを重複除去
    public static List exSeqDuplicate(List<String> dupList) {
        List list = new ArrayList();
        String tmp = "";
        for (String el : dupList) {
            if (tmp.equals(el)) {
                continue;
            }
            tmp = el;
            list.add(tmp);
        }

        return list;
    }

    //日付をまとめて整形　2018/08/09 -> 20180809
    public static Map dateFormalize(Map dateMap) {
        Map map = new TreeMap();
        for (Object date : dateMap.keySet()) {
            //日付
            String d = date.toString().replace("/", "");
            map.put(d, dateMap.get(date));
        }
        return map;
    }

    //空の情報を削除
    public static void removeEmptyObject(SyaryoObject syaryo) {
        List<String> deleteKey = new ArrayList();
        for (String key : syaryo.getMap().keySet()) {
            if (syaryo.get(key) != null) {
                if (syaryo.get(key).isEmpty()) {
                    deleteKey.add(key);
                }
            } else {
                deleteKey.add(key);
            }
        }

        for (String dkey : deleteKey) {
            syaryo.remove(dkey);
        }
    }

    //日付が重複する場合に連番を付与　20180809が2つ登場したとき-> 20180809#0001
    public static String dup(String key, Map map) {
        int cnt = 0;
        String k = key;
        while (map.get(k) != null) {
            k = key + "#" + df.format(++cnt);
        }
        return k;
    }
}
