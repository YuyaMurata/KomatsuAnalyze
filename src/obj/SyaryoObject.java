/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject {

    public String name;
    public Map<String, Object> map = new LinkedHashMap();

    public SyaryoObject(String name) {
        this.name = name;
    }
    
    //生産、出荷、廃車
    public void add(String key, String s1){
        map.put(key, s1);
    }
    
    //国、更新日
    public void add(String key, String s1, String s2){
        if(key.equals("国")) addCountry(s1, s2);
        else addLast(s1, s2);
    }
    
    //仕様, SMR, GPS, エンジン
    public void add(String key, String s1, String s2, String s3){
        if(key.equals("経歴")) addHistory(s1, s2, s3);
        else if(key.equals("仕様")) addSpec(s1, s2, s3);
        else if(key.contains("SMR")) addSMR(s1, s2, s3);
        else if(key.contains("GPS")) addGPS(s1, s2, s3);
        else if(key.contains("ENGINE")) addEngine(s1, s2, s3);
    }
    
    //エラー
    public void add(String key, String s1, String s2, String s3, String s4){
        if(key.contains("ERROR")) addError(s1, s2, s3, s4);
    }
    
    //顧客
    
    //新・中古車、経歴、サービス、作業、部品、警告
    public void add(String key, String s1, String s2, List l1, String s3){
        if(key.equals("新車")) addNew(s1, s2, l1, s3);
        else if(key.equals("中古")) addUsed(s1, s2, l1, s3);
        else if(key.equals("受注")) addService(s1, s2, l1, s3);
        else if(key.equals("作業")) addWork(s1, s2, l1, s3);
        else if(key.equals("部品")) addParts(s1, s2, l1, s3);
        else if(key.contains("CAUTION")) addCaution(s1, s2, l1, s3);
    }

    public void addBorn(String date) {
        map.put("生産", date);
    }

    public void addDeploy(String date) {
        map.put("出荷", date);
    }

    public void addDead(String date) {
        map.put("廃車", date);
    }

    public void addNew(String date, String cid, List price, String source) {
        TreeMap newmap = new TreeMap();
        newmap.put(date, new Object[]{cid, price, source});
        map.put("新車", newmap);
    }

    public void addSpec(String category, String komtrax, String source) {
        map.put("仕様", new Object[]{category, komtrax, source});
    }

    public void addUsed(String date, String cid, List price, String source) {
        TreeMap usedmap = (TreeMap) map.get("中古");
        if (usedmap == null) {
            usedmap = new TreeMap();
        }
        usedmap.put(date_no("中古", date), new Object[]{cid, price, source});
        map.put("中古", usedmap);
    }
    
    public void addCountry(String date, String country) {
        TreeMap contmap = (TreeMap) map.get("国");
        if (contmap == null) {
            contmap = new TreeMap();
        }
        contmap.put(date_no("国", date), country);
        map.put("国", contmap);
    }

    public void addLast(String date, String source) {
        TreeMap lastmap = (TreeMap) map.get("最終更新日");
        if (lastmap == null) {
            lastmap = new TreeMap();
        }
        lastmap.put(date_no("最終更新日", date), source);
        map.put("最終更新日", lastmap);
    }

    public void addOwner(String date, String id, List list, String source) {
        TreeMap ownmap = (TreeMap) map.get("顧客");
        if (ownmap == null) {
            ownmap = new TreeMap();
        }
        ownmap.put(date_no("顧客", date), new Object[]{id, list, source});
        map.put("顧客", ownmap);
    }

    public void addSMR(String date, String smr, String source) {
        TreeMap smrmap = (TreeMap) map.get("SMR");
        if (smrmap == null) {
            smrmap = new TreeMap();
        }
        smrmap.put(date_no("SMR", date), new String[]{smr, source});
        map.put("SMR", smrmap);
    }

    public void addHistory(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("経歴");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("経歴", date), new Object[]{id, source});
        map.put("経歴", histmap);
    }

    public void addService(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("受注");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("受注", date), new Object[]{id, list, source});
        map.put("受注", histmap);
    }

    public void addWork(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("作業");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("作業", date), new Object[]{id, list, source});
        map.put("作業", histmap);
    }

    public void addParts(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("部品");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("部品", date), new Object[]{id, list, source});
        map.put("部品", histmap);
    }

    public void addGPS(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("GPS");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("GPS", date), new String[]{id, source});
        map.put("GPS", histmap);
    }
    
    public void addError(String date, String id, String count, String source) {
        TreeMap histmap = (TreeMap) map.get("エラー");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("エラー", date), new String[]{id, count, source});
        map.put("エラー", histmap);
    }
    
    public void addCaution(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("警告");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("警告", date), new Object[]{id, list, source});
        map.put("警告", histmap);
    }
    
    public void addEngine(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("エンジン");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        histmap.put(date_no("エンジン", date), new String[]{id, source});
        map.put("エンジン", histmap);
    }

    public String getName() {
        return name;
    }
    
    public String getBorn() {
        return (String) map.get("生産");
    }

    public String getDeploy() {
        return (String) map.get("出荷");
    }

    public String getNew() {
        return (String) map.get("新車");
    }

    public Map getUsed() {
        return (Map) map.get("中古");
    }

    public String getDead() {
        return (String) map.get("廃車");
    }

    public Map getLast() {
        return (Map) map.get("最終更新日");
    }

    public Map getSMR() {
        return (Map) map.get("SMR");
    }

    public Map<String, List> getOwner() {
        return (Map) map.get("顧客");
    }

    public Map getHistory() {
        return (Map) map.get("経歴");
    }

    public Map getCountry() {
        return (Map) map.get("国");
    }
    
    public Map<String, List> getWork() {
        return (Map) map.get("作業");
    }
    
    public Map<String, List> getParts() {
        return (Map) map.get("部品");
    }

    public String date_no(String key, String date) {
        Map oldMap = (Map) map.get(key);
        if (oldMap == null) {
            return date;
        } else {
            if (oldMap.get(date) == null) {
                return date;
            } else {
                int i = 1;
                while (oldMap.get(date + "#" + i) != null) {
                    i++;
                }
                return date + "#" + i;
            }
        }
    }

    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        sb.append(map);
        return sb.toString();
    }
}
