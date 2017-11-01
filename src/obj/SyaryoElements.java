/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kaeru
 */
public class SyaryoElements {
	//"経歴","最終更新日" は除く
	public static final String[] list = {
			"仕様", "詳細", "生産", "出荷", "廃車", "新車",
			"中古車", "顧客", "国", "受注", "作業", "部品",
			 "警告", "エラー", "エンジン", "SMR", "GPS"
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
	
	//Spec
	public enum Spec implements Element{
		Category("製品分類B", 0),
		Komtrax("Komtrax", 1),
		DB_Company("DB_会社",2);
		
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
		SCategory("小分類名",2),
		Source("DB_会社",3);
		
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
		Format("format:yyyyMMdd", 0);
		
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
		Date("出荷日", 0),
		Format("format:yyyyMMdd", 0);
		
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
		Date("廃車日", 0),
		Format("format:yyyyMMdd", 0);
		
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
		Date("納品日", 0),
		SPrice("標準価格", 0),
		HPrice("表面価格", 1),
		RPrice("実質価格", 2),
		Source("DB_会社",3),
		Format("format:yyyyMMdd", 0);
		
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
		Date("納品日", 0),
		SPrice("標準価格", 0),
		HPrice("表面価格", 1),
		RPrice("実質価格", 2),
		Source("DB_会社",3),
		Format("format:yyyyMMdd", 0);
		
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
		Date("登録日", 0),
		Customer("顧客ID", 0),
		Code("業種コード", 1),
		Name("顧客名", 2),
		Source("DB_会社",3),
		Format("format:yyyyMMdd", 0);
		
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
		Date("登録日", 0),
		Code("国", 0),
		Format("format:yyyyMMdd", 0);
		
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
	public enum Order implements Element{
		Date("受注日", 0),
		RDate("作番登録日", 0),
		PDate("実施予定日", 1),
		FDate("完了日", 2),
		ID("作番", 3),
		Sell("単・修", 4),
		Status("作番ステータス", 5),
		Customer("顧客ID", 10),
		Name("顧客名", 11),
		ContractNo("契約管理番号", 12),
		ContractCode("契約種別コード", 13),
		ContractFlg("契約適用フラグ", 14),
		Step("工数", 15),
		DStep("指示工数", 16),
		Invoice("一般請求", 17),
		KInvoice("コマツ請求", 18),
		CInvoice("社内請求", 19),
		Summary("概要", 20),
		Source("DB_会社",21),
		Format("format:yyyyMMdd", 0);
		
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
	public enum Work implements Element{
		Date("実施予定日", 0),
		ID("作番", 0),
		SID("作業明細番号", 1),
		SFCode("作業形態コード", 2),
		SFName("作業形態名", 3),
		SCode("作業コード", 4),
		SName("作業名", 5),
		Finish("完了フラグ", 6),
		Quant("数量", 7),
		Price("標準金額", 8),
		Outsorce("外注フラグ", 9),
		OPrice("外注原価", 10),
		SStep("標準工数", 11),
		RStep("請求工数", 12),
		DStep("指示工数", 13),
		RTStep("実質累計工数", 14),
		Source("DB_会社",15),
		Format("format:yyyyMMdd", 0);
		
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
	public enum Parts implements Element{
		Date("日付", 0),
		ID("作番", 0),
		PID("部品明細番号", 1),
		HID("品番", 2),
		HNmae("品名", 3),
		Quant("数量", 4),
		Cancel("キャンセル数量", 5),
		Price("標準金額", 6),
		Source("DB_会社",7),
		Format("format:yyyyMMdd", 0);
		
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
	
	//Caution
	public enum Caution implements Element{
		Date("日付", 0),
		Unit("ユニット", 0),
		Map("マップ", 1),
		Count("カウント", 2),
		Source("DB_会社",3),
		Format("format:yyyy/MM/dd HH:MM:SS", 0);
		
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
		Date("日付", 0),
		Code("エラーコード", 0),
		Count("カウント", 1),
		Source("DB_会社",2),
		Format("format:yyyy/MM/dd HH:MM:SS", 0);
		
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
		Date("日付", 0),
		Throttle("スロットル", 0),
		Source("DB_会社",1),
		Format("format:yyyy/MM/dd HH:MM:SS", 0);
		
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
		Date("日付", 0),
		_SMR("SMR", 0),
		Source("DB_会社",1),
		Format("format:yyyy/MM/dd HH:MM:SS", 0);
		
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
		Date("日付", 0),
		_SMR("GPS", 0),
		Source("DB_会社",1),
		Format("format:yyyy/MM/dd HH:MM:SS", 0),
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
