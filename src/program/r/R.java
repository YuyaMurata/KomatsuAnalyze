/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.r;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    
    private R(){
        settings();
    }
    
    private static void settings(){
        engine = new Rengine(new String[]{"--no-save"}, false, null);
        System.out.println("R_PATH="+KomatsuDataParameter.R_FUNC_PATH.replace("\\", "/"));
        REXP result = engine.eval("source('"+KomatsuDataParameter.R_FUNC_PATH.replace("\\", "/")+"')");
    }
    
    public static R getInstance(){
        return instance;
    }
    
    //スミルノフ・グラブス検定 (http://aoki2.si.gunma-u.ac.jp/lecture/Grubbs/Grubbs.html)
    public List detectOuters(List series){
        String x = "x <- c("+series.stream().map(s -> s.toString()).collect(Collectors.joining(","))+")";
        System.out.println(x);
        REXP result = engine.eval(x);
        System.out.println(result);
        result = engine.eval("remove.outliers(x)");
        System.out.println(result);
        
        double arr[] = result.asVector().at(1).asDoubleArray();
        List outers = new ArrayList();
        if(arr != null)
            for(double d : arr)
                outers.add(String.valueOf(Double.valueOf(d).intValue()));
        
        List list = new ArrayList();
        for(Object s : series)
            if(!outers.contains(s))
                list.add(s);
            else
                list.add("");
        
        return list;
    }
    
    public static void close(){
        engine.end();
    }
}
