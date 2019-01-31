/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import analizer.SyaryoAnalizer;
import google.map.MapPathData;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import param.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import file.MapToJSON;
import file.SyaryoToCompress;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import program.r.R;

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

    public static void main(String[] args) {
        form(KISY);
    }

    private static String currentKey;

    private static void form(String kisy) {
        LOADER.setFile(kisy + "_km");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        
        //本社コード
        Map<String, String> honsyIndex = new MapToJSON().toMap(HONSY_INDEXPATH);

        //生産日情報
        Map<String, String> productIndex = new MapToJSON().toMap(PRODUCT_INDEXPATH);

        //車両の削除
        rejectSyaryo(syaryoMap, new String[]{"company,UR", "company,GC", "新車,20170501"});

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
            newMap = formProduct(syaryo.get("生産"), productIndex, syaryo.getName());
            syaryo.put("生産", newMap);

            //顧客の整形
            newMap = formOwner(syaryo.get("顧客"), LOADER.indexes("顧客"), honsyIndex, syaryo.get("経歴"), LOADER.indexes("経歴"), rule);
            syaryo.put("顧客", newMap);

            //新車の整形
            if(KomatsuDataParameter.PC_KR_SMASTER.get(syaryo.getName().split("-")[0]+"-"+syaryo.getName().split("-")[2]) != null)
                syaryo.put("新車", null);
            newMap = formNew(syaryo.get("新車"), syaryo.get("生産"), syaryo.get("出荷"), LOADER.indexes("新車"));
            syaryo.put("新車", newMap);
            rule.addNew(newMap.keySet().stream().findFirst().get().toString());

            //中古車の整形
            newMap = formUsed(syaryo.get("中古車"), LOADER.indexes("中古車"), rule.getNew(), rule.getKUEC());
            syaryo.put("中古車", newMap);

            //受注
            newMap = formOrder(syaryo.get("受注"), LOADER.indexes("受注"), rule);
            syaryo.put("受注", newMap);
            List sbnList = null;
            if (syaryo.get("受注") != null) {
                sbnList = new ArrayList(syaryo.get("受注").keySet());
            }

            //廃車
            newMap = formDead(syaryo.get("廃車"), rule.currentDate, LOADER.indexes("廃車"));
            syaryo.put("廃車", newMap);

            //作業
            newMap = formWork(syaryo.get("作業"), sbnList, LOADER.indexes("作業"), rule.getWORKID());
            syaryo.put("作業", newMap);

            //部品
            newMap = formParts(syaryo.get("部品"), sbnList, LOADER.indexes("部品"), rule.getPARTSID());
            syaryo.put("部品", newMap);

            //SMR
            newMap = formSMR(syaryo.get("SMR"), LOADER.indexes("SMR"));
            syaryo.put("SMR", newMap);

            //AS
            newMap = formAS(syaryo.get("オールサポート"), LOADER.indexes("オールサポート"));
            syaryo.put("オールサポート", newMap);

            //Komtrax
            formKomtrax(syaryo, syaryo.get("出荷"));

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
        
        //Headerの付加
        LOADER.close();
        
        new SyaryoToCompress().write(LOADER.getFilePath().replace(".bz2", "_form.bz2"), syaryoMap);
    }
    
    private static Map reduce(Map<String, List> r, List<Integer> exp){
        Map map =r.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey(), 
                                                    e ->exp.stream().map(i -> e.getValue().get(i)).collect(Collectors.toList())));
        
        return map;
    }
    
    private static Map combine(Map c1, Map c2){
        if(c2 == null)
            return c1;
        else if(c1 == null)
            return c2;
        
        Map map = new TreeMap(c1);
        
        for(Object key : c2.keySet()){
            map.put(dup(key.toString(), c1), c2.get(key));
        }
        
        return map;
    }

    //車両の削除
    private static void rejectSyaryo(Map<String, SyaryoObject> syaryoMap, String[] deleteRule) {
        //List<String> reject = new ArrayList();
        Map<String, Integer> reject = new ConcurrentHashMap();
        syaryoMap.values().parallelStream().forEach(syaryo -> {
            syaryo.startHighPerformaceAccess();

            for (String rule : deleteRule) {
                Boolean check = false;

                String data = rule.split(",")[0];
                String value = rule.split(",")[1];

                if (data.equals("company")) {
                    //Delete Company
                    if (syaryo.get("最終更新日") != null) {
                        check = syaryo.get("最終更新日").values().stream()
                            .filter(list -> list.contains(value)) //Company
                            .findFirst().isPresent();
                    }
                } else {
                    //Delete 新車 日付閾値以降のデータ削除
                    if (syaryo.get(data) == null) {
                        data = "生産";
                    }

                    if (syaryo.get(data) != null) {
                        check = syaryo.get(data).keySet().stream()
                            .filter(k -> Integer.valueOf(k.split("#")[0].replace("/", "")) >= Integer.valueOf(value))
                            .findFirst().isPresent();
                    }
                }

                if (check) {
                    //System.out.println(data+":"+key);
                    reject.put(syaryo.name, 0);
                    //reject.add(key);
                }
            }

            syaryo.stopHighPerformaceAccess();
        });

        System.out.println("カンパニ、日付で削除される車両数:" + reject.size());
        for (String key : reject.keySet()) {
            syaryoMap.remove(key);
        }
    }

    private static Map formProduct(Map<String, List> product, Map<String, String> index, String name) {
        Map<String, List> map = new TreeMap();
        String id = name.split("-")[0] + "-" + name.split("-")[2];
        String date = product.keySet().stream().findFirst().get();
        String pdate = index.get(id);
        if (pdate != null) {
            if (Integer.valueOf(date) < Integer.valueOf(pdate)) {
                map.put(pdate, product.get(date));
                System.out.println(currentKey + ":生産-" + pdate + " 異常:" + date);
            } else {
                map.put(date, product.get(date));
            }
        } else {
            map.put(date, product.get(date));
        }

        return map;
    }

    private static Map formOwner(Map<String, List> owner, List indexList, Map<String, String> honsyIndex, Map<String, List> history, List indexHisList, DataRejectRule reject) {
        if (owner == null || history == null) {
            //System.out.println("Not found owner!");
            return null;
        }

        Integer company = indexList.indexOf("会社CD");
        Integer ownerID = indexList.indexOf("顧客CD");
        Integer ownerName = indexList.indexOf("顧客名");

        Integer hist_cid = indexHisList.indexOf("顧客CD");
        Integer syareki = indexHisList.indexOf("車歴区分");
        
        //日付データ揃え
        owner = dateFormalize(owner);

        //本社コード揃え
        for (String d : owner.keySet()) {
            List list = owner.get(d);
            String com = list.get(company).toString();
            String id = list.get(ownerID).toString();

            if (honsyIndex.get(com + "_" + id) != null) {
                id = honsyIndex.get(com + "_" + id).split("_")[0];
            }

            reject.addKUEC(id, d.split("#")[0]); //KUECを登録

            list.set(ownerID, id);
        }

        for (String d : history.keySet()) {
            List list = history.get(d);
            String com = list.get(company).toString();
            String id = list.get(hist_cid).toString();

            if (honsyIndex.get(com + "_" + id) != null) {
                id = honsyIndex.get(com + "_" + id).split("_")[0];
            }

            list.set(hist_cid, id);
        }

        //ID重複排除 ##排除
        //System.out.println(owner.values().stream().map(l -> l.get(ownerID)).collect(Collectors.toList()));
        List owners = owner.values().stream()
            .map(l -> l.get(ownerID))
            .filter(id -> !id.toString().contains("##")) //工場IDが振られていない
            .filter(id -> !id.toString().equals("None")) //IDが存在する
            .filter(id -> !KomatsuDataParameter.KUEC_LIST.contains(id)) //KUECでない
            .collect(Collectors.toList());
        //System.out.println(owners);
        owners = exSeqDuplicate(owners);
        
        if (owners.isEmpty()) {
            //System.out.println("使用顧客が存在しない車両(後で削除)");
            return null;
        }

        Map<String, List> map = new TreeMap();
        int i = 0;
        for (String date : owner.keySet()) {
            if (date.length() >= 8) {
                String id = owner.get(date).get(ownerID).toString();
                if (id.equals(owners.get(i))) {
                    map.put(date, owner.get(date));
                    i++;
                    if (owners.size() <= i) {
                        break;
                    }
                }
            }
        }

        return map;
    }

    private static Map formNew(Map<String, List> news, Map<String, List> born, Map<String, List> deploy, List indexList) {
        Map<String, List> map = new TreeMap();
        
        Integer deff = 0;
        if(news != null){
            deff = Math.abs(SyaryoAnalizer.time(news.keySet().stream().findFirst().get(), deploy.keySet().stream().findFirst().get())) / 30;
        }
        
        if (news == null || deff > 6) {
            //出荷情報を取得する処理
            String date = "";
            if (deploy != null) {
                date = deploy.keySet().stream().findFirst().get();
            }
            if (date.equals("") || date.equals("None")) {
                date = born.keySet().stream().findFirst().get();
            }
            List list = new ArrayList();
            for (Object s : indexList) {
                list.add("None");
            }
            map.put(date.split("#")[0], list);
            return map;
        }

        //List price index
        int hyomen = indexList.indexOf("HM_URI_KN");
        int jitsu = indexList.indexOf("RL_URI_KN");
        int hyojun = indexList.indexOf("STD_SY_KKU");

        //修正しない
        if (news.size() == 1) {
            List<String> list = news.values().stream().findFirst().get();
            if (list.get(hyomen).contains("+")) {
                for (int i = hyomen; i < list.size(); i++) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i)).intValue()));
                }
            }
            return news;
        }

        //複数存在するときの処理
        List list = new ArrayList();
        String key = "";
        String[] price = new String[3];
        Boolean flg = true;
        for (String date : news.keySet()) {
            list = news.get(date);
            if (flg) {
                key = date.split("#")[0];
                price[0] = list.get(hyomen).toString();
                price[1] = list.get(jitsu).toString();
                price[2] = list.get(hyojun).toString();
                flg = false;
            } else {
                if (list.get(hyomen) != "None") {
                    price[0] = list.get(hyomen).toString();
                }
                if (list.get(jitsu) != "None") {
                    price[1] = list.get(jitsu).toString();
                }
                if (list.get(hyojun) != "None") {
                    price[2] = list.get(hyojun).toString();
                }
            }
        }

        //List整形
        int i = 0;
        for (String s : price) {
            if (!s.equals("None")) {
                list.set(hyomen + i, String.valueOf(Double.valueOf(s).intValue()));
            }
        }

        map.put(key, list);

        return map;
    }

    private static Map formUsed(Map<String, List> used, List indexList, String newd, List kuec) {
        if (used == null) {
            //System.out.println("Not found Used");
            return null;
        }

        //List price index
        int hyomen = indexList.indexOf("HM_URI_KN");
        int jitsu = indexList.indexOf("RL_URI_KN");
        int hyojun = indexList.indexOf("STD_SY_KKU");

        //日付揃え
        used = dateFormalize(used);

        Map<String, List> map = new TreeMap();

        //修正しない
        if (used.size() == 1) {
            //KUEC売却後、使用ユーザー存在しない
            if (kuec.size() > 0 || (Integer.valueOf(used.keySet().stream().findFirst().get()) <= Integer.valueOf(newd))) {
                return null;
            }

            List<String> list = used.values().stream().findFirst().get();
            if (list.get(hyomen).contains("+") || list.get(hyomen).contains("_")) {
                for (int i = hyomen; i < list.size(); i++) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i).replace("_", "")).intValue()));
                }
            }
            return used;
        }

        //複数存在するときの処理
        String key = "";
        for (String date : used.keySet()) {
            List list = used.get(date);
            String d = date.split("#")[0].replace("/", "");

            //KUECを除外
            if (kuec.contains(d)) {
                continue;
            }

            //新車より前に存在する中古車情報を削除
            if (Integer.valueOf(d) <= Integer.valueOf(newd)) {
                continue;
            }

            if (!key.equals(d)) {
                key = d;
                map.put(key, list);
            } else {
                if (!list.get(hyomen).toString().equals("None") && !map.get(d).get(hyomen).toString().contains("+")) {
                    map.get(d).set(hyomen, list.get(hyomen).toString());
                }
                if (!list.get(jitsu).toString().equals("None") && !map.get(d).get(jitsu).toString().contains("+")) {
                    map.get(d).set(jitsu, list.get(jitsu).toString());
                }
                if (!list.get(hyojun).toString().equals("None") && !map.get(d).get(hyojun).toString().contains("+")) {
                    map.get(d).set(hyojun, list.get(hyojun).toString());
                }
            }
        }

        //KUECしか存在しない
        if (map.isEmpty()) {
            //System.out.println("KUECデータしか存在しない!");
            //System.exit(0);
            return null;
        }

        //List整形
        for (String d : map.keySet()) {
            List list = map.get(d);
            for (int i = hyomen; i < hyomen + 3; i++) {
                if (!list.get(i).toString().equals("None")) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i).toString().replace("_", "")).intValue()));
                }
            }
        }

        return map;
    }

    private static Map formDead(Map<String, List> dead, String leastdate, List indexList) {
        if (dead == null) {
            //System.out.println("Not found Dead");
            return null;
        }

        Map<String, List> map = new TreeMap();
        String date = "0";
        for (String d : dead.keySet()) {
            if (!d.equals("None")) {
                //System.out.println(d);
                if (Integer.valueOf(date) < Integer.valueOf(d)) {
                    date = d;
                }
            }
        }

        if (!date.equals("0") && Integer.valueOf(leastdate) <= Integer.valueOf(date)) {
            map.put(date, dead.get(date));
            return map;
        } else {
            return null;
        }
    }

    //受注情報を整形
    private static Map formOrder(Map<String, List> order, List indexList, DataRejectRule reject) {
        if (order == null) {
            //System.out.println("Not found Order!");
            return null;
        }

        //日付ソート
        int date = indexList.indexOf("ODDAY"); //kom_orderが紐づく場合 ODDAY
        //System.out.println(order);

        Map<String, Integer> sortMap = order.entrySet().stream()
            .filter(e -> !e.getValue().get(date).equals("None"))
            .collect(Collectors.toMap(e -> e.getKey(), e -> Integer.valueOf(e.getValue().get(date).toString())));
        
        //作番重複除去
        List<String> sbnList = sortMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue()).map(s -> s.getKey())
            .map(k -> k.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        //System.out.println("作番:"+sbnList);
        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");
        int price = indexList.indexOf("SKKG");
        int kind = indexList.indexOf("ODR_KBN");

        //6桁以下の規格外の作番を取得するリスト
        List<String> sixSBN = new ArrayList();

        for (String sbn : sbnList) {
            //重複作番を取り出す
            List<String> sbnGroup = order.keySet().stream()
                .filter(s -> s.contains(sbn))
                .collect(Collectors.toList());

            //KOMPAS 受注テーブル情報が存在するときは取り出す
            Optional<List> kom = sbnGroup.stream()
                .map(s -> order.get(s))
                .filter(l -> l.get(db).toString().equals("kom_order"))
                .findFirst();

            if (kom.isPresent()) {
                map.put(sbn, kom.get());
            } else {
                for (String sg : sbnGroup) {
                    List<String> list = order.get(sg);
                    if (map.get(sbn) == null) {
                        map.put(sbn, list);
                    } else {
                        //System.out.println("サービス経歴に金額の入ったデータが2つ以上存在");
                        if (!(map.get(sbn).get(price).equals("None") || list.get(price).equals("None"))) {
                            if (Double.valueOf(map.get(sbn).get(price).toString()) < Double.valueOf(list.get(price))) {
                                map.put(sbn, list);
                            }
                        } else if (list.get(price).contains("+")) {
                            map.put(sbn, list);
                        }
                    }
                }
            }

            //規格外の作番取得
            if (sbn.length() < 7) {
                sixSBN.add(sbn);
            }

            //System.out.println(map.get(sbn));
            reject.addPARTSID(sbn);
            if (map.get(sbn).get(kind).equals("2")) {
                reject.addWORKID(sbn);
            }
        }

        //規格外作番で修正されている場合は削除
        List<String> removeSixSBN = new ArrayList<>();
        for (String sbn : map.keySet()) {
            if (sixSBN.contains(sbn)) {
                continue;
            }

            Optional<String> sbnCheck = sixSBN.stream().filter(s -> sbn.contains(s)).findFirst();
            if (sbnCheck.isPresent()) {
                //System.out.println(sbn+":"+sixSBN);
                removeSixSBN.add(sbnCheck.get());
            }
        }
        removeSixSBN.stream().forEach(s -> map.remove(s));

        //金額の整形処理
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));

            //最新の受注日
            reject.currentDate = list.get(date);
        }

        return map;
    }

    //作業明細を整形
    private static Map formWork(Map<String, List> work, List odrSBN, List indexList, List workOrder) {
        if (work == null || odrSBN == null) {
            //System.out.println("Not found Work!");
            return null;
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");

        for (Object sbn : odrSBN) {
            //重複作番を取り出す
            List<String> sbnGroup = work.keySet().stream()
                .filter(s -> s.contains(sbn.toString()))
                .collect(Collectors.toList());

            //KOMPAS 作業情報が存在するときは取り出す
            Optional<List> kom = sbnGroup.stream()
                .map(s -> work.get(s))
                .filter(l -> l.get(db).toString().equals("work_info"))
                .findFirst();
            if (kom.isPresent()) {
                sbnGroup.stream()
                    .filter(s -> !work.get(s).get(db).equals("service"))
                    .forEach(s -> map.put(s, work.get(s)));
            } else {
                sbnGroup.stream()
                    .forEach(s -> map.put(s, work.get(s)));
            }
        }

        //代表作業抽出と設定
        int daihyoIdx = indexList.indexOf("DIHY_SGYO_FLG");
        int sgcdIdx = indexList.indexOf("SGYOCD");
        int sgnmIdx = indexList.indexOf("SGYO_NM");
        work.entrySet().stream()
            .filter(w -> w.getValue().get(daihyoIdx).equals("1"))
            .forEach(w -> {
                map.entrySet().stream()
                    .filter(e -> e.getKey().contains(w.getKey().split("#")[0]))
                    .filter(e -> e.getValue().get(sgcdIdx).equals(w.getValue().get(sgcdIdx)))
                    .filter(e -> e.getValue().get(sgnmIdx).equals(w.getValue().get(sgnmIdx)))
                    .forEach(e -> map.get(e.getKey()).set(daihyoIdx, "1"));
            });

        Integer[] kosu = new Integer[]{
            indexList.indexOf("HJUN_KOS"),
            indexList.indexOf("INV_KOS"),
            indexList.indexOf("SIJI_KOS")
        };
        int price = indexList.indexOf("SKKG");

        //工数・金額の整形処理
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            for (int i : kosu) {
                if (!list.get(i).equals("None")) {
                    list.set(i, String.valueOf(Double.valueOf(list.get(i)).floatValue()));
                }
            }
            if (!list.get(price).equals("None")) {
                list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
            }
        }

        return map;
    }

    //部品明細を整形
    private static Map formParts(Map<String, List> parts, List odrSBN, List indexList, List partsOrder) {
        if (parts == null || odrSBN == null) {
            //System.out.println("Not found Parts!");
            return null;
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("DB");

        for (Object sbn : odrSBN) {
            //重複作番を取り出す
            List<String> sbnGroup = parts.keySet().stream()
                .filter(s -> s.contains(sbn.toString()))
                .collect(Collectors.toList());

            //KOMPAS 部品情報が存在するときは取り出す
            Optional<List> kom = sbnGroup.stream()
                .map(s -> parts.get(s))
                .filter(l -> l.get(db).toString().equals("parts"))
                .findFirst();
            if (kom.isPresent()) {
                sbnGroup.stream()
                    .filter(s -> !parts.get(s).get(db).equals("service"))
                    .forEach(s -> map.put(s, parts.get(s)));
            } else {
                sbnGroup.stream()
                    .forEach(s -> map.put(s, parts.get(s)));
            }
        }

        int quant = indexList.indexOf("JISI_SU");
        int price = indexList.indexOf("SKKG");
        List cancel = new ArrayList();
        //金額の整形処理・キャンセル作番の削除
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            if (list.get(quant).equals("0")) {
                cancel.add(sbn);
            }

            if (!list.get(price).equals("None")) {
                list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
            }
        }
        cancel.stream().forEach(s -> map.remove(s));

        return map;
    }

    //サービスのSMRを整形
    private static Map formSMR(Map<String, List> smr, List indexList) {
        if (smr == null) {
            //System.out.println("Not found Work!");
            return null;
        }
        
        int smridx = indexList.indexOf("VALUE");
        String checkNumber = currentKey.split("-")[2].substring(0, 5);

        //日付重複除去
        List<String> dateList = smr.keySet().stream()
            .filter(s -> !s.contains("None")) //日付が存在しない
            .map(s -> s.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        Map<String, List<String>> map = new TreeMap();
        Boolean zeroflg = false;
        for (String date : dateList) {
            String stdate = date;
            
            //重複日付を取り出す
            List<String> dateGroup = smr.keySet().stream()
                .filter(s -> s.contains(stdate))
                .filter(s -> !smr.get(s).get(smridx).equals("None")) //SMRが存在しない
                .filter(s -> !smr.get(s).get(smridx).equals("999"))  //怪しい数値の削除
                .filter(s -> !smr.get(s).get(smridx).equals("9999")) //怪しい数値の削除
                .filter(s -> !smr.get(s).get(smridx).equals(checkNumber)) //怪しい数値の削除
                .collect(Collectors.toList());

            //欠損データのみのため
            if (dateGroup.isEmpty()) {
                continue;
            }

            if (date.length() > 8) {
                date = date.substring(0, 8);
            }

            for (String dg : dateGroup) {
                List list = smr.get(dg);

                if (map.get(date) == null) {
                    map.put(date, list);
                } else {
                    if (Float.valueOf(map.get(date).get(smridx)) < Float.valueOf(list.get(smridx).toString())) {
                        map.put(date, list);
                    }
                }
            }

            //整形処理
            map.get(date).set(smridx, map.get(date).get(smridx).split("\\.")[0]);
            if (map.get(date).get(smridx).equals("0")) {
                if (zeroflg) {
                    map.remove(date);
                } else {
                    zeroflg = true;
                }
            }
        }

        if (map.isEmpty()) {
            return null;
        }

        //異常データの排除
        return map;
    }

    //オールサポートの整形　(解約日を終了日とする)
    private static Map formAS(Map<String, List> as, List indexList) {
        if (as == null) {
            return null;
        }

        int kaiyaku = indexList.indexOf("満了日");

        Map newMap = new TreeMap();
        for (String date : as.keySet()) {
            String kiyk = as.get(date).get(kaiyaku).toString();
            if (Integer.valueOf(date.split("#")[0]) <= Integer.valueOf(kiyk)) {
                newMap.put(date.split("#")[0], as.get(date));
            }
        }

        if (newMap.isEmpty()) {
            return null;
        }

        return newMap;
    }

    private static void formExtra(SyaryoObject syaryo, String[] removeInfo) {
        for (String remove : removeInfo) {
            syaryo.remove(remove);
        }
    }

    //日付に混じっているごみを削除 (日付が無い or 日付がおかしい)
    private static void formDate(SyaryoObject syaryo, List indexList, Integer date) {
        for (Object key : syaryo.getMap().keySet()) {
            Map<String, List> map = syaryo.get(key.toString());
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

    //KOMTRAXデータの整形 (値の重複除去、日付の整形、小数->整数)
    private static void formKomtrax(SyaryoObject syaryo, Map<String, List> deploy) {
        //ALL
        List<String> kmList = KomatsuDataParameter.DATA_ORDER.stream().filter(s -> s.contains("KOMTRAX")).collect(Collectors.toList());
        String stdate = deploy.keySet().stream().findFirst().get();
        
        for (String id : kmList) {
            if (syaryo.get(id) == null) {
                continue;
            }

            //Formalize Date
            Map newMap = new TreeMap();
            String tmp = "";
            for (String date : syaryo.get(id).keySet()) {
                
                String d = date.split("#")[0].split(" ")[0].replace("/", "");
                String str = syaryo.get(id).get(date).toString();
                
                //出荷より前のセンサー情報を消す
                if(Integer.valueOf(d) < Integer.valueOf(stdate))
                    continue;

                if (!tmp.equals(str)) {
                    List value = getTransformValue(id, syaryo.get(id).get(date));
                    if(id.equals("KOMTRAX_SMR"))
                        newMap.put(d, value);
                    else
                        newMap.put(dup(d, newMap), value);
                    tmp = str;

                }
            }

            syaryo.put(id, newMap);
        }
    }

    //KOMTRAXデータを整数値に変換(SMR, FUEL_CONSUME)
    private static List<String> getTransformValue(String id, List<String> kmvalue) {
        if (id.contains("SMR")) {
            kmvalue.set(0, String.valueOf(Double.valueOf(kmvalue.get(0)).intValue()));
        }else if(id.contains("FUEL")){
            kmvalue.set(0, String.valueOf(Double.valueOf(kmvalue.get(0))));
        }else if(id.contains("GPS")){
            //緯度
            kmvalue.set(0, String.valueOf(Double.valueOf(MapPathData.compValue(kmvalue.get(0)))));
            //経度
            kmvalue.set(1, String.valueOf(Double.valueOf(MapPathData.compValue(kmvalue.get(1)))));
        }

        return kmvalue;
    }

    //重複して並んでいるIDを重複除去
    private static List exSeqDuplicate(List<String> dupList) {
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
    private static Map dateFormalize(Map dateMap) {
        Map map = new TreeMap();
        for (Object date : dateMap.keySet()) {
            //日付
            String d = date.toString().replace("/", "");
            map.put(d, dateMap.get(date));
        }
        return map;
    }

    //空の情報を削除
    private static void removeEmptyObject(SyaryoObject syaryo) {
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
    private static String dup(String key, Map map) {
        int cnt = 0;
        String k = key;
        while (map.get(k) != null) {
            k = key + "#" + df.format(++cnt);
        }
        return k;
    }

    //SMRの異常値を除去　出来が悪いのでつくり直し
    private static Map rejectSMRData(Map<String, List<String>> smr, int idx) {

        //MA
        List smrList = smr.values().stream()
            .map(s -> s.get(idx)).collect(Collectors.toList());
        List dates = smr.keySet().stream().collect(Collectors.toList());
        //List ma = MovingAverage.avg(smr, 5);

        //Regression
        Map<String, String> reg = R.getInstance().residuals(dates, smrList);
        List res = new ArrayList();
        for (String d : reg.keySet()) {
            String c = String.valueOf(Double.valueOf(Double.valueOf(reg.get(d)) - Double.valueOf(smrList.get(dates.indexOf(d)).toString())).intValue());
            res.add(c);
        }
        Map<String, String> sgtest = R.getInstance().detectOuters(dates, res);
        for (String date : sgtest.keySet()) {
            if (!sgtest.get(date).equals("NaN")) {
                sgtest.put(date, smr.get(date).get(idx));
            }
        }

        //異常データの排除
        Map<String, Integer> sortMap = sgtest.entrySet().stream()
            .filter(e -> !e.getValue().equals("NaN"))
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(e -> e.getKey(), e -> Integer.valueOf(e.getValue()), (e, e2) -> e, LinkedHashMap::new));
        //List list = R.getInstance().detectOuters(sortMap.keySet().stream().collect(Collectors.toList()));
        //System.out.println(sortMap);
        List<String> sortList = sortMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .map(e -> e.getKey())
            .collect(Collectors.toList());
        //System.out.println(sortList);
        Deque<String> q = new ArrayDeque<String>();
        for (String date : sortList) {
            if (!q.isEmpty()) {
                while (Integer.valueOf(q.getLast()) > Integer.valueOf(date)) {
                    q.removeLast();
                    if (q.isEmpty()) {
                        break;
                    }
                }
            }
            q.addLast(date);
        }

        //System.out.println(q);
        Map<String, String> resultMap = new TreeMap<>();
        for (String date : sgtest.keySet()) {
            if (q.contains(date)) {
                resultMap.put(date, sortMap.get(date).toString());
            } else {
                resultMap.put(date, "NaN");
            }
        }

        for (String date : resultMap.keySet()) {
            if (resultMap.get(date).equals("NaN")) {
                smr.remove(date);
            }
        }

        return smr;
    }
}
