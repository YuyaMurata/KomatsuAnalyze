/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.source.*;
import db.HiveDB;
import java.io.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoTemplate;
import json.SyaryoTemplateToJson;
import creator.template.SyaryoTemplate;

/**
 * DBから車両テンプレートを作成する
 *
 * @author ZZ17390
 */
public class SyaryoTemplateCreate extends HiveDB {

    private static final String FILENAME = "..\\KomatsuData\\車両テンプレート\\syaryo_history_template.json";

    public static void main(String[] args) throws IOException {
        //DB接続
        Connection con = getConnection(); //HiveDB

        //テンプレート生成
        //template(con);
        Map<String, SyaryoTemplate> syaryoTemplate = readTempate();

        //KOMPAS車両データの読み込み
        //Map<String, SyaryoTemplate> syaryoMap = syaryo(con, syaryoTemplate);

        //KOMPAS車両マスタとテンプレートのマージ。 消せば工場データが中心となる。
        //syaryoTemplate = merge(syaryoTemplate, syaryoMap);

        //データ入力
        
        //EQP
        //eqp_syaryo(con, syaryoTemplate);
        //eqp_spec(con, syaryoTemplate);
        //eqp_keireki(con, syaryoTemplate);
        
        //KOMPAS
        //service_s(con, syaryoTemplate);
        //service_t(con, syaryoTemplate);
        //order(con, syaryoTemplate);
        work(con, syaryoTemplate);
        parts(con, syaryoTemplate);
        sell(con, syaryoTemplate);
        sell_used(con, syaryoTemplate);
        
        //KOSMIC
        sell_old(con, syaryoTemplate);
        
        //KOMTRAX
        //komtrax(con, syaryoTemplate);*/
    }

    public static void template(Connection con) {
        //テンプレート作成
        Map<String, SyaryoTemplate> syaryoTemplate = new CreateSyaryoTemplate().createTemplate(con);
        new SyaryoTemplateToJson().write(FILENAME, syaryoTemplate);
    }

    public static Map<String, SyaryoTemplate> readTempate() {
        //テンプレートの読み込み
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        return obj.reader(FILENAME);
    }

    //KOMPAS車両に絞込み
    public static Map<String, SyaryoTemplate> merge(Map<String, SyaryoTemplate> eqp_syaryo, Map<String, SyaryoTemplate> kom_syaryo) {
        TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();

        for (String name : eqp_syaryo.keySet()) {
            if (kom_syaryo.get(name) != null) {
                SyaryoTemplate s = kom_syaryo.get(name);
                syaryoMap.put(name, new SyaryoTemplate(s.kisy, s.type, s.s_type, s.kiban));
            }
        }

        new SyaryoTemplateToJson().write(FILENAME, syaryoMap);

        return syaryoMap;
    }

    public static Map<String, SyaryoTemplate> syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        //車両マスタ
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_syaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SyaryoData().addSyaryoCategory(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_syaryo.json"), syaryoMap);

            return syaryoMap;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //EQP車両
    public static void eqp_syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpsyaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSyaryoData().addEQPSyaryo(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpsyaryo.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP仕様
    public static void eqp_spec(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpspec_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSpec().addEQPSpec(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpspec.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP経歴
    public static void eqp_keireki(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpkeireki_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPKeirekiData().addSyaryoKeireki(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpkeireki.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_単販
    public static void service_t(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service1123_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, 1, 1, 1, 2, 3);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_service1123.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_修販            
    public static void service_s(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        //一般請求
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service2111_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, 1, 2, 1, 1, 1);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_service2111.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        //コマツ請求
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service2112_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, 1, 2, 1, 1, 2);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_service2112.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //受注
    public static void order(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order111_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, 1, 1, 1);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_order111.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order112_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, 1, 1, 2);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_order112.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order123_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, 1, 2, 3);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_order123.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //作業明細
    public static void work(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_workinfo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new WorkData().addWork(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_workinfo.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //部品明細
    public static void parts(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_parts_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new PartsData().addParts(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_parts.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上
    public static void sell(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addSell(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //中古売上
    public static void sell_used(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_used_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addUsed(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell_used.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上(OLD)
    public static void sell_old(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_old_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addOld(con, pw, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell_old.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //Komtrax
    public static void komtrax(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        new KomtraxData().addKomtrax(FILENAME, con, syaryoTemplate);
    }
}
