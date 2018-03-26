/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import json.JSONToBSON;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplate1{
    private static Map<String, String> validate = new HashMap();
    
    public String kisy, type, s_type, kiban;
	public Map<String, String> map = new LinkedHashMap();

	public SyaryoTemplate1(String kisy, String type, String s_type, String kiban) {
		this.kisy = kisy;
        this.type = type;
        this.s_type = s_type;
        this.kiban = kiban;
	}

	public static Boolean errorCheck(String date) {
        String d = date;
        if(d.contains("/")){
            d = d.replace("/", "").substring(0, 8);
        }
        
		if (d.equals("")) {
			return true;
		} else if (Integer.valueOf(d.replace(" ", "")) <= 1950000) {
			return true;
		}
		return false;
	}

	public void add(String key, String[] s) {
		if (key.equals("仕様")) {
			addSpec(s[0], s[1]);
		}
		if (key.equals("詳細")) {
			addDetail(s[0], s[1], s[2]);
		}
		if (key.equals("生産")) {
			addBorn(s[0], s[1]);
		}
		if (key.equals("出荷")) {
			addDeploy(s[0]);
		}
		if (key.equals("新車")) {
			addNew(s[0], s[1], s[2], s[3], s[4], s[5]);
		}
		if (key.equals("中古車")) {
			addUsed(s[0], s[1], s[2], s[3], s[4], s[5]);
		}
		if (key.equals("廃車")) {
			addDead(s[0], s[1], s[2]);
		}
		if (key.equals("最終更新日")) {
			addLast(s[0], s[1], s[2]);
		}
		if (key.equals("顧客")) {
			addOwner(s[0], s[1], s[2], s[3], s[4], s[5]);
		}
		if (key.equals("SMR")) {
			addSMR(s[0], s[1], s[2], s[3]);
		}
		if (key.equals("経歴")) {
			addHistory(s[0], s[1], s[2], s[3]);
		}
		if (key.equals("受注")) {
			addOrder(s[0], s[1], s[2], s[3], s[4], s[5], s[6],
				s[7], s[8], s[9], s[10], s[11], s[12], s[13],
				s[14], s[15], s[16], s[17]);
		}
		if (key.equals("作業")) {
			addWork(s[0], s[1], s[2], s[3], s[4], s[5], s[6],
				s[7], s[8], s[9], s[10], s[11], s[12], s[13],
				s[14], s[15], s[16], s[17]);
		}
		if (key.equals("部品")) {
			addParts(s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7], s[8], s[9], s[10]);
		}
		if (key.equals("国")) {
			addCountry(s[0], s[1], s[2], s[3]);
		}
        if (key.equals("コマツケア")) {
			addCare(s[0], s[1], s[2], s[3], s[4], s[5], s[6]);
		}
        if (key.equals("コマツケア前受け金")) {
			addCarePrePrice(s[0], s[1], s[2]);
		}
		if (key.equals("GPS")) {
			addGPS(s[0], s[1], s[2], s[3], s[4]);
		}
		if (key.equals("KMSMR")) {
			addKMSMR(s[0], s[1], s[2], s[3]);
		}
		if (key.equals("KMENGINE")) {
			addKMEngine(s[0], s[1], s[2], s[3]);
		}
		if (key.equals("KMERROR")) {
			addKMError(s[0], s[1], s[2], s[3], s[4], s[5]);
		}
		if (key.equals("KMCAUTION")) {
			addKMCaution(s[0], s[1], s[2], s[3], s[4]);
		}
        if (key.equals("KMFUELCONSUME")) {
			addKMFuelConsume(s[0], s[1], s[2], s[3]);
		}
	}

	public void addSpec(String komtrax, String category) {
		String str = "";
		if (map.get("仕様") == null) {
			str = "Komtrax, 製品分類B \n ";
		} else {
			str = map.get("仕様") + " \n ";
		}

		str += komtrax + "," + category;

		map.put("仕様", str);
	}

	public void addDetail(String unit_id, String mname, String sname) {
		String str = "";
		if (map.get("詳細") == null) {
			str = "販売ユニットコード, 中分類名, 小分類名 \n ";
		} else {
			str = map.get("詳細") + " \n ";
		}

		str += unit_id + "," + mname + "," + sname;

		map.put("詳細", str);
	}

	public void addBorn(String date, String plant) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("生産") == null) {
			str = "生産年月日, 工場コード \n ";
		} else {
			str = map.get("生産") + " \n ";
		}

		str += date + "," + plant;

		map.put("生産", str);
	}

	public void addDeploy(String date) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("出荷") == null) {
			str = "出荷年月日 \n ";
		} else {
			str = map.get("出荷") + " \n ";
		}

		str += date;

		map.put("出荷", str);
	}

	public void addNew(String db, String company, String date, String price, String hyomenPrice, String jishitsuPrice) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("新車") == null) {
			str = "DB, 会社コード, 納入年月日, 標準価格, 表面売上高, 実質売上高 \n ";
		} else {
			str = map.get("新車") + " \n ";
		}

		str += db + "," + company + "," + date + "," + price + "," + hyomenPrice + "," + jishitsuPrice;
		map.put("新車", str);
	}

	public void addUsed(String db, String company, String date, String price, String hyomenPrice, String jishitsuPrice) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("中古車") == null) {
			str = "DB, 会社コード, 納入年月日, 標準価格, 表面売上高, 実質売上高 \n ";
		} else {
			str = map.get("中古車") + " \n ";
		}

		str += db + "," + company + "," + date + "," + price + "," + hyomenPrice + "," + jishitsuPrice;
		map.put("中古車", str);
	}

	public void addDead(String db, String company, String date) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("廃車") == null) {
			str = "DB, 会社コード, 廃車年月日 \n ";
		} else {
			str = map.get("廃車") + " \n ";
		}

		str += db + "," + company + "," + date;
		map.put("廃車", str);
	}

	public void addLast(String db, String company, String date) {
		if (date.contains("-")) {
			date = date.replace("-", "").substring(0, 8);
		}
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("最終更新日") == null) {
			str = "DB, 会社コード, 最終更新日 \n ";
		} else {
			str = map.get("最終更新日") + " \n ";
		}

		str += db + "," + company + "," + date.substring(0, 8);
		map.put("最終更新日", str);
	}

	public void addOwner(String db, String company, String date, String gyosyu, String id, String name) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("顧客") == null) {
			str = "DB, 会社コード, 日付, 業種, 顧客ID, 顧客名 \n ";
		} else {
			str = map.get("顧客") + " \n ";
		}

		str += db + "," + company + "," + date + "," + gyosyu + "," + id + "," + name;
		map.put("顧客", str);
	}

	public void addSMR(String db, String company, String date, String smr) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("SMR") == null) {
			str = "DB, 会社コード, 日付, SMR \n ";
		} else {
			str = map.get("SMR") + " \n ";
		}

		str += db + "," + company + "," + date + "," + smr;
		map.put("SMR", str);
	}

	public void addHistory(String db, String company, String date, String id) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("経歴") == null) {
			str = "DB, 会社コード, 日付, ID \n ";
		} else {
			str = map.get("経歴") + " \n ";
		}

		str += db + "," + company + "," + date + "," + id;
		map.put("経歴", str);
	}

	public void addOrder(String db, String company, String date, String sbnDate, String startDate, String finishDate,
		String sbn, String hanbai, String status,
		String kokyakuId, String kokyakuName, String id, String name,
		String step, String sijistep,
		String uag_kbn, String price, String gaiyo
	) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("受注") == null) {
			str = "DB, 会社コード, 日付, 作番登録日, 実施予定日, 完了日, "
				+ "作番, 修・単, 作番ステータス, "
				+ "顧客ID, 顧客名, 保有顧客ID, 保有顧客名, "
				+ "工数, 指示工数, "
				+ "売上区分, 請求,"
				+ "概要 \n ";
		} else {
			str = map.get("受注") + " \n ";
		}

		str += db + "," + company + "," + date + "," + sbnDate + "," + startDate + "," + finishDate + "," + sbn + "," + hanbai + "," + status + ","
			+ kokyakuId + "," + kokyakuName + "," + id + "," + name + ","
			+ step + "," + sijistep + ","
			+ uag_kbn + "," + price + "," + gaiyo;
		map.put("受注", str);
	}

	public void addWork(String db, String company, String date, String sbn, String sg_id,
		String keitaiId, String keitaiName,
		String id, String name, String finish,
		String suryo, String kingaku,
		String gaichu, String gaichuKingaku,
		String step, String seikyuStep, String sijiStep, String jisekiStep) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("作業") == null) {
			str = "DB, 会社コード, 受注日, 作番, 作業明細番号, "
				+ "作業形態コード, 作業形態名, "
				+ "作業コード, 作業名, 完了フラグ, "
				+ "数量, 標準金額, "
				+ "外注フラグ, 外注原価, "
				+ "標準工数, 請求工数, 指示工数, 実績累計工数 \n ";
		} else {
			str = map.get("作業") + " \n ";
		}

		str += db + "," + company + "," + date + "," + sbn + "," + sg_id + ","
			+ keitaiId + "," + keitaiName + ","
			+ id + "," + name + "," + finish + ","
			+ suryo + "," + kingaku + ","
			+ gaichu + "," + gaichuKingaku + ","
			+ step + "," + seikyuStep + "," + sijiStep + "," + jisekiStep;
		map.put("作業", str);
	}

	public void addParts(String db, String company, String date, String sbn, String mid, String maker, String id, String name, String suryo, String cancel, String kingaku) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("部品") == null) {
			str = "DB, 会社コード, 日付, 作番, 部品明細番号, 部品メーカ区分, 品番, "
				+ "品名, 受注数量, キャンセル数量, 標準金額 \n ";
		} else {
			str = map.get("部品") + " \n ";
		}

		str += db + "," + company + "," + date + "," + sbn + "," + mid + "," + maker + "," + id + "," + name + "," + suryo + "," + cancel + "," + kingaku;
		map.put("部品", str);
	}

	public void addCountry(String db, String company, String date, String country) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("国") == null) {
			str = "DB, 会社コード, 日付, 国コード \n ";
		} else {
			str = map.get("国") + " \n ";
		}

		str += db + "," + company + "," + date + "," + country;
		map.put("国", str);
	}
    
    public void addCare(String db, String company, String date, String id, String c_no, String kind, String price) {
		if (errorCheck(date)) {
			return;
		}

		String str = "";
		if (map.get("コマツケア") == null) {
			str = "DB, 会社コード, 日付, 代理店コード, クレーム番号, 保証種別, 金額 \n ";
		} else {
			str = map.get("コマツケア") + " \n ";
		}

		str += db + "," + company + "," + date + "," + id + "," + c_no + "," + kind + "," + price;
		map.put("コマツケア", str);
	}
    
    public void addCarePrePrice(String db, String company, String price) {

		String str = "";
		if (map.get("コマツケア前受け金") == null) {
			str = "DB, 会社コード, 金額 \n ";
		} else {
			str = map.get("コマツケア前受け金") + " \n ";
		}

		str += db + "," + company + "," + price;
		map.put("コマツケア前受け金", str);
	}

	//Komtrax
	//GPS
	public void addGPS(String db, String company, String date, String latitude, String longitude) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("GPS") == null) {
			str = "DB, 会社コード, 日付, 緯度, 経度 \n ";
		} else {
			str = map.get("GPS") + " \n ";
		}

		str += db + "," + company + "," + date + "," + latitude + "," + longitude;
		map.put("GPS", str);
	}

	//SMR
	public void addKMSMR(String db, String company, String date, String smr) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMSMR") == null) {
			str = "DB, 会社コード, 日付, SMR \n ";
		} else {
			str = map.get("KMSMR") + " \n ";
		}

		str += db + "," + company + "," + date + "," + smr;
		map.put("KMSMR", str);
	}

	//ENGINE
	public void addKMEngine(String db, String company, String date, String engine) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMENGINE") == null) {
			str = "DB, 会社コード, 日付, エンジンスロットル \n ";
		} else {
			str = map.get("KMENGINE") + " \n ";
		}

		str += db + "," + company + "," + date + "," + engine;
		map.put("KMENGINE", str);
	}

	//ERROR
	public void addKMError(String db, String company, String date, String error_code, String error_kind, String error_cnt) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMERROR") == null) {
			str = "DB, 会社コード, 日付, エラーコード, エラー種類, カウント \n ";
		} else {
			str = map.get("KMERROR") + " \n ";
		}

		str += db + "," + company + "," + date + "," + error_code + "," + error_kind + "," + error_cnt;
		map.put("KMERROR", str);
	}

	//CAUTION
	public void addKMCaution(String db, String company, String date, String caution_icon, String caution_cnt) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMCAUTION") == null) {
			str = "DB, 会社コード, 日付, ICONコード, カウント \n ";
		} else {
			str = map.get("KMCAUTION") + " \n ";
		}

		str += db + "," + company + "," + date + "," + caution_icon + "," + caution_cnt;
		map.put("KMCAUTION", str);
	}
    
    //FUEL CONSUME
	public void addKMFuelConsume(String db, String company, String date, String consume_cnt) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMFUELCONSUME") == null) {
			str = "DB, 会社コード, 日付, カウント \n ";
		} else {
			str = map.get("KMFUELCONSUME") + " \n ";
		}

		str += db + "," + company + "," + date + "," + consume_cnt;
		map.put("KMFUELCONSUME", str);
	}
    
    //ACT DATA
	public void addKMAct(String db, String company, String date, String act_cnt) {
		String check_date = date.replace("/", "").substring(0, 8);
		if (errorCheck(check_date)) {
			return;
		}

		date = date.substring(0, 10) + " " + date.substring(11, date.length());

		String str = "";
		if (map.get("KMACT") == null) {
			str = "DB, 会社コード, 日付, カウント \n ";
		} else {
			str = map.get("KMACT") + " \n ";
		}

		str += db + "," + company + "," + date + "," + act_cnt;
		map.put("KMACT", str);
	}

	public String get(String id) {
		return map.get(id);
	}

	public Map<String, String> getAll() {
		return map;
	}

	public String getName() {
		return kisy+"-"+type+s_type+"-"+kiban;
	}
    
    public static String getName(String kisy, String type, String s_type, String kiban){
        return kisy+"-"+type+s_type+"-"+kiban;
    }
    
    public void setting(){
        String name = kisy + "-" + type + s_type + "-" + kiban;
        validate.put(name, name);
        validate.put(kisy+"-"+type+"-"+kiban, name);
        validate.put(kisy+"--"+kiban, name);
    }
    
    public static String check(String kisy, String type, String s_type, String kiban){
        String n = validate.get(kisy+"-"+type+s_type+"-"+kiban);
        if(n == null)
            n = validate.get(kisy+"--"+kiban);
        return n;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder(getName());
		for (Object key : map.keySet()) {
			sb.append("|");
			sb.append(key);
			sb.append(":");
			sb.append(map.get(key));
		}
		return sb.toString();
	}
    
	public SyaryoTemplate1 clone() {	
		return new SyaryoTemplate1(kisy, type, s_type, kiban);
	}
    
    //test
    public void add(String table, String header, String record){
        StringBuilder sb = new StringBuilder();
        if(map.get(table) == null)
            sb.append(header);
        sb.append("\n");
        sb.append(record);
        
        map.put(table, sb.toString());
    }
}