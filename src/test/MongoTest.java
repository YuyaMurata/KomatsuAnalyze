/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

/**
 *
 * @author ZZ17807
 */
public class MongoTest {

    public static void main(String[] args) {
        // MongoDBサーバに接続
        MongoClient client = MongoClients.create();
        // 利用するDBを取得
        MongoDatabase db = client.getDatabase("json");
        
        //コレクション
        MongoCollection<Document> coll = db.getCollection("komatsuDB_PC200");
        
        Document myDoc = coll.find(eq("name", "PC200-10- -451749")).first();
        Document mapdoc = (Document) myDoc.get("map");
        System.out.println(mapdoc.size());
        
        // サーバから切断
        client.close();
    }
}
