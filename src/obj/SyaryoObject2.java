/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.io.IOException;
import java.text.DecimalFormat;
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
import json.JSONToBSON;

/**
 *
 * @author ZZ17390
 */
public class SyaryoObject2 {
    public String name;
	public Map map = new LinkedHashMap();
	public byte[] mapData;	
    private transient DecimalFormat dformat = new DecimalFormat("000");

	public SyaryoObject2(String name) {
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
			return d;
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		return sdf2.format(date);
	}

	public String date_no(Object key, String date) {
		Map oldMap = (Map) map.get(key);
        
        String df = dateFormat(date);
        
		if (oldMap == null) {
			return df;
		} else {
			if (oldMap.get(df) == null) {
				return df;
			} else {
				int i = 1;
				while (oldMap.get(df + "#" + dformat.format(i)) != null) {
					i++;
				}
				return df + "#" + dformat.format(i);
			}
		}
	}

	/**
	 * Add Data
	 */
	public Integer add(Map template) {
		int n = 0;
		for (Object field : template.keySet()) {
			if (template.get(field) == null) {
				continue;
			}

			String[] lines = template.get(field).toString().split("\n");
			Boolean header_flg = true;
            Integer numHeaders = 0;
            Integer key = 0;
            
			for (String line : lines) {
				String[] s = line.trim().split(",");
                
                if (header_flg) {
                    numHeaders = s.length;
                    if(s.length > 2)
                        if(s[2].contains("日"))
                            key = 2;
                        
					header_flg = false;
					continue;
				}

				try {
					if(numHeaders != s.length)
                        throw new ArrayIndexOutOfBoundsException();
                    
                    if(map.get(field) == null)
                        map.put(field, new TreeMap());
                    
                    TreeMap newMap = (TreeMap) map.get(field);
                    
                    String date = date_no(field, s[key]);
                    newMap.put(date, new ArrayList());
                    
                    for(String str : s)
                        ((List)newMap.get(date)).add(str);
                    
				} catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println(numHeaders+", "+s.length+", "+line);
					System.out.println(field + ":" + Arrays.asList(s));
					n++;
				}
			}
		}
        
        return n;
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

	public Map<String, List> getDead() {
		return (Map<String, List>) map.get("廃車");
	}
    
    public Map<String, List> getCarePrice() {
		return (Map<String, List>) map.get("コマツケア前受け金");
	}
    
    public Map<String, List> getCare() {
		return (Map<String, List>) map.get("コマツケア");
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

	public Map<String, List> getSpec() {
		return (Map) map.get("仕様");
	}

	public Boolean getKomtrax() {
        if(getSpec() == null)
            return false;
        return getSpec().values().stream().findFirst().get().get(SyaryoElements.Spec.Komtrax.getNo()).toString().equals("1");
	}

	//Komtrax
	public Map<String, List> getSMR() {
		return (Map) map.get("KMSMR");
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
		return (Map) map.get("KMERROR");
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
				for (Element e : SyaryoElements.map.get(key)) {
					list.add("NA");
				}
			} else if (((Map<String, List<String>>) map.get(key)).get(date) == null) {
				list.addAll(((Map<String, List<String>>) map.get(key)).get("0"));
			} else {
				list.addAll(((Map<String, List<String>>) map.get(key)).get(date));
			}
		}

		return list;
	}

	public String getFirstDate() {
		return getNew().keySet().stream().findFirst().get();
	}

	public String getAge(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
			Date birth = sdf.parse(getFirstDate().replace("/", ""));
			Date last = sdf2.parse(date.replace("/", ""));
			Long age = (last.getTime() - birth.getTime()) / (1000 * 60 * 60 * 24);

			System.out.println(sdf.format(birth) + " - " + sdf.format(last) +" = "+age);
			return age.toString();
		} catch (ParseException ex) {
			return "NA";
		}
	}

	public Integer getNumUsed() {
		if (getUsed() == null) {
			return 0;
		} else {
			return getUsed().size();
		}
	}

	public String getSMR(String date) {
		String d = date.split("#")[0];

		TreeMap<String, List<String>> map = new TreeMap(getSMR());

		String smr = "0";
		if (map.floorEntry(d) != null) {
			smr = map.floorEntry(d).getValue().get(SyaryoElements.SMR._SMR.getNo());
		}

		return smr;
	}
    
    //Get
    public Map<String, List> get(String key) {
		return (Map) map.get(key);
	}
    
    //Get
    public Map<String, Map> getAll() {
		return map;
	}
    
    public void compress(Boolean flg){
        try{
            if(flg)
                mapData = JSONToBSON.toBson(map);
            if(map != null)
                map.clear();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public void decompress(){
        try{
            byte[] b = mapData;
            this.map = JSONToBSON.toMap(b);
            mapData = null;
        }catch(IOException e){
            e.printStackTrace();
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
