/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kaeru
 */
public class SyaryoElements {
	//"経歴","最終更新日" は除く
	public static final String[] list = {
			"仕様", "詳細", "生産", "出荷", "廃車", "新車"
			, "中古車", "顧客", "国", "受注", "作業", "部品"
			/*, "警告", "エラー", "エンジン", "SMR", "GPS"*/
	};
	
	public static final Map<String, Element[]> map = new HashMap(){{
		put(list[0], SyaryoElements.Spec.values());
		put(list[1], SyaryoElements.Detail.values());
		put(list[2], SyaryoElements.Product.values());
		put(list[3], SyaryoElements.Deploy.values());
		put(list[4], SyaryoElements.Dead.values());
		put(list[5], SyaryoElements.New.values());
		put(list[6], SyaryoElements.Used.values());
		put(list[7], SyaryoElements.Customer.values());
		put(list[8], SyaryoElements.Country.values());
		put(list[9], SyaryoElements.Order.values());
		put(list[10], SyaryoElements.Work.values());
		put(list[11], SyaryoElements.Parts.values());
		put(list[12], SyaryoElements.Caution.values());
		put(list[13], SyaryoElements.Error.values());
		put(list[14], SyaryoElements.Engine.values());
		put(list[15], SyaryoElements.SMR.values());
		put(list[16], SyaryoElements.GPS.values());
	}};
	
    //リストのインデックスを取得
    public static Integer get(String key, String element){
        Element[] elements = map.get(key);
        return Arrays.stream(elements)
                        .filter(e -> e.getText().equals(element))
                        .findFirst().map(e -> e.getNo())
                        .get();
    }
    
    //-1 = key, -2 = value, -3 = keyとの複合でしか利用不可, 0~N = index
	//Spec
	public enum Spec implements Element{
        Komtrax("Komtrax", 0),
        Category("製品分類B", 1);
		
