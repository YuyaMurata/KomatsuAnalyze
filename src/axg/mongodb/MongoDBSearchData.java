/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

/**
 *
 * @author ZZ17807
 */
public class MongoDBSearchData {
    private static MongoDBSearchData instance = new MongoDBSearchData();
    private MongoClient client;
    private  MongoCollection<Document> coll;
    
    private MongoDBSearchData(){
        initialize();
    }
    
    private void initialize(){
        client = MongoClients.create();
    }
    
    public static MongoDBSearchData getInstance(){
        return instance;
    }
    
    public void set(String db, String col){
        this.coll = client.getDatabase(db).getCollection(col);
    }
    
    public Document getHeader(){
        return this.coll.find(exists("header")).first();
    }
    
    public Document get(String field, String key){
        return this.coll.find(eq("field", key)).first();
    }
    
    public void close(){
        client.close();
    }
}
