/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class SuvivalDataAnalysis {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        createSurviveData("受注", "", LOADER.getSyaryoMap());
    }

    private static void createSurviveData(String key, String target, Map<String, SyaryoObject> map) {
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_suvivaldata.csv")) {
            pw.println("SID,ADMIT_D,FOLD_D,Y,SMR,FSTAT,AGE");
            map.values().stream().forEach(s -> {
                TimeSeriesObject t = new TimeSeriesObject(new SyaryoAnalizer(s, false), key, target);

                try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                    if (!t.series.isEmpty()) {
                        //System.out.println(t.sid+":"+t.series);
                        Map.Entry smr = a.getDateToSMR(t.series.get(0));
                        pw.println(a.name + "," + a.lifestart + "," + t.series.get(0) + "," + smr.getKey() + "," + smr.getValue() + ",1,"+(a.currentAge_day/365));
                    }else{
                        Map.Entry smr = a.getDateToSMR(a.lifestop);
                        pw.println(a.name + "," + a.lifestart + "," + a.lifestop + "," + smr.getKey() + "," + smr.getValue() + ",0,"+(a.currentAge_day/365));
                    }
                } catch (Exception ex) {
                    System.exit(0);
                }
            });
        }
    }
}
