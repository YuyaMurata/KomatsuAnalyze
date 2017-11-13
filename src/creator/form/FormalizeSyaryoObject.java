/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoElements;
import obj.SyaryoObject;

/**
 * 車両オブジェクトから重複部分を排除して整形
 *
 * @author ZZ17390
 */
public class FormalizeSyaryoObject {

	public static void main(String[] args) {
		String kisy = "WA470";
		String filename = "json\\syaryo_obj_" + kisy + ".json";
		String output = "json\\syaryo_obj_" + kisy + "_form.json";

		//1次処理
		Map map = new JsonToSyaryoObj().reader(filename);
		Map syaryoMap = new FormalizeSyaryoObject().formalize(map);
		new SyaryoObjToJson().write(output, syaryoMap);
		System.out.println("Finish forming json data!");
		map = null;
		syaryoMap = null;

		//2次処理
		map = new JsonToSyaryoObj().reader(output);
		syaryoMap = new FormalizeSyaryoObject().split(map);
		new SyaryoObjToJson().write(output, syaryoMap);
		System.out.println("Finish deleting old type data!");

		//3次処理　結合・圧縮
		//Map syaryoMap = new FormalizeSyaryoObject().join(new String[]{"PC200", "PC210", "HB205", "HB215"});
		//new SyaryoObjToJson().write2("syaryo_obj_"+kisy+"_form.json", syaryoMap);
		//System.out.println("Finish joining & compressing json data!");
	}

	//古い型を除去
	public Map<String, SyaryoObject> split(Map<String, SyaryoObject> syaryoMap) {
		//Type
		Map map = new TreeMap();
		for (SyaryoObject syaryo : syaryoMap.values()) {
			if (Integer.valueOf(syaryo.getType()) > 6) {
				map.put(syaryo.getName(), syaryo);
			}
		}

		return map;
	}

	public Map<String, SyaryoObject> join(String[] kisy) {
		//Type
		Map map = new TreeMap();
		for (String s : kisy) {
			Map map1 = new JsonToSyaryoObj().reader("syaryo_obj_" + s + "_form.json");
			map.putAll(map1);
		}

		return map;
	}

	public Map<String, SyaryoObject> formalize(Map<String, SyaryoObject> syaryoMap) {
		Map map = new TreeMap();

		for (String name : syaryoMap.keySet()) {
			//新車
			formNew(syaryoMap.get(name).getNew());

			//中古
			formUsed(syaryoMap.get(name).getUsed());

			//顧客
			formOwner(syaryoMap.get(name).getOwner());

			//経歴
			//formHistory(syaryoMap.get(name).getHistory());
			syaryoMap.get(name).remove("経歴");

			//受注
			formOrder(syaryoMap.get(name).getOrder());

			//Last
			//formLastUpdate(syaryoMap.get(name).getLast());
			syaryoMap.get(name).remove("最終更新日");

			//SMR
			formSMR(syaryoMap.get(name).getSMR());

			//GPS
			formGPS(syaryoMap.get(name).getGPS(), syaryoMap.get(name).getSMR());
            
            //Caution
            syaryoMap.get(name).remove("警告");
            
            //Error
            formError(syaryoMap.get(name).getError());

			//Country
			formCountry(syaryoMap.get(name).getCountry());

			//2009年以降のサービス経歴を作業，部品から削除
			formWorkParts(syaryoMap.get(name).getWork(), syaryoMap.get(name).getParts());

			map.put(name, syaryoMap.get(name));
		}

		return map;
	}

