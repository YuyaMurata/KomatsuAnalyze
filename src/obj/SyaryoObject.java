/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    private transient int deteilno = 0;

    public SyaryoObject(String name) {
        this.name = name;
    }

    //Date Util
    private String dateFormat(String d) {
        SimpleDateFormat sdf1;
        if (d.contains(":")) {
            sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        } else {
            sdf1 = new SimpleDateFormat("yyyyMMdd");
        }

        Date date = null;
        try {
            date = sdf1.parse(d);
        } catch (ParseException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
        return sdf2.format(date);
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

    /**
     * Add Data
     */
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
                        addSpec(s[0], s[1], "eqp+syaryo_?");
                    } else if (field.equals("詳細")) {
                        list.add(s[1]);
                        list.add(s[2]);
                        addDetail(s[0], list, "eqp_syaryo_?");
                    } else if (field.equals("生産")) {
                        addBorn(s[0]);
                    } else if (field.equals("出荷")) {
                        addDeploy(s[0]);
                    } else if (field.equals("廃車")) {
                        addDead(s[2]);
                    } else if (field.equals("新車")) {
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addNew(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.equals("中古車")) {
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
                        for (int j = 4; j < s.length; j++) {
                            list.add(s[j]);
                        }
                        addCaution(s[2], s[3], list, (s[0] + "_" + s[1]));
                    } else if (field.toString().contains("ERROR")) {
                        addError(s[2], s[3], s[4], s[5], (s[0] + "_" + s[1]));
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

    //Service
    public void addBorn(String date) {
        map.put("生産", dateFormat(date));
    }

    public void addDeploy(String date) {
        map.put("出荷", dateFormat(date));
    }

    public void addDead(String date) {
        map.put("廃車", dateFormat(date));
    }

    public void addNew(String date, String cid, List price, String source) {
        TreeMap newmap = (TreeMap) map.get("新車");
        if (newmap == null) {
            newmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(cid);
        list.addAll(price);
        list.add(source);
        newmap.put(date_no("新車", dateFormat(date)), list);
        map.put("新車", newmap);
    }

    public void addSpec(String category, String komtrax, String source) {
        List<Object> list = new ArrayList<>();
        list.add(category);
        list.add(komtrax);
        list.add(source);
        TreeMap specmap = new TreeMap();
        specmap.put(0, list);
        map.put("仕様", specmap);
    }

    public void addDetail(String unitID, List names, String source) {
        TreeMap detailmap = (TreeMap) map.get("詳細");
        if (detailmap == null) {
            detailmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(unitID);
        list.addAll(names);
        list.add(source);
        detailmap.put(deteilno++, list);
        map.put("詳細", detailmap);
    }

    public void addUsed(String date, String cid, List price, String source) {
        TreeMap usedmap = (TreeMap) map.get("中古車");
        if (usedmap == null) {
            usedmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(cid);
        list.addAll(price);
        list.add(source);
        usedmap.put(date_no("中古車", dateFormat(date)), list);
        map.put("中古車", usedmap);
    }

    public void addCountry(String date, String country) {
        TreeMap contmap = (TreeMap) map.get("国");
        if (contmap == null) {
            contmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(country);
        contmap.put(date_no("国", dateFormat(date)), list);
        map.put("国", contmap);
    }

    public void addLast(String date, String source) {
        TreeMap lastmap = (TreeMap) map.get("最終更新日");
        if (lastmap == null) {
            lastmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(source);
        lastmap.put(date_no("最終更新日", dateFormat(date)), list);
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
        ownmap.put(date_no("顧客", dateFormat(date)), list);
        map.put("顧客", ownmap);
    }

    //一部Komtrax
    public void addSMR(String date, String smr, String source) {
        TreeMap smrmap = (TreeMap) map.get("SMR");
        if (smrmap == null) {
            smrmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(smr);
        list.add(source);
        smrmap.put(date_no("SMR", dateFormat(date)), list);
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
        histmap.put(date_no("経歴", dateFormat(date)), list);
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
        histmap.put(date_no("受注", dateFormat(date)), list);
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
        histmap.put(date_no("作業", dateFormat(date)), list);
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
        histmap.put(date_no("部品", dateFormat(date)), list);
        map.put("部品", histmap);
    }

    //Komtrax
    public void addGPS(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("GPS");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(source);
        histmap.put(date_no("GPS", dateFormat(date)), list);
        map.put("GPS", histmap);
    }

    public void addError(String date, String id, String kind, String count, String source) {
        TreeMap histmap = (TreeMap) map.get("エラー");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        List<Object> list = new ArrayList<>();
        list.add(id);
        list.add(kind);
        list.add(count);
        list.add(source);
        histmap.put(date_no("エラー", dateFormat(date)), list);
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
        histmap.put(date_no("警告", dateFormat(date)), list);
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
        histmap.put(date_no("エンジン", dateFormat(date)), list);
        map.put("エンジン", histmap);
    }

    /**
     * Get Data
     */
    //Service
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

    public Map<String, List> getUsed() {
        return (Map) map.get("中古車");
    }

    public String getDead() {
        return (String) map.get("廃車");
    }

    public Map getLast() {
        return (Map) map.get("最終更新日");
    }

    public Map<String, List> getOrder() {
        return (Map) map.get("受注");
    }

    public Map<String, List> getOwner() {
        return (Map) map.get("顧客");
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

    //Komtrax
    public Map<String, List> getSMR() {
        return (Map) map.get("SMR");
    }

    public Map<String, List> getGPS() {
        return (Map) map.get("GPS");
    }

    public Map<String, List> getEngine() {
        return (Map) map.get("エンジン");
    }

    public Map<String, List> getCaution() {
        return (Map) map.get("警告");
    }

    public Map<String, List> getError() {
        return (Map) map.get("エラー");
    }

    /**
     * Get Util
     */
    public Integer getSize(String key) {
        if (key.contains("機種") || key.contains("仕様") || key.contains("生産") || key.contains("出荷") || key.contains("廃車")) {
            return 1;
        }
        if (map.get(key) == null) {
            return 0;
        }
        return ((Map<String, List<String>>) map.get(key)).size();
    }

    public List<String> getCol(String key, Integer index) {
        List list = new ArrayList();
        if (key.contains("機種")) {
            list.add(getName());
            return list;
        } else if (map.get(key) == null) {
            list.add("NA");
            return list;
        }

        switch (index) {
            case -1:
                return ((Map<String, List>) map.get(key)).keySet().stream().collect(Collectors.toList());
            case -2:
                list.add(map.get(key));
                return list;
            case -3:
                for (String date : ((Map<String, List>) map.get(key)).keySet().stream().collect(Collectors.toList())) {
                    list.add(getAge(date));
                }
                return list;
            default:
                break;
        }
        return ((Map<String, List>) map.get(key)).values().stream().map(l -> l.get(index).toString()).collect(Collectors.toList());
    }

    public List<String> getRow(String key, String date) {
        List list = new ArrayList();
        if (key.contains("生産") || key.contains("出荷") || key.contains("廃車")) {
            list.add(map.get(key));
        } else if (key.contains("経過日")) {
            list.add(getAge(date.split("#")[0]));
        } else {
            if (map.get(key) == null) {
                for(Element e : SyaryoElements.map.get(key))
                    list.add("NA");
            } else if (((Map<String, List<String>>) map.get(key)).get(date) == null) {
                list.addAll(((Map<String, List<String>>) map.get(key)).get("0"));
            } else {
                list.addAll(((Map<String, List<String>>) map.get(key)).get(date));
            }
        }

        return list;
    }

    private String getAge(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date birth = sdf.parse(getNew().keySet().stream().findFirst().get());
            Date last = sdf.parse(date);
            Long age = (last.getTime() - birth.getTime()) / (1000 * 60 * 60 * 24);

            //System.out.println(sdf.format(birth) + " - " + sdf.format(last) +" = "+age);
            return age.toString();
        } catch (ParseException ex) {
            return "NA";
        }
    }

    //Remove
    public void remove(String key) {
        map.remove(key);
    }

    //Dump
    public String dump() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(":");
        sb.append(map);
        return sb.toString();
    }
}
