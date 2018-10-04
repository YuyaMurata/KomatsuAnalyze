/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class CalculateParser {
    List<String> LOGIC = Arrays.asList(new String[]{"==", "<>"});
    List<String> COMP = Arrays.asList(new String[]{"<=", ">=", "<", ">"});
    List<String> ALITH = Arrays.asList(new String[]{"+", "-", "/", "*"});
    
    public static void main(String[] args) {
        String s1 = "komtrax_CW_ACT_DATA.DAILY_UNIT#6>=30";
        String s2 = "komtrax_CW_ACT_DATA.DAILY_UNIT#6==30?2*komtrax_CW_ACT_DATA.ACT_COUNT#5/3600+4-2:komtrax_CW_ACT_DATA.DAILY_UNIT#6==1?komtrax_CW_ACT_DATA.ACT_COUNT#5/60:NA";
        String s3 = "2*komtrax_CW_ACT_DATA.ACT_COUNT#5/3600";
        
        CalculateParser calc = new CalculateParser();
        String after_s2 = calc.dataParser(s1);
        
        calc.formularParser(after_s2);
    }
    
    private Pattern ifp = Pattern.compile("[?:]");
    private Pattern lp = Pattern.compile(String.join("|",LOGIC));
    private Pattern cp = Pattern.compile(String.join("|",COMP));
    private Pattern ap = Pattern.compile("["+String.join("",ALITH)+"]");
    private List<String> formularParser(String s){
        String[] sarr = ifp.split(s);
        System.out.println(Arrays.asList(sarr));
        
        for(String sa : sarr){
            Matcher m = lp.matcher(sa);
            
            if(m.find()){
                System.out.println(Arrays.asList(lp.split(sa)));
            }
            
            m = cp.matcher(sa);
            if(m.find()){
                System.out.println(Arrays.asList(cp.split(sa)));
            }
            
            m = ap.matcher(sa);
            if(m.find()){
                System.out.println(Arrays.asList(ap.split(sa)));
            }
        }
        
        return null;
    }
    
    private Pattern p1 = Pattern.compile("[?=:<>*/+-]");
    private Pattern p2 = Pattern.compile("\\w.+#\\d{1,2}");
    private Map<String, String> dataMap;
    private String dataParser(String s){
        char a = 'A'; 
        dataMap = new HashMap();
        
        //Origin
        System.out.println(s);
        
        String temp = s;
        for(String dstr : p1.split(s)){
            Matcher m = p2.matcher(dstr);
            if(m.find())
                if(dataMap.get(m.group()) == null){
                    temp = temp.replace(m.group(), getData(""+a));
                    dataMap.put(m.group(), getData(""+(a++)));
                }
        }
        
        System.out.println(temp);
        
        return temp;
    }
    
    private String getData(String data){
        return data;
    }
    
    //論理演算用
    private Boolean logic(String l, String r, String op){
        switch(op){
            case "==":
                return l.equals(r);
            case "<>":
                return !l.equals(r);
            default :
                return null;
        }
    }
    
    //比較演算用 数字
    private Boolean comp(int l, int r, String op){
        switch(op){
            case "<=":
                return l <= r;
            case ">=":
                return l >= r;
            case "<":
                return l < r;
            case ">":
                return l > r;
            default :
                return null;
        }
    }
    
    //算術演算用 Double
    private Integer arith(int l, int r, String op){
        switch(op){
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            default :
                return null;
        }
    }
    
    //算術演算用 Double
    private Double arith(double l, double r, String op){
        switch(op){
            case "+":
                return l + r;
            case "-":
                return l - r;
            case "*":
                return l * r;
            case "/":
                return l / r;
            default :
                return null;
        }
    }
}
