/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.filter;

import java.util.List;
import java.util.Map;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class MainteFilter {
    private static Map<String, List> mainte = KomatsuDataParameter.PERIOD_MAINTE;
    private static Integer priceTh = 5000;
    
    public static Boolean detect(String sgkt, String hnbn, Integer price){
        //作業形態に基づくフィルタ
        if(mainte.get("受注.SGYO_KTICD").contains(sgkt))
            return true;
        
        //品番に基づくフィルタ
        if(mainte.get("部品.HNBN").contains(hnbn))
            return true;
            
        //KES部品のフィルタ
        String[] kes = hnbn.split("-");
        if(kes.length == 2)
            if(kes[0].length() == 5 && kes[1].length() == 5)
                return true;
        
        //金額フィルタ
        if(price < priceTh)
            return true;
        
        return false;
    }
}
