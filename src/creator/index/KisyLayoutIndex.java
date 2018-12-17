/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import file.MapToJSON;

/**
 *
 * @author ZZ17390
 */
public class KisyLayoutIndex {
    public static void main(String[] args) {
        
        Map kisyMap = createKisyMap();
        new MapToJSON().toJSON("index\\kisy_index.json", kisyMap);
        
        Map layoutMap = createLayoutMap();
        new MapToJSON().toJSON("index\\layout_index.json", layoutMap);
    }
    
    private static Map createKisyMap(){
        Map<String, List> kisyIndex = new LinkedHashMap();
        kisyIndex.put("_FORMAT", new ArrayList());
        kisyIndex.get("_FORMAT").add("機種名, Komtrax");
        kisyIndex.put("ALL", new ArrayList());
        kisyIndex.put("ENABLE", new ArrayList());
        
        try(BufferedReader kisy = CSVFileReadWrite.reader("resource\\kind\\機種.csv")){
            String k = "";
            while((k = kisy.readLine()) != null)
                kisyIndex.get("ALL").add(k);
        } catch (IOException ex) {
        }
        
        return kisyIndex;
    }
    
    private static Map createLayoutMap(){
        Map<String, List> layoutIndex = new TreeMap();
        layoutIndex.put("_FORMAT", new ArrayList());
        layoutIndex.get("_FORMAT").add("フィールド名, 説明");
        
        File[] flist = (new File("resource\\layout")).listFiles();
        
        for(File f : flist){
            String table = f.getName().replace("Layout_", "").replace(".csv", "");
            System.out.println(table);
            layoutIndex.put(table+"_ALL", new ArrayList());
            layoutIndex.put(table+"_ENABLE", new ArrayList());
            try(BufferedReader layout = CSVFileReadWrite.readerSJIS(f.getAbsolutePath())){
                String l = layout.readLine();
                while((l=layout.readLine()) != null){
                    layoutIndex.get(table+"_ALL").add(l.split(",")[2]+","+l.split(",")[1]);
                    
                    if(l.split(",").length > 5)
                        if(l.split(",")[5].equals("1"))
                            layoutIndex.get(table+"_ENABLE").add(l.split(",")[2]+","+l.split(",")[1]);
                }
            } catch (IOException ex) {
            }
        }
        
        return layoutIndex;
    }
}
