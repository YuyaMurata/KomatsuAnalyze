/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Map;
import jsonDatabase.JsonDB;
import jsonDatabase.JsonDataObject;

/**
 *
 * @author ZZ17807
 */
public class TestJSONMAnage {

    public static void main(String[] args) {        
        // 接続先のMongoDBの設定
        String host = "localhost";
        int port = 27017;
        JsonDB db = new JsonDB(host, port);
        db.setCollectionName("komatsuDB_PC200");
        
        // IDが“aaa-bbb-ccc-ddd”のJsonデータを取得
        String id = "PC200-10- -450635";
        JsonDataObject jdo = db.getJsonDataObject(id);
        
        System.out.println(jdo.getDataMap());
        
        // IDに紐づく車両データを出力
        Map syaryoData = jdo.getTableData("受注");
        System.out.println(syaryoData);

    }
}
