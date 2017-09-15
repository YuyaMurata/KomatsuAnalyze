/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;
import json.SyaryoObjToJson;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SparateCorrectData {

    private static final String FILENAME = "syaryo_history_pc200.json";

    public static void main(String[] args) {
        JsonToSyaryoObj json = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = json.reader(FILENAME);

        Map<String, SyaryoObject> syaryoMap1 = new HashMap<>();
        Map<String, SyaryoObject> syaryoMap2 = new HashMap<>();

        for (SyaryoObject syaryo : syaryoMap.values()) {
            Boolean check = true;
            
            //check = SMRCheck(syaryo);
            check = SyarekiCheck(syaryo);
            
            if (check) {
                syaryoMap1.put(syaryo.getName2(), syaryo);
            } else {
                syaryoMap2.put(syaryo.getName2(), syaryo);
            }
        }

        System.out.println("total correct=" + syaryoMap1.size());
        System.out.println("total=" + syaryoMap2.size());
        //JSON
        SyaryoObjToJson obj = new SyaryoObjToJson();
        obj.write("correct_" + FILENAME, syaryoMap1);
        obj.write("unexpect_" + FILENAME, syaryoMap2);
    }

    public static Boolean SMRCheck(SyaryoObject syaryo) {
        //SMRチェック
        if (syaryo.getSMR() == null)
            return true;
            
        TreeMap smr = new TreeMap(syaryo.getSMR());
        Object temp = null;
        for (Object t : smr.values()) {
            if (temp != null) {
                if (Double.valueOf(t.toString()) < Double.valueOf(temp.toString())) {
                    return false;
                }
            }

            temp = t;
        }

        return true;
    }
    
    public static Boolean SyarekiCheck(SyaryoObject syaryo){
        //経歴チェック
        if (syaryo.getHistory() == null)
            return true;
            
        TreeMap history = new TreeMap(syaryo.getHistory());
        Boolean dead = false;
        for (Object date : history.keySet()) {
            if(dead)
                return false;
            
            if(history.get(date).equals("8A") || history.get(date).equals("8B") ||
                    history.get(date).equals("8C") || history.get(date).equals("9A") ||
                    history.get(date).equals("9B") || history.get(date).equals("9X"))
                dead = true;
            
        }

        return true;
    }
}
