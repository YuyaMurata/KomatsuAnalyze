/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.detect;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import param.KomatsuDataParameter;
import program.py.PythonCommand;

/**
 *
 * @author ZZ17390
 */
public class AbnomalyDection {
    //X^2検定
    public static Map<String, String> detectChiSquare(Map<String, String> data, Double alpha, Boolean graph){
        //一時ファイルの生成
        output(KomatsuDataParameter.DETECT_TEMP_FILE, data);
        
        //Pythonの実行 AbnomalyDetect
        String[] args;
        if(graph)
            args = new String[]{KomatsuDataParameter.DETECT_TEMP_FILE, alpha.toString(), "g"};
        else
            args = new String[]{KomatsuDataParameter.DETECT_TEMP_FILE, alpha.toString()};
        PythonCommand.py(KomatsuDataParameter.DETECT_PY, args);
        
        //生成ファイルの読み込み
        Map<String, String> map = new HashMap<>();
        try(BufferedReader br = CSVFileReadWrite.reader(KomatsuDataParameter.DETECT_TEMP_FILE)){
            //Header 除去
            String header = br.readLine();
            
            br.lines().forEach(l ->{
                String[] sarr = l.split(",");
                
                String key = sarr[0];
                String value = sarr[sarr.length-1];
                
                map.put(key, value);
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return map;
    }
    
    private static void output(String fname, Map<String, String> data){
        try(PrintWriter pw = CSVFileReadWrite.writer(fname)){
            pw.println("SID,TARGET");
            data.entrySet().stream().map(d -> d.getKey()+","+d.getValue()).forEach(pw::println);
        }
    }
    
    public static void main(String[] args) {
        //Test
        Map<String, String> map = detectChiSquare(null, 0.01, true);
        map.entrySet().stream().filter(s -> s.getValue().equals("1")).map(s -> s.getKey()+" = "+s.getValue()).forEach(System.out::println);
    }
}
