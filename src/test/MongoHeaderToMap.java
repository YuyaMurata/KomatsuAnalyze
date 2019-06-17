/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import axg.mongodb.MongoDBData;
import file.CSVFileReadWrite;
import file.MapToJSON;
import java.io.PrintWriter;
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
        MongoDBData mongo = MongoDBData.create();
        mongo.set("json", "komatsuDB_PC200");

        List<String> hin = mongo.getHeader();
        Map<String, Map<String, List<String>>> head = new HashMap<>();
        Boolean flg = true;
        for(String s : hin){
            if(s.equals("id "))
                continue;
            
            System.out.println(s);
            
            String k = s.split("\\.")[0];
            
            if(head.get(k) == null){
                head.put(k, new HashMap<>());
                head.get(k).put(k+".subKey", new ArrayList<>());
                flg = false;
            }
            
            if(flg){
                head.get(k).get(k+".subKey").add(s);
            }else{
                flg = true;
            }
        }
        
        new MapToJSON().toJSON("mongoobj_syaryo.json", head);
        
        System.out.println(hin);
        mongo.close();
    }
}
