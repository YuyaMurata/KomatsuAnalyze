/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.util.Map;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class SyaryoCheck {
    private static final String filename = "syaryo_history_template.json";
    
    public static void main(String[] args) {
        JsonToSyaryoObj obj = new JsonToSyaryoObj();
        Map<String, SyaryoObject> syaryoMap = obj.reader(filename);
        
        //Check 1:
        MNF_SHIP_DateCheck(syaryoMap);
    }
    
    private static void MNF_SHIP_DateCheck(Map<String, SyaryoObject> syaryoMap){
        int i = 0;
        int j = 0;
        for(SyaryoObject syaryo : syaryoMap.values()){
            if(syaryo.getBorn().equals("") || syaryo.getDeploy().equals("")){
                System.out.println(syaryo.getName()+": MNF_Date = "+syaryo.getBorn());
                System.out.println(syaryo.getName()+": SHIP_Date = "+syaryo.getDeploy());
                i++;
            }
            
            if(syaryo.getDeploy().compareTo(syaryo.getBorn()) < 0){
                System.out.println(syaryo.getName()+
                        ": MNF_Date("+syaryo.getBorn()+
                        ") > SHIP_Date("+syaryo.getDeploy()+")");
                j++;
            }
        }
        
        System.out.println("日付がどちらか空 : "+i);
        System.out.println("生産、出荷が逆? : " +j);
    }
}
