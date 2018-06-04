/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import creator.create.KomatsuDataParameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import json.MapIndexToJSON;
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author zz17390
 */
public class SyaryoObjectFormatting {

	private static String KISY = "PC138US";
	private static String INDEXPATH = KomatsuDataParameter.SHUFFLE_FORMAT_PATH;
	private static String OBJPATH = KomatsuDataParameter.OBJECT_PATH;
	private static String INDEX_PATH = KomatsuDataParameter.CUSTOMER_INDEX_PATH;
	private static Map<String, List> dataIndex;

	public static void main(String[] args) {
		//Data Index
		dataIndex = index();

		form(KISY);
	}

	private static void form(String kisy) {
		SyaryoToZip3 zip3 = new SyaryoToZip3();
		String filename = OBJPATH + "syaryo_obj_" + kisy + ".bz2";
		Map<String, SyaryoObject4> syaryoMap = zip3.read(filename);

		//本社コード
		Map<String, String> honsyIndex = new MapIndexToJSON().reader(INDEX_PATH);

		int n = 0;
		for (String key : syaryoMap.keySet()) {
			System.out.print(key);
			SyaryoObject4 syaryo = syaryoMap.get(key);
			syaryo.decompress();

			//整形時のデータ削除ルールを設定
			DataRejectRule rule = new DataRejectRule();
			
			//整形後出力するMap
			Map newMap;
				
			//顧客の整形
			newMap = formOwner(syaryo.get("顧客"), dataIndex.get("顧客"), honsyIndex, rule);
			syaryo.map.put("顧客", newMap);

			//新車の整形
			newMap = formNew(syaryo.get("新車"), syaryo.get("生産"), syaryo.get("出荷"), dataIndex.get("新車"));
			syaryo.map.put("新車", newMap);

			//中古車の整形
			if (!rule.getKUEC().isEmpty()) {
				//System.out.println(key + ":" + rule.getKUEC());
			}
			newMap = formUsed(syaryo.get("中古車"), dataIndex.get("中古車"), rule.getKUEC());
			syaryo.map.put("中古車", newMap);
			
			//受注
			newMap = formOrder(syaryo.get("受注"), dataIndex.get("受注"));
			syaryo.map.put("受注", newMap);

			syaryo.compress(true);
			n++;

			if (n % 1000 == 0) {
				System.out.println(n + "台処理");
			}
		}

		String outfile = OBJPATH + "syaryo_obj_" + kisy + "_form.bz2";
		zip3.write(outfile, syaryoMap);
	}

	private static Map formOwner(Map<String, List> owner, List indexList, Map<String, String> honsyIndex, DataRejectRule reject) {
		if (owner == null) {
			//System.out.println("Not found owner!");
			return null;
		}

		Integer company = indexList.indexOf("KSYCD");
		Integer ownerID = indexList.indexOf("NNSCD");
		Integer ownerName = indexList.indexOf("NNSK_NM_1");

		//日付データ揃え
		owner = dateFormalize(owner);
		//本社コード揃え
		for (String d : owner.keySet()) {
			List list = owner.get(d);
			String com = list.get(company).toString();
			String id = list.get(ownerID).toString();

			if (honsyIndex.get(com + "_" + id) != null) {
				id = honsyIndex.get(com + "_" + id).split("_")[0];
			}

			reject.addKUEC(id, d.split("#")[0]); //KUECを登録

			list.set(ownerID, id);
		}

		//ID重複排除 ##排除
		//System.out.println(owner.values().stream().map(l -> l.get(ownerID)).collect(Collectors.toList()));
		List owners = owner.values().stream()
			.map(l -> l.get(ownerID))
			.filter(id -> !id.toString().contains("##")) //工場IDが振られていない
			.filter(id -> !id.toString().equals("")) //IDが存在する
			.filter(id -> !KomatsuDataParameter.KUEC_LIST.contains(id)) //KUECでない
			.collect(Collectors.toList());
		//System.out.println(owners);
		owners = exSeqDuplicate(owners);

		if (owners.isEmpty()) {
			//System.out.println("使用顧客が存在しない車両(後で削除)");
			return null;
		}

		Map<String, List> map = new TreeMap();
		int i = 0;
		for (String date : owner.keySet()) {
			if (date.length() >= 8) {
				String id = owner.get(date).get(ownerID).toString();
				if (id.equals(owners.get(i))) {
					map.put(date, owner.get(date));
					i++;
					if (owners.size() <= i) {
						break;
					}
				}
			}
		}

		//名称重複排除
		/*owners = map.values().stream()
                        .map(l -> l.get(ownerName))
                        .distinct()
                        .collect(Collectors.toList());
        Map<String, List> map2 = new TreeMap();
        i = 0;
        for(String date : map.keySet()){
            String name = map.get(date).get(ownerName).toString();
            if(name.equals(owners.get(i))){
                map2.put(date, map.get(date));
                i++;
                if(owners.size() <= i)
                    break;
            }
        }*/
		return map;
	}

