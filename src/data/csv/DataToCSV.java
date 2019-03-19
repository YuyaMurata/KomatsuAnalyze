/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.csv;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class DataToCSV {
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");

        parts("作動油チェック.csv", LOADER.getSyaryoMap(), Arrays.asList(new String[]{"SYEO-T", "NYEO-T"}));
    }
    
    private static void parts(String filename, Map<String, SyaryoObject> map, List<String> targets) {
        int comp = LOADER.index("受注", "会社CD");
        int hnbn = LOADER.index("部品", "HNBN");
        int hnnm = LOADER.index("部品", "BHN_NM");
        int quant = LOADER.index("部品", "JISI_SU");
        int price = LOADER.index("部品", "SKKG");
        

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(filename)) {
            //Header
            pw.println("会社,SBN,HNBN,HNMN,合計数量,合計金額");
            
            map.values().stream().filter(syaryo -> syaryo.get("受注") != null).forEach(syaryo -> {
                try(SyaryoAnalizer s = new SyaryoAnalizer(syaryo, false)){
                    String sid = s.get().name;
                    Map<String, List<String>> odr = s.get().get("受注");
                    
                    for(String sbn : odr.keySet()){
                        String c = odr.get(sbn).get(comp);
                        Map<String, List<String>> p = s.getSBNParts(sbn);
                        if(p == null)
                            continue;
                        
                        List<List<String>> gp = new ArrayList<>();
                        
                        for(List<String> l : p.values()){
                            if(!targets.stream().filter(t -> l.get(hnbn).contains(t)).findFirst().isPresent())
                                continue;
                            
                            gp.add(l);
                        }
                        
                        if(gp.size() < 1)
                            continue;
                        
                        //集計
                        String hb = "";
                        String hn = "";
                        int num = 0;
                        int pr = 0;
                        for(List<String> l : gp){
                            hb += l.get(hnbn)+"_";
                            hn += l.get(hnnm)+"_";
                            num += Integer.valueOf(l.get(quant));
                            pr += Integer.valueOf(l.get(price));
                        }
                        
                        pw.println(sid+","+c+","+sbn+","+hb+","+hn+","+num+","+pr);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
