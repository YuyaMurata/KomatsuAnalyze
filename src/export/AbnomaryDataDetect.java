/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import static data.detect.AbnomalyDection.detectChiSquare;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class AbnomaryDataDetect {

    private static String KISY = "PC200";
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile(KISY + "_form");

        //工数分析
        detectSteps();
    }

    private static void detectSteps() {
        Map<String, SyaryoObject> syaryoMap = LOADER.getSyaryoMap();
        Map<String, String> data = new HashMap();
        
        int compidx = LOADER.index("受注", "会社CD");
        int smidx1 = LOADER.index("受注", "GAIYO_1");
        int smidx2 = LOADER.index("受注", "GAIYO_2");
        int idx = LOADER.index("作業", "INV_KOS");

        syaryoMap.values().stream().filter(s -> s.get("受注") != null).forEach(s -> {
            try (SyaryoAnalizer sa = new SyaryoAnalizer(s, false)) {
                Map<String, List<String>> odr = s.get("受注");

                odr.keySet().stream().filter(sbn -> sa.getSBNWork(sbn) != null).forEach(sbn -> {
                    Map<String, List<String>> w = sa.getSBNWork(sbn);
                    Double st = w.values().stream().mapToDouble(l -> Double.valueOf(l.get(idx))).sum();

                    String key = s.name+"_"+sbn+"_"+w.get(sbn).get(compidx)+"_"+odr.get(sbn).get(smidx1)+"_"+odr.get(sbn).get(smidx2);
                    data.put(key, st.toString());
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });
        
        Map<String, String> map = detectChiSquare(data, 0.01, true);
        map.entrySet().stream().filter(s -> s.getValue().equals("1")).map(s -> s.getKey().replace("_", ",")+","+s.getValue()).forEach(System.out::println);
        
    }
}
