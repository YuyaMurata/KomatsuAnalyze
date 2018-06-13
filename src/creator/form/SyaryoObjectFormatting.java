/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import analyze.MovingAverage;
import java.util.ArrayDeque;
import param.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import program.r.R;

/**
 *
 * @author zz17390
 */
public class SyaryoObjectFormatting {

    private static String KISY = "PC138US";
    private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
    private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;
    private static String INDEX_PATH = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
    private static Map<String, List> dataIndex;

    public static void main(String[] args) {
        //Data Index
        dataIndex = index();

        form(KISY);
    }

    private static String currentKey;
    private static void form(String kisy) {
        SyaryoToZip3 zip3 = new SyaryoToZip3();
        String filename = OBJPATH + "syaryo_obj_" + kisy + ".bz2";
        Map<String, SyaryoObject4> syaryoMap = zip3.read(filename);

        //本社コード
        Map<String, String> honsyIndex = new MapIndexToJSON().reader(INDEX_PATH);
        
        //車両の削除
        rejectSyaryo(syaryoMap, new String[]{"UR", "GC"});
        
        int n = 0;
        for (String key : syaryoMap.keySet()) {
            System.out.println(key);
            currentKey = key;
            
            SyaryoObject4 syaryo = syaryoMap.get(key);
            syaryo.decompress();

            //整形時のデータ削除ルールを設定
            DataRejectRule rule = new DataRejectRule();

            //整形後出力するMap
            Map newMap;

            //顧客の整形
            newMap = formOwner(syaryo.get("顧客"), dataIndex.get("顧客"), honsyIndex, rule);
            syaryo.map.put("顧客", newMap);

            //新車の整形
            newMap = formNew(syaryo.get("新車"), syaryo.get("生産"), syaryo.get("出荷"), dataIndex.get("新車"));
            syaryo.map.put("新車", newMap);
            rule.addNew(newMap.keySet().stream().findFirst().get().toString());

            //中古車の整形
            newMap = formUsed(syaryo.get("中古車"), dataIndex.get("中古車"), rule.getNew(), rule.getKUEC());
            syaryo.map.put("中古車", newMap);

            //受注
            newMap = formOrder(syaryo.get("受注"), dataIndex.get("受注"), rule.getWORKID(), rule.getPARTSID());
            syaryo.map.put("受注", newMap);

            //作業
            newMap = formWork(syaryo.get("作業"), dataIndex.get("作業"), rule.getWORKID());
            syaryo.map.put("作業", newMap);

            //部品
            newMap = formParts(syaryo.get("部品"), dataIndex.get("部品"), rule.getPARTSID());
            syaryo.map.put("部品", newMap);
            
            //SMR
            newMap = formSMR(syaryo.get("SMR"), dataIndex.get("SMR"));
            syaryo.map.put("SMR", newMap);
            
            //Komtrax
            formKomtrax(syaryo);
            
            //余計な情報を削除
            formExtra(syaryo, new String[]{"最終更新日", "経歴", "国"});
            
            syaryo.compress(true);
            n++;

            if (n % 1000 == 0) {
                System.out.println(n + "台処理");
            }
        }
        
        //R
        R.close();
        
        String outfile = OBJPATH + "syaryo_obj_" + kisy + "_form.bz2";
        zip3.write(outfile, syaryoMap);
    }
    
    private static void rejectSyaryo(Map<String, SyaryoObject4> syaryoMap, String[] company){
        List<String> reject = new ArrayList();
        for (String key : syaryoMap.keySet()) {
            SyaryoObject4 syaryo = syaryoMap.get(key);
            syaryo.decompress();
            
            //Delete Company
            Optional check = syaryo.get("最終更新日").values().stream()
                    .filter(list -> list.contains(company[0])
                    || list.contains(company[1]))
                    .findFirst();
            
            if(check.isPresent())
                reject.add(key);
            
            syaryo.compress(true);
        }
        
        for(String key : reject)
            syaryoMap.remove(key);
    }
    
    private static Map formOwner(Map<String, List> owner, List indexList, Map<String, String> honsyIndex, DataRejectRule reject) {
        if (owner == null) {
            //System.out.println("Not found owner!");
            return null;
        }

        Integer company = indexList.indexOf("KSYCD");
        Integer ownerID = indexList.indexOf("NNSCD");
        Integer ownerName = indexList.indexOf("NNSK_NM_1");

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

        //名称重複排除
        /*owners = map.values().stream()
                        .map(l -> l.get(ownerName))
                        .distinct()
                        .collect(Collectors.toList());
        Map<String, List> map2 = new TreeMap();
        i = 0;
        for(String date : map.keySet()){
            String name = map.get(date).get(ownerName).toString();
            if(name.equals(owners.get(i))){
                map2.put(date, map.get(date));
                i++;
                if(owners.size() <= i)
                    break;
            }
        }*/
        return map;
    }

