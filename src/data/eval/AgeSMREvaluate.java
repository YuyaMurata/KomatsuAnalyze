/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17807
 */
public class AgeSMREvaluate {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    
    public static void main(String[] args) {
        LOADER.setFile(KISY+"_loadmap");
        
        //クラスタリング結果の読み込み
        List<String> clus = ListToCSV.toList(KomatsuUserParameter.AZ_PATH+"user\\PC200_クラスタリング結果.csv");
        clus.remove(0);
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_suvivaldata.csv")) {
            //pw.println("SID,メンテナンス評価,使われ方評価,ADMIT_D,FOLD_D,Y,SMR,FSTAT,AGE,ACCIDENT");
            pw.println("SID,メンテナンス評価,使われ方評価,SBN,"+String.join(",", LOADER.indexes("受注"))+",DATE,SMR,SMR2");
            for(String c : clus){
                String sid = c.split(",")[0];
                SyaryoObject s = LOADER.getSyaryoMap().get(sid);
                
                try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                    a.rejectAttachement();
                    a.rejectManiteData();
                    a.rejectSellParts();
                    
                    Integer accident = a.numAccident > 0 ? 1 : 0;
                    
                    TimeSeriesObject t = new TimeSeriesObject(a, "受注", "");
                    
                    if (!t.series.isEmpty()) {
                        //System.out.println(t.sid+":"+t.series);
                        Map.Entry smr = a.getDateToSMR(t.series.get(0));
                        //pw.println(c + "," + a.lifestart + "," + t.series.get(0) + "," + smr.getKey() + "," + smr.getValue() + ",1,"+(a.currentAge_day/365)+","+accident);
                        pw.println(c+","+t.getFirstOrder(a) + "," +t.series.get(0)+","+ smr.getKey() + "," + smr.getValue());    
                    }else{
                        Map.Entry smr = a.getDateToSMR(a.lifestop);
                        //pw.println(c + "," + a.lifestart + "," + a.lifestop + "," + smr.getKey() + "," + smr.getValue() + ",0,"+(a.currentAge_day/365)+","+accident);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
