/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obj;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author ZZ17390
 */
public class SyaryoTemplate {

    public String name;
    public Map<String, String> map = new LinkedHashMap();

    public SyaryoTemplate(String kisy, String type, String kiban) {
        this.name = kisy + "-" + type + "-" + kiban;
    }
    
    private Boolean errorCheck(String date){
        if(date.equals("")) return true ;
        else if(Integer.valueOf(date) <= 1950000) return true;
        return false;
    }
    
    public void addSpec(String komtrax, String category){
        String str = "";
        if(map.get("仕様") == null)
            str = "Komtrax, 製品分類B \n ";
        else
            str = map.get("仕様")+" \n ";
        
        str += komtrax+","+category;
        
        map.put("仕様", str);
    }

    public void addBorn(String date, String plant) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("生産") == null)
            str = "生産年月日, 工場コード \n ";
        else
            str = map.get("生産")+" \n ";
        
        str += date+","+plant;
        
        map.put("生産", str);
    }

    public void addDeploy(String date) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("出荷") == null)
            str = "出荷年月日 \n ";
        else
            str = map.get("出荷")+" \n ";
        
        str += date;
        
        map.put("出荷", str);
    }

    public void addNew(String db, String company, String date, String price, String hyomenPrice, String jishitsuPrice) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("新車") == null)
            str = "DB, 会社コード, 納入年月日, 標準価格, 表面売上高, 実質売上高 \n ";
        else
            str = map.get("新車")+" \n ";
        
        str += db+","+company+","+date+","+price+","+hyomenPrice+","+jishitsuPrice;
        map.put("新車", str);
    }

    public void addUsed(String db, String company, String date, String price, String hyomenPrice, String jishitsuPrice) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("中古") == null)
            str = "DB, 会社コード, 納入年月日, 標準価格, 表面売上高, 実質売上高 \n ";
        else
            str = map.get("中古")+" \n ";
        
        str += db+","+company+","+date+","+price+","+hyomenPrice+","+jishitsuPrice;
        map.put("中古", str);
    }

    public void addDead(String db, String company, String date) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("廃車") == null)
            str = "DB, 会社コード, 廃車年月日 \n ";
        else
            str = map.get("廃車")+" \n ";
        
        str += db+","+company+","+date;
        map.put("廃車", str);
    }

    public void addLast(String db, String company, String date) {
        if(date.contains("-")) date = date.replace("-", "").substring(0,8);
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("最終更新日") == null)
            str = "DB, 会社コード, 最終更新日 \n ";
        else
            str = map.get("最終更新日")+" \n ";
        
        str += db+","+company+","+date.substring(0, 8);
        map.put("最終更新日", str);
    }

    public void addOwner(String db, String company, String date, String gyosyu, String id, String name) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("顧客") == null)
            str = "DB, 会社コード, 日付, 業種, 顧客ID, 顧客名 \n ";
        else
            str = map.get("顧客")+" \n ";
        
        str += db+","+company+","+date+","+gyosyu+","+id+","+name;
        map.put("顧客", str);
    }

    public void addSMR(String db, String company, String date, String smr) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("SMR") == null)
            str = "DB, 会社コード, 日付, SMR \n ";
        else
            str = map.get("SMR")+" \n ";
        
        str += db+","+company+","+date+","+smr;
        map.put("SMR", str);
    }

    public void addHistory(String date, String db, String company, String id) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("経歴") == null)
            str = "日付, DB, 会社コード, ID \n ";
        else
            str = map.get("経歴")+" \n ";
        
        str += date+","+db+","+company+","+id;
        map.put("経歴", str);
    }
    
    public void addOrder(String db, String company, String date, String sbnDate, String startDate, String finishDate,
                            String sbn, String hanbai, String status,
                            String kokyakuId, String kokyakuName, String keiyakuId, String keiyakuName, String id, String name, 
                            String keiyaku, String keiyakuSyubetu, String keiyaku_flg,
                            String step , String sijistep,
                            String komatsu, String ippan, String syanai, String gaiyo
                            ) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("受注") == null)
            str = "DB, 会社コード, 日付, 作番登録日, 実施予定日, 完了日, "
                    + "作番, 修・単, 作番ステータス, "
                    + "顧客ID, 顧客名, 契約顧客ID, 契約顧客名, 保有顧客ID, 保有顧客名, "
                    + "契約管理番号, 契約種別コード, 契約適用フラグ, "
                    + "工数, 指示工数, "
                    + "コマツ請求, 一般請求, 社内請求"
                    + "概要 \n ";
        else
            str = map.get("受注")+" \n ";
        
        str += db+","+company+","+date+","+sbnDate+","+startDate+","+finishDate+","+sbn+","+hanbai+","+status+","+
                kokyakuId+","+kokyakuName+","+keiyakuId+","+keiyakuName+","+id+","+name+","+
                keiyaku+","+keiyakuSyubetu+","+keiyaku_flg+","+
                step+","+sijistep+","+
                komatsu+","+ippan+","+syanai+","+gaiyo;
        map.put("受注", str);
    }
    
    public void addWork(String db, String company, String date, String sbn, String sg_id,  
                        String keitaiId, String keitaiName, 
                        String id, String name, String finish,
                        String suryo, String kingaku,
                        String gaichu, String gaichuKingaku, 
                        String step, String seikyuStep, String sijiStep, String jisekiStep) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("作業") == null)
            str = "DB, 会社コード, 実施予定日, 作番, 作業明細番号, "
                    + "作業形態コード, 作業形態名, "
                    + "作業コード, 作業名, 完了フラグ, "
                    + "数量, 標準金額, "
                    + "外注フラグ, 外注原価, "
                    + "標準工数, 請求工数, 指示工数, 実績累計工数 \n ";
        else
            str = map.get("作業")+" \n ";
        
        str += db+","+company+","+date+","+sbn+","+sg_id+","+
                keitaiId+","+keitaiName+","+
                id+","+name+","+finish+","+
                suryo+","+kingaku+","+
                gaichu+","+gaichuKingaku+","+
                step+","+seikyuStep+","+sijiStep+","+jisekiStep;
        map.put("作業", str);
    }
    
    public void addParts(String db, String company, String date, String sbn, String mid, String id, String name, String suryo, String cancel, String kingaku) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("部品") == null)
            str = "DB, 会社コード, 日付, 作番, 部品明細番号, 品番, 品名, 受注数量, キャンセル数量, 標準金額 \n ";
        else
            str = map.get("部品")+" \n ";
        
        str += db+","+company+","+date+","+sbn+","+mid+","+id+","+name+","+suryo+","+cancel+","+kingaku;
        map.put("部品", str);
    }

    public void addCountry(String db, String company, String date, String country) {
        if(errorCheck(date)) return ;
        
        String str = "";
        if(map.get("国") == null)
            str = "国コード \n ";
        else
            str = map.get("国")+" \n ";
        
        str += db+","+company+","+date+","+country;
        map.put("国", str);
    }
    
    //Komtrax
     //GPS
    public void addGPS(String db, String company, String date, String latitude, String longitude){
        String check_date = date.replace("/", "").substring(0,8);
        if(errorCheck(check_date)) return ;
        
        date = date.substring(0, 10)+" "+date.substring(11, date.length());
        
        String str = "";
        if(map.get("GPS") == null)
            str = "DB, 会社コード, 日付, 緯度, 経度 \n ";
        else
            str = map.get("GPS")+" \n ";
        
        str += db+","+company+","+date+","+latitude+","+longitude;
        map.put("GPS", str);
    }
    
     //SMR
    public void addKMSMR(String db, String company, String date, String smr){
        String check_date = date.replace("/", "").substring(0,8);
        if(errorCheck(check_date)) return ;
        
        date = date.substring(0, 10)+" "+date.substring(11, date.length());
        
        String str = "";
        if(map.get("KMSMR") == null)
            str = "DB, 会社コード, 日付, SMR \n ";
        else
            str = map.get("KMSMR")+" \n ";
        
        str += db+","+company+","+date+","+smr;
        map.put("KMSMR", str);
    }
    
    //ENGINE
    public void addKMEngine(String db, String company, String date, String engine){
        String check_date = date.replace("/", "").substring(0,8);
        if(errorCheck(check_date)) return ;
        
        date = date.substring(0, 10)+" "+date.substring(11, date.length());
        
        String str = "";
        if(map.get("KMENGINE") == null)
            str = "DB, 会社コード, 日付, エンジンスロットル \n ";
        else
            str = map.get("KMENGINE")+" \n ";
        
        str += db+","+company+","+date+","+engine;
        map.put("KMENGINE", str);
    }
    
    //ERROR
    public void addKMError(String db, String company, String date, String error_code, String error_cnt){
        String check_date = date.replace("/", "").substring(0,8);
        if(errorCheck(check_date)) return ;
        
        date = date.substring(0, 10)+" "+date.substring(11, date.length());
        
        String str = "";
        if(map.get("KMERROR") == null)
            str = "DB, 会社コード, 日付, エラーコード, カウント \n ";
        else
            str = map.get("KMERROR")+" \n ";
        
        str += db+","+company+","+date+","+error_code+","+error_cnt;
        map.put("KMERROR", str);
    }
    
    //CAUTION
    public void addKMCaution(String db, String company, String date, String caution_unit, String caution_map, String caution_cnt){
        String check_date = date.replace("/", "").substring(0,8);
        if(errorCheck(check_date)) return ;
        
        date = date.substring(0, 10)+" "+date.substring(11, date.length());
        
        String str = "";
        if(map.get("KMCAUTION") == null)
            str = "DB, 会社コード, 日付, エラーコード, カウント \n ";
        else
            str = map.get("KMCAUTION")+" \n ";
        
        str += db+","+company+","+date+","+caution_unit+","+caution_map+","+caution_cnt;
        map.put("KMCAUTION", str);
    }
    
    public String get(String id){
        return map.get(id);
    }
    
    public String getName() {
        return name;
    }

    public String getName2() {
        return name.split("-")[0] + "-" + name.split("-")[2];
    }
}
