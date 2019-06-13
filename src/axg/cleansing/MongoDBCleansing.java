/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.cleansing;

import axg.mongodb.MongoDBData;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import static com.mongodb.client.model.Filters.eq;
import org.bson.Document;

/**
 *
 * @author ZZ17807
 */
public class MongoDBCleansing {
    public static void main(String[] args) {
        /*MongoDBData mongo = MongoDBData.create();
        mongo.set("json", "komatsuDB_PC200");
        mongo.copyTo("komatsuDB_PC200_Temp");
        mongo.close();
        */
        
        MongoDBData mongo2 = MongoDBData.create();
        mongo2.set("json","komatsuDB_PC200_Temp" );
        mongo2.get("name", "PC200-8-N1-351483", "顧客_S").values().forEach((Block<Document>) t -> System.out::println);
        mongo2.close();
    }
}
