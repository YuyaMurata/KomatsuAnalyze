/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class LoadMapCreate {
    private static String INPATH = "C:\\Users\\zz17390\\Desktop\\output";
    private static String ROOTPATH = KomatsuDataParameter.MIDDLEDATA_PATH;
    private static String OUTPATH;
    
    private static String[] keys = new String[]{"SMR", "実エンジン回転VSエンジントルク", "作業機操作状況", "ポンプ圧_MAX", "ポンプ圧_F", "ポンプ圧_R", "作業モード選択状況", "エンジン水温VS作動油温", "ST_USERTIME", "ポンプ斜板_F", "ポンプ斜板_R", "可変マッチング"};
    
    public static void main(String[] args) {
        create("PC200");
    }
    
    public static void create(String kisy){
        OUTPATH = ROOTPATH + kisy + "\\shuffle\\";
        
        //Load File
        File[] flist = (new File(INPATH)).listFiles();
        for(File f : flist){
            if(!f.getName().contains("_output_hour.csv"))
                continue;
            
            System.out.println(f);
            try(BufferedReader csv = CSVFileReadWrite.readerSJIS(f.getAbsolutePath())){
                String line = "";
                Map<String, Map> temp = new HashMap();
                
                int idx = 0;
                String key = "LOADMAP_";
                while((line = csv.readLine()) != null){
                    System.out.println(line);
                    if(line.contains("smr")){
                        temp.put(key+keys[idx], new LinkedHashMap());
                        String[] sarr =  line.replace(" ", "").split(",");
                        temp.get(key+keys[idx++]).put("num", Arrays.asList(sarr).subList(1, 1));
                    }else if(line.contains("実エンジン回転VSエンジントルク")){
                        temp.put(key+keys[idx], new LinkedHashMap());
                        csv.readLine();
                        for(int i = 0; i < 12; i++){
                            line = csv.readLine();
                            String[] sarr =  line.replace(" ", "").split(",");
                            temp.get(key+keys[idx]).put(sarr[0], Arrays.asList(sarr).subList(1, 10));
                        }
                        idx++;
                    }else if(line.contains("作業機操作状況")){
                        temp.put(key+keys[idx], new LinkedHashMap());
                        csv.readLine();
                        line = csv.readLine();
                        temp.get(key+keys[idx++]).put("num", line.split(",")[1].replace(" ", ""));
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
