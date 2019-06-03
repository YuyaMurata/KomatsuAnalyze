/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import static data.eval.EvaluateTemplate.LOADER;
import data.eval.MainteEvaluate;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17807
 */
public class ExportMainteData {

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoAnalizer.rejectSettings(false, false, false);
        MainteEvaluate mainte = new MainteEvaluate();
        Map<String, Map<String, List<String>>> map = mainte.getdata(LOADER.getSyaryoMap());

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_メンテ評価元データ.csv")) {
            pw.println("SID,SMR,項目,系列");
            map.entrySet().stream().forEach(d -> {
                try (SyaryoAnalizer s = new SyaryoAnalizer(LOADER.getSyaryoMap().get(d.getKey()), Boolean.TRUE)) {
                    d.getValue().entrySet().stream()
                            .map(l -> d.getKey()+","+s.maxSMR[4]+","+l.getKey()+","+String.join(",", l.getValue()))
                            .forEach(pw::println);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                pw.println();
            });
        }
    }
}
