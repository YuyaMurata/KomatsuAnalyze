/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.MapIndexToJSON;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject;
import obj.SyaryoObject2;

/**
 * 車両オブジェクトから重複部分を排除して整形
 *
 * @author ZZ17390
 */
public class FormalizeSyaryoObject2 {

    //private static String kisy = "PC200";
    //private static List extract = Arrays.asList(new String[]{"PC200-8", "PC200-8N1", "PC200-10", "PC200LC-8", "PC200LC-10", "PC200LC-8N1"});
    //private static String kisy = "PC210";
    //private static List extract = Arrays.asList(new String[]{"PC210-8", "PC210-8N1", "PC210-10", "PC210LC-8","PC210LC-10","PC210LC-8N1"});
    //private static String kisy = "WA470";
    //private static List extract = Arrays.asList(new String[]{"WA470-7", "WA470-8"});
    private static String kisy = "PC138US";
    private static List extract = Arrays.asList(new String[]{"PC138US-2", "PC138US-8", "PC138US-10"});

    private static String filename = "json\\syaryo_obj_" + kisy + ".gz";
    private static String spritfilename = "json\\syaryo_obj_" + kisy + "_extract.gz";
    private static String output = "json\\syaryo_obj_" + kisy + "_form.gz";

    public static void main(String[] args) {

        //sprit(new SyaryoToZip().readObject(filename));
        forming(new SyaryoToZip().readObject(spritfilename));

        //Map joinMap = joinData("PC200", "PC210");
        /*map = new JsonToSyaryoObj().reader(output);
        syaryoMap = new FormalizeSyaryoObject2().split(map);
        new SyaryoObjToJson().write(output, syaryoMap);
        System.out.println("Finish deleting old type data!");

        //3次処理 補間処理
        map = new JsonToSyaryoObj().reader(output);
        syaryoMap = new FormalizeSyaryoObject2().interFormalize(map);
        new SyaryoObjToJson().write(output, syaryoMap);
        System.out.println("Finish interpolation date!");*/
        //3次処理　結合・圧縮
        //Map syaryoMap = new FormalizeSyaryoObject().join(new String[]{"PC200", "PC210", "HB205", "HB215"});
        //new SyaryoObjToJson().write2("syaryo_obj_"+kisy+"_form.json", syaryoMap);
        //System.out.println("Finish joining & compressing json data!");
        System.out.println("Finish forming json data!");

    }

    public static void sprit(Map map) {
        //1次処理 データの分離
        Map formMap = splitForming(map);
        new SyaryoToZip().write(spritfilename, (Map) formMap.get("extract"));
        new SyaryoToZip().write(filename.replace(kisy, kisy + "_reject"), (Map) formMap.get("reject"));
    }

    public static Map forming(Map map) {

        //2次処理 データ削減
        Map formMap = firstForming(map);

        //3次処理 重複除去
        formMap = secondForming(formMap);

        new SyaryoToZip().write(output, (Map) formMap);

        return formMap;
    }

    public static Map firstForming(Map<String, SyaryoObject2> map) {
        Map newMap = new TreeMap();
        //Before Count
        System.out.println("Before:" + map.size());

        //for (SyaryoObject2 syaryo : map.values()) {
        map.values().parallelStream().forEach(syaryo -> {
            syaryo.decompress();
            if (syaryo.get("最終更新日") == null) {
                System.out.println("除外:"+syaryo.getName());
            } else {
                //System.out.println(syaryo.getName());
                //Delete Company
                Optional check = syaryo.get("最終更新日").values().stream()
                    .filter(list -> list.contains("UR")
                    || list.contains("GC"))
                    .findFirst();
                if (!check.isPresent()) {
                    //Delete Field
                    syaryo.remove("経歴");
                    syaryo.remove("最終更新日");
                    syaryo.remove("国");

                    syaryo.compress(true);
                    newMap.put(syaryo.getName(), syaryo);
                }
            }
        });

        //After Count
        System.out.println("After:" + newMap.size());

        return newMap;
    }

