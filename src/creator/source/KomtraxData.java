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
import json.SyaryoObjToJson;
import obj.SyaryoTemplate;

/**
 *
 * @author ZZ17390
 */
public class KomtraxData {
    private Connection con;
    private Map<String, SyaryoTemplate> syaryoMap;
    private Map<String, SyaryoTemplate> noneType;
    private String FILENAME;
    
    //KOMTRAX DATA
    public void addKomtrax(String FILENAME, Connection con, Map<String, SyaryoTemplate> syaryoMap, Map<String, SyaryoTemplate> noneType) {
        this.FILENAME = FILENAME;
        this.con = con;
        this.syaryoMap = syaryoMap;
        this.noneType = noneType;
        
        //JSON Writer
        SyaryoObjToJson json = new SyaryoObjToJson();
        
        //Map<String, SyaryoTemplate> komtraxMap = addGPS();
        //json.write(FILENAME.replace(".json", "_komtrax_gps.json"), komtraxMap);
        
        //Map<String, SyaryoTemplate> komtraxMap = addSMR();
        //json.write(FILENAME.replace(".json", "_komtrax_smr.json"), komtraxMap);
        
        //Map<String, SyaryoTemplate> komtraxMap = addEngine();
        //json.write(FILENAME.replace(".json", "_komtrax_engine.json"), komtraxMap);
        
        //Map<String, SyaryoTemplate> komtraxMap = addError();
        //json.write(FILENAME.replace(".json", "_komtrax_error.json"), komtraxMap);
        
        Map<String, SyaryoTemplate> komtraxMap = addCaution();
        json.write(FILENAME.replace(".json", "_komtrax_caution.json"), komtraxMap);
    }
    
    //GPS
    public Map<String, SyaryoTemplate> addGPS(){       
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
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_GPS.KIND.get());
                String type = res.getString(Komtrax.CW_GPS.TYPE.get());
                String kiban = res.getString(Komtrax.CW_GPS.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

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
                
                //GPS
                syaryo.addGPS(db, company, date, latitude, longitude);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    //SMR
    public Map<String, SyaryoTemplate> addSMR(){       
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
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_SERVICE_METER.KIND.get());
                String type = res.getString(Komtrax.CW_SERVICE_METER.TYPE.get());
                String kiban = res.getString(Komtrax.CW_SERVICE_METER.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

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
                
                //SMR
                syaryo.addKMSMR(db, company, date, smr);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    //Engine
    public Map<String, SyaryoTemplate> addEngine(){       
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
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_DAILY_THROTTLE.KIND.get());
                String type = res.getString(Komtrax.CW_DAILY_THROTTLE.TYPE.get());
                String kiban = res.getString(Komtrax.CW_DAILY_THROTTLE.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

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
                
                //Engine
                syaryo.addKMEngine(db, company, date, engine_th);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    //Error
    public Map<String, SyaryoTemplate> addError(){       
        Map map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_error_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_ERROR
            String sql = String.format("select %s,%s,%s, %s, %s, %s from %s",
                    Komtrax.CW_ERROR.KIND, Komtrax.CW_ERROR.TYPE, Komtrax.CW_ERROR.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_ERROR.ERROR_TIME,    //Error Date
                    Komtrax.CW_ERROR.ERROR_CODE,    //Error code
                    Komtrax.CW_ERROR.COUNT,
                    Komtrax.TABLE.CW_ERROR.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_ERROR.KIND.get());
                String type = res.getString(Komtrax.CW_ERROR.TYPE.get());
                String kiban = res.getString(Komtrax.CW_ERROR.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

                //Error
                String date = res.getString(Komtrax.CW_ERROR.ERROR_TIME.get());    //Error Date
                String error_code = res.getString(Komtrax.CW_ERROR.ERROR_CODE.get());    //Error
                String error_cnt = res.getString(Komtrax.CW_ERROR.COUNT.get());    //Error count

                //DB
                String db = "komtrax_error";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + error_code + "," + error_cnt);
                    continue;
                }
                
                //Error
                syaryo.addKMError(db, company, date, error_code, error_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
    
    //Caution
    public Map<String, SyaryoTemplate> addCaution(){       
        Map map = new TreeMap();

        try {
            PrintWriter errpw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_komtrax_caution_error.csv")))));
            
            Statement stmt = con.createStatement();

            //CW_CAUTION
            String sql = String.format("select %s,%s,%s, %s, %s, %s, %s from %s",
                    Komtrax.CW_CAUTION_DATA.KIND, Komtrax.CW_CAUTION_DATA.TYPE, Komtrax.CW_CAUTION_DATA.MACHINE_NUMBER, //Unique ID
                    Komtrax.CW_CAUTION_DATA.CAUTION_DATE,    //Error Date
                    Komtrax.CW_CAUTION_DATA.CAUTION_UNIT,    //Error code
                    Komtrax.CW_CAUTION_DATA.CAUTION_MAP,
                    Komtrax.CW_CAUTION_DATA.CAUTION_COUNT,
                    Komtrax.TABLE.CW_CAUTION_DATA.get()
            );
            System.out.println("Running: " + sql);

            ResultSet res = stmt.executeQuery(sql);

            int n = 0;
            while (res.next()) {
                n++;

                //Name
                String kisy = res.getString(Komtrax.CW_CAUTION_DATA.KIND.get());
                String type = res.getString(Komtrax.CW_CAUTION_DATA.TYPE.get());
                String kiban = res.getString(Komtrax.CW_CAUTION_DATA.MACHINE_NUMBER.get());

                //車両
                SyaryoTemplate syaryo = null;
                syaryo = syaryoMap.get(kisy + "-" + type + "-" + kiban);
                if((syaryo == null) && (noneType.get(kisy + "-" + kiban) != null))
                    syaryo = syaryoMap.get(noneType.get(kisy + "-" + kiban));

                //Caution
                String date = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_DATE.get());    //Caution Date
                String caution_unit = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_UNIT.get());    //Caution Unit
                String caution_map = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_MAP.get());    //Caution Map
                String caution_cnt = res.getString(Komtrax.CW_CAUTION_DATA.CAUTION_COUNT.get());    //Caution count

                //DB
                String db = "komtrax_caution";
                String company = "?"; //会社コード

                //車両チェック
                String name = kisy + "-" + type + "-" + kiban;
                if (syaryo == null) {
                    errpw.println(n + "," + name + "," + date + "," + db + "," + company + "," + caution_unit + "," + caution_map + "," + caution_cnt);
                    continue;
                }
                
                //Error
                syaryo.addKMCaution(db, company, date, caution_unit, caution_map, caution_cnt);
                
                
                //AddSyaryo
                map.put(syaryo.getName(), syaryo);

                if (n % 10000 == 0) {
                    System.out.println("Syaryo Processed : " + n);
                }
            }

            System.out.println("Total Processed Syaryo = " + n);
            System.out.println("Total Update Syaryo = " + map.size());
            
            errpw.close();
            
            return map;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
