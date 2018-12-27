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
    
    public static Boolean allDetect(String sgkt, String hnbn, Integer price){
        Boolean f = 
            skDetect(sgkt) || 
            partsDetect(hnbn) ||
            egoilDetect(hnbn) ||
            kesDetect(hnbn) ||
            priceDetect(price);
        
        return f;
    }
    
    public static Boolean skDetect(String sgkt){
        //作業形態に基づくフィルタ
        if(mainte.get("受注.SGYO_KTICD").contains(sgkt)){
            return true;
        }

        return false;
    }
    
    public static Boolean partsDetect(String hnbn){    
        //品番に基づくフィルタ
        if(mainte.get("部品.HNBN").contains(hnbn)){
            return true;
        }
        
        return false;
    }
    
    public static Boolean egoilDetect(String hnbn){    
        //品番に基づくフィルタ
        if((hnbn.contains("SYEO-") && !hnbn.contains("SYEO-T")) || (hnbn.contains("NYEO-") && !hnbn.contains("NYEO-T"))){
            return true;
        }
        
        return false;
    }
    
    public static Boolean pwoilDetect(String hnbn){    
        //品番に基づくフィルタ
        if(hnbn.contains("SYEO-T")|| hnbn.contains("NYEO-T")){
            return true;
        }
        
        return false;
    }
    
    public static Boolean kesDetect(String hnbn){   
        //KES部品のフィルタ
        String[] kes = hnbn.split("-");
        if(kes.length == 2)
            if(kes[0].length() == 5 && kes[1].length() == 5){
                return true;
            }
        
        return false;
    }
    
    public static Boolean priceDetect(Integer price){
        //金額フィルタ
        if(price < priceTh){
            return true;
        }
        
        return false;
    }
}
