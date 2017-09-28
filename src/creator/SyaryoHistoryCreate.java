/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator;

import creator.source.*;
import db.HiveDB;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import json.JsonToSyaryoTemplate;
import json.SyaryoObjToJson;
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

        Map<String, SyaryoTemplate> syaryoTemplate;

        //DB接続
        Connection con = getConnection(); //HiveDB

        //テンプレート作成
        /*syaryoMap = new CreateSyaryoTemplate().createTemplate(con);
        json.write(FILENAME, syaryoMap);
        System.exit(0);*/
        
        //テンプレートの読み込み
        JsonToSyaryoTemplate obj = new JsonToSyaryoTemplate();
        syaryoTemplate = obj.reader(FILENAME);

        //型が無いときのインデックス
        Map noneTypeSearch = new HashMap();
        for (SyaryoTemplate syaryo : syaryoTemplate.values()) {
            noneTypeSearch.put(syaryo.getName2(), syaryo.getName());
        }
        
        //保存用
        Map<String, SyaryoTemplate> syaryoMap;
        
        //EQP車両
        /*PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpsyaryo_error.csv")))));
        syaryoMap = new EQPSyaryoData().addEQPSyaryo(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_eqpsyaryo.json"), syaryoMap);
        pw.close();
        
        //EQP経歴情報
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_eqpkeireki_error.csv")))));
        syaryoMap = new EQPKeirekiData().addSyaryoKeireki(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_eqpkeireki.json"), syaryoMap);
        pw.close();
        
        //車両マスタ
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_syaryo_error.csv")))));
        syaryoMap = new SyaryoData().addSyaryoCategory(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_syaryo.json"), syaryoMap);
        pw.close();
        
        //サービス経歴
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_service1_error.csv")))));
        syaryoMap = new ServiceData().addService(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_service1.json"), syaryoMap);
        pw.close();
       
        //受注
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_order_error.csv")))));
        syaryoMap = new OrderData().addOrder(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_order.json"), syaryoMap);
        pw.close();
        
        //作業明細
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_workinfo_error.csv")))));
        syaryoMap = new WorkData().addWork(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_workinfo.json"), syaryoMap);
        pw.close();
        
        //部品明細
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_parts_error.csv")))));
        syaryoMap = new PartsData().addParts(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_parts.json"), syaryoMap);
        pw.close();
        
        //本体売上
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sell_error.csv")))));
        syaryoMap = new SellsData().addSell(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_sell.json"), syaryoMap);
        pw.close();*/
        
        //中古売上
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File(FILENAME.replace(".json", "_sellused_error.csv")))));
        syaryoMap = new SellsData().addUsed(con, pw, syaryoTemplate, noneTypeSearch);
        json.write(FILENAME.replace(".json", "_sellused.json"), syaryoMap);
        pw.close();
        
        //Komtrax
        //new KomtraxData().addKomtrax(FILENAME, con, syaryoTemplate, noneTypeSearch);
        
    }
}
