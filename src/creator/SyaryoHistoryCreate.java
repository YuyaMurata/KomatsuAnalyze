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
import obj.SyaryoTemplate;

/**
 * DBから車両テンプレートを作成する
 *
 * @author ZZ17390
 */
public class SyaryoHistoryCreate extends HiveDB {

    private static final String FILENAME = "syaryo_history_template.json";

    public static void main(String[] args) throws IOException {
        //DB接続
        Connection con = getConnection(); //HiveDB

        //テンプレート生成
        template(con);
        Map<String, SyaryoTemplate> syaryoTemplate = readTempate();

        //インデックス作成
        Map noneTypeSearch = createNoneTypeSearch(syaryoTemplate);

        //KOMPAS車両データの読み込み
        Map<String, SyaryoTemplate> syaryoMap = syaryo(con, syaryoTemplate, noneTypeSearch);

        //KOMPAS車両マスタとテンプレートのマージ。 消せば工場データが中心となる。
        syaryoTemplate = merge(syaryoTemplate, syaryoMap);

        //インデックス再生成
        noneTypeSearch = createNoneTypeSearch(syaryoTemplate);

        //データ入力
        
        //EQP
        eqp_syaryo(con, syaryoTemplate, noneTypeSearch);
        
        System.exit(0);
        eqp_spec(con, syaryoTemplate, noneTypeSearch);
        eqp_keireki(con, syaryoTemplate, noneTypeSearch);
        
        //KOMPAS
        service_s(con, syaryoTemplate, noneTypeSearch);
        service_t(con, syaryoTemplate, noneTypeSearch);
        order(con, syaryoTemplate, noneTypeSearch);
        work(con, syaryoTemplate, noneTypeSearch);
        parts(con, syaryoTemplate, noneTypeSearch);
        sell(con, syaryoTemplate, noneTypeSearch);
        sell_used(con, syaryoTemplate, noneTypeSearch);
        
        //KOSMIC
        sell_old(con, syaryoTemplate, noneTypeSearch);
        
        //KOMTRAX
        komtrax(con, syaryoTemplate, noneTypeSearch);

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

    public static Map createNoneTypeSearch(Map<String, SyaryoTemplate> syaryoTemplate) {
        Map noneTypeSearch = new HashMap();
        for (SyaryoTemplate syaryo : syaryoTemplate.values()) {
            noneTypeSearch.put(syaryo.getName2(), syaryo.getName());
        }
        return noneTypeSearch;
    }

    //KOMPAS車両に絞込み
    public static Map<String, SyaryoTemplate> merge(Map<String, SyaryoTemplate> eqp_syaryo, Map<String, SyaryoTemplate> kom_syaryo) {
        TreeMap<String, SyaryoTemplate> syaryoMap = new TreeMap();

        for (String name : eqp_syaryo.keySet()) {
            if (kom_syaryo.get(name) != null) {
                syaryoMap.put(name, new SyaryoTemplate(name));
            }
        }

        new SyaryoTemplateToJson().write(FILENAME, syaryoMap);

        return syaryoMap;
    }

    public static Map<String, SyaryoTemplate> syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        //車両マスタ
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_syaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SyaryoData().addSyaryoCategory(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_syaryo.json"), syaryoMap);

            return syaryoMap;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //EQP車両
    public static void eqp_syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpsyaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSyaryoData().addEQPSyaryo(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpsyaryo.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP仕様
    public static void eqp_spec(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpspec_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSpec().addEQPSpec(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpspec.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP経歴
    public static void eqp_keireki(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpkeireki_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPKeirekiData().addSyaryoKeireki(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_eqpkeireki.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_単販
    public static void service_t(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service1_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, noneTypeSearch, "=1", "=1");
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_service1.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_修販            
    public static void service_s(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service2_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, noneTypeSearch, "=1", "=2");
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_service2.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //受注
    public static void order(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_order.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //作業明細
    public static void work(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_workinfo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new WorkData().addWork(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_workinfo.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //部品明細
    public static void parts(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_parts_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new PartsData().addParts(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_parts.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上
    public static void sell(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addSell(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //中古売上
    public static void sell_used(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_used_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addUsed(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell_used.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上(OLD)
    public static void sell_old(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_old_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addOld(con, pw, syaryoTemplate, noneTypeSearch);
            new SyaryoTemplateToJson().write(FILENAME.replace(".json", "_sell_old.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //Komtrax
    public static void komtrax(Connection con, Map<String, SyaryoTemplate> syaryoTemplate, Map noneTypeSearch) {
        new KomtraxData().addKomtrax(FILENAME, con, syaryoTemplate, noneTypeSearch);
    }
}