    public static Map secondForming(Map<String, SyaryoObject2> map) {
        //Customer Index
        Map index = new MapIndexToJSON().reader("index\\customer_index.json");

        Map newMap = new TreeMap();

        //for (SyaryoObject2 syaryo : map.values()) {
        map.values().stream().forEach(syaryo -> {
            syaryo.decompress();

            System.out.println(syaryo.getName());
            formNew(syaryo.get("新車"));
            formUsed(syaryo.get("中古"));
            formOwner(syaryo.get("顧客"), index);
            formOrder(syaryo.get("受注"));
            formKMSMR(syaryo.get("KMSMR"));
            formAllSupport(syaryo.get("オールサポート"));
            
            syaryo.compress(true);
            newMap.put(syaryo.getName(), syaryo);
        });

        return newMap;
    }

    public static Map splitForming(Map<String, SyaryoObject2> map) {
        Map splitMap = new HashMap();

        List formList = split(map, extract);
        Map newMap = (Map) formList.get(0);
        System.out.println("Extract:" + newMap.size());

        //除外データ
        Map rejectMap = (Map) formList.get(1);
        System.out.println("Reject:" + rejectMap.size());

        splitMap.put("extract", newMap);
        splitMap.put("reject", rejectMap);

        return splitMap;
    }

    public static List<Map<String, SyaryoObject2>> split(Map<String, SyaryoObject2> syaryoMap, List extract) {
        //Type
        Map map1 = new TreeMap();
        Map map2 = new TreeMap();

        for (String name : syaryoMap.keySet()) {
            SyaryoObject2 syaryo = syaryoMap.get(name);
            syaryo.name = name;
            
            if (extract.stream()
                .filter(ksyType -> syaryo.getName().contains(ksyType + "-"))
                .findFirst().isPresent()) {
                map1.put(syaryo.getName(), syaryo);
            } else {
                //System.out.println(syaryo.getName());
                map2.put(syaryo.getName(), syaryo);
            }
        }

        List list = new ArrayList();
        list.add(map1);
        list.add(map2);

        return list;
    }

    public static Map joinData(String kisy1, String kisy2) {
        //1次処理 データ削減
        kisy = kisy1;
        Map map1 = new JsonToSyaryoObj().reader3(output);

        kisy = kisy2;
        Map map2 = new JsonToSyaryoObj().reader3(output);

        map1.putAll(map2);

        return map1;
    }

    private static void formNew(Map<String, List> news) {
        if (news == null) {
            return;
        }
        
        Map<String, List<String>> update = new TreeMap();

        Boolean flg = false;
        for (String date : news.keySet()) {
            List<String> obj = news.get(date);

            //System.out.println(date+":"+obj);
            if (date.contains("#")) {
                date = date.split("#")[0];
            }

            if (obj.get(SyaryoElements.New.DB.getNo()).contains("sell")) {
                if (flg) {
                    break;
                }
                if (!obj.get(SyaryoElements.New.DB.getNo()).contains("old")) {
                    flg = true;
                }
                obj.set(SyaryoElements.New.HPrice.getNo(), String.valueOf(Double.valueOf(obj.get(SyaryoElements.New.HPrice.getNo())).intValue()));
                obj.set(SyaryoElements.New.RPrice.getNo(), String.valueOf(Double.valueOf(obj.get(SyaryoElements.New.RPrice.getNo())).intValue()));
                obj.set(SyaryoElements.New.SPrice.getNo(), String.valueOf(Double.valueOf(obj.get(SyaryoElements.New.SPrice.getNo())).intValue()));
                update.put(date, obj);
            }
        }

        if (update.isEmpty()) {
            String date = news.keySet().stream().findFirst().get();
            update.put(date, news.get(date));
        }

        news.clear();
        news.putAll(update);
    }