    private static Map formNew(Map<String, List> news, Map<String, List> born, Map<String, List> deploy, List indexList) {
        Map<String, List> map = new TreeMap();
        if (news == null) {
            //出荷情報を取得する処理
            String date = "";
            if (deploy != null) {
                date = deploy.keySet().stream().findFirst().get();
            }
            if(date.equals("") || date.equals("None")){
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
            if(Integer.valueOf(d) <= Integer.valueOf(newd))
                continue;

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

    private static Map formOrder(Map<String, List> order, List indexList, List workOrder, List partsOrder) {
        if (order == null) {
            //System.out.println("Not found Order!");
            return null;
        }

        //作番重複除去
        List<String> sbnList = order.keySet().stream()
            .map(s -> s.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        //System.out.println("作番:"+sbnList);
        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("kom_order");
        int price = indexList.indexOf("SKKG");
        int kind = indexList.indexOf("ODR_KBN");

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
                        }else if(list.get(price).contains("+")){
                            map.put(sbn, list);
                        }
                    }

                }
            }

            //System.out.println(map.get(sbn));
            partsOrder.add(sbn);
            if(map.get(sbn).get(kind).equals("2"))
                workOrder.add(sbn);
        }

        //金額の整形処理
        for (String sbn : map.keySet()) {
            List<String> list = map.get(sbn);
            list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
        }

