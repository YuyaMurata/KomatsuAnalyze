/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ErrCodeServicePriceSMRAge {
    private static String PATH = KomatsuDataParameter.SYARYOOBJECT_FDPATH;
    private static String KISY = "PC138US";
    private static String filename = "errcodeServiceSMRAge_"+KISY+".csv";
    public static void main(String[] args) {
        
    }
    
    public void export(){
        //Syaryo
        Map<String, SyaryoObject> syaryoMap = new SyaryoToCompress().read(PATH+"syaryo_obj_"+KISY+"_form.bz2");
        List<SyaryoObject> analyzeSyaryo = new ArrayList();
        //分析対象を絞る
        for(SyaryoObject syaryo : syaryoMap.values()){
            if((syaryo.get("KOMTRAX_ERROR") != null) && 
                (syaryo.get("受注") != null) &&
                (syaryo.get("KOMTRAX_SMR") != null)){
                analyzeSyaryo.add(syaryo);
            }
        }
        
        //データ出力
        try(PrintWriter pw = CSVFileReadWrite.writer(filename)){
            pw.println("SID,");
            for(SyaryoObject syaryo : analyzeSyaryo){
                SyaryoAnalizer analyzer = new SyaryoAnalizer(syaryo);
                
            }
        }
    }
}
