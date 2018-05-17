/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.Map;
import json.JsonToSyaryoObj;
import obj.SyaryoObject1;

/**
 *
 * @author ZZ17390
 */
public class DTWTest {
    public static void main(String[] args) {
        JsonToSyaryoObj jobj = new JsonToSyaryoObj();
        Map<String, SyaryoObject1> smap = jobj.reader("syaryo_obj_WA470_from.json");
        
        
    }
}
