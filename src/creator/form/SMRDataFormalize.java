/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17390
 */
public class SMRDataFormalize {
    //異常値候補
    //String[] irrv = new String[]{"999","9999","99999","99009","123"}
    
    public static Map<String, List<String>> form(Map<String, List<String>> data, int idx){
        //差分データへの変換
        Map<String, Double> diff = new TreeMap<>();
        Double before = 0d;
        for(String date : data.keySet()){
            Double after = Double.valueOf(data.get(date).get(idx));
            
        }
        
        return null;
    }
    
    
}