	private static Map formNew(Map<String, List> news, Map<String, List> born, Map<String, List> deploy, List indexList) {
		Map<String, List> map = new TreeMap();
		if (news == null) {
			//出荷情報を取得する処理
			String date;
			if (deploy != null) {
				date = deploy.keySet().stream().findFirst().get();
			} else {
				date = born.keySet().stream().findFirst().get();
			}
			List list = new ArrayList();
			for (Object s : indexList) {
				list.add("None");
			}
			map.put(date, list);
			return map;
		}

		//List price index
		int hyomen = indexList.indexOf("HM_URI_KN");
		int jitsu = indexList.indexOf("RL_URI_KN");
		int hyojun = indexList.indexOf("STD_SY_KKU");

		//修正しない
		if (news.size() == 1) {
			List<String> list = news.values().stream().findFirst().get();
			if (list.get(hyomen).contains("+")) {
				for (int i = hyomen; i < list.size(); i++) {
					list.set(i, String.valueOf(Double.valueOf(list.get(i)).intValue()));
				}
			}
			return news;
		}

		//複数存在するときの処理
		List list = new ArrayList();
		String key = "";
		String[] price = new String[3];
		Boolean flg = true;
		for (String date : news.keySet()) {
			list = news.get(date);
			if (flg) {
				key = date.split("#")[0];
				price[0] = list.get(hyomen).toString();
				price[1] = list.get(jitsu).toString();
				price[2] = list.get(hyojun).toString();
				flg = false;
			} else {
				if (list.get(hyomen) != "None") {
					price[0] = list.get(hyomen).toString();
				}
				if (list.get(jitsu) != "None") {
					price[1] = list.get(jitsu).toString();
				}
				if (list.get(hyojun) != "None") {
					price[2] = list.get(hyojun).toString();
				}
			}
		}

		//List整形
		int i = 0;
		for (String s : price) {
			if (!s.equals("None")) {
				list.set(hyomen + i, String.valueOf(Double.valueOf(s).intValue()));
			}
		}

		map.put(key, list);

		return map;
	}

	private static Map formUsed(Map<String, List> used, List indexList, List kuec) {
		if (used == null) {
			//System.out.println("Not found Used");
			return null;
		}

		//List price index
		int hyomen = indexList.indexOf("HM_URI_KN");
		int jitsu = indexList.indexOf("RL_URI_KN");
		int hyojun = indexList.indexOf("STD_SY_KKU");

		//日付揃え
		used = dateFormalize(used);

		Map<String, List> map = new TreeMap();

		//修正しない
		if (used.size() == 1) {
			//KUEC売却後、使用ユーザー存在しない
			if (kuec.size() > 0) {
				return null;
			}

			List<String> list = used.values().stream().findFirst().get();
			if (list.get(hyomen).contains("+") || list.get(hyomen).contains("_")) {
				for (int i = hyomen; i < list.size(); i++) {
					list.set(i, String.valueOf(Double.valueOf(list.get(i).replace("_", "")).intValue()));
				}
			}
			return used;
		}

		//複数存在するときの処理
		String key = "";
		for (String date : used.keySet()) {
			List list = used.get(date);
			String d = date.split("#")[0].replace("/", "");

			//KUECを除外
			if (kuec.contains(d)) {
				continue;
			}

			if (!key.equals(d)) {
				key = d;
				map.put(key, list);
			} else {
				if (!list.get(hyomen).toString().equals("None") && !map.get(d).get(hyomen).toString().contains("+")) {
					map.get(d).set(hyomen, list.get(hyomen).toString());
				}
				if (!list.get(jitsu).toString().equals("None") && !map.get(d).get(jitsu).toString().contains("+")) {
					map.get(d).set(jitsu, list.get(jitsu).toString());
				}
				if (!list.get(hyojun).toString().equals("None") && !map.get(d).get(hyojun).toString().contains("+")) {
					map.get(d).set(hyojun, list.get(hyojun).toString());
				}
			}
		}

		//KUECしか存在しない
		if (map.isEmpty()) {
			//System.out.println("KUECデータしか存在しない!");
			//System.exit(0);
			return null;
		}

		//List整形
		for (String d : map.keySet()) {
			List list = map.get(d);
			for (int i = hyomen; i < hyomen + 3; i++) {
				if (!list.get(i).toString().equals("None")) {
					list.set(i, String.valueOf(Double.valueOf(list.get(i).toString().replace("_", "")).intValue()));
				}
			}
		}

		return map;
	}
	
