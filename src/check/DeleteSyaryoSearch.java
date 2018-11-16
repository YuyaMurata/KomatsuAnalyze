/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package check;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import json.SyaryoToZip3;
import obj.SyaryoObject4;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class DeleteSyaryoSearch {
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC200";
    private static String filename = PATH + "syaryo_obj_" + KISY + "_km_form.bz2";
    private static Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(filename);
    
    //ALL Syaryo
     private static String all = "resource\\車両マスタ.csv";
    
    public static void main(String[] args) {
        List<String> kisySyaryo = getAllSyaryo();
        
        for(String id : kisySyaryo){
            if(syaryoMap.get(id) == null)
                System.out.println(id);    
        }
            
        System.out.println("all="+kisySyaryo.size()+" obj="+syaryoMap.size());
    }
    
    private static List<String> getAllSyaryo(){
        List s = new ArrayList();
        try(BufferedReader csv = CSVFileReadWrite.reader(all)){
            String line = csv.readLine();
            while((line = csv.readLine()) != null){
                if(!line.split(",")[0].equals(KISY))
                    continue;
                
                String id = line.split(",")[0]+"-"+line.split(",")[1]+line.split(",")[2]+"-"+line.split(",")[3];
                s.add(id);
            }
        } catch (IOException ex) {
        }
        
        return s;
    }
}
