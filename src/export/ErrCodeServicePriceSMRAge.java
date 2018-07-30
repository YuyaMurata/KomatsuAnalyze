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
import json.SyaryoToZip3;
import obj.SyaryoObject4;
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
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(PATH+"syaryo_obj_"+KISY+"_form.bz2");
        List<SyaryoObject4> analyzeSyaryo = new ArrayList();
        //分析対象を絞る
        for(SyaryoObject4 syaryo : syaryoMap.values()){
            syaryo.decompress();
            if((syaryo.get("KOMTRAX_ERROR") != null) && 
                (syaryo.get("受注") != null) &&
                (syaryo.get("KOMTRAX_SMR") != null)){
                analyzeSyaryo.add(syaryo);
            }
            syaryo.compress(true);
        }
        
        //データ出力
        try(PrintWriter pw = CSVFileReadWrite.writer(filename)){
            for(SyaryoObject4 syaryo : analyzeSyaryo){
                SyaryoAnalizer analyzer = new SyaryoAnalizer(syaryo);
                
            }
        }
    }
}