        return map;
    }
    
    private static Map formWork(Map<String, List> work, List indexList, List workOrder) {
        if (work == null) {
            //System.out.println("Not found Work!");
            return null;
        }

        //作番重複除去
        List<String> sbnList = work.keySet().stream()
            .map(s -> s.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        if (sbnList.size() != workOrder.size()) {
            //差分
            List diff = sbnList.stream().filter(s -> !workOrder.contains(s)).collect(Collectors.toList());
            //System.out.println("order:" + workOrder);
            //System.out.println("work:" + sbnList);
            //System.out.println("受注作番と作業の発行数が違う");
            //System.out.println(currentKey+",差分[work-order],"+diff);
            /*if(!diff.isEmpty())
                diff.stream()
                    .map(s -> currentKey+","+work.get(s).get(indexList.indexOf("KSYCD"))+","+s+","+work.get(s).get(indexList.indexOf("LAST_UPD_DAYT")))
                    .forEach(System.out::println);
            */
            //受注にない作番を削除 (4月中に売り上げが完了していないもの)
            sbnList = sbnList.stream().filter(s -> !diff.contains(s)).collect(Collectors.toList());
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("work_info");

        for (String sbn : sbnList) {
            //重複作番を取り出す
            List<String> sbnGroup = work.keySet().stream()
                .filter(s -> s.contains(sbn))
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

    private static Map formParts(Map<String, List> parts, List indexList, List partsOrder) {
        if (parts == null) {
            //System.out.println("Not found Parts!");
            return null;
        }

        //作番重複除去
        List<String> sbnList = parts.keySet().stream()
            .map(s -> s.split("#")[0])
            .distinct()
            .collect(Collectors.toList());

        if (sbnList.size() != partsOrder.size()) {
            //差分
            List diff = sbnList.stream().filter(s -> !partsOrder.contains(s)).collect(Collectors.toList());
            
            //System.out.println("order:" + partsOrder);
            //System.out.println("parts:" + sbnList);
            //System.out.println("受注作番と作業の発行数が違う");
            //System.out.println(currentKey+",差分[parts-order],"+diff);
            /*if(!diff.isEmpty())
                diff.stream()
                    .map(s -> currentKey+","+parts.get(s).get(indexList.indexOf("KSYCD"))+","+s+","+parts.get(s).get(indexList.indexOf("LAST_UPD_DAYT")))
                    .forEach(System.out::println);
            */
            //受注にない作番を削除 (4月中に売り上げが完了していないもの)
            sbnList = sbnList.stream().filter(s -> !diff.contains(s)).collect(Collectors.toList());
        }

        Map<String, List<String>> map = new LinkedHashMap();

        int db = indexList.indexOf("service");

        for (String sbn : sbnList) {
            //重複作番を取り出す
            List<String> sbnGroup = parts.keySet().stream()
                .filter(s -> s.contains(sbn))
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
    
    private static Map formSMR(Map<String, List> smr, List indexList) {
        if (smr == null) {
            //System.out.println("Not found Work!");
            return null;
        }
        
        int smridx = indexList.indexOf("SVC_MTR");
        
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
                .collect(Collectors.toList());
            
            //欠損データのみのため
            if(dateGroup.isEmpty())
                continue;
            
            if(date.length() > 8)
                date = date.substring(0, 8);
            
            for(String dg : dateGroup){
                List list = smr.get(dg);
                
                if(map.get(date) == null){
                    map.put(date, list);
                }else{
                    if(Float.valueOf(map.get(date).get(smridx)) < Float.valueOf(list.get(smridx).toString()))
                        map.put(date, list);
                }
            }
            
            //整形処理
            map.get(date).set(smridx, map.get(date).get(smridx).split("\\.")[0]);
            if(map.get(date).get(smridx).equals("0")){
                if(zeroflg)
                    map.remove(date);
                else 
                    zeroflg = true;
            }
        }
        
        if(map.isEmpty())
            return null;
        
        //異常データの排除
        map = rejectSMRData(map, smridx);

        return map;
    }
    
    private static void formExtra(SyaryoObject4 syaryo, String[] removeInfo){
        for(String remove : removeInfo)
            syaryo.map.remove(remove);
    }
    
    private static void formKomtrax(SyaryoObject4 syaryo){
        //ALL
        List<String> kmList = dataIndex.keySet().stream().filter(s -> s.contains("KOMTRAX")).collect(Collectors.toList());
        for(String id : kmList){
            if(syaryo.get(id) == null)
                continue;
            
            //Formalize Date
            Map newMap = new TreeMap();
            String tmp = "";
            for(String date : syaryo.get(id).keySet()){
                String d = date.split("#")[0].split(" ")[0].replace("/", "");
                String str = syaryo.get(id).get(date).toString();
                
                if(!tmp.equals(str))
                    if(newMap.get(d) == null){
                        newMap.put(d, syaryo.get(id).get(date));
                        tmp = str;
                    }
            }
            
            syaryo.map.put(id, newMap);
        }
    }
    
    //Util
    private static Map<String, List> index() {
        Map<String, Map<String, List<String>>> index = new MapIndexToJSON().reader(INDEXPATH);
        Map<String, List> formIndex = new HashMap();

        for (String key : index.keySet()) {
            List<String> list = new ArrayList();
            List<String> list2 = new ArrayList();
            int n = 0;
            for (Object id : index.get(key).keySet()) {
                int s = index.get(key).get(id).stream().mapToInt(le -> le.contains("#") ? 1 : 0).sum();
                if (s == index.get(key).get(id).size()) {
                    list = index.get(key).get(id);
                }
                if (n < s) {
                    list2 = index.get(key).get(id);
                    n = s;
                }
            }

            if (list.isEmpty()) {
                list = list2;
            }

            //List内の整形
            list = list.stream().filter(le -> !(le.contains("=") || le.contains("<") || le.contains(">"))).collect(Collectors.toList());
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                if (str.contains("#")) {
                    str = str.split("#")[0].split("\\.")[1];
                }
                list.set(i, str);
            }

            formIndex.put(key, list);
        }
        formIndex.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).forEach(System.out::println);
        return formIndex;
    }

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

    private static Map dateFormalize(Map dateMap) {
        Map map = new TreeMap();
        for (Object date : dateMap.keySet()) {
            //日付
            String d = date.toString().replace("/", "");
            map.put(d, dateMap.get(date));
        }
        return map;
    }
    
    private static Map rejectSMRData(Map<String, List<String>> smr, int idx){
        //MA
        List smrList = smr.values().stream()
                                .map(s -> s.get(idx)).collect(Collectors.toList());
        List dates = smr.keySet().stream().collect(Collectors.toList());
        //List ma = MovingAverage.avg(smr, 5);
        
        //Regression
        Map<String, String> reg = R.getInstance().residuals(dates, smrList);
        List res = new ArrayList();
        for(String d : reg.keySet()){
            String c = String.valueOf(Double.valueOf(Double.valueOf(reg.get(d)) - Double.valueOf(smrList.get(dates.indexOf(d)).toString())).intValue());
            res.add(c);
        }
        Map<String, String> sgtest = R.getInstance().detectOuters(dates, res);
        for(String date : sgtest.keySet())
            if(!sgtest.get(date).equals("NaN"))
                sgtest.put(date, smr.get(date).get(idx));
        
        //異常データの排除
        Map<String, Integer> sortMap = sgtest.entrySet().stream()
                                            .filter(e -> !e.getValue().equals("NaN"))
                                            .sorted(Map.Entry.comparingByKey())
                                            .collect(Collectors.toMap(e -> e.getKey(), e  -> Integer.valueOf(e.getValue()), (e, e2) -> e, LinkedHashMap::new));
        //List list = R.getInstance().detectOuters(sortMap.keySet().stream().collect(Collectors.toList()));
        //System.out.println(sortMap);
        List<String> sortList = sortMap.entrySet().stream()
                                    .sorted(Map.Entry.comparingByValue())
                                    .map(e -> e.getKey())
                                    .collect(Collectors.toList());
        //System.out.println(sortList);
        Deque<String> q = new ArrayDeque<String>();
        for(String date : sortList){
            if(!q.isEmpty())
                while(Integer.valueOf(q.getLast()) > Integer.valueOf(date)){
                    q.removeLast();
                    if(q.isEmpty())
                        break;
                }
            q.addLast(date);
        }
        
        //System.out.println(q);
        
        Map<String, String> resultMap = new TreeMap<>();
        for(String date : sgtest.keySet()){
            if(q.contains(date))
                resultMap.put(date, sortMap.get(date).toString());
            else
                resultMap.put(date, "NaN");
        }
        
        for(String date : resultMap.keySet())
            if(resultMap.get(date).equals("NaN"))
                smr.remove(date);
        
        return smr;
    }
}
