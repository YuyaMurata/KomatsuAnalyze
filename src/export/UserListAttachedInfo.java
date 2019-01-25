/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.ListToCSV;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class UserListAttachedInfo {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String file = "user\\syaryo_list_anomaly_order.csv";
    private static String KISY = "PC200";

    private static List<String> ACCIDENT_WORDS = KomatsuDataParameter.ACCIDENT_WORDS;

    public static void main(String[] args) {
        addOrderInfo();
    }

    private static void addOrderInfo() {
        LOADER.setFile(KISY + "_sv_form");
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();

        int odrtxtidx = LOADER.index("受注", "GAIYO_1");
        int odrtxtidx2 = LOADER.index("受注", "GAIYO_2");
        int wrktxtidx = LOADER.index("作業", "SGYO_NM");

        List<String> list = ListToCSV.toList(file);
        try (PrintWriter pw = CSVFileReadWrite.writer(file+"_addinfo.csv")) {
            Boolean stflg = true;
            for (String str : list) {
                if (stflg) {
                    stflg = false;
                    str = str + ",事故,概要1,概要2,作業名1";
                    pw.println(str);
                    continue;
                }

                String[] d = str.split(",");

                String sbn = d[2];

                SyaryoObject syaryo = syaryoMap.get(d[0]);
                try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo)) {

                    String txt = syaryo.get("受注").get(sbn).get(odrtxtidx) + ","
                        + syaryo.get("受注").get(sbn).get(odrtxtidx2) + ","
                        + (analize.getSBNWork(sbn) != null ? analize.getSBNWork(sbn).values().stream().map(w -> w.get(wrktxtidx)).collect(Collectors.joining(",")) : "None");

                    //事故車両判定
                    Optional<String> accidents = ACCIDENT_WORDS.stream().filter(w -> txt.contains(w)).findFirst();
                    if (accidents.isPresent()) {
                        str = str + ",1,";
                    } else {
                        str = str + ",0,";
                    }

                    //受注情報の追加
                    pw.println(str+String.join(",", Arrays.asList(txt.split(",")).subList(0, 3)));
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
