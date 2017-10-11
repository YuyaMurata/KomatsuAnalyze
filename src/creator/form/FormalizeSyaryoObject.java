/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class FormalizeSyaryoObject {

	public Map<String, SyaryoObject> formalize(Map<String, SyaryoObject> syaryoMap) {
		Map map = new TreeMap();

		for (String name : syaryoMap.keySet()) {
			//中古
			formUsed(syaryoMap.get(name).getUsed());

			//顧客
			formOwner(syaryoMap.get(name).getOwner());
			//経歴
			formHistory(syaryoMap.get(name).getHistory());
			//Last
			formLastUpdate(syaryoMap.get(name).getLast());
			//SMR
			formSMR(syaryoMap.get(name).getSMR());

			//Country
			formCountry(syaryoMap.get(name).getCountry());
			
			//
			
			map.put(name, syaryoMap.get(name));
		}

		return map;
	}

	private void formUsed(Map<String, List> used) {
		Map update = new TreeMap();

		if (used == null) {
			return;
		}

		for (String date : used.keySet()) {
			List obj = used.get(date);

			//System.out.println(date+":"+obj);
			if (date.contains("#")) {
				date = date.split("#")[0];
			}
			update.put(date, obj);
		}

		used.clear();
		used.putAll(update);
	}

	private void formOwner(Map<String, List> owner) {
		Map update = new TreeMap();
		Object[] temp = new Object[2];

		if (owner == null) {
			return;
		}

		for (String date : owner.keySet()) {
			List obj = owner.get(date);

			//System.out.println(date+":"+obj);
			if (!obj.get(0).equals(temp[0]) && !((List) obj.get(1)).get(1).equals(temp[1])) {
				if (!obj.get(0).toString().contains("##")) {
					if (date.contains("#")) {
						date = date.split("#")[0];
					}
					update.put(date, obj);
				}
				temp[0] = obj.get(0);
				temp[1] = ((List) obj.get(1)).get(1);
			}
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

	private void formLastUpdate(Map<String, String> last) {
		Map<String, String> update = new TreeMap();

		if (last == null) {
			return;
		}

		for (String date : last.keySet()) {
			String key = date.split("#")[0] + "-" + last.get(date);
			if (update.get(key) != null) {
				update.put(key, String.valueOf(Integer.valueOf(update.get(key)) + 1));
			} else {
				update.put(key, "1");
			}
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

			if (!date.contains("/")) {
				date = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8) + " 0:00:00";
			}

			if (obj.get(0).toString().contains("?")) {
				continue;
				//obj.remove(0);
				//obj.add(0, "-1");
			}

			if (!obj.get(0).equals(temp)) {
				update.put(date, obj);
				temp = obj.get(0);
			}

		}

		smr.clear();
		smr.putAll(update);
	}

	private void formCountry(Map<String, String> country) {
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

	public static void main(String[] args) {
		Map map = new JsonToSyaryoObj().reader("syaryo_obj_WA470.json");
		Map syaryoMap = new FormalizeSyaryoObject().formalize(map);

		new SyaryoObjToJson().write("syaryo_obj_WA470_form.json", syaryoMap);
	}
}
