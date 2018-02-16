/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

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
import json.SyaryoObjToJson;
import obj.SyaryoElements;
import obj.SyaryoObject;
import obj.SyaryoObject2;

/**
 * 車両オブジェクトから重複部分を排除して整形
 *
 * @author ZZ17390
 */
public class FormalizeSyaryoObject2 {
    private static String kisy = "PC200";
    private static List extract = Arrays.asList(new String[]{"8", "10"});
    
    private static String filename = "json\\syaryo_obj_" + kisy + ".json";
    private static String output = "json\\syaryo_obj_" + kisy + "_form.json";
    private static String output2 = "json\\syaryo_obj_" + kisy + "_reject.json";
        
    public static void main(String[] args) {
        

        Map map = new JsonToSyaryoObj().reader3(filename);

        //1次処理 データ削減
        Map formMap = firstForming(map);

        //2次処理 重複除去
        formMap = secondForming(formMap);

        //3次処理 データの分離
        formMap = thirdForming(formMap);
        
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
        new SyaryoObjToJson().write(output, formMap);
        System.out.println("Finish forming json data!");

    }

    public static Map firstForming(Map<String, SyaryoObject2> map) {
        Map newMap = new TreeMap();
        //Before Count
        System.out.println("Before:" + map.size());

        for (SyaryoObject2 syaryo : map.values()) {
            //Delete Company
            Optional check = syaryo.get("最終更新日").values().stream()
                .filter(list -> list.contains("UR")
                || list.contains("GC"))
                .findFirst();
            if (check.isPresent()) {
                continue;
            }

            //Delete Country
            /*check = syaryo.get("国").values().stream()
                .filter(list -> !list.contains("JP"))
                .findFirst();
            if(check.isPresent()){
                continue;
            }*/
            
            //Delete Field
            syaryo.remove("経歴");
            syaryo.remove("最終更新日");
            syaryo.remove("国");

            newMap.put(syaryo.getName(), syaryo);
        }

        //After Count
        System.out.println("After:" + newMap.size());

        return newMap;
    }

    public static Map secondForming(Map<String, SyaryoObject2> map) {
        Map newMap = new TreeMap();

        for (SyaryoObject2 syaryo : map.values()) {
            formNew(syaryo.get("新車"));
            int numOwners = formUsed(syaryo.get("中古"));
            formOwner(syaryo.get("顧客"), numOwners);
            formOrder(syaryo.get("受注"));

            newMap.put(syaryo.getName(), syaryo);
        }

        return newMap;
    }

    public static Map thirdForming(Map<String, SyaryoObject2> map) {
        Map newMap = new TreeMap();
        
        List formList = split(map, extract);
        newMap = (Map) formList.get(0);
        System.out.println("Extract:"+newMap.size());
        
        //除外データ
        Map rejectMap = (Map) formList.get(1);
        new SyaryoObjToJson().write(output2, rejectMap);
        System.out.println("Reject:"+rejectMap.size());
        
        return newMap;
    }
    
    public static List<Map<String, SyaryoObject2>> split(Map<String, SyaryoObject2> syaryoMap, List extract) {
        //Type
        Map map1 = new TreeMap();
        Map map2 = new TreeMap();
        
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            if (extract.stream()
                        .filter(type -> syaryo.getType().contains(type.toString()))
                        .findFirst().isPresent()
                ) {
                map1.put(syaryo.getName(), syaryo);
            } else{
                //System.out.println(syaryo.getName());
                map2.put(syaryo.getName(), syaryo);
            }
        }
        
        List list = new ArrayList();
        list.add(map1);
        list.add(map2);
        