	private static Map formOrder(Map<String, List> order, List indexList){
		if (order == null) {
			//System.out.println("Not found Order!");
			return null;
		}
		
		//作番重複除去
		List<String> sbnList = order.keySet().stream()
							.map(s -> s.split("#")[0])
							.distinct()
							.collect(Collectors.toList());
		
		//System.out.println("作番:"+sbnList);
		
		Map<String, List<String>> map = new LinkedHashMap();
		
		int kom_order = indexList.indexOf("kom_order");
		int price = indexList.indexOf("SKKG");
		
		for(String sbn : sbnList){
			//KOMPAS 受注テーブル情報が存在するときは取り出す
			Optional<List> kom = order.values().stream().filter(s -> s.get(kom_order).toString().equals("kom_order")).findFirst();
			if(kom.isPresent()){
				map.put(sbn, kom.get());
				continue;
			}
			
			//重複作番を取り出す
			List<String> sbnGroup = order.keySet().stream()
									.filter(s -> s.contains(sbn))
									.collect(Collectors.toList());
			for(String sg : sbnGroup){
				List<String> list = order.get(sg);
				if(list.get(price).contains("+"))
					if(map.get(sbn) == null)
						map.put(sbn, list);
					else
						System.out.println("サービス経歴に金額の入ったデータが2つ以上存在");
			}
		}
		
		//金額の整形処理
		for(String sbn : map.keySet()){
			List<String> list = map.get(sbn);
			list.set(price, String.valueOf(Double.valueOf(list.get(price)).intValue()));
		}
		
		return map;
	}

	//Util
	private static Map<String, List> index() {
		Map<String, Map<String, List<String>>> index = new MapIndexToJSON().reader(INDEXPATH);
		Map<String, List> formIndex = new HashMap();

		for (String key : index.keySet()) {
			List<String> list = new ArrayList();
			List<String> list2 = new ArrayList();
			int n = 0;
			for (Object id : index.get(key).keySet()) {
				int s = index.get(key).get(id).stream().mapToInt(le -> le.contains("#") ? 1 : 0).sum();
				if (s == index.get(key).get(id).size()) {
					list = index.get(key).get(id);
				}
				if (n < s) {
					list2 = index.get(key).get(id);
					n = s;
				}
			}

			if (list.isEmpty()) {
				list = list2;
			}

			//List内の整形
			list = list.stream().filter(le -> !(le.contains("=") || le.contains("<") || le.contains(">"))).collect(Collectors.toList());
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i);
				if (str.contains("#")) {
					str = str.split("#")[0].split("\\.")[1];
				}
				list.set(i, str);
			}

			formIndex.put(key, list);
		}
		formIndex.entrySet().stream().map(e -> e.getKey() + ":" + e.getValue()).forEach(System.out::println);
		return formIndex;
	}

	private static List exSeqDuplicate(List<String> dupList) {
		List list = new ArrayList();
		String tmp = "";
		for (String el : dupList) {
			if (tmp.equals(el)) {
				continue;
			}
			tmp = el;
			list.add(tmp);
		}

		return list;
	}

	private static Map dateFormalize(Map dateMap) {
		Map map = new TreeMap();
		for (Object date : dateMap.keySet()) {
			//日付
			String d = date.toString().replace("/", "");
			map.put(d, dateMap.get(date));
		}
		return map;
	}
}
