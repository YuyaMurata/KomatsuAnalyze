/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import creator.template.SimpleTemplate;
import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import file.SyaryoTemplateToJSON;
import param.KomatsuDataParameter;

/**
 * 読み込んだデータの機種を判定
 * @author ZZ17390
 */
public class KISYHantei {
    private static String sourcePath = KomatsuDataParameter.ERR_DATAPROCESS_PATH;
    public static void main(String[] args) {
        SyaryoTemplateToJSON json = new SyaryoTemplateToJSON();
        Map<String, SimpleTemplate> all = json.ToTemplate(sourcePath+"allsyaryo_index.json");
        Map<String, SimpleTemplate> other = json.ToTemplate(sourcePath+"othersyaryo_index.json");
        
        Map<String, Integer> allkisy = new HashMap();
        for(String name : all.keySet()){
            String kisy = name.split("-")[0];
            allkisy.put(kisy, 0);
        }
        
        Map<String, Integer> otherkisy = new HashMap();
        for(String name : other.keySet()){
            String kisy = name.split("-")[0];
            otherkisy.put(kisy, 1);
        }
        
        try(BufferedReader csv = CSVFileReadWrite.readerSJIS(sourcePath+"エラー機種.csv");
            PrintWriter out = CSVFileReadWrite.writer(sourcePath+"エラー機種_区分.csv")){
            String header = csv.readLine();
            out.println(header+",区分");
            String line;
            while((line = csv.readLine()) != null){
                if(allkisy.get(line) != null)
                    out.println(line+","+allkisy.get(line));
                else if(otherkisy.get(line) != null)
                    out.println(line+","+otherkisy.get(line));
                else
                    out.println(line+","+2);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    //othersyaryoIndexの修正
    private static void formatingOthers(){
        //othersyaryoIndexの修正
        SyaryoTemplateToJSON json = new SyaryoTemplateToJSON();
        Map<String, SimpleTemplate> all = json.ToTemplate(sourcePath+"allsyaryo_index.json");
        Map<String, SimpleTemplate> other = json.ToTemplate(sourcePath+"othersyaryo_index.json");
        
        Map<String, List> otherKisyMap = new HashMap();
        //Other kisyMap
        for(String name : other.keySet()){
            String kisy = name.split("-")[0];
            if(otherKisyMap.get(kisy) == null)
                otherKisyMap.put(kisy, new ArrayList());
            otherKisyMap.get(kisy).add(name);
        }
            
        
        for(String name : all.keySet()){
            String kisy = name.split("-")[0];
            if(otherKisyMap.get(kisy) != null){
                System.out.println("kisy:"+kisy);
                for(Object n : otherKisyMap.get(kisy)){
                    System.out.println("\t"+n);
                    other.remove(n);
                }
                otherKisyMap.remove(kisy);
            }
        }
        
        json.toJSON(sourcePath+"othersyaryo_index.json", other);
    }
}
