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
import json.JsonToSyaryoTemplate;
import json.SyaryoTemplateToJson;
import creator.template.SyaryoTemplate;
import java.util.List;
import json.MapIndexToJSON;

/**
 * DBから車両テンプレートを作成する
 *
 * @author ZZ17390
 */
public class SyaryoTemplateCreate extends HiveDB {

    private static String KISY;
    private static String FILEPATH;
    private static String FILENAME;

    public static void main(String[] args) throws IOException {
        Map<String, List> kisyIndex = new MapIndexToJSON().reader("index\\kisy_index.json");
        //for(Object kisy : kisyIndex.get("ENABLE")){
        SyaryoTemplateCreate.execute("PC200");
        //}
    }

    public static void execute(String KISY) {
        SyaryoTemplateCreate.KISY = KISY;
        SyaryoTemplateCreate.FILEPATH = "..\\KomatsuData\\車両テンプレート\\" + KISY + "\\";
        SyaryoTemplateCreate.FILENAME = "syaryo_" + KISY + "_template.json";

        long start = System.currentTimeMillis();

        //Folder
        File f = new File(FILEPATH);

        if (!f.exists()) {
            //フォルダ作成実行
            f.mkdirs();
            (new File(FILEPATH + "template")).mkdir();
            (new File(FILEPATH + "error")).mkdir();
        } else {
        }

        //DB接続
        Connection con = getConnection(); //HiveDB

        //テンプレート生成 true=KOMPAS車両 false=EQP車両
        template(con, true);
        Map<String, SyaryoTemplate> syaryoTemplate = readTempate();
        
        //EQP
        eqp_syaryo(con, syaryoTemplate);
        eqp_spec(con, syaryoTemplate);
        eqp_keireki(con, syaryoTemplate);

        //KOMPAS
        syaryo(con, new HashMap(syaryoTemplate));
        service_s(con, syaryoTemplate);
        service_t(con, syaryoTemplate);
        order(con, syaryoTemplate);
        work(con, syaryoTemplate);
        parts(con, syaryoTemplate);
        sell(con, syaryoTemplate);
        sell_used(con, syaryoTemplate);
        allsupport(con, syaryoTemplate);

        //KOSMIC
        sell_old(con, syaryoTemplate);

        //GCPS
        care(con, syaryoTemplate);
        
        //KOMTRAX
        komtrax(con, syaryoTemplate);
        
        long stop = System.currentTimeMillis();

        System.out.println((stop - start) + "ms");
    }

    public static void template(Connection con, Boolean syaryoFilter) {
        //テンプレート作成
        Map<String, SyaryoTemplate> syaryoTemplate = new CreateSyaryoTemplate().createTemplate(con, KISY, KISY.split("%")[0] + "0", syaryoFilter);
        new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME, syaryoTemplate);
    }

    public static Map<String, SyaryoTemplate> readTempate() {
        //テンプレートの読み込み
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        return obj.reader(FILEPATH + "template\\" + FILENAME);
    }

    public static void syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        //車両マスタ
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_syaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SyaryoData().addSyaryoCategory(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_syaryo.json"), syaryoMap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP車両
    public static void eqp_syaryo(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_eqpsyaryo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSyaryoData().addEQPSyaryo(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_eqpsyaryo.json"), syaryoMap);
            System.out.println("EQPSyaryo not update List:" + EQPSyaryoData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP仕様
    public static void eqp_spec(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_eqpspec_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPSpecData().addEQPSpec(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_eqpspec.json"), syaryoMap);
            System.out.println("EQPSpec not update List:" + EQPSpecData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //EQP経歴
    public static void eqp_keireki(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_eqpkeireki_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new EQPKeirekiData().addSyaryoKeireki(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_eqpkeireki.json"), syaryoMap);
            System.out.println("EQPKeireki not update List:" + EQPKeirekiData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_単販
    public static void service_t(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_service1_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, KISY, syaryoTemplate, 1, 1);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_service1.json"), syaryoMap);
            System.out.println("Service_t not update List:" + ServiceData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //サービス経歴_修販            
    public static void service_s(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_service2_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new ServiceData().addService(con, pw, KISY, syaryoTemplate, 1, 2);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_service2.json"), syaryoMap);
            System.out.println("Service_s not update List:" + ServiceData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //受注
    public static void order(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_order_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new OrderData().addOrder(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_order.json"), syaryoMap);
            System.out.println("Order not update List:" + OrderData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //作業明細
    public static void work(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_workinfo_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new WorkData().addWork(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_workinfo.json"), syaryoMap);
            System.out.println("Work not update List:" + WorkData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //部品明細
    public static void parts(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_parts_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new PartsData().addParts(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_parts.json"), syaryoMap);
            System.out.println("Parts not update List:" + PartsData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上
    public static void sell(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_sell_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addSell(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_sell.json"), syaryoMap);
            System.out.println("Sell not update List:" + SellsData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //中古売上
    public static void sell_used(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_sell_used_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addUsed(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_sell_used.json"), syaryoMap);
            System.out.println("UsedSell not update List:" + SellsData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //本体売上(OLD)
    public static void sell_old(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_sell_old_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new SellsData().addOld(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_sell_old.json"), syaryoMap);
            System.out.println("Sell(Old) not update List:" + SellsData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //コマツケア
    public static void care(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_care_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new KomatsuCareData().addCare(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_care.json"), syaryoMap);
            System.out.println("Komatsu Care not update List:" + KomatsuCareData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //前受け金
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_care_preprice_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new KomatsuCareData().addPreprice(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_care_preprice.json"), syaryoMap);
            System.out.println("Komatsu Care not update List:" + KomatsuCareData.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //オールサポート
    public static void allsupport(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILEPATH + "error\\" + FILENAME.replace(".json", "_allsupport_error.csv")))))) {
            Map<String, SyaryoTemplate> syaryoMap = new AllSupport().addAllsupport(con, pw, KISY, syaryoTemplate);
            new SyaryoTemplateToJson().write(FILEPATH + "template\\" + FILENAME.replace(".json", "_allsupport.json"), syaryoMap);
            System.out.println("AllSupport not update List:" + AllSupport.dataCheck());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Komtrax
    public static void komtrax(Connection con, Map<String, SyaryoTemplate> syaryoTemplate) {
        new KomtraxData().addKomtrax(FILEPATH + "template\\" + FILENAME, con, KISY, syaryoTemplate);
    }
}
