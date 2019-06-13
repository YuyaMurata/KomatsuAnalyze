/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package axg.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import java.util.Arrays;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ZZ17807
 */
public class MongoDBData {

    private MongoClient client;
    private MongoDatabase db;
    public MongoCollection<Document> coll;
    public MongoCollection<BasicDBObject> upcoll;

    private MongoDBData() {
        initialize();
    }

    private void initialize() {
        client = MongoClients.create();
    }

    public static MongoDBData create() {
        return new MongoDBData();
    }

    public void set(String dbn, String col) {
        this.db = client.getDatabase(dbn);
        this.coll = db.getCollection(col);
        this.upcoll = db.getCollection(col, BasicDBObject.class);
    }

    public Document getHeader() {
        return this.coll.find(exists("header")).first();
    }

    public Document get(String field, String key) {
        return this.coll.find(eq(field, key)).first();
    }
    
    public Document get(String field, String key, String element) {
        Document doc = this.coll.find(eq(field, key)).first();
        Document ndoc = (Document) ((Document)doc.get("map")).get(element);
        return ndoc;
    }

    //将来的に動作しなくなるため修正
    public void copyTo(String newcollname) {
        if(db.getCollection(newcollname).countDocuments() != 0)
            throw new MongoException("Already exists "+newcollname);
        
        MongoCollection newcoll = db.getCollection(newcollname);
        this.coll.find().forEach((Block<Document>) newcoll::insertOne);
    }

    public void close() {
        client.close();
    }
}
