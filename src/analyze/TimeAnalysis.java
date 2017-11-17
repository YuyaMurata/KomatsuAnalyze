/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class TimeAnalysis {

	public static void main(String[] args) {
		String filename = "json\\syaryo_obj_WA470_form.json";
		Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader(filename);
		orderDate(filename, syaryoMap);
	}

	public Map create(List<String> newDate, List<String> timeToOrder) {
		Map<Integer, Integer> map = new TreeMap();

		String dateMax = String.valueOf(timeToOrder.stream().map(s -> Integer.valueOf(s)).max(Comparator.naturalOrder()).get());
		String dateMin = String.valueOf(newDate.stream().map(s -> Integer.valueOf(s)).min(Comparator.naturalOrder()).get());

		System.out.println(dateMin + " ~ " + dateMax);
		Integer ny = 12 * Integer.valueOf(dateMin.substring(0, 4));
		Integer nm = Integer.valueOf(dateMin.substring(4, 6));
		Integer y = 12 * Integer.valueOf(dateMax.substring(0, 4));
		Integer m = Integer.valueOf(dateMax.substring(4, 6));
		int time = (y + m) - (ny + nm);
		System.out.println(time);

		for (int i = 0; i < time + 1; i++) {
			map.put(i, 0);
		}

		for (int i = 0; i < timeToOrder.size(); i++) {
			ny = 12 * Integer.valueOf(newDate.get(i).substring(0, 4));
			nm = Integer.valueOf(newDate.get(i).substring(4, 6));
			y = 12 * Integer.valueOf(timeToOrder.get(i).substring(0, 4));
			m = Integer.valueOf(timeToOrder.get(i).substring(4, 6));
			time = (y + m) - (ny + nm);

			if (time < 0) {
				time = 0;
			}

			map.put(time, map.get(time) + 1);
		}

		return map;
	}

	private static Boolean errorCheck(String date) {
		if (date.equals("")) {
			return true;
		} else if (Integer.valueOf(date.replace(" ", "")) <= 1950000) {
			return true;
		}
		return false;
	}

	public static void orderDate(String filename, Map<String, SyaryoObject> syaryoMap) {
		Map<String, Map<String, List>> allData = new HashMap<>();
		int n = 0;
		List<String> type = syaryoMap.values().stream()
				.map(s -> s.getType()).distinct().collect(Collectors.toList());

		for (String typ : type) {
			Map map = new HashMap();
			List list1 = new ArrayList();
			List list2 = new ArrayList();

			List<SyaryoObject> syaryo = syaryoMap.values().stream()
					.filter(s -> s.getType().equals(typ))
					.filter(s -> s.getOrder() != null)
					.collect(Collectors.toList());

			for (SyaryoObject s : syaryo) {
				if (s.getNew() == null) {
					System.out.println(s.getName());
					n++;
					continue;
				} else {
					if (s.getNew().containsKey("19000101")) {
						System.out.println(s.getName());
						n++;
						continue;
					}

				}

				for (String date : s.getOrder().keySet()) {
					list1.add(s.getNew().keySet().stream().findFirst().get());
					list2.add(date.split("#")[0]);
				}
			}

			map.put("NewDate", list1);
			map.put("Date", list2);

			System.out.println("除外(新車納入暦が無いため)　n=" + n);

			allData.put(typ, map);
		}

		for (String typ : type) {
			Map map = new TimeAnalysis().create(
					allData.get(typ).get("NewDate"),
					allData.get(typ).get("Date"));

			PrintWriter pw;
			try {
				pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename.split("_")[2] + "_order_time_t" + typ + ".csv"))));
				for (Object key : map.keySet()) {
					pw.println(key + "," + map.get(key));
				}
				pw.close();
			} catch (IOException ex) {
			}
		}
	}
}
