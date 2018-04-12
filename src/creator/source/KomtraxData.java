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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ZZ17390
 */
public class KomtraxData {
    private static List nonUpdateSyaryoList;
    private Connection con;
    private String FILENAME;
    private String machine;
    
    //KOMTRAX DATA
    public void addKomtrax(String FILENAME, Connection con, String machine, Map<String, SyaryoTemplate> syaryoMap) {
        this.FILENAME = FILENAME;
        this.con = con;
        this.machine = machine;
        
        //JSON Writer
        
        addGPS(syaryoMap);
        //System.out.println("Komtrax GPS not update List:"+dataCheck());
        addSMR(syaryoMap);
        //System.out.println("Komtrax SMR not update List:"+dataCheck());
        addEngine(syaryoMap);
        //System.out.println("Komtrax Engine not update List:"+dataCheck());
        addError(syaryoMap);
        //System.out.println("Komtrax Error not update List:"+dataCheck());
        addCaution(syaryoMap);
        //System.out.println("Komtrax Caution not update List:"+dataCheck());
        addFuel(syaryoMap);
        //System.out.println("Komtrax Fuel not update List:"+dataCheck());
        addAct(syaryoMap);
        //System.out.println("Komtrax Act not update List:"+dataCheck());
    }
    
    //GPS
    public void addGPS(Map<String, SyaryoTemplate> syaryoMap){       
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_gps_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_GPS
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_GPS.KISY, Komtrax.CW_GPS.TYP, Komtrax.CW_GPS.KIBAN, //Unique ID
                    Komtrax.CW_GPS.GPS_TIME,    //GPS Date
                    Komtrax.CW_GPS.LATITUDE,    //緯度
                    Komtrax.CW_GPS.LONGITUDE,   //経度
                    Komtrax.TABLE.CW_GPS.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;
                
                //Name
                String kisy = res.getString(Komtrax.CW_GPS.KISY.get());
                String type = res.getString(Komtrax.CW_GPS.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_GPS.KIBAN.get());

                //GPS
                String date = res.getString(Komtrax.CW_GPS.GPS_TIME.get());    //GPS Date
                String latitude = res.getString(Komtrax.CW_GPS.LATITUDE.get());    //緯度
                String longitude = res.getString(Komtrax.CW_GPS.LONGITUDE.get());   //経度

                //DB
                String db = "komtrax_gps";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + latitude + "," + longitude);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
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
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_gps.json"), map);
            
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //SMR
    public void addSMR(Map<String, SyaryoTemplate> syaryoMap){      
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_smr_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_SERVICE_METER
            String sql = String.format("select %s,%s,%s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_SERVICE_METER.KISY, Komtrax.CW_SERVICE_METER.TYP, Komtrax.CW_SERVICE_METER.KIBAN, //Unique ID
                    Komtrax.CW_SERVICE_METER.SMR_TIME,    //SMR Date
                    Komtrax.CW_SERVICE_METER.SMR_VALUE,    //SMR
                    Komtrax.TABLE.CW_SERVICE_METER.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_SERVICE_METER.KISY.get());
                String type = res.getString(Komtrax.CW_SERVICE_METER.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_SERVICE_METER.KIBAN.get());

                //SMR
                String date = res.getString(Komtrax.CW_SERVICE_METER.SMR_TIME.get());        //SMR Date
                String smr = res.getString(Komtrax.CW_SERVICE_METER.SMR_VALUE.get());   //SMR

                //DB
                String db = "komtrax_smr";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + smr);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
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
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_smr.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Engine
    public void addEngine(Map<String, SyaryoTemplate> syaryoMap){
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();
        
        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_engine_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_DAILY_THROTTLE
            String sql = String.format("select %s,%s,%s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_DAILY_THROTTLE.KISY, Komtrax.CW_DAILY_THROTTLE.TYP, Komtrax.CW_DAILY_THROTTLE.KIBAN, //Unique ID
                    Komtrax.CW_DAILY_THROTTLE.THROTTLE_DATE,    //Engine Date
                    Komtrax.CW_DAILY_THROTTLE.AVE_THROTTLE_RATE,    //Engine
                    Komtrax.TABLE.CW_DAILY_THROTTLE.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_DAILY_THROTTLE.KISY.get());
                String type = res.getString(Komtrax.CW_DAILY_THROTTLE.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_DAILY_THROTTLE.KIBAN.get());

                //Engine
                String date = res.getString(Komtrax.CW_DAILY_THROTTLE.THROTTLE_DATE.get());    //Engine Date
                String engine_th = res.getString(Komtrax.CW_DAILY_THROTTLE.AVE_THROTTLE_RATE.get());    //Engine

                //DB
                String db = "komtrax_engine";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + engine_th);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
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
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_engine.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Error
    public void addError(Map<String, SyaryoTemplate> syaryoMap){
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_error_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_ERROR
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_ERROR.KISY, Komtrax.CW_ERROR.TYP, Komtrax.CW_ERROR.KIBAN, //Unique ID
                    Komtrax.CW_ERROR.ERROR_TIME,    //Error Date
                    Komtrax.CW_ERROR.ERROR_CODE,    //Error code
                    Komtrax.CW_ERROR.ERROR_KIND,    //Error Kind
                    Komtrax.CW_ERROR.COUNT,
                    Komtrax.TABLE.CW_ERROR.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m=0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_ERROR.KISY.get());
                String type = res.getString(Komtrax.CW_ERROR.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_ERROR.KIBAN.get());

