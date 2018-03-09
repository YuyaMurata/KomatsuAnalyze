/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import json.JsonToSyaryoObj;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class DeviceMaintenance {
    private static String kisy = "PC200";
    
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form.json";
        Map<String, SyaryoObject2> syaryoMap = new JsonToSyaryoObj().reader3(filename);

        String outputname = "device_smr_year_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractDeviceMaintenance(syaryoMap, csv);
        }
    }
    
    public static void extractDeviceMaintenance(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv){
        int cnt = 0;

        csv.println("Company,ID,Kisy,Type,業種コード,経年,SMR,作番,メーカー,品番,品名,装置コード,金額");
        for (SyaryoObject2 syaryo : syaryoMap.values()) {
            cnt++;
            if(syaryo.getParts() == null){
                System.out.println(syaryo.getName());
                continue;
            }
            
            TreeMap<Integer, String> gyousyuCode = new TreeMap();
            for(String date : syaryo.getOwner().keySet()){
                gyousyuCode.put(Integer.valueOf(date.replace("/", "")), 
                                (String)syaryo.getOwner().get(date).get(SyaryoElements.Customer.Code.getNo()));
            }
            
            TreeMap<Integer, String> smr = new TreeMap();
            for(String date : syaryo.getSMR().keySet()){
                smr.put(Integer.valueOf(date.replace("/", "")), 
                                (String)syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
            }
            
            StringBuilder sb = new StringBuilder();    
            for(String date : syaryo.getParts().keySet()){
                String company = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Company.getNo());
                String sbnID = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.ID.getNo());
                String marker = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.MAKER.getNo());
                String partsID = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.PID.getNo());
                String partsName = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.HName.getNo());
                String price = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Price.getNo());
                
                String deviceID = "?";
                if(partsID.split("-").length > 1)
                    deviceID = partsID.split("-")[1];
                else if(partsID.length() > 5)
                    deviceID = partsID.substring(5,2);
                
                Integer date_int = Integer.valueOf(date.replace("/", ""));
                    
                sb.append(company); sb.append(",");
                sb.append(syaryo.getName()); sb.append(",");
                sb.append(syaryo.getMachine()); sb.append(",");
                sb.append(syaryo.getType()); sb.append(",");
                sb.append(gyousyuCode.floorEntry(date_int).getValue()); sb.append(",");
                sb.append(syaryo.getAge(date)); sb.append(",");
                sb.append(smr.floorEntry(date_int).getValue()); sb.append(",");
                sb.append(sbnID); sb.append(",");
                sb.append(marker); sb.append(",");
                sb.append(partsID); sb.append(",");
                sb.append(partsName); sb.append(",");
                sb.append(deviceID); sb.append(",");
                sb.append(price);
                sb.append("\n");
            }
            
            csv.println(sb.toString());
        }
    }
    
}
