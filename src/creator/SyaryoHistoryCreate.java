/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import db.HiveDB;
import field.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoObjToJson;
import obj.SyaryoObject;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class SyaryoHistoryCreate extends HiveDB {

    private static final String FILENAME = "syaryo_history_template.json";

    public static void main(String[] args) throws IOException {
        SyaryoHistoryCreate syaryoHistory = new SyaryoHistoryCreate();
        SyaryoObjToJson json = new SyaryoObjToJson();
        
        //Error File
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_error.csv")))));
        
        Map<String, SyaryoTemplate> syaryoMap;
        
        //DB接続
        Connection con = getConnection(); //HiveDB
        
        //テンプレート作成
        //syaryoMap = syaryoHistory.createTemplate(con);
        //json.write(FILENAME, syaryoMap);
        
        //テンプレート作成後に実行
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        syaryoMap = obj.reader(FILENAME);
        
        //型が無いときのインデックス
        Map noneTypeSearch = new HashMap();
        for (SyaryoTemplate syaryo : syaryoMap.values())
            noneTypeSearch.put(syaryo.getName2(), syaryo.getName());
        
        //EQP経歴情報
        syaryoMap = syaryoHistory.addSyaryoKeireki(con, pw, syaryoMap, noneTypeSearch);

        //車両マスタ
        syaryoMap = syaryoHistory.addSyaryoCategory(con, pw, syaryoMap, noneTypeSearch);
        
        //保存
        pw.close();
        json.write(FILENAME.replace("template", "eqp"), syaryoMap);
    }

    public Map<String, SyaryoTemplate> createTemplate(Connection con) {

        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s from %s",
                    EQP.Syaryo.KISY, EQP.Syaryo.TYP, EQP.Syaryo.KIBAN, //Unique ID
                    EQP.Syaryo.MNF_DATE, //生産日
                    EQP.Syaryo.PLANT,   //生産工場(catalog)
                    EQP.Syaryo.SHIP_DATE, //出荷日
                    EQP.Syaryo.DELI_CTG, //中古納入区分
                    EQP.Syaryo.LTST_DELI_DATE, //最新納入日
                    EQP.Syaryo.SCRAP_DATE,  //廃車日
                    EQP.Syaryo.CUST_CD,    //顧客コード
                    EQP.Syaryo.CUST_NM,     //顧客名
                    EQP.Syaryo.DB,          //代理店
                    EQP.Syaryo.LTST_SMR_DATE,   //最新SMR日付
                    EQP.Syaryo.LTST_SMR,        //SMR
                    TABLE.EQP_SYARYO
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();
            int n = 0;
            while (res.next()) {
                n++;
                //Name
                String kisy = res.getString(EQP.Syaryo.KISY.get());
                String type = res.getString(EQP.Syaryo.TYP.get());
                String kiban = res.getString(EQP.Syaryo.KIBAN.get());

                //Date
                String mnf_date = res.getString(EQP.Syaryo.MNF_DATE.get());
                String ship_date = res.getString(EQP.Syaryo.SHIP_DATE.get());
                String used_date = res.getString(EQP.Syaryo.LTST_DELI_DATE.get());
                String scrap_date = res.getString(EQP.Syaryo.SCRAP_DATE.get());
                String smr_date = res.getString(EQP.Syaryo.LTST_SMR_DATE.get());
                
                //Plant
                String plant = res.getString(EQP.Syaryo.PLANT.get());
                
                //Used
                String used_flg = res.getString(EQP.Syaryo.DELI_CTG.get());
                
                //Customer
                String cid = res.getString(EQP.Syaryo.CUST_CD.get());
                String cname = res.getString(EQP.Syaryo.CUST_NM.get());
                
                //DB
                String db = "eqp_syaryo";
                String company = res.getString(EQP.Syaryo.DB.get());
                if(company.length() > 1)
                    company = company.substring(0,2);
                else
                    company = "??";
                
                //SMR
                String smr = res.getString(EQP.Syaryo.LTST_SMR.get());
                
                //Syaryo Template
                SyaryoTemplate syaryo = new SyaryoTemplate(kisy, type, kiban);
                syaryo.addBorn(mnf_date, plant);
                syaryo.addDeploy(ship_date);
                syaryo.addDead(db, company, scrap_date);
                if(used_flg.equals("1")){
                    syaryo.addUsed(db, company, used_date, "-1", "-1", "-1");
                    syaryo.addOwner(db, company, used_date, "-1", cid, cname);
                }
                syaryo.addSMR(db, company, smr_date, smr);
                
                syaryoMap.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Created SyaryoTemplate = " + n);

            return syaryoMap;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
    
    public Map<String, SyaryoTemplate> addSyaryoKeireki(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) throws IOException{
        try {
            Statement stmt = con.createStatement();

            //EQP_Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s from %s",
                    EQP.Keireki.KISY, EQP.Keireki.TYP, EQP.Keireki.KIBAN, //Unique ID
                    EQP.Keireki.HIS_DATE, //経歴日
                    EQP.Keireki.HIS_INFO_CD,   //経歴コード
                    EQP.Keireki.HIS_SMR,    //SMR
                    EQP.Keireki.CNTRY,      //国コード
                    EQP.Keireki.CUST_CD,    //顧客コード
                    EQP.Keireki.CUST_NM,     //顧客名
                    EQP.Keireki.DB,          //代理店
                    TABLE.EQP_KEIREKI
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);
            
            Map<String, SyaryoTemplate> map = new TreeMap<>();
            
            int n = 0;
            while (res.next()) {
                n++;
                
                //Name
                String kisy = res.getString(EQP.Keireki.KISY.get());
                String type = res.getString(EQP.Keireki.TYP.get());
                String kiban = res.getString(EQP.Keireki.KIBAN.get());
                
                //Date
                String date = res.getString(EQP.Keireki.HIS_DATE.get());
                
                //Kireki
                String id = res.getString(EQP.Keireki.HIS_INFO_CD.get());
                
                //Customer
                String cid = res.getString(EQP.Keireki.CUST_CD.get());
                String cname = res.getString(EQP.Keireki.CUST_NM.get());
                
                //Country
                String country = res.getString(EQP.Keireki.CNTRY.get());
                
                //SMR
                String smr = res.getString(EQP.Keireki.HIS_SMR.get());
                
                //DB
                String db = "eqp_keireki";
                String company = res.getString(EQP.Keireki.DB.get());
                if(company.length() > 1)
                    company = company.substring(0,2);
                else
                    company = "??";
                
                //車両チェック
                String name = kisy+"-"+type+"-"+kiban;
                SyaryoTemplate syaryo = null;
                if(type.equals(""))
                    syaryo = syaryoMap.get(noneType.get(kisy+"-"+kiban));
                else
                    syaryo = syaryoMap.get(kisy+"-"+type+"-"+kiban);
                if(syaryo == null){
                    errpw.println(n+","+name+","+date+","+db+","+company+","+cid+","+cname+","+country);
                    continue;
                }
                
                //Add SyaryoTemplate
                syaryo.addHistory(date, db, company, id);
                syaryo.addOwner(db, company, date, "-1", cid, cname);
                syaryo.addSMR(db, company, date, smr);
                syaryo.addCountry(db, company, date, country);
                
                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
                
                map.put(syaryo.getName(), syaryo);
            }
            
            System.out.println("Total Processed Syaryo = " + n);
            
            return map;
            
        }catch(SQLException sqle){
            sqle.printStackTrace();
            return null;
        }
    }
    
    public Map<String, SyaryoTemplate> addSyaryoCategory(Connection con, PrintWriter errpw, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {

        try {
            Statement stmt = con.createStatement();

            //Syaryo
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s, %s, %s, %s, %s from %s",
                    Syaryo._Syaryo.KISY, Syaryo._Syaryo.TYP, Syaryo._Syaryo.KIBAN, //Unique ID
					Syaryo._Syaryo.KSYCD,				
                    Syaryo._Syaryo.SEHN_BNR_CD_B,   //製品分類コード B
                    Syaryo._Syaryo.NU_KBN,          //NU区分
                    Syaryo._Syaryo.NNY_YMD,         //納入年月日
                    Syaryo._Syaryo.HY_KKYKCD,       //保有顧客
                    Syaryo._Syaryo.SYRK_KBN,        //車歴
                    Syaryo._Syaryo.SYRK_YMD,        //車歴変更日付
					Syaryo._Syaryo.KOMTRX_SOTY_KBN,
					Syaryo._Syaryo.LAST_UPD_DAYT,
                    TABLE.SYARYO
            );
            System.out.println("Running: " + sql);
            
			ResultSet res = stmt.executeQuery(sql);
            
			Map<String, SyaryoTemplate> map = new TreeMap<>();
			
	int n = 0;
            while (res.next()) {
                n++;
				
                //Name
                String kisy     = res.getString(Syaryo._Syaryo.KISY.get());
                String type     = res.getString(Syaryo._Syaryo.TYP.get());
                String kiban    = res.getString(Syaryo._Syaryo.KIBAN.get());
               
				//車両
                SyaryoTemplate syaryo = null;
                if(type.equals(""))
                    syaryo = syaryoMap.get(noneType.get(kisy+"-"+kiban));
                else
                    syaryo = syaryoMap.get(kisy+"-"+type+"-"+kiban);
                
                //車両マスタ
                String category = res.getString(Syaryo._Syaryo.SEHN_BNR_CD_B.get());
                String nu       = res.getString(Syaryo._Syaryo.NU_KBN.get());
                
				//Code
                String syareki  = res.getString(Syaryo._Syaryo.SYRK_KBN.get());
					String komtrax = res.getString(Syaryo._Syaryo.KOMTRX_SOTY_KBN.get());
				
				//Customer
				String cid = res.getString(Syaryo._Syaryo.HY_KKYKCD.get());
				
				//Date
				String nounyu   = res.getString(Syaryo._Syaryo.NNY_YMD.get());
				String syareki_date = res.getString(Syaryo._Syaryo.SYRK_YMD.get());
				String last_date = res.getString(Syaryo._Syaryo.LAST_UPD_DAYT.get());
                
				//DB
				String db = "syaryo";
				String company = res.getString(Syaryo._Syaryo.KSYCD.get());
			
				 //車両チェック
		String name = kisy+"-"+type+"-"+kiban;
                if(syaryo == null){
                    errpw.println(n+","+name+","+last_date+","+db+","+company+","+cid+","+komtrax+","+syareki_date+","+syareki);
                    continue;
                }
                
                //Spec
				syaryo.addSpec(komtrax, category);
		//NU
                if(nu.equals("N")){	
				syaryo.addNew(db, company, nounyu, "-1", "-1", "-1");
				syaryo.addOwner(db, company, nounyu, "-1", cid, "?");
                }else if(nu.equals("U")){
                    syaryo.addUsed(db, company, nounyu, "-1", "-1", "-1");
                    syaryo.addOwner(db, company, nounyu, "-1", cid, "?");
                }
                
				//History
                syaryo.addHistory(syareki_date, db, company, syareki);
				
				//AddSyaryo
				map.put(syaryo.getName(), syaryo);
                
                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        }
    }
}
