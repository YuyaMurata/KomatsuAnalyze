/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import static check.SyaryoCheck.customerCheck;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoElements;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class KomtraxCheck {

	private static final String kisy = "WA470";
	private static Map<String, SyaryoObject> syaryoMap;

	public static void main(String[] args) {
		JsonToSyaryoObj obj = new JsonToSyaryoObj();
		syaryoMap = obj.reader("json\\syaryo_obj_" + kisy + "_form2.json");
		System.out.println(syaryoMap.size());
		
		//formSMR();
		
		//Syaryo SMR
		for(SyaryoObject syaryo : syaryoMap.values()){
			Map map = new HashMap();
			for(String date : syaryo.getSMR().keySet()){
				String d = date.split(" ")[0];
				
				if(!syaryo.getSMR().get(date).get(SyaryoElements.SMR.Company.getNo()).toString().contains("komtrax"))
					continue;
				
				if(map.get(d) != null)
					System.out.println(syaryo.name+":"+d+" "+map.get(d)+" - "+syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
				map.put(d, syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
			}
		}
		
		//new SyaryoObjToJson().write("json\\syaryo_obj_" + kisy + "_form2.json", syaryoMap);
	}
	
	private static void formSMR(){
		for(SyaryoObject syaryo : syaryoMap.values()){
			Map<String, List> smrmap = new TreeMap();
			Map map = new HashMap();
			for(String date : syaryo.getSMR().keySet()){
				String d = date.split(" ")[0];
				
				if(!syaryo.getSMR().get(date).get(SyaryoElements.SMR.Company.getNo()).toString().contains("komtrax"))
					continue;
				
				if(map.get(d) != null){
					System.out.println(syaryo.name+":"+d+" "+map.get(d)+" - "+syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
					Integer smr = 0;
					if(smrmap.get(d) != null)
						smr = Double.valueOf(smrmap.get(d).get(SyaryoElements.SMR._SMR.getNo()).toString()).intValue();
					
					smr = Math.max(Math.max(smr, Double.valueOf(map.get(d).toString()).intValue()),Double.valueOf(syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()).toString()).intValue());
					List list = syaryo.getSMR().get(date);
					list.set(SyaryoElements.SMR._SMR.getNo(), smr);
					smrmap.put(d, list);
				}else
					smrmap.put(d, syaryo.getSMR().get(date));
				
				map.put(d, syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
			}
			syaryo.getSMR().clear();
			syaryo.getSMR().putAll(smrmap);
		}
	}
}