	private void formNew(Map<String, List> news) {
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

			if (obj.get(SyaryoElements.New.Source.getNo()).contains("sell")) {
                if(flg) break;
                if(!obj.get(SyaryoElements.New.Source.getNo()).contains("old"))
                    flg = true;
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

	private void formUsed(Map<String, List> used) {
		Map<String, List> update = new TreeMap();

		if (used == null) {
			return;
		}

		for (String date : used.keySet()) {
			List obj = used.get(date);
			if (!obj.get(0).equals("-1") || !obj.get(1).equals("-1") || !obj.get(2).equals("-1")) {
				obj.set(0, String.valueOf(Float.valueOf(obj.get(0).toString()).intValue()));
				if (obj.get(1).equals("")) {
					obj.set(1, "-1");
				}
				obj.set(1, String.valueOf(Float.valueOf(obj.get(1).toString()).intValue()));
				obj.set(2, String.valueOf(Float.valueOf(obj.get(2).toString()).intValue()));
			}

			//System.out.println(date+":"+obj);
			if (date.contains("#")) {
				date = date.split("#")[0];
				if (update.get(date) != null) {
					if (!update.get(date).get(0).equals("-1") || !update.get(date).get(1).equals("-1") || !update.get(date).get(2).equals("-1")) {
						obj = update.get(date);
					}
				}
			}
			update.put(date, obj);
		}

		used.clear();
		used.putAll(update);
	}

	private void formOwner(Map<String, List> owner) {
		Map update = new TreeMap();
		String[] temp = new String[4];
		temp[1] = "?";

		if (owner == null) {
			return;
		}

		for (String date : owner.keySet()) {
			List obj = owner.get(date);

			//System.out.println(date+":"+obj);
			if (obj.get(0).toString().contains("##") || obj.get(0).toString().equals("")) {
				continue;
			}

			if (temp[0] != null) {
				if (obj.get(0).equals(temp[0]) || obj.get(2).toString().contains(temp[2])) {
					if(!obj.get(1).toString().contains("?") && !obj.get(1).toString().equals("-1") ){
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

		owner.clear();
		owner.putAll(update);
	}

	private void formHistory(Map<String, List> history) {
		Map update = new TreeMap();

		if (history == null) {
			return;
		}

		List dupList = history.entrySet().stream()
			.map(entity -> entity.getValue().get(0) + entity.getValue().get(1).toString().split("_")[1])
			.distinct()
			.collect(Collectors.toList());

		for (String date : history.keySet()) {
			if (history.get(date).get(1).toString().contains("service") || history.get(date).get(1).toString().contains("order")) {
				if (dupList.contains(history.get(date).get(0) + history.get(date).get(1).toString().split("_")[1])) {
					update.put(date, history.get(date));
					dupList.remove(history.get(date).get(0));
				}
			} else {
				update.put(date, history.get(date));
			}
		}

		history.clear();
		history.putAll(update);
	}
    
    private Boolean serviceYearCheck(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        int diff = 0;
        try {
            Date baseDate = DateFormat.getInstance().parse("2009/01/01");
            diff = sdf.parse(date).compareTo(baseDate);
        } catch (ParseException ex) {
        }
        
        return  diff > 0;
    }

	private void formOrder(Map<String, List> order) {
		Map update = new TreeMap();

		if (order == null) {
			return;
		}

		for (String date : order.keySet()) {
			if (serviceYearCheck(date.split("#")[0])) {
				if (order.get(date).get(21).toString().contains("service")) {
					continue;
				}
			}

			List list = order.get(date);
			if (!list.get(15).equals("-1")) {
				list.set(15, Float.valueOf(list.get(15).toString()).toString());
			}
			if (!list.get(16).equals("-1")) {
				list.set(16, Float.valueOf(list.get(16).toString()).toString());
			}
			list.set(17, String.valueOf(Float.valueOf(list.get(17).toString()).intValue()));
			list.set(18, String.valueOf(Float.valueOf(list.get(18).toString()).intValue()));
			list.set(19, String.valueOf(Float.valueOf(list.get(19).toString()).intValue()));
			update.put(date, list);
		}

		order.clear();
		order.putAll(update);
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

	private void formSMR(Map<String, List> smr) {
		Map update = new TreeMap();
		Object temp = null;

		if (smr == null) {
			return;
		}

		for (String date : smr.keySet()) {
			List obj = smr.get(date);

			//System.out.println(date+":"+obj);
			date = date.split("#")[0];

			if (obj.get(0).toString().contains("?")) {
				continue;
			}

			//komtraxSMR 分→時間 変換
			if (obj.get(1).toString().contains("komtrax")) {
				obj.set(0, Integer.valueOf(obj.get(0).toString()) / 60);
			}

			if (!obj.get(0).equals(temp)) {
				update.put(date, obj);
				temp = obj.get(0);
			}

		}

		smr.clear();
		smr.putAll(update);
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
				if (serviceYearCheck(date.split("#")[0])) {
					if (work.get(date).get(15).toString().contains("service")) {
						continue;
					}
				}
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
				if (serviceYearCheck(date.split("#")[0])) {
					if (parts.get(date).get(7).toString().contains("service")) {
						continue;
					}
				}
				List list = parts.get(date);
				list.set(6, String.valueOf(Float.valueOf(list.get(6).toString()).intValue()));
				update2.put(date, list);
			}
			parts.clear();
			parts.putAll(update2);
		}

	}
    
    private void formError(Map<String, List> error){
        if(error == null) return;
        Map<String, Integer> errorCnt = new HashMap();
        for(String date : error.keySet()){
            String code = (String) error.get(date).get(SyaryoElements.Error.Code.getNo());
            Integer cnt = Integer.valueOf((String) error.get(date).get(SyaryoElements.Error.Count.getNo()));
            
            if(errorCnt.get(code) == null) errorCnt.put(code, 0);
            error.get(date).set(SyaryoElements.Error.Count.getNo(),String.valueOf(cnt - errorCnt.get(code)));
            
            errorCnt.put(code, cnt);
        }
    }
}
