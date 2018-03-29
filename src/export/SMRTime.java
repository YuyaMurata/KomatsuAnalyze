/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class SMRTime {
    private static String kisy = "PC138US";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form";
        Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);
        
        String outputname = "max_smr_time_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractMaxSMRandTime(syaryoMap, csv);
        }
    }
    
    private static void extractMaxSMRandTime(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv){
        int cnt = 0;

        csv.println("ID,Kisy,Type,MaxSMR,経年");
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            //System.out.println(syaryo.getName());
            syaryo.decompress();
            
            if(syaryo.getSMR() == null){
                System.out.println("SMRなし:"+syaryo.getName());
                continue;
            }
            
            TreeMap<String, List> smr = new TreeMap(syaryo.getSMR());
            String maxsmr = (String) smr.get(smr.lastKey()).get(SyaryoElements.SMR._SMR.getNo());
            String date = smr.lastKey();
                         
            //System.out.println(maxsmr+":"+date);
            
            StringBuilder sb = new StringBuilder();
            sb.append(syaryo.getName());
            sb.append(",");
            sb.append(syaryo.getMachine());
            sb.append(",");
            sb.append(syaryo.getType());
            sb.append(",");
            sb.append(maxsmr);
            sb.append(",");
            sb.append(syaryo.getAge(date));
            
            csv.println(sb.toString());
            
            syaryo.compress(false);
        }
    }
}