        return list;
    }
    
    private static void formNew(Map<String, List> news) {
        Map<String, List<String>> update = new TreeMap();

        if (news == null) {
            return;
        }

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

    private static Integer formUsed(Map<String, List> used) {
        Map<String, List> update = new TreeMap();

        if (used == null) {
            return 0;
        }

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

        return used.size();
    }

    private static void formOwner(Map<String, List> owner, Integer numUsed) {
        Map update = new TreeMap();
        String[] temp = new String[4];
        temp[1] = "?";

        if (owner == null) {
            return;
        }

        for (String date : owner.keySet()) {
            List obj = owner.get(date);

            //System.out.println(date+":"+obj);
            if (obj.get(SyaryoElements.Customer.ID.getNo()).toString().contains("##")
                || obj.get(SyaryoElements.Customer.ID.getNo()).toString().equals("")) {
                continue;
            } else if (obj.get(SyaryoElements.Customer.ID.getNo()).toString().length() < 6) {
                continue;
            } else if (obj.get(SyaryoElements.Customer.Name.getNo()).toString().contains("コマツ")
                || obj.get(SyaryoElements.Customer.Name.getNo()).toString().contains("小松")) {
                continue;
            }

            if (temp[0] != null) {
                if (obj.get(SyaryoElements.Customer.ID.getNo()).equals(temp[0])
                    || obj.get(SyaryoElements.Customer.Name.getNo()).toString().contains(temp[2])) {
                    if (!obj.get(SyaryoElements.Customer.Code.getNo()).toString().contains("?")
                        && !obj.get(SyaryoElements.Customer.Code.getNo()).toString().equals("-1")) {
                        update.put(temp[3], obj);
                    }
                    continue;
                }
            }

            if (date.contains("#")) {
                date = date.split("#")[0];
            }
            update.put(date, obj);
            temp[0] = (String) obj.get(0);
            temp[1] = (String) obj.get(1);
            temp[2] = (String) obj.get(2);
            temp[3] = date;
        }

        if (update.isEmpty()) {
            for (String date : owner.keySet()) {
                List obj = owner.get(date);

                //System.out.println(date+":"+obj);
                if (obj.get(SyaryoElements.Customer.ID.getNo()).toString().contains("##")
                    || obj.get(SyaryoElements.Customer.ID.getNo()).toString().equals("")) {
                    continue;
                }

                if (temp[0] != null) {
                    if (obj.get(SyaryoElements.Customer.ID.getNo()).equals(temp[0])
                        || obj.get(SyaryoElements.Customer.Name.getNo()).toString().contains(temp[2])) {
                        if (!obj.get(SyaryoElements.Customer.Code.getNo()).toString().contains("?")
                            && !obj.get(SyaryoElements.Customer.Code.getNo()).toString().equals("-1")) {
                            update.put(temp[3], obj);
                        }
                        continue;
                    }
                }

                if (date.contains("#")) {
                    date = date.split("#")[0];
                }
                update.put(date, obj);
                temp[0] = (String) obj.get(0);
                temp[1] = (String) obj.get(1);
                temp[2] = (String) obj.get(2);
                temp[3] = date;

            }
        }

        if (update.size() > numUsed + 1) {
            int i = 0;

            List removeList = new ArrayList();
            for (Object date : update.keySet()) {
                if (i++ > numUsed) {
                    removeList.add(date);
                }
            }

            for (Object date : removeList) {
                update.remove(date);
            }
        }

        owner.clear();
        owner.putAll(update);
    }

    private static void formOrder(Map<String, List> order) {
        Map update = new TreeMap();

        if (order == null) {
            return;
        }

        for (String date : order.keySet()) {
            List list = order.get(date);
            
            setValue(SyaryoElements.Order.Step.getNo(), list);
            setValue(SyaryoElements.Order.DStep.getNo(), list);
            setValue(SyaryoElements.Order.Step.getNo(), list);
            setValue(SyaryoElements.Order.Invoice.getNo(), list);
            setValue(SyaryoElements.Order.KInvoice.getNo(), list);

            update.put(date, list);
        }

        order.clear();
        order.putAll(update);
    }
    //Form Order SetList
    private static void setValue(int index, List list){
        try {
            if (!list.get(index).equals("-1"))
                list.set(index, Float.valueOf(list.get(index).toString()).toString());
        } catch (NumberFormatException e) {
            list.set(index, "-1");
        }
    }

    private void formLastUpdate(Map<String, List> last) {
        Map<String, List> update = new TreeMap();

        if (last == null) {
            return;
        }

        for (String date : last.keySet()) {
            String key = date.split("#")[0] + "-" + last.get(date);
            List list = new ArrayList();
            if (update.get(key) != null) {
                list.add((int) update.get(key).get(0) + 1);
            } else {
                list.add(1);
            }
            update.put(key, list);
        }

        last.clear();
        last.putAll(update);
    }

    private void formSMR(Map<String, List> smrMap, Boolean komtrax) {
        Map<String, List> update = new TreeMap();

        if (smrMap == null) {
            return;
        }

        int source = SyaryoElements.SMR.DB.getNo();
        int smr = SyaryoElements.SMR._SMR.getNo();

        for (String date : smrMap.keySet()) {
            List obj = smrMap.get(date);

            if (komtrax) {
                if (!obj.get(source).toString().contains("komtrax")) {
                    continue;
                }
            }

            //System.out.println(date+":"+obj);
            date = date.split("#")[0];

            if (obj.get(smr).toString().contains("?")) {
                continue;
            }

            //komtraxSMR 分→時間 変換
            if (obj.get(source).toString().contains("komtrax")) {
                obj.set(smr, String.valueOf((Integer) (Integer.valueOf(obj.get(smr).toString()) / 60)));
            }

            update.put(date, obj);
        }
        smrMap.clear();
        smrMap.putAll(update);
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