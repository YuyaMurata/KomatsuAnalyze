/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import static export.DeviceMaintenance.extractDeviceMaintenance;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.Map;
import json.SyaryoToZip3;
import obj.SyaryoElements;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class PartsData {
    private static String kisy = "PC138US";
    public static void main(String[] args) {
        String filename = "json\\syaryo_obj_" + kisy + "_form.bz2";
		Map<String, SyaryoObject3> syaryoMap = new SyaryoToZip3().read(filename);
        
        String outputname = "parts_" + kisy + ".csv";
		try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractPartsData(syaryoMap, csv);
		}catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static void extractPartsData(Map<String, SyaryoObject3> syaryoMap, PrintWriter csv){
        csv.println("ID,Kisy,Type,会社コード,作番,品番,品名,数量,キャンセル数量,金額");
        
        int cnt=0;
		for (SyaryoObject3 syaryo : syaryoMap.values()) {
            //System.out.print(syaryo.name);
			syaryo.decompress();
        
            if (syaryo.getParts()== null) {
				System.out.println("部品なし："+syaryo.getName());
                syaryo.compress(false);
				continue;
			}
            
			for (String date : syaryo.getParts().keySet()) {
				String company = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Company.getNo());
                String sbnID = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.ID.getNo());
                String hinban = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.HID.getNo());
                String hname = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.HName.getNo());
                String num = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Quant.getNo());
                String cancel = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Cancel.getNo());
                String price = (String) syaryo.getParts().get(date).get(SyaryoElements.Parts.Price.getNo());
                
                StringBuilder sb = new StringBuilder();
                sb.append(syaryo.getName()).append(",");
                sb.append(syaryo.getMachine()).append(",");
                sb.append(syaryo.getType()).append(",");
                sb.append(company).append(",");
                sb.append(sbnID).append(",");
                sb.append(hinban).append(",");
                sb.append(hname).append(",");
                sb.append(num).append(",");
                sb.append(cancel).append(",");
                sb.append(String.valueOf(Double.valueOf(price).intValue()));
                
                csv.println(sb.toString());
            }
            cnt++;
            syaryo.compress(false);
            
            if(cnt%1000 == 0){
                System.out.println(cnt);
            }
        }
    }
}
