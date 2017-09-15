/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject {

	public String name;
	public String category;
	public int komtrax = 0;
	public Map<String, Object> map = new LinkedHashMap();

	public SyaryoObject(String kisy, String type, String kiban, String category, String kmt, String kmtp) {
		this.name = kisy + "-" + type + "-" + kiban;

		if (kmtp.equals("Y")) {
			komtrax = 2;
		} else if (kmt.equals("Y")) {
			komtrax = 1;
		} else if (kmtp.equals("N") && kmt.equals("N")) {
			komtrax = -1;
		}

		this.category = category;
	}

	public void addBorn(String date) {
		map.put("生産", date);
	}

	public void addDeploy(String date) {
		map.put("出荷", date);
	}

	public void addNew(String date) {
		map.put("新車", date);
	}

	public void addPrice(String price) {
		map.put("価格", price);
	}

	public void addUsed(String date, String price) {
		Map usedMap = (Map) map.get("中古");
		if (usedMap == null) {
			usedMap = new TreeMap();
		}
		usedMap.put(date, price);
		map.put("中古", usedMap);
	}

	public void addDead(String date) {
		map.put("廃車", date);
	}

	public void addLast(String date) {
		map.put("最終更新日", date.substring(0, 8));
	}

	public void addOwner(String id, String name, String date) {
		Map ownerMap = (Map) map.get("顧客");
		if (ownerMap == null) {
			ownerMap = new TreeMap();
		}

		Map idname = (Map) ownerMap.get(date);
		if (idname == null) {
			idname = new HashMap();
		}
		idname.put(id.replace(".", "_"), name);
		ownerMap.put(date, idname);
		map.put("顧客", ownerMap);
	}

	public void addMirage(String date, String distance) {
		Map mirageMap = (Map) map.get("走行距離");
		if (mirageMap == null) {
			mirageMap = new TreeMap();
		}
		if (distance.equals("")) {
			distance = "0";
		}
		if (date.length() > 8) {
			date = date.substring(0, 8);
		}
		mirageMap.put(date, distance);
		map.put("走行距離", mirageMap);
	}

	public void addSMR(String date, String smr) {
		Map smrMap = (Map) map.get("SMR");
		if (smrMap == null) {
			smrMap = new TreeMap();
		}
		if (smr.equals("")) {
			smr = "0";
		}
		if (date.length() > 8) {
			date = date.substring(0, 8);
		}
		smrMap.put(date, smr);
		map.put("SMR", smrMap);
	}

	public void addHistory(String id, String date) {
		Map historyMap = (Map) map.get("経歴");
		if (historyMap == null) {
			historyMap = new TreeMap();
		} else {
			int i = 1;
			String tmp = date;
			while (historyMap.get(tmp) != null) {
				tmp = date;
				tmp = tmp + "-" + i;
				i++;
			}
			date = tmp;

			//System.out.println(id+" : "+date);
		}

		historyMap.put(date, id);

		map.put("経歴", historyMap);
	}

	public void addCountry(String country, String date) {
		Map countryMap = (Map) map.get("国");
		if (countryMap == null) {
			countryMap = new HashMap();
		}
		countryMap.put(date, country);
		map.put("国", countryMap);
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

	public Integer getKomtrax() {
		return komtrax;
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
			sb.append("\n\t" + "最終更新日:"+getLast());
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