		final String name;
		final Integer index;
		private Spec(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Detail
	public enum Detail implements Element{
		Unit("販売ユニットコード", 0),
		MCategory("中分類名", 1),
		SCategory("小分類名",2);
		
		final String name;
		final Integer index;
		private Detail(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Product
	public enum Product implements Element{
		Date("生産日", 0),
		Plant("工場コード", 1);
		
		final String name;
		final Integer index;
		private Product(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Deploy
	public enum Deploy implements Element{
		Date("出荷日", 0);
		
		final String name;
		final Integer index;
		private Deploy(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Dead
	public enum Dead implements Element{
		DB("DB", 0),
        Company("会社コード", 1),
		Date("日付", 2);
		
		final String name;
		final Integer index;
		private Dead(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//New
	public enum New implements Element{
		DB("DB", 0),
        Company("会社",1),
        Date("日付", 2),
		SPrice("標準価格", 3),
		HPrice("表面価格", 4),
		RPrice("実質価格", 5);
		
		final String name;
		final Integer index;
		private New(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Used
	public enum Used implements Element{
		DB("DB", 0),
        Company("会社",1),
        Date("日付", 2),
		SPrice("標準価格", 3),
		HPrice("表面価格", 4),
		RPrice("実質価格", 5);
		
		final String name;
		final Integer index;
		private Used(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Customer
	public enum Customer implements Element{
		DB("DB", 0),
        Company("会社", 1),
        Date("日付", 2),
        //KBN("顧客区分", 3),
		Code("業種コード", 3),
        ID("顧客ID", 4),
		Name("顧客名", 5);
		
		final String name;
		final Integer index;
		private Customer(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Country
	public enum Country implements Element{
        DB("DB", 0),
        Company("会社", 1),
		Date("登録日", 2),
		Code("国", 3);
		
		final String name;
		final Integer index;
		private Country(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Order
    //"受注":DB, 会社コード, 日付, 作番登録日, 実施予定日, 完了日, 作番, 作業形態コード, 作業形態名 修・単, 作番ステータス, 顧客ID, 顧客名, 保有顧客ID, 保有顧客名, 工数, 指示工数, 売上区分, 一般請求, コマツ請求, 概要
	public enum Order implements Element{
		DB("DB", 0),
        Company("会社", 1),
        Date("日付", 2),
		RDate("作番登録日", 3),
		PDate("実施予定日", 4),
		FDate("完了日", 5),
		ID("作番", 6),
        SG_Code("作業形態コード", 7),
        SG_Name("作業形態名", 8),
		Sell("単・修", 7),
		Status("作番ステータス", 8),
		SCustomer("顧客ID", 9),
		SName("顧客名", 10),
        Customer("顧客ID", 11),
		Name("顧客名", 12),
		Step("工数", 13),
		DStep("指示工数", 14),
        FLAG("売上区分", 15),
		Invoice("請求", 16),
		Summary("概要", 17);
		
		final String name;
		final Integer index;
		private Order(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Work
    //"作業": "DB, 会社コード, 受注日, 作番, 作業明細番号, 作業コード, 作業名, 完了フラグ, 数量, 標準金額, 外注フラグ, 外注原価, 標準工数, 請求工数, 指示工数, 実績累計工数
	public enum Work implements Element{
		DB("DB", 0),
        Company("会社",1),
        Date("日付", 2),
		ID("作番", 3),
		SID("作業明細番号", 4),
		SCode("作業コード", 5),
		SName("作業名", 6),
		Finish("完了フラグ", 7),
		Quant("数量", 8),
		Price("請求金額", 9),
		Outsorce("外注フラグ", 10),
		OPrice("外注原価", 11),
		SStep("標準工数", 12),
		RStep("請求工数", 13),
		DStep("指示工数", 14),
		RTStep("実質累計工数", 15);
		
		final String name;
		final Integer index;
		private Work(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Parts
    //"部品": "DB, 会社コード, 日付, 作番, 部品明細番号, 部品メーカ区分, 品番, 品名, 受注数量, キャンセル数量, 標準金額
	public enum Parts implements Element{
		DB("DB", 0),
        Company("会社",1),
        Date("日付", 2),
		ID("作番", 3),
		PID("部品明細番号", 4),
        MAKER("部品メーカー区分", 5),
		HID("品番", 6),
		HName("品名", 7),
		Quant("数量", 8),
		Cancel("キャンセル数量", 9),
		Price("請求金額", 10);
		
		final String name;
		final Integer index;
		private Parts(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
    
    //"コマツケア": "DB, 会社コード, 日付, 代理店コード, クレーム番号, 保証種別, 金額
    public enum Care implements Element{
		DB("DB", 0),
        Company("会社",1),
        Date("日付", 2),
		ID("代理店コード", 3),
		CID("クレーム番号", 4),
        KIND("保証種別", 5),
		Price("標準金額", 6);
		
		final String name;
		final Integer index;
		private Care(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
    
    //"コマツケア前受け金": "DB, 会社コード, 金額
	public enum CarePrice implements Element{
		DB("DB", 0),
        Company("会社",1),
		Price("標準金額", 2);
		
		final String name;
		final Integer index;
		private CarePrice(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
    
	//Caution
	public enum Caution implements Element{
		Date("日付", -1),
		Icon("ICONコード", 0),
		Count("カウント", 1),
		Company("会社",2),
        DB("DB", 3),
        Age("経過日", -3),
		Format("format:yyyy/MM/dd", 0);
		
		final String name;
		final Integer index;
		private Caution(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Error
	public enum Error implements Element{
	DB("DB", 0),
	Company("会社",1),
	Date("日付", 2),
		Code("エラーコード", 3),
        Kind("エラー種類", 4),
		Count("カウント", 5),
		
        Age("経過日", -3),
		Format("format:yyyy/MM/dd", 0);
		
		final String name;
		final Integer index;
		private Error(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//Engine
	public enum Engine implements Element{
		Date("日付", -1),
		Throttle("スロットル", 0),
		Company("会社",1),
        DB("DB", 2),
        Age("経過日", -3),
		Format("format:yyyy/MM/dd", 0);
		
		final String name;
		final Integer index;
		private Engine(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//SMR
	public enum SMR implements Element{
		Company("会社", 0),
        DB("DB", 1),
		Date("日付",2),
		_SMR("SMR", 3),
		
        Age("経過日", -3),
		Format("format:yyyy/MM/dd", 0);
		
		final String name;
		final Integer index;
		private SMR(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
	
	//GPS
	public enum GPS implements Element{
		Date("日付", -1),
		_GPS("GPS", 0),
		Company("会社", 1),
        DB("DB", 2),
		Format("format:yyyy/MM/dd", 0),
        Age("経過日", -3),
		GPSFormat("format:N99.99.99.999_E999.99.99.999", 0);
		
		final String name;
		final Integer index;
		private GPS(String name, Integer index){
			this.name = name;
			this.index = index;
		}
		
		public String getText(){
			return name;
		}
		
		public Integer getNo(){
			return index;
		}
	}
}
