/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import file.ListToCSV;
import file.SyaryoToCompress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;
import program.r.R;
import viewer.graph.TimeSpreadChart;

/**
 *
 * @author ZZ17390
 */
public class SMRDataFormalize {

	private static String KISY = "PC200";
	private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
	//private static List<String> testList = ListToCSV.toList("user\\SMR異常系列.csv");

	public static void main(String[] args) {
		LOADER.setFile(KISY + "_loadmap");
		Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

		//for (String n : testList)
		//    oneSampleForm(syaryoMap.get(n));
		//oneSampleForm(syaryoMap.get("PC200-10-450730"));
		//Compare SMR ACT_SMR
		for (SyaryoObject s : LOADER.getSyaryoMap().values()) {
			System.out.println(s.name);
			Map actsmr = actToSMR(s);
			if(actsmr != null)
                            s.put("KOMTRAX_ACT_DATA", actsmr);
		}
		
		LOADER.close();
		new SyaryoToCompress().write(LOADER.getFilePath().replace("loadmap", "form_loadmap"), syaryoMap);
		
		//R.close();
	}

	private static Map<String, List<String>> actToSMR(SyaryoObject s) {
		if (s.get("KOMTRAX_ACT_DATA") == null) {
			return null;
		}

		Map<String, List<String>> act = s.get("KOMTRAX_ACT_DATA");
		//check(s.name, act);
		
		return transACTSMRData(act, 0, 1);
	}

	private static void check(String name, Map<String, List<String>> act) {
		Map<String, Integer> check = new HashMap<>();

		act.keySet().stream().forEach(k -> {
			if (check.get(k.split("#")[0]) != null) {
				System.out.println(name);
				System.out.println("before  -- " + k.split("#")[0] + " : " + calcActSMR(act.get(k.split("#")[0])));
				System.out.println("after -- " + k + " : " + calcActSMR(act.get(k)));
			}

			check.put(k, 1);
		});
	}

	private static Double calcActSMR(List<String> actList) {
		Integer value = Integer.valueOf(actList.get(0));
		Integer unit = Integer.valueOf(actList.get(1));

		return value / unit / 60d;
	}

	private static void oneSampleForm(SyaryoObject syaryo) {
		Map<String, Integer> sv_smr = transSMRData(syaryo.get("SMR"), LOADER.index("SMR", "VALUE"));
		Map<String, Integer> km_smr = transSMRData(syaryo.get("KOMTRAX_SMR"), LOADER.index("KOMTRAX_SMR", "VALUE"));
		Map<String, Integer> km_acsmr = transACTSMRData(syaryo.get("KOMTRAX_ACT_DATA"), LOADER.index("KOMTRAX_ACT_DATA", "VALUE"), LOADER.index("KOMTRAX_ACT_DATA", "DAILY_UNIT"));

		//sv_smr = formProcess(sv_smr);
		//km_smr = formProcess(km_smr);
		//Map<String, Integer> smr = mergeSMR(sv_smr, km_smr);
		Map truth = mergeGraphData(km_smr, km_acsmr);
		testGraph(syaryo, truth);

		//Map dm = mergeData(smr, m);
		//testGraph(syaryo, dm);
	}

	private static Map transSMRData(Map<String, List<String>> smr, int idx) {
		Map map = new TreeMap();

		if (smr == null) {
			return map;
		}

		smr.entrySet().forEach(s -> {
			map.put(s.getKey(), Integer.valueOf(s.getValue().get(idx)));
		});

		return map;
	}

	private static Map transACTSMRData(Map<String, List<String>> smr, int idx, int unit) {
		Map<String, Double> map = new TreeMap();

		if (smr == null) {
			return map;
		}

		smr.entrySet().forEach(s -> {
			map.put(s.getKey(), calcActSMR(s.getValue()));
		});

		//累積値に変換
		Double acm = 0d;
		Map<String, List<String>>actmap = new TreeMap();
		for (String d : map.keySet()) {
			acm += map.get(d);
			List v = new ArrayList();
			v.add(acm.toString());
			actmap.put(d, v);
		}

		return actmap;
	}

	private static Map<String, Integer> formProcess(Map<String, Integer> smr) {
		while (true) {
			int temp = smr.size();

			Map ma = movingAverage(smr, 3, 3);
			Map dif = diff(smr, ma);
			Map outers = detectOuters(dif);

			Map m = rejectOuters(smr, outers);
			if (temp != m.size()) {
				smr = m;
			} else {
				return m;
			}
		}
	}

