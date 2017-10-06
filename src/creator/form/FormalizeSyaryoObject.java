/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class FormalizeSyaryoObject {

    public void formalize(Map<String, SyaryoObject> syaryoMap) {

        for (String name : syaryoMap.keySet()) {
            SyaryoObject syaryo = syaryoMap.get(name);
            Map<String, List> owner = syaryo.getOwner();
            Map update = new TreeMap();
            Object[] temp = new Object[2];
            
            for (String date : owner.keySet()) {
                List obj = owner.get(date);
                
                System.out.println(date+":"+obj);
                if(!obj.get(0).equals(temp[0]) && !((List)obj.get(1)).get(1).equals(temp[1])){
                    update.put(date, obj);
                    temp[0] = obj.get(0);
                    temp[1] = ((List)obj.get(1)).get(1);
                }
            }
            
            System.out.println(update);
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        Map map = new JsonToSyaryoObj().reader("syaryo_obj_WA470.json");
        new FormalizeSyaryoObject().formalize(map);
    }
}
