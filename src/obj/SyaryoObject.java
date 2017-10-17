/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
    
    public void add(Map template) {
        int n = 0;
        for (Object field : template.keySet()) {
            if (template.get(field) == null) {
                continue;
            }

            String[] lines = template.get(field).toString().split("\n");
            Boolean header_flg = true;
            for (String line : lines) {
                if (header_flg) {
                    header_flg = false;
                    continue;
                }

                String[] s = line.trim().split(",");
                List list = new ArrayList();

                try {
                    if (field.equals("仕様")) {
                        addSpec(s[0], s[1], "syaryo_?");
                    } else if (field.equals("生産")) {
                        addBorn(s[0]);
                    } else if (field.equals("出荷")) {
                        addDeploy(s[0]);
                    } else if (field.equals("廃車")) {
                        addDead(s[0]);
                    } else if (field.equals("新車")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addNew(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("中古")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addUsed(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("顧客")) {
                        list.add(s[3]);
                        list.add(s[5]);
                        addOwner(s[2], s[4], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("国")) {
                        addCountry(s[2], s[3]);
                    } else if (field.equals("経歴")) {
                        addHistory(s[2], s[3], (s[0] + "_" + s[1]));
                    } else if (field.equals("受注")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addOrder(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("作業")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addWork(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("部品")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addParts(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("CAUTION")) {
                        addCaution(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("ERROR")) {
                        addError(s[2], s[3], s[4], (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("ENGINE")) {
                        addEngine(s[2], s[3], (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("SMR")) {
                        addSMR(s[2], s[3], (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("GPS")) {
                        addGPS(s[2], (s[3] + "_" + s[4]), (s[0] + "_" + s[1]));
                    } else if (field.equals("最終更新日")) {
                        addLast(s[2], (s[0] + "_" + s[1]));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(field + ":" + Arrays.asList(s));
                    n++;
                }
            }
        }

        if (n > 0) {
            System.out.println("欠損データ=" + n);
        }
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
        List<Object> list = new ArrayList<>();
        list.add(cid);
        list.addAll(price);
        list.add(source);
        newmap.put(date, list);
        map.put("新車", newmap);
    }

    public void addSpec(String category, String komtrax, String source) {
        List<Object> list = new ArrayList<>();
        list.add(category);
        list.add(komtrax);
        list.add(source);
        map.put("仕様", list);
    }

    public void addUsed(String date, String cid, List price, String source) {
        TreeMap usedmap = (TreeMap) map.get("中古");
        if (usedmap == null) {
            usedmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(cid);
        list.addAll(price);
        list.add(source);
        usedmap.put(date_no("中古", date), list);
        map.put("中古", usedmap);
    }

    public void addCountry(String date, String country) {
        TreeMap contmap = (TreeMap) map.get("国");
        if (contmap == null) {
            contmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(country);
        contmap.put(date_no("国", date), list);
        map.put("国", contmap);
    }

    public void addLast(String date, String source) {
        TreeMap lastmap = (TreeMap) map.get("最終更新日");
        if (lastmap == null) {
            lastmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(source);
        lastmap.put(date_no("最終更新日", date), list);
        map.put("最終更新日", lastmap);
    }

    public void addOwner(String date, String id, List customer, String source) {
        TreeMap ownmap = (TreeMap) map.get("顧客");
        if (ownmap == null) {
            ownmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(customer);
        list.add(source);
        ownmap.put(date_no("顧客", date), new Object[]{id, list, source});
        map.put("顧客", ownmap);
    }

    public void addSMR(String date, String smr, String source) {
        TreeMap smrmap = (TreeMap) map.get("SMR");
        if (smrmap == null) {
            smrmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(smr);
        list.add(source);
        smrmap.put(date_no("SMR", date), list);
        map.put("SMR", smrmap);
    }

    public void addHistory(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("経歴");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(source);
        histmap.put(date_no("経歴", date), list);
        map.put("経歴", histmap);
    }

    public void addOrder(String date, String id, List od, String source) {
        TreeMap histmap = (TreeMap) map.get("受注");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(od);
        list.add(source);
        histmap.put(date_no("受注", date), list);
        map.put("受注", histmap);
    }

    public void addWork(String date, String id, List wr, String source) {
        TreeMap histmap = (TreeMap) map.get("作業");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(wr);
        list.add(source);
        histmap.put(date_no("作業", date), list);
        map.put("作業", histmap);
    }

    public void addParts(String date, String id, List pa, String source) {
        TreeMap histmap = (TreeMap) map.get("部品");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(pa);
        list.add(source);
        histmap.put(date_no("部品", date), list);
        map.put("部品", histmap);
    }

    public void addGPS(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("GPS");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(source);
        histmap.put(date_no("GPS", date), list);
        map.put("GPS", histmap);
    }

    public void addError(String date, String id, String count, String source) {
        TreeMap histmap = (TreeMap) map.get("エラー");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(count);
        list.add(source);
        histmap.put(date_no("エラー", date), list);
        map.put("エラー", histmap);
    }

    public void addCaution(String date, String id, List cau, String source) {
        TreeMap histmap = (TreeMap) map.get("警告");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.addAll(cau);
        list.add(source);
        histmap.put(date_no("警告", date), list);
        map.put("警告", histmap);
    }

    public void addEngine(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("エンジン");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(source);
        histmap.put(date_no("エンジン", date), list);
        map.put("エンジン", histmap);
    }

    public String getName() {
        return name;
    }

    public String getMachine() {
        return name.split("-")[0];
    }

    public String getType() {
        return name.split("-")[1];
    }

    public String getBorn() {
        return (String) map.get("生産");
    }

    public String getDeploy() {
        return (String) map.get("出荷");
    }

    public Map<String, List> getNew() {
        return (Map) map.get("新車");
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

    public Map<String, List> getSMR() {
        return (Map) map.get("SMR");
    }

    public Map<String, List> getOrder() {
        return (Map) map.get("受注");
    }

    //行
    public List<Object> getOrder(String date) {
        if (date.length() >= 6) {
            Map<String, List> order = getOrder();
            return order.get(date);
        } else {
            Map<String, List> order = getOrder();
            int i = Integer.valueOf(date);
            return order.values().stream().map(l -> l.get(i)).collect(Collectors.toList());
        }
    }

    public Map<String, List> getOwner() {
        return (Map) map.get("顧客");
    }

    public List getNewOwner() {
        Map<String, List> owner = getOwner();
        List first = owner.values().stream().findFirst().get();
        return first;
    }

    public Map<String, List> getHistory() {
        return (Map) map.get("経歴");
    }

    public Map<String, List> getCountry() {
        return (Map) map.get("国");
    }

    public Map<String, List> getWork() {
        return (Map) map.get("作業");
    }

    public Map<String, List> getParts() {
        return (Map) map.get("部品");
    }

    public Boolean getKomtrax() {
        return ((List) map.get("仕様")).get(0).toString().equals("1");
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
