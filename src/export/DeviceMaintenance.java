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
import json.MapIndexToJSON;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class DeviceMaintenance {

	private static String kisy = "PC138US";

	public static void main(String[] args) {
		String filename = "json\\syaryo_obj_" + kisy + "_form";
		Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);

		String outputname = "device_smr_year_KM_" + kisy + ".csv";
		try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
			extractDeviceMaintenance(syaryoMap, csv);
		}catch(Exception e){
            e.printStackTrace();
        }
	}

	public static void extractDeviceMaintenance(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv) {
		int cnt = 0;
        //AS Index
        Map index = new MapIndexToJSON().reader("index\\allsupport_index.json");

		csv.println("Company,ID,Kisy,Type,業種コード,経年,SMR,作番,作業形態,作業コード,作業名,装置コード,対象,金額");
		for (SyaryoObject2 syaryo : syaryoMap.values()) {
			syaryo.decompress();

			cnt++;
			if (syaryo.getWork() == null) {
				System.out.println("作業なし："+syaryo.getName());

				continue;
			}
            if (syaryo.getOrder() == null) {
				System.out.println("受注なし："+syaryo.getName());
				continue;
			}

			TreeMap<Integer, String> gyousyuCode = new TreeMap();
			for (String date : syaryo.getOwner().keySet()) {
				if (date.contains("#")) {
					continue;
				}
				gyousyuCode.put(Integer.valueOf(date.replace("/", "")),
					(String) syaryo.getOwner().get(date).get(SyaryoElements.Customer.Code.getNo()));
			}

			TreeMap<Integer, String> smr = new TreeMap();
			for (String date : syaryo.getSMR().keySet()) {
				if (date.contains("#")) {
					continue;
				}
				smr.put(Integer.valueOf(date.replace("/", "")),
					(String) syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
			}

			StringBuilder sb = new StringBuilder();
			for (String date : syaryo.getWork().keySet()) {
				String company = (String) syaryo.getWork().get(date).get(SyaryoElements.Work.Company.getNo());
				String sbnID = (String) syaryo.getWork().get(date).get(SyaryoElements.Work.ID.getNo());
                String keitai = (String) syaryo.getOrder().values().stream()
                                                    .filter(o -> o.get(SyaryoElements.Order.ID.getNo()).equals(sbnID))
                                                    .map(o -> o.get(SyaryoElements.Order.SG_Code.getNo()))
                                                    .findFirst().get();
				String workID = (String) syaryo.getWork().get(date).get(SyaryoElements.Work.SCode.getNo());
				String workName = (String) syaryo.getWork().get(date).get(SyaryoElements.Work.SName.getNo());
				String price = (String) syaryo.getWork().get(date).get(SyaryoElements.Work.Price.getNo());
				
                //System.out.println(workID);
                String deviceID4 = "";
                try{
                    deviceID4 = workID.substring(0, 4);
                }catch(Exception e){
                    System.out.println(workID);
                }
                String deviceID2 = workID.substring(0, 2);
                
				Integer date_int = Integer.valueOf(date.replace("/", "").split("#")[0]);

				sb.append(company);
				sb.append(",");
				sb.append(syaryo.getName());
				sb.append(",");
				sb.append(syaryo.getMachine());
				sb.append(",");
				sb.append(syaryo.getType());
				sb.append(",");
				try {
					sb.append(gyousyuCode.floorEntry(date_int).getValue());
				} catch (NullPointerException e) {
                    try{
					sb.append(gyousyuCode.higherEntry(date_int).getValue());
                    }catch(NullPointerException ex){
                    sb.append("?");    
                    }
				}
				sb.append(",");
				sb.append(syaryo.getAge(date));
				sb.append(",");
				try {
					sb.append(smr.floorEntry(date_int).getValue());
				} catch (NullPointerException e) {
					sb.append(smr.higherEntry(date_int).getValue());
				}
				sb.append(",");
				sb.append(sbnID);
				sb.append(",");
				sb.append(keitai);
				sb.append(",");
				sb.append(workID);
				sb.append(",");
				sb.append(workName);
				sb.append(",");
                if(index.get(deviceID4) != null){
                    sb.append(deviceID4);
                    sb.append(",");
                    sb.append(index.get(deviceID4));
                } else if(index.get(deviceID2) != null){
                    sb.append(deviceID2);
                    sb.append(",");
                    sb.append(index.get(deviceID2));
                }else{
                    sb.append(deviceID4);
                    sb.append(",");
                    sb.append("0");
                }
				sb.append(",");
				sb.append(price);
				sb.append("\n");
			}

			csv.println(sb.toString());

			syaryo.compress(false);
		}
	}

}
