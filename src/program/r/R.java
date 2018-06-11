/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.r;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import param.KomatsuDataParameter;

/**
 *
 * @author kaeru_yuya
 */
public class R {

    private static R instance = new R();
    private static Rengine engine;

    private R() {
        settings();
    }

    private static void settings() {
        engine = new Rengine(new String[]{"--no-save"}, false, null);
        System.out.println("R_PATH=" + KomatsuDataParameter.R_FUNC_PATH.replace("\\", "/"));
        REXP result = engine.eval("source('" + KomatsuDataParameter.R_FUNC_PATH.replace("\\", "/") + "')");
    }

    public static R getInstance() {
        return instance;
    }

    //スミルノフ・グラブス検定 (http://aoki2.si.gunma-u.ac.jp/lecture/Grubbs/Grubbs.html)
    public Map detectOuters(List time, List series) {
        List<String> outers = new ArrayList();
        
        //List<String> trend = trends(time, series);
        String x = "x <- c(" + series.stream().collect(Collectors.joining(",")) + ")";
        System.out.println(x);
        REXP result = engine.eval(x);
        System.out.println(result);
        result = engine.eval("remove.outliers(x)");
        System.out.println(result);

        double arr[] = result.asVector().at(1).asDoubleArray();
        if (arr != null) {
            for (double d : arr) {
                outers.add(String.valueOf(Double.valueOf(d).intValue()));
            }
        }

        Map map = new TreeMap();
        for (int i = 0; i < time.size(); i++) {
            map.put(time.get(i), series.get(i));
        }

        System.out.println(series);
        for (Object d : outers) {
            int i = series.indexOf(d);
            System.out.println(d+":"+i);
            map.put(time.get(i), "NaN");
        }

        return map;
    }
    
    private List trends(List idx, List series) {
        List trend = new ArrayList();

        String t0 = idx.get(0).toString();
        Long s0 = Long.valueOf(series.get(0).toString());
        for (int i = 0; i < idx.size(); i++) {
            Integer si = Integer.valueOf(series.get(i).toString());
            Long t = diffDate(t0, idx.get(i).toString());
            Long s = si - s0;
            if (t == 0L) {
                t = 1L;
            }
            System.out.println(t + ":" + s);
            trend.add(String.valueOf(s / t));

            t0 = idx.get(i).toString();
            s0 = Long.valueOf(series.get(i).toString());
        }

        return trend;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private Long diffDate(String from, String to) {
        try {
            Date dateFrom = sdf.parse(from);
            Date dateTo = sdf.parse(to);

            long dayDiff = (dateTo.getTime() - dateFrom.getTime()) / (1000 * 60 * 60 * 24);
            return dayDiff;
        } catch (ParseException ex) {
        }
        return null;
    }
    
    public Map residuals(List idx, List series){
        SimpleRegression sr = new SimpleRegression();
        String t0 = idx.get(0).toString();
        for(int i=0; i < idx.size(); i++){
            Long day = diffDate(t0, idx.get(i).toString());
            sr.addData(Double.valueOf(day), Double.valueOf(series.get(i).toString()));
        }
        
        System.out.println("y="+sr.getSlope()+"x"+sr.getIntercept());
        Map map = new TreeMap();
        for(Object d : idx){
            Long day = diffDate(t0, d.toString());
            String calc = String.valueOf(Double.valueOf(day*sr.getSlope()+sr.getIntercept()).intValue());
            map.put(d, calc);
        }
        
        return map;
    }

    public static void close() {
        engine.end();
    }
}
