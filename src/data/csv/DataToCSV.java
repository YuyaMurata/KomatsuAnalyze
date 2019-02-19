/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.csv;

import analizer.SyaryoAnalizer;
import data.filter.MainteFilter;
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

        //mainte("PC200_parts_data.csv", dataHeader, syaryoMap);
        parts("作動油チェック.csv", LOADER.getSyaryoMap(), Arrays.asList(new String[]{"SYEO-T", "NYEO-T"}));
    }

    private static void mainte(String name, SyaryoObject header, Map<String, SyaryoObject> map) {
        int sgkt = header.get("受注").get("受注").indexOf("SGYO_KTICD");
        int kbn = header.get("受注").get("受注").indexOf("ODR_KBN");
        int hnbn = header.get("部品").get("部品").indexOf("HNBN");
        int hnnm = header.get("部品").get("部品").indexOf("BHN_NM");
        int price = header.get("部品").get("部品").indexOf("SKKG");
        int quant = header.get("部品").get("部品").indexOf("JISI_SU");

        try (PrintWriter pw = CSVFileReadWrite.writer(name)) {
            //Header
            pw.println("SBN,SGKT,ODR_KBN," + String.join(",", header.get("部品").get("部品")) + ",単価,m_detect,d_sgkt,d_hnbn,d_egoil,d_pwoil,d_kes,d_price");

            map.values().stream().forEach(s -> {
                s.startHighPerformaceAccess();
                
                System.out.println(s.name);
                
                Map<String, List<String>> p = s.get("部品");
                for (String id : p.keySet()) {
                    String sg = (String) s.get("受注").get(id.split("#")[0]).get(sgkt);
                    String odr = (String) s.get("受注").get(id.split("#")[0]).get(kbn);
                    String hn = (String) p.get(id).get(hnbn);
                    
                    Integer pr = -1, q = -1, u = -1;
                    try{
                        pr = Integer.valueOf((String) p.get(id).get(price));
                        q = Integer.valueOf((String) p.get(id).get(quant));
                        u = pr / q;
                    }catch(NumberFormatException e){
                        System.err.println(s.name+":"+id+"["+p.get(id).get(price)+","+p.get(id).get(quant)+"]");
                        continue;
                    }
                    List<String> csv = new ArrayList();
                    csv.add(id.split("#")[0]);
                    csv.add(sg);
                    csv.add(odr);
                    List newp = p.get(id);
                    newp.add(hnnm+1, KomatsuDataParameter.PC_PID_SYSDEFNAME.get(p.get(id).get(hnbn)));
                    csv.addAll(newp);
                    
                    csv.add(String.valueOf(pr/q));
                    
                    csv.add(MainteFilter.allDetect(sg, hn, u) ? "1" : "0");
                    csv.add(MainteFilter.skDetect(sg) ? "1" : "0");
                    csv.add(MainteFilter.partsDetect(hn) ? "1" : "0");
                    csv.add(MainteFilter.egoilDetect(hn) ? "1" : "0");
                    csv.add(MainteFilter.pwoilDetect(hn) ? "1" : "0");
                    csv.add(MainteFilter.kesDetect(hn) ? "1" : "0");
                    csv.add(MainteFilter.priceDetect(u) ? "1" : "0");

                    pw.println(String.join(",", csv));
                }
                
                s.stopHighPerformaceAccess();
            });
        }
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
