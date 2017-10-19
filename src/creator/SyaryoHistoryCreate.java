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
import obj.SyaryoTemplate;

/**
 * DBから車両テンプレートを作成する
 * @author ZZ17390
 */
public class SyaryoHistoryCreate extends HiveDB {
    private static final String FILENAME = "syaryo_history_template.json";
    
    public static void main(String[] args) throws IOException {
        SyaryoHistoryCreate syaryoHistory = new SyaryoHistoryCreate();
        SyaryoTemplateToJson json = new SyaryoTemplateToJson();
        
        //変数
        Map<String, SyaryoTemplate> syaryoTemplate;
        Map<String, SyaryoTemplate> syaryoMap;
        PrintWriter pw;

        //DB接続
        Connection con = getConnection(); //HiveDB

        /*//テンプレート作成
        syaryoTemplate = new CreateSyaryoTemplate().createTemplate(con);
        json.write(FILENAME, syaryoTemplate);
        */
        
        //テンプレートの読み込み
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        syaryoTemplate = obj.reader(FILENAME);

        //型が無いときのインデックス
        Map noneTypeSearch = createNoneTypeSearch(syaryoTemplate);
        
        //車両マスタ
        /*pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_syaryo_error.csv")))));
        syaryoMap = new SyaryoData().addSyaryoCategory(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_syaryo.json"), syaryoMap);
        pw.close();
        
        //KOMPAS車両マスタとテンプレートのマージ。 消せば工場データが中心となる。
        syaryoTemplate = new CreateSyaryoTemplate().mergeTemplate(syaryoTemplate, syaryoMap);
        json.write(FILENAME, syaryoTemplate);
        
        //型が無いときのインデックス つくり直し
        noneTypeSearch = createNoneTypeSearch(syaryoTemplate);
        */
        
        //EQP車両
        /*pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpsyaryo_error.csv")))));
        syaryoMap = new EQPSyaryoData().addEQPSyaryo(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_eqpsyaryo.json"), syaryoMap);
        pw.close();
        
        //EQP経歴情報
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpkeireki_error.csv")))));
        syaryoMap = new EQPKeirekiData().addSyaryoKeireki(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_eqpkeireki.json"), syaryoMap);
        pw.close();
        
        //サービス経歴
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service1_error.csv")))));
        syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, noneTypeSearch, "=1", "=1");
        json.write(FILENAME.replace(".json", "_service1.json"), syaryoMap);
        pw.close();
        
        //サービス経歴
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service2_error.csv")))));
        syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, noneTypeSearch, "=1", "=2");
        json.write(FILENAME.replace(".json", "_service2.json"), syaryoMap);
        pw.close();*/
       
        //受注
        /*pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order_error.csv")))));
        syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_order.json"), syaryoMap);
        pw.close();
        
        //作業明細
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_workinfo_error.csv")))));
        syaryoMap = new WorkData().addWork(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_workinfo.json"), syaryoMap);
        pw.close();
        
        //部品明細
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_parts_error.csv")))));
        syaryoMap = new PartsData().addParts(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_parts.json"), syaryoMap);
        pw.close();
        
        //本体売上
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_error.csv")))));
        syaryoMap = new SellsData().addSell(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_sell.json"), syaryoMap);
        pw.close();
        
        //中古売上
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_used_error.csv")))));
        syaryoMap = new SellsData().addUsed(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_sell_used.json"), syaryoMap);
        pw.close();*/
        
        //本体売上(OLD)
        pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_old_error.csv")))));
        syaryoMap = new SellsData().addOld(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_sell_old.json"), syaryoMap);
        pw.close();
        
        //Komtrax
        //new KomtraxData().addKomtrax(FILENAME, con, syaryoTemplate, noneTypeSearch);

    }
    
    public static Map createNoneTypeSearch(Map<String, SyaryoTemplate> syaryoTemplate){
        Map noneTypeSearch = new HashMap();
        for (SyaryoTemplate syaryo : syaryoTemplate.values()) {
            noneTypeSearch.put(syaryo.getName2(), syaryo.getName());
        }
        return noneTypeSearch;
    }
}
