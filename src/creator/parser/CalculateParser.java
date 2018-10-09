/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String s3 = "2*komtrax_CW_ACT_DATA.ACT_COUNT#5/3600==20?komtrax_CW_ACT_DATA.DAILY_UNIT#6==1?komtrax_CW_ACT_DATA.ACT_COUNT#5/60:NA:24";

        CalculateParser calc = new CalculateParser();
        String after_s2 = calc.dataParser(s3);

        calc.formularParser(after_s2);
    }

    private Pattern ifp = Pattern.compile("[?:]");
    private Pattern lp = Pattern.compile(String.join("|", LOGIC));
    private Pattern cp = Pattern.compile(String.join("|", COMP));
    private Pattern ap = Pattern.compile("[" + String.join("", ALITH) + "]");

    private List<String> formularParser(String s) {
        String[] sarr = ifp.split(s);
        System.out.println(Arrays.asList(sarr));

        List<String> ifobj = new ArrayList();
        for (String c : s.split("")) {
            switch (c) {
                case "?":
                    if (ifobj.isEmpty()) {
                        ifobj.add("if");
                    } else {
                        ifobj.set(ifobj.size() - 1, ifobj.get(ifobj.size() - 1) + "_if");
                    }
                    ifobj.add("then");
                    break;
                case ":":
                    ifobj.add("else");
                    break;
            }
        }

        System.out.println(ifobj);
        List<String> ifvalue = new ArrayList<>();
        
        for (String sa : sarr) {
            String[] state = lp.split(sa);
            System.out.println("Logic:" + Arrays.asList(state));
            
            for(String ls : state){
                String[] cps = cp.split(ls);
                
            }
        }

        return null;
    }

    private Pattern p1 = Pattern.compile("[?=:<>*/+-]");
    private Pattern p2 = Pattern.compile("\\w.+#\\d{1,2}");
    private Map<String, String> dataMap;

    private String dataParser(String s) {
        char a = 'A';
        dataMap = new HashMap();

        //Origin
        System.out.println(s);

        String temp = s;
        for (String dstr : p1.split(s)) {
            Matcher m = p2.matcher(dstr);
            if (m.find()) {
                if (dataMap.get(m.group()) == null) {
                    temp = temp.replace(m.group(), getData("" + a));
                    dataMap.put(m.group(), getData("" + (a++)));
                }
            }
        }

        System.out.println(temp);

        return temp;
    }

    Map<String, String> test = new HashMap() {
        {
            put("A", "100");
            put("B", "200");
        }
    };

    private String getData(String data) {
        return test.get(data);
    }

    //論理演算用
    private Boolean logic(String l, String r, String op) {
        switch (op) {
            case "==":
                return l.equals(r);
            case "<>":
                return !l.equals(r);
            default:
                return null;
        }
    }

    //比較演算用
    private Boolean comp(String l, String r, String op) {
        Double ld = -1d, rd = -1d;
        Integer li = -1, ri = -1;
        if (l.contains("\\.") || l.contains("\\.")) {
            ld = Double.valueOf(l);
            rd = Double.valueOf(r);
        } else {
            li = Integer.valueOf(l);
            ri = Integer.valueOf(r);
        }

        if (li == -1) {
            switch (op) {
                case "<=":
                    return ld <= rd;
                case ">=":
                    return ld >= rd;
                case "<":
                    return ld < rd;
                case ">":
                    return ld > rd;
                default:
                    return null;
            }
        }

        if (ld == -1) {
            switch (op) {
                case "<=":
                    return li <= ri;
                case ">=":
                    return li >= ri;
                case "<":
                    return li < ri;
                case ">":
                    return li > ri;
                default:
                    return null;
            }
        }

        return null;
    }

    //算術演算用
    private String arith(String l, String r, String op) {
        Double ld = -1d, rd = -1d;
        Integer li = -1, ri = -1;
        if (l.contains("\\.") || l.contains("\\.")) {
            ld = Double.valueOf(l);
            rd = Double.valueOf(r);
        } else {
            li = Integer.valueOf(l);
            ri = Integer.valueOf(r);
        }

        if (li == -1) {
            switch (op) {
                case "+":
                    return String.valueOf(ld + rd);
                case "-":
                    return String.valueOf(ld - rd);
                case "*":
                    return String.valueOf(ld * rd);
                case "/":
                    return String.valueOf(ld / rd);
                default:
                    return null;
            }
        }

        if (ld == -1) {
            switch (op) {
                case "+":
                    return String.valueOf(ld + rd);
                case "-":
                    return String.valueOf(ld - rd);
                case "*":
                    return String.valueOf(ld * rd);
                case "/":
                    return String.valueOf(ld / rd);
                default:
                    return null;
            }
        }

        return null;
    }
}
