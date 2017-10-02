/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.HashMap;
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
        map.put("新車", newmap.put(date, new Object[]{cid, price, source}));
    }

    public void addSpec(String category, String komtrax, String source) {
        map.put("仕様", new Object[]{category, komtrax, source});
    }

    public void addUsed(String date, String cid, List price, String source) {
        TreeMap usedmap = (TreeMap) map.get("中古");
        if (usedmap == null) {
            usedmap = new TreeMap();
        }
        map.put("中古", usedmap.put(date_no("中古", date), new Object[]{cid, price, source}));
    }
    
    public void addCountry(String date, String country) {
        TreeMap contmap = (TreeMap) map.get("国");
        if (contmap == null) {
            contmap = new TreeMap();
        }
        map.put("国", contmap.put(date_no("国", date), country));
    }

    public void addLast(String date, String source) {
        TreeMap lastmap = (TreeMap) map.get("最終更新日");
        if (lastmap == null) {
            lastmap = new TreeMap();
        }
        map.put("最終更新日", lastmap.put(date_no("最終更新日", date), source));
    }

    public void addOwner(String date, String gyosyu, String id, String name, String source) {
        TreeMap ownmap = (TreeMap) map.get("顧客");
        if (ownmap == null) {
            ownmap = new TreeMap();
        }
        map.put("顧客", ownmap.put(date_no("顧客", date), new String[]{gyosyu, id, name, source}));
    }

    public void addSMR(String date, String smr, String source) {
        TreeMap smrmap = (TreeMap) map.get("SMR");
        if (smrmap == null) {
            smrmap = new TreeMap();
        }
        map.put("SMR", smrmap.put(date_no("SMR", date), new String[]{smr, source}));
    }

    public void addHistory(String date, String id, List list,String source) {
        TreeMap histmap = (TreeMap) map.get("経歴");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("経歴", histmap.put(date_no("経歴", date), new Object[]{id, list, source}));
    }

    public void addService(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("サービス");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("サービス", histmap.put(date_no("サービス", date), new Object[]{id, list, source}));
    }

    public void addWork(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("作業");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("作業", histmap.put(date_no("作業", date), new Object[]{id, list, source}));
    }

    public void addParts(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("部品");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("部品", histmap.put(date_no("部品", date), new Object[]{id, list, source}));
    }

    public void addGPS(String date, String id, String source) {
        TreeMap histmap = (TreeMap) map.get("GPS");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("GPS", histmap.put(date_no("GPS", date), new String[]{id, source}));
    }
    
    public void addError(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("エラー");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("エラー", histmap.put(date_no("エラー", date), new Object[]{id, list, source}));
    }
    
    public void addCaution(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("警告");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("警告", histmap.put(date_no("警告", date), new Object[]{id, list, source}));
    }
    
    public void addEngine(String date, String id, List list, String source) {
        TreeMap histmap = (TreeMap) map.get("エンジン");
        if (histmap == null) {
            histmap = new TreeMap();
        }
        map.put("エンジン", histmap.put(date_no("エンジン", date), new Object[]{id, list, source}));
    }

    public String getName() {
        return name;
    }

    public String getName2() {
        return name.split("-")[0] + "-" + name.split("-")[2];
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

    public String getNew() {
        return (String) map.get("新車");
    }

    public Map getUsed() {
        return (Map) map.get("中古");
    }

    public String getDead() {
        return (String) map.get("廃車");
    }

    public String getLast() {
        return (String) map.get("最終更新日");
    }

    public Map getMirage() {
        return (Map) map.get("走行距離");
    }

    public Map getSMR() {
        return (Map) map.get("SMR");
    }

    public Map getOwner() {
        return (Map) map.get("顧客");
    }

    public Map getHistory() {
        return (Map) map.get("経歴");
    }

    public String getPrice() {
        return (String) map.get("価格");
    }

    public Map getCountry() {
        return (Map) map.get("国");
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

        sb.append("name:" + getName() + "\n");
        sb.append("category:" + "" + "\n");
        sb.append("komtrax:" + getKomtrax() + "\n");

        sb.append("history:");
        if (getBorn() != null) {
            sb.append("\n\t" + "製造:" + getBorn());
        }

        if (getNew() != null) {
            sb.append("\n\t" + "新車:" + getNew());
        }

        if (getUsed() != null) {
            sb.append("\n\t" + "中古:" + getUsed());
        }

        if (getDead() != null) {
            sb.append("\n\t" + "廃車:" + getDead());
        }

        if (getLast() != null) {
            sb.append("\n\t" + "最終更新日:" + getLast());
        }

        //Owner
        sb.append("\nQwner:\n");
        sb.append("\t" + getOwner() + "\n");

        sb.append("History:\n");
        if (getHistory() != null) {
            sb.append(getHistory() + "\n");
        }

        if (getCountry() != null) {
            sb.append(getCountry() + "\n");
        }

        return sb.toString();
    }
}