    private static void formUsed(Map<String, List> used) {
        if (used == null) {
            return ;
        }
        Map<String, List> update = new TreeMap();

        for (String date : used.keySet()) {
            List obj = used.get(date);
            if (!obj.get(SyaryoElements.Used.SPrice.getNo()).equals("-1")) {
                obj.set(SyaryoElements.Used.SPrice.getNo(), String.valueOf(Float.valueOf(obj.get(SyaryoElements.Used.SPrice.getNo()).toString()).intValue()));
            }

            if (!obj.get(SyaryoElements.Used.HPrice.getNo()).equals("-1")) {
                obj.set(SyaryoElements.Used.HPrice.getNo(), String.valueOf(Float.valueOf(obj.get(SyaryoElements.Used.HPrice.getNo()).toString()).intValue()));
            }

            if (!obj.get(SyaryoElements.Used.RPrice.getNo()).equals("-1")) {
                obj.set(SyaryoElements.Used.RPrice.getNo(), String.valueOf(Float.valueOf(obj.get(SyaryoElements.Used.RPrice.getNo()).toString()).intValue()));
            }

            //System.out.println(date+":"+obj);
            if (date.contains("#")) {
                date = date.split("#")[0];
                if (update.get(date) != null) {
                    if (!update.get(date).get(SyaryoElements.Used.SPrice.getNo()).equals("-1")
                        || !update.get(date).get(SyaryoElements.Used.HPrice.getNo()).equals("-1")
                        || !update.get(date).get(SyaryoElements.Used.RPrice.getNo()).equals("-1")) {
                        obj = update.get(date);
                    }
                }
            }
            update.put(date, obj);
        }

        used.clear();
        used.putAll(update);
    }

    private static void formOwner(Map<String, List> owner, Map index) {
        if(owner == null)
            return ;
        
        Map update = new TreeMap();

        String newdate = "???", id = "???", name = "???";
        String[] code = new String[]{"?", "?"};
        List temp = null;
        
        List owners = owner.values().stream()
                                .map(l -> l.get(SyaryoElements.Customer.ID.getNo()))
                                .distinct()
                                .collect(Collectors.toList());
        
        int i = 0;
        for (String date : owner.keySet()) {
            List obj = owner.get(date);
            if(obj.get(SyaryoElements.Customer.ID.getNo()).equals(owners.get(i))){
                i++;
                update.put(date.split("#")[0], obj);
            }
            if(i > (owners.size()-1))
                break;
        }
        
        /*
        for (String date : owner.keySet()) {
            List obj = owner.get(date);

            //System.out.println(date+":"+obj);
            if (obj.get(SyaryoElements.Customer.ID.getNo()).toString().contains("##")
                || obj.get(SyaryoElements.Customer.ID.getNo()).toString().equals("")) {
                continue;
            } else if (obj.get(SyaryoElements.Customer.ID.getNo()).toString().length() < 6) {
                continue;
            }

            //code formating
            if (obj.get(SyaryoElements.Customer.Code.getNo()).toString().equals("-1")
                || obj.get(SyaryoElements.Customer.Code.getNo()).toString().split("-").length < 2) {
                obj.set(SyaryoElements.Customer.Code.getNo(), "?-?");
            }

            if (!obj.get(SyaryoElements.Customer.ID.getNo()).equals(id)
                && !obj.get(SyaryoElements.Customer.Name.getNo()).toString().contains(name)) {

                newdate = date.split("#")[0];
                code = obj.get(SyaryoElements.Customer.Code.getNo()).toString().split("-");

                id = (String) obj.get(SyaryoElements.Customer.ID.getNo());
                name = (String) obj.get(SyaryoElements.Customer.Name.getNo());
                temp = obj;

                update.put(newdate, temp);

                //System.out.println(newdate);
            } else {
                String gyosyu = obj.get(SyaryoElements.Customer.Code.getNo()).toString();
                //System.out.println("1:"+gyosyu);
                if (!code[0].equals("?") && !code[1].equals("?")) {
                    continue;
                }
                if (!gyosyu.equals("-1")) {
                    //System.out.println("2:"+gyosyu);
                    if (!gyosyu.equals("?-?")) {
                        code[0] = gyosyu.split("-")[0];
                        code[1] = gyosyu.split("-")[1];
                    } else if (!gyosyu.split("-")[0].equals("?")) {
                        code[0] = gyosyu.split("-")[0];
                    } else if (!gyosyu.split("-")[1].equals("?")) {
                        code[1] = gyosyu.split("-")[1];
                    }
                    temp.set(SyaryoElements.Customer.Code.getNo(), code[0] + "-" + code[1]);
                    //System.out.println("After:"+code[0] + "-" + code[1]);
                    temp.set(SyaryoElements.Customer.Name.getNo(), obj.get(SyaryoElements.Customer.Name.getNo()));
                }
            }
        }
        //System.out.println(update);

        //index check
        String temp_id = "???";
        List removeDate = new ArrayList();

        for (Object date : update.keySet()) {
            String customer_id = ((List) update.get(date)).get(SyaryoElements.Customer.Company.getNo())
                + "_" + ((List) update.get(date)).get(SyaryoElements.Customer.ID.getNo());

            if (index.get(customer_id) != null) {
                if (index.get(temp_id) != null) {
                    if (index.get(temp_id).equals(index.get(customer_id))) {
                        removeDate.add(date);
                    }
                }
            }

            temp_id = customer_id;
        }
        for (Object date : removeDate) {
            update.remove(date);
        }*/

        owner.clear();

        owner.putAll(update);
    }

