/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class OneDayForm {
    private static String DATE_FORMAT = KomatsuDataParameter.DATE_FORMAT;
    private static SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    
    public Date date;
    
    public OneDayForm(String date){
        try {
            this.date = sdf.parse(date);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setDayData(Map<String, List<String>> sdata){
        
    }
}