	private static Map movingAverage(Map<String, Integer> smr, int span, int min) {
		if (smr.size() < min) {
			return null;
		}

		Map ma = new TreeMap();
		Queue<Integer> q = new LinkedList();
		for (String date : smr.keySet()) {
			q.offer(smr.get(date));

			if (q.size() % span == 0) {
				Double value = q.stream().mapToInt(v -> v).average().getAsDouble();
				ma.put(date, value.intValue());
				q.poll();
			}
		}

		System.out.println(ma);
		return ma;
	}

	private static Map<String, Integer> diff(Map<String, Integer> map1, Map<String, Integer> map2) {
		Map<String, Integer> map = new TreeMap<>();
		TreeMap<String, Integer> m1 = new TreeMap<>(map1);
		TreeMap<String, Integer> m2 = new TreeMap<>(map1);
		if (map2 != null) {
			m2.clear();
			m2.putAll(map2);
		}

		List<String> date = new ArrayList<>();
		date.addAll(m1.keySet());
		date.addAll(m2.keySet());

		date.stream().forEach(k -> {
			Integer s1 = m1.get(k);
			Integer s2 = m2.get(k);
			if (s1 == null) {
				s1 = m1.ceilingEntry(k).getValue();
				if (s1 == null) {
					s1 = m1.lowerEntry(k).getValue();
				}
			}

			if (s2 == null) {
				s2 = m2.ceilingEntry(k).getValue();
				if (s2 == null) {
					s2 = m2.lowerEntry(k).getValue();
				}
			}

			map.put(k, Math.abs(s1 - s2));
		});

		return map;
	}

	private static Map<String, Integer> rejectOuters(Map<String, Integer> map, Map<String, Integer> outers) {
		Map m = map.entrySet().stream()
			.filter(e -> outers.get(e.getKey()) != null)
			.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

		return new TreeMap<>(m);
	}

	private static Map<String, Integer> detectOuters(Map<String, Integer> map) {
		List key = new ArrayList(map.keySet());
		List value = new ArrayList(map.values());

		Map<String, Integer> data = R.getInstance().detectOuters(key, value);

		return data;
	}

	private static Map<String, String> PY_SCRIPT = KomatsuDataParameter.GRAPH_PY;
	private static String PY_CSV_FILE = KomatsuDataParameter.GRAPH_TEMP_FILE;
	private static String PY_PATH = KomatsuDataParameter.PYTHONE_PATH;

	private static void testGraph(SyaryoObject syaryo, Map<String, String> smr) {
		String select = "SMR";
		String script = PY_PATH + PY_SCRIPT.get(select);

		List<String> graphData = new ArrayList<>();
		graphData.add("Syaryo," + syaryo.name + ":" + select);

		//header
		String header = IntStream.range(1, smr.values().stream().findFirst().get().split(",").length + 1)
			.boxed().map(i -> "SMR" + i).collect(Collectors.joining(","));
		graphData.add("Date," + header);

		smr.entrySet().stream().map(e -> e.getKey() + "," + e.getValue()).forEach(s -> graphData.add(s));

		//CSV生成
		ListToCSV.toCSV(PY_CSV_FILE, graphData);

		//Python 実行
		new TimeSpreadChart().exec(syaryo.getName(), script);
	}

	private static Map<String, Integer> mergeSMR(Map<String, Integer> map1, Map<String, Integer> map2) {
		Map<String, Integer> map = new TreeMap<>();

		List<String> date = new ArrayList<>();
		date.addAll(map1.keySet());
		date.addAll(map2.keySet());

		date.stream().forEach(k -> {
			if (map1.get(k) != null) {
				map.put(k, map1.get(k));
			} else {
				map.put(k, map2.get(k));
			}
		});

		return map;
	}

	private static Map<String, String> mergeGraphData(Map map1, Map map2) {
		Map<String, String> map = new TreeMap<>();

		List date = new ArrayList<>();
		date.addAll(map1.keySet());
		date.addAll(map2.keySet());

		date.stream().forEach(k -> {
			map.put(k.toString(), map1.get(k) + "," + map2.get(k));
		});

		return map;
	}
}