    private static void formOrder(Map<String, List> order) {
        if (order == null) {
            return;
        }
        
        Map update = new TreeMap();

        for (String date : order.keySet()) {
            List list = order.get(date);

            setValue(SyaryoElements.Order.Step.getNo(), list);
            setValue(SyaryoElements.Order.DStep.getNo(), list);
            setValue(SyaryoElements.Order.Step.getNo(), list);
            setValue(SyaryoElements.Order.Invoice.getNo(), list);

            update.put(date, list);
        }

        order.clear();
        order.putAll(update);
    }

    //Form Order SetList
    private static void setValue(int index, List list) {
        try {
            if (!list.get(index).equals("-1")) {
                list.set(index, Float.valueOf(list.get(index).toString()).toString());
            }
        } catch (NumberFormatException e) {
            list.set(index, "-1");
        }
    }

    private static void formKMSMR(Map<String, List> kmsmrMap) {
        if (kmsmrMap == null) {
            return;
        }

        TreeMap update = new TreeMap();
        for (String date : kmsmrMap.keySet()) {
            //System.out.println(date+":"+obj);
            if (date.contains("#")) {
                continue;
            }
            
            //komtraxSMR 分→時間 変換
            List list = kmsmrMap.get(date);
            list.set(3, String.valueOf((Integer) (Integer.valueOf(list.get(3).toString()) / 60)));
            
            update.put(date, list);
        }
        
        kmsmrMap.clear();
        kmsmrMap.putAll(update);
    }
    
    private static DecimalFormat dformat = new DecimalFormat("00");
    private static void formAllSupport(Map<String, List> asMap) {
        if (asMap == null) {
            return;
        }
        
        if(1 < asMap.size()){
            List removeKey = asMap.keySet().stream().limit(asMap.size()-1).collect(Collectors.toList());
            for(Object key : removeKey)
                asMap.remove(key);
        }
        List list = asMap.values().stream().findFirst().get();
        String finish = (String) list.get(SyaryoElements.AllSupport.FINISH.getNo());
        String[] formFinish = finish.split("/");
        formFinish[1] = dformat.format(Integer.valueOf(formFinish[1]));
        formFinish[2] = dformat.format(Integer.valueOf(formFinish[2]));
        list.set(SyaryoElements.AllSupport.FINISH.getNo(), String.join("", formFinish));
    }

    private void formGPS(Map<String, List> gps, Map<String, List> smr) {
        Map update = new TreeMap();

        if (gps == null) {
            return;
        }

        Map check = new HashMap();
        for (String date : gps.keySet()) {
            update.put(date, gps.get(date));
        }

        gps.clear();
        gps.putAll(update);
    }

    private void formCountry(Map<String, List> country) {
        Map update = new TreeMap();
        Object temp = null;

        if (country == null) {
            return;
        }

        for (String date : country.keySet()) {
            if (!country.get(date).equals(temp)) {
                update.put(date, country.get(date));
                temp = country.get(date);
            }
        }

        country.clear();
        country.putAll(update);
    }

