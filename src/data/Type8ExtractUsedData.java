/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.HashMap;
import java.util.Map;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class Type8ExtractUsedData {
    private static final String FILENAME = "syaryo_history_pc200.json";
    
    public static void main(String[] args) {
        JsonToSyaryoObj json = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = json.reader(FILENAME);
        
        for(SyaryoObject syaryo : syaryoMap.values()){
            if(syaryo.getName().split("-")[1].equals("8"))
                if(!syaryo.getUsed().isEmpty())
                    System.out.println(syaryo.getName2());
        }
    }
}
