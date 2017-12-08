/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.source;

import db.field.Komtrax;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.TreeMap;
import json.SyaryoTemplateToJson;
import creator.template.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class KomtraxData {
    private Connection con;
    private Map<String, SyaryoTemplate> noneType;
    private String FILENAME;
    
    //KOMTRAX DATA
    public void addKomtrax(String FILENAME, Connection con, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        this.FILENAME = FILENAME;
        this.con = con;
        this.noneType = noneType;
        
        //JSON Writer
        
        //addGPS(syaryoMap);
        //addSMR(syaryoMap);
        //addEngine(syaryoMap);
        addError(syaryoMap);
        //addCaution(syaryoMap);
        
    }
    
    //GPS
    public void addGPS(Map<String, SyaryoTemplate> syaryoMap){       
        Map map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_gps_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_GPS
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s",
                    Komtrax.CW_GPS.KIND, Komtrax.CW_GPS.TYPE, Komtrax.CW_GPS.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_GPS.GPS_TIME,    //GPS Date
                    Komtrax.CW_GPS.LATITUDE,    //緯度
                    Komtrax.CW_GPS.LONGITUDE,   //経度
                    Komtrax.TABLE.CW_GPS.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;
                
                //Name
                String kisy = res.getString(Komtrax.CW_GPS.KIND.get());
                String type = res.getString(Komtrax.CW_GPS.TYPE.get());
                String kiban = res.getString(Komtrax.CW_GPS.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //GPS
                String date = res.getString(Komtrax.CW_GPS.GPS_TIME.get());    //GPS Date
                String latitude = res.getString(Komtrax.CW_GPS.LATITUDE.get());    //緯度
                String longitude = res.getString(Komtrax.CW_GPS.LONGITUDE.get());   //経度

                //DB
                String db = "komtrax_gps";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + latitude + "," + longitude);
                    continue;
                }
                
                m++;
                
                //GPS
                syaryo.addGPS(db, company, date, latitude, longitude);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_gps.json"), map);
            
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //SMR
    public void addSMR(Map<String, SyaryoTemplate> syaryoMap){       
        Map map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_smr_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_SERVICE_METER
            String sql = String.format("select %s,%s,%s, %s, %s from %s",
                    Komtrax.CW_SERVICE_METER.KIND, Komtrax.CW_SERVICE_METER.TYPE, Komtrax.CW_SERVICE_METER.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_SERVICE_METER.SMR_TIME,    //SMR Date
                    Komtrax.CW_SERVICE_METER.SMR_VALUE,    //SMR
                    Komtrax.TABLE.CW_SERVICE_METER.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_SERVICE_METER.KIND.get());
                String type = res.getString(Komtrax.CW_SERVICE_METER.TYPE.get());
                String kiban = res.getString(Komtrax.CW_SERVICE_METER.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //SMR
                String date = res.getString(Komtrax.CW_SERVICE_METER.SMR_TIME.get());        //SMR Date
                String smr = res.getString(Komtrax.CW_SERVICE_METER.SMR_VALUE.get());   //SMR

                //DB
                String db = "komtrax_smr";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + smr);
                    continue;
                }
                
                m++;
                
                //SMR
                syaryo.addKMSMR(db, company, date, smr);
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_smr.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Engine
    public void addEngine(Map<String, SyaryoTemplate> syaryoMap){       
        Map map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_engine_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_DAILY_THROTTLE
            String sql = String.format("select %s,%s,%s, %s, %s from %s",
                    Komtrax.CW_DAILY_THROTTLE.KIND, Komtrax.CW_DAILY_THROTTLE.TYPE, Komtrax.CW_DAILY_THROTTLE.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_DAILY_THROTTLE.THROTTLE_DATE,    //Engine Date
                    Komtrax.CW_DAILY_THROTTLE.AVE_THROTTLE_RATE,    //Engine
                    Komtrax.TABLE.CW_DAILY_THROTTLE.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_DAILY_THROTTLE.KIND.get());
                String type = res.getString(Komtrax.CW_DAILY_THROTTLE.TYPE.get());
                String kiban = res.getString(Komtrax.CW_DAILY_THROTTLE.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //Engine
                String date = res.getString(Komtrax.CW_DAILY_THROTTLE.THROTTLE_DATE.get());    //Engine Date
                String engine_th = res.getString(Komtrax.CW_DAILY_THROTTLE.AVE_THROTTLE_RATE.get());    //Engine

                //DB
                String db = "komtrax_engine";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + engine_th);
                    continue;
                }
                
                m++;
                
                //Engine
                syaryo.addKMEngine(db, company, date, engine_th);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_engine.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Error
    public void addError(Map<String, SyaryoTemplate> syaryoMap){       
        Map map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_error_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_ERROR
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s from %s",
                    Komtrax.CW_ERROR.KIND, Komtrax.CW_ERROR.TYPE, Komtrax.CW_ERROR.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_ERROR.ERROR_TIME,    //Error Date
                    Komtrax.CW_ERROR.ERROR_CODE,    //Error code
                    Komtrax.CW_ERROR.ERROR_KIND,    //Error Kind
                    Komtrax.CW_ERROR.COUNT,
                    Komtrax.TABLE.CW_ERROR.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_ERROR.KIND.get());
                String type = res.getString(Komtrax.CW_ERROR.TYPE.get());
                String kiban = res.getString(Komtrax.CW_ERROR.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //Error
                String date = res.getString(Komtrax.CW_ERROR.ERROR_TIME.get());    //Error Date
                String error_kind = res.getString(Komtrax.CW_ERROR.ERROR_KIND.get());    //Error
                String error_code = res.getString(Komtrax.CW_ERROR.ERROR_CODE.get());    //Error
                String error_cnt = res.getString(Komtrax.CW_ERROR.COUNT.get());    //Error count

                //DB
                String db = "komtrax_error";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," 
                                    + error_code + "," + error_kind + "," + error_cnt);
                    continue;
                }
                
                m++;
                
                //Error
                syaryo.addKMError(db, company, date, error_code, error_kind, error_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_error.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Caution
    public void addCaution(Map<String, SyaryoTemplate> syaryoMap){       
        Map map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_caution_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_CAUTION
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s",
                    Komtrax.CW_CAUTION_DATA.KIND, Komtrax.CW_CAUTION_DATA.TYPE, Komtrax.CW_CAUTION_DATA.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_CAUTION_DATA.CAUTION_DATE,    //Caution Date
                    Komtrax.CW_CAUTION_DATA.ICON_CODE,  //Caution Icon code
                    Komtrax.CW_CAUTION_DATA.CAUTION_COUNT,
                    Komtrax.TABLE.CW_CAUTION_DATA.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_CAUTION_DATA.KIND.get());
                String type = res.getString(Komtrax.CW_CAUTION_DATA.TYPE.get());
                String kiban = res.getString(Komtrax.CW_CAUTION_DATA.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if(syaryo != null){
                    syaryo = new SyaryoTemplate(syaryo.getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }else if((noneType.get(kisy + "-" + kiban) != null)){
                    syaryo = new SyaryoTemplate(syaryoMap.get(noneType.get(kisy + "-" + kiban)).getName());
                    if(map.get(syaryo.name) != null) syaryo = (SyaryoTemplate) map.get(syaryo.name);
                }

                //Caution
                String date = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_DATE.get());    //Caution Date
                String caution_icon = res.getString(Komtrax.CW_CAUTION_DATA.ICON_CODE.get());    //Caution Icon Code
                String caution_cnt = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_COUNT.get());    //Caution count

                //DB
                String db = "komtrax_caution";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + caution_icon + "," + caution_cnt);
                    continue;
                }
                
                m++;
                
                //Caution
                syaryo.addKMCaution(db, company, date, caution_icon, caution_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_caution.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
}