    private void formWorkParts(Map<String, List> work, Map<String, List> parts) {
        //Work
        if (work != null) {
            Map update1 = new TreeMap();
            for (String date : work.keySet()) {

                List list = work.get(date);
                if (list.get(8).equals("")) {
                    list.set(8, "-1");
                }
                list.set(8, String.valueOf(Float.valueOf(list.get(8).toString()).intValue()));
                list.set(10, String.valueOf(Float.valueOf(list.get(10).toString()).intValue()));
                if (!list.get(11).equals("-1")) {
                    list.set(11, Float.valueOf(list.get(11).toString()).toString());
                }
                if (!list.get(12).equals("-1")) {
                    list.set(12, Float.valueOf(list.get(12).toString()).toString());
                }
                if (!list.get(13).equals("-1")) {
                    list.set(13, Float.valueOf(list.get(13).toString()).toString());
                }
                if (!list.get(14).equals("-1")) {
                    list.set(14, Float.valueOf(list.get(14).toString()).toString());
                }

                update1.put(date, list);
            }
            work.clear();
            work.putAll(update1);
        }

        if (parts != null) {
            Map update2 = new TreeMap();

            //Parts
            for (String date : parts.keySet()) {

                List list = parts.get(date);
                list.set(SyaryoElements.Parts.Price.getNo(), String.valueOf(Float.valueOf(list.get(SyaryoElements.Parts.Price.getNo()).toString()).intValue()));

                //注文数量 == キャンセル数量
                if (list.get(SyaryoElements.Parts.Quant.getNo()).toString().compareTo(list.get(SyaryoElements.Parts.Cancel.getNo()).toString()) == 0) {
                    continue;
                }

                update2.put(date, list);
            }
            parts.clear();
            parts.putAll(update2);
        }

    }

    private void formError(Map<String, List> error) {
        if (error == null) {
            return;
        }
        Map<String, Integer> errorCnt = new HashMap();
        for (String date : error.keySet()) {
            String code = (String) error.get(date).get(SyaryoElements.Error.Code.getNo());
            Integer cnt = Integer.valueOf((String) error.get(date).get(SyaryoElements.Error.Count.getNo()));

            if (errorCnt.get(code) == null) {
                errorCnt.put(code, 0);
            }
            error.get(date).set(SyaryoElements.Error.Count.getNo(), String.valueOf(cnt - errorCnt.get(code)));

            errorCnt.put(code, cnt);
        }
    }

    //オブジェクトデータの補間
    private Map<String, SyaryoObject> interFormalize(Map<String, SyaryoObject> syaryoMap) {
        Map map = new TreeMap();

        for (String name : syaryoMap.keySet()) {
            //System.out.println(name);

            interSMR(syaryoMap.get(name), syaryoMap.get(name).getSMR());

            map.put(name, syaryoMap.get(name));
        }

        return map;
    }

    //SMRの補間
    private void interSMR(SyaryoObject syaryo, Map<String, List> smrMap) {
        Map<String, List> update = new TreeMap();

        if (smrMap == null) {
            return;
        }

        int smr = SyaryoElements.SMR._SMR.getNo();

        LinkedList<String> queue = new LinkedList<>();

        for (String date : smrMap.keySet()) {
            List obj = smrMap.get(date);

            //3データから線形補間
            queue.offer(date);
            if (queue.size() == 3) {
                List<Integer> smrValue = queue.stream()
                    .map(s -> Integer.valueOf(smrMap.get(s).get(smr).toString()))
                    .collect(Collectors.toList());

                if (smrValue.get(1) < smrValue.get(0)) {
                    String interValue = linearInterpolation(queue, smrValue);
                    System.out.println(" before = " + queue + ":" + smrMap.get(queue.get(1)));
                    smrMap.get(queue.get(1)).set(smr, interValue);
                    System.out.println(" after = " + queue + ":" + smrMap.get(queue.get(1)));
                }
                queue.poll();
            }
        }
    }

    //線形補間
    private String linearInterpolation(List<String> date, List<Integer> value) {
        Integer a = diffDate(date.get(1), date.get(0)) / diffDate(date.get(2), date.get(0));
        Integer interValue = value.get(0) + a * (value.get(2) - value.get(0));
        return interValue.toString();
    }

    //日付の差分計算
    public static int diffDate(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {
            long datetime1 = sdf.parse(date1).getTime();
            long datetime2 = sdf.parse(date2).getTime();
            long one_date_time = 1000 * 60 * 60 * 24;
            long diffDays = (datetime1 - datetime2) / one_date_time;
            return (int) diffDays;
        } catch (ParseException ex) {
            return -1;
        }
    }
}