                //Error
                String date = res.getString(Komtrax.CW_ERROR.ERROR_TIME.get());    //Error Date
                String error_kind = res.getString(Komtrax.CW_ERROR.ERROR_KIND.get());    //Error
                String error_code = res.getString(Komtrax.CW_ERROR.ERROR_CODE.get());    //Error
                String error_cnt = res.getString(Komtrax.CW_ERROR.COUNT.get());    //Error count

                //DB
                String db = "komtrax_error";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," 
                                    + error_code + "," + error_kind + "," + error_cnt);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
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
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_error.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Caution
    public void addCaution(Map<String, SyaryoTemplate> syaryoMap){
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_caution_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_CAUTION
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_CAUTION_DATA.KISY, Komtrax.CW_CAUTION_DATA.TYP, Komtrax.CW_CAUTION_DATA.KIBAN, //Unique ID
                    Komtrax.CW_CAUTION_DATA.CAUTION_DATE,    //Caution Date
                    Komtrax.CW_CAUTION_DATA.ICON_CODE,  //Caution Icon code
                    Komtrax.CW_CAUTION_DATA.CAUTION_COUNT,
                    Komtrax.TABLE.CW_CAUTION_DATA.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_CAUTION_DATA.KISY.get());
                String type = res.getString(Komtrax.CW_CAUTION_DATA.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_CAUTION_DATA.KIBAN.get());

                //Caution
                String date = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_DATE.get());    //Caution Date
                String caution_icon = res.getString(Komtrax.CW_CAUTION_DATA.ICON_CODE.get());    //Caution Icon Code
                String caution_cnt = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_COUNT.get());    //Caution count

                //DB
                String db = "komtrax_caution";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + caution_icon + "," + caution_cnt);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
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
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_caution.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Fuel
    public void addFuel(Map<String, SyaryoTemplate> syaryoMap){
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_fuelconsume_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_CAUTION
            String sql = String.format("select %s,%s,%s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_DAILY_FUEL_CONSUME.KISY, Komtrax.CW_DAILY_FUEL_CONSUME.TYP, Komtrax.CW_DAILY_FUEL_CONSUME.KIBAN, //Unique ID
                    Komtrax.CW_DAILY_FUEL_CONSUME.CONSUME_DATE,    //Caution Date
                    Komtrax.CW_DAILY_FUEL_CONSUME.CONSUME_COUNT,
                    Komtrax.TABLE.CW_DAILY_FUEL_CONSUME.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_DAILY_FUEL_CONSUME.KISY.get());
                String type = res.getString(Komtrax.CW_DAILY_FUEL_CONSUME.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_DAILY_FUEL_CONSUME.KIBAN.get());

                //Fuel Consume
                String date = res.getString(Komtrax.CW_DAILY_FUEL_CONSUME.CONSUME_DATE.get());    //Consume Date
                String consume_cnt = res.getString(Komtrax.CW_DAILY_FUEL_CONSUME.CONSUME_COUNT.get());    //Fuel Consume

                //DB
                String db = "komtrax_fuelconsume";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + consume_cnt);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
                m++;
                
                //Caution
                syaryo.addKMFuelConsume(db, company, date, consume_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_fuelconsume.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    //Act
    public void addAct(Map<String, SyaryoTemplate> syaryoMap){
        nonUpdateSyaryoList = new ArrayList();
        Map<String, SyaryoTemplate> map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_actdata_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_CAUTION
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s where kisy='%s'",
                    Komtrax.CW_ACT_DATA.KISY, Komtrax.CW_ACT_DATA.TYP, Komtrax.CW_ACT_DATA.KIBAN, //Unique ID
                    Komtrax.CW_ACT_DATA.ACT_DATE,    //Caution Date
                    Komtrax.CW_ACT_DATA.ACT_COUNT,
                    Komtrax.CW_ACT_DATA.DAILY_UNIT,
                    Komtrax.TABLE.CW_ACT_DATA.get(),
                    machine
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            int m = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_ACT_DATA.KISY.get());
                String type = res.getString(Komtrax.CW_ACT_DATA.TYP.get());
                String s_type = "";
                String kiban = res.getString(Komtrax.CW_ACT_DATA.KIBAN.get());

                //Act Data
                String date = res.getString(Komtrax.CW_ACT_DATA.ACT_DATE.get());    //Act Date
                String act_cnt = res.getString(Komtrax.CW_ACT_DATA.ACT_COUNT.get());    //Act Count
                String unit = res.getString(Komtrax.CW_ACT_DATA.DAILY_UNIT.get());    //Daily Unit
                
                //DB
                String db = "komtrax_actdata";
                String company = "?"; //会社コード

                //車両チェック
                String name = SyaryoTemplate.check(kisy, type, s_type, kiban);
                if (name == null || SyaryoTemplate.errorCheck(date)) {
                    errpw.println(n + "," + SyaryoTemplate.getName(kisy, type, s_type, kiban) + "," + date + "," + db + "," + company + "," + act_cnt + "," + unit);
                    continue;
                }
                
                //車両
                SyaryoTemplate syaryo;
                if(map.get(name) == null)
                    syaryo = syaryoMap.get(name).clone();
                else
                    syaryo = map.get(name);
                
                m++;
                
                //Act
                act_cnt = String.valueOf(Integer.parseInt(act_cnt) / Integer.parseInt(unit));
                syaryo.addKMAct(db, company, date, act_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = "+  m + "/" + n);
            System.out.println("Total Created SyaryoTemplate = " + map.size() + "/" + syaryoMap.size());
            
            for(String name : syaryoMap.keySet())
                if(map.get(name) == null)
                    nonUpdateSyaryoList.add(name);
            
            errpw.close();
            
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_komtrax_actdata.json"), map);
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        } catch (IOException ex) {
        }
    }
    
    public static List dataCheck(){
        return nonUpdateSyaryoList;
    }
}
