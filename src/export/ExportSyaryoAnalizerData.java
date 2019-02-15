/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class ExportSyaryoAnalizerData {

    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile(KISY+"_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        
        try (PrintWriter csv = CSVFileReadWrite.writer(KISY + "_syaryo_analize_summary.csv")) {
            csv.println(SyaryoAnalizer.getHeader());
            for (SyaryoObject syaryo : syaryoMap.values()) {
                System.out.println(syaryo.name);
                try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, true)) {
                    csv.println(analize.toPrint());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(0);
                }
            }
        }
    }
}
