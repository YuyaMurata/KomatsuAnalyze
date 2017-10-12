/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;

/**
 *
 * @author ZZ17390
 */
public class DataCheck {
    public static void main(String[] args) throws IOException {
        //車両テンプレートのサマリ
        //templateCheck();
        
        //紐づかない車両のサマリ
        //errorCheck();
        
        //2csv 比較
        csvCheck();
    }
    
    public static void templateCheck() throws IOException{
        String path = "車両テンプレート";
        File[] flist = (new File(path)).listFiles();
        System.out.println("データソース,データ (件),N/A (件),車両 (台),車両 N/A (台),車両 N/A (空白)");
        String[] data = new String[6];
        for(File f : flist){
            if(!f.getName().contains("sell_used")) continue;
            data[0] = f.getName().replace("syaryo_history_template_", "").replace("_error.csv", "").replace(".json", "");
            data[1] = "-1";
            //System.out.println(f.getName());
            Map syaryo = null;
            if(f.toString().contains("json")){
                syaryo = new JsonToSyaryoObj().reader(f.toString());
                //System.out.println("車両："+syaryo.size());
                data[3] = String.valueOf(syaryo.size());
                
                if(data[0].contains("template")){
                    System.out.println(String.join(",", data));
                    data = new String[6];
                }
            }else{
                long n = Files.lines(Paths.get(f.toString())).count();
                long s = Files.lines(Paths.get(f.toString())).map(str -> str.split(",")[1]).distinct().count();
                long no = Files.lines(Paths.get(f.toString())).filter(str -> str=="").count();
                //System.out.println("車両(N/A)："+n+"件 ("+s+", "+no+")");
                
                data[2] = String.valueOf(n);
                data[4] = String.valueOf(s);
                data[5] = String.valueOf(no);
                
                System.out.println(String.join(",", data));
                data = new String[6];
            }
        }
    }
    
    public static void errorCheck() throws IOException{
        String path = "車両テンプレートエラー分離";
        File[] flist = (new File(path)).listFiles();
        
        for(File f : flist){
            long n = Files.lines(Paths.get(f.toString())).count();
            long s = Files.lines(Paths.get(f.toString())).map(str -> str.split(",")[1]).distinct().count();
            
            System.out.println(f.getName()+","+n+","+s);
        }
    }
    
    public static void csvCheck() throws IOException{
        String path = "check";
        File[] flist = (new File(path)).listFiles();
        
        Map<String, List<Object[]>> data = new HashMap<>();
        for(File f : flist){
            if(!f.getName().contains("data")) continue;
            
            data.put(f.getName(), Files.lines(Paths.get(f.toString()))
                                        .map(s -> s.replace("\"", "").split(",")).collect(Collectors.toList()));
        }
        
        Map<String, Object[]> map = new TreeMap();
        for(String fname : data.keySet()){
            List<Object[]> list = data.get(fname);
            for(Object[] s : list){
                String key = s[1]+","+s[2];
                if(map.get(key) == null) map.put(key, new Object[]{1, s[0], fname});
                else map.put(key, new Object[]{(int) map.get(key)[0]+1, s[0], fname});
            }
        }
        
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(new File("service_order_差分.csv"))));
        for(Object key: map.keySet()){
            if((int)map.get(key)[0] == 1 && map.get(key)[2].toString().contains("service")) pw.println(map.get(key)[1]+","+key+","+map.get(key)[2]);
        }
        pw.close();
    }
}
