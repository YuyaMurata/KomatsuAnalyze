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
import json.MapIndexToJSON;
import json.SyaryoToZip;
import json.SyaryoToZip3;
import obj.SyaryoElements;
import obj.SyaryoObject2;
import obj.SyaryoObject3;

/**
 *
 * @author ZZ17390
 */
public class DeviceMaintenance {

	private static String kisy = "PC138US";

	public static void main(String[] args) {
		String filename = "json\\syaryo_obj_" + kisy + "_form.bz2";
		Map<String, SyaryoObject3> syaryoMap = new SyaryoToZip3().read(filename);

		String outputname = "smr_year_service_" + kisy + ".csv";
		try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
			extractDeviceMaintenance(syaryoMap, csv);
		}catch(Exception e){
            e.printStackTrace();
        }
	}

	public static void extractDeviceMaintenance(Map<String, SyaryoObject3> syaryoMap, PrintWriter csv) {
		int cnt = 0;
        //AS Index
        Map index = new MapIndexToJSON().reader("index\\allsupport_index.json");

		csv.println("Company,ID,Kisy,Type,業種コード,経過年,SMR,売上区分,作番,作業形態,作業コード,作業名,パワーライン対象装置,オールサポート,金額,事故フラグ,概要");
		for (SyaryoObject3 syaryo : syaryoMap.values()) {
            //System.out.print(syaryo.name);
			syaryo.decompress();
            
			cnt++;
			if (syaryo.getWork() == null) {
				System.out.println("作業なし："+syaryo.getName());
                syaryo.compress(false);
				continue;
			}
            if (syaryo.getOrder() == null) {
				System.out.println("受注なし："+syaryo.getName());
                syaryo.compress(false);
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
			for (String date : syaryo.getOrder().keySet()) {
				String company = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.Company.getNo());
				String sbnID = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.ID.getNo());
                String uagekbn = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.FLAG.getNo());
                String summary = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.Summary.getNo());
                String keitai = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.SG_Code.getNo());
				List workIDs = syaryo.getWork().entrySet().parallelStream().filter(e -> e.getKey().contains(date))
                                                                                .map(e -> e.getValue().get(SyaryoElements.Work.SCode.getNo()))
                                                                                .collect(Collectors.toList());
				List workNames = syaryo.getWork().entrySet().parallelStream().filter(e -> e.getKey().contains(date))
                                                                                .map(e -> e.getValue().get(SyaryoElements.Work.SName.getNo()))
                                                                                .collect(Collectors.toList());
				String price = (String) syaryo.getOrder().get(date).get(SyaryoElements.Order.Invoice.getNo());
				
                if(workIDs.isEmpty())
                    continue;
                
                //System.out.println(workID);
                
				Integer date_int = Integer.valueOf(date.replace("/", "").split("#")[0]);

				sb.append(company);
				sb.append(",");
				sb.append(syaryo.getName());
				sb.append(",");
				sb.append(syaryo.getMachine());
				sb.append(",");
				sb.append(syaryo.getType());
				sb.append(",'");
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
                Double age = Double.valueOf(syaryo.getAge(date)) / 365;
				sb.append(age);
				sb.append(",");
				try {
					sb.append(smr.floorEntry(date_int).getValue());
				} catch (NullPointerException e) {
					sb.append(smr.higherEntry(date_int).getValue());
				}
                sb.append(",'");
                sb.append(uagekbn);
				sb.append(",'");
				sb.append(sbnID);
				sb.append(",");
				sb.append(keitai);
				sb.append(",'");
				sb.append(workIDs.toString().replace(",", "|"));
				sb.append(",");
				sb.append(workNames.toString().replace(",", "|"));
				sb.append(",");
                sb.append(powerlineCheck(workIDs, index));
				sb.append(",");
                sb.append(syaryo.getAllSupport(date));
                sb.append(",");
				sb.append(price);
                sb.append(",");
                if(summary.contains("事故") || summary.contains("横転") || summary.contains("転倒") || summary.contains("水没") || summary.contains("火災"))
                    sb.append("1");
                else
                    sb.append("0");
                sb.append(",");
                sb.append(summary);
				sb.append("\n");
			}

			csv.println(sb.toString());

			syaryo.compress(false);
            
            //System.out.println(" OK");
		}
	}
    
    private static String powerlineCheck(List workIDs, Map index){
        for(Object id : workIDs){
            if(id.toString().length() > 3){
                String device4 = id.toString().substring(0, 4);
                if(index.get(device4) != null)
                    return "1";
            }
            
            String device2 = id.toString().substring(0, 2);
            if(index.get(device2) != null)
                return "1";
        }
        
        return "0";
    }
}
