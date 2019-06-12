/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import axg.mongodb.MongoDBSearchData;
import file.MapToJSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ZZ17807
 */
public class MongoHeaderToMap {
    public static void main(String[] args) {
        MongoDBSearchData mongo = MongoDBSearchData.getInstance();
        mongo.set("json", "komatsuDB_PC200");
        
        Document h = mongo.getHeader();
        List<String> hin = (List)h.get("header");
        Map<String, List<String>> head = new HashMap<>();
        
        for(String s : hin){
            if(s.equals("id "))
                continue;
            
            System.out.println(s);
            
            String k = s.split("\\.")[0];
            String sk = s.split("\\.")[1];
            
            if(head.get(k) == null)
                head.put(k, new ArrayList<>());
            head.get(k).add(sk);
        }
        
        new MapToJSON().toJSON("mongoobj_syaryo.json", head);
        
        System.out.println(hin);
        mongo.close();
    }
}
