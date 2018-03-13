/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import static export.DeviceMaintenance.extractDeviceMaintenance;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import json.JsonToSyaryoObj;
import json.SyaryoToZip;
import obj.SyaryoElements;
import obj.SyaryoObject2;

/**
 *
 * @author ZZ17390
 */
public class AccidentData {

	private static String kisy = "WA470";

	public static void main(String[] args) {
		String filename = "json\\syaryo_obj_" + kisy + "_form";
		Map<String, SyaryoObject2> syaryoMap = new SyaryoToZip().readObject(filename);

		String outputname = "device_smr_year_" + kisy + ".csv";
		try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
			extractAccident(syaryoMap, csv);
		}
	}

	public static void extractAccident(Map<String, SyaryoObject2> syaryoMap, PrintWriter csv) {
		int cnt = 0;

		csv.println("Company,ID,Kisy,Type,業種コード,経年,SMR,作番,売上区分,金額,エラーコード,エラー種類,概要");
		for (SyaryoObject2 syaryo : syaryoMap.values()) {
			syaryo.decompress();
			
			cnt++;
			if (syaryo.getOrder() == null) {
				System.out.println(syaryo.getName());
				continue;
			}

			List<List> accident = syaryo.getOrder().values().stream()
				.filter(s -> s.get(SyaryoElements.Order.Summary.getNo()).toString().contains("横転")
				|| s.get(SyaryoElements.Order.Summary.getNo()).toString().contains("水没")
				|| s.get(SyaryoElements.Order.Summary.getNo()).toString().contains("転倒"))
				.collect(Collectors.toList());

			if (accident == null) {
				continue;
			}

			TreeMap<Integer, String> gyousyuCode = new TreeMap();
			for (String date : syaryo.getOwner().keySet()) {
				gyousyuCode.put(Integer.valueOf(date.replace("/", "")),
					(String) syaryo.getOwner().get(date).get(SyaryoElements.Customer.Code.getNo()));
			}

			TreeMap<Integer, String> smr = new TreeMap();
			for (String date : syaryo.getSMR().keySet()) {
				smr.put(Integer.valueOf(date.replace("/", "").split("#")[0]),
					(String) syaryo.getSMR().get(date).get(SyaryoElements.SMR._SMR.getNo()));
			}

			TreeMap<Integer, String> error = new TreeMap();
			if (syaryo.getError() != null) {
				for (String date : syaryo.getError().keySet()) {
					error.put(Integer.valueOf(date.replace("/", "").split("#")[0]),
						(String) syaryo.getError().get(date).get(SyaryoElements.Error.Code.getNo()) + ","
						+ (String) syaryo.getError().get(date).get(SyaryoElements.Error.Kind.getNo()));
				}
			} else {
				error.put(0, "None,None");
			}

			StringBuilder sb = new StringBuilder();
			for (List list : accident) {
				String date = (String) list.get(SyaryoElements.Order.Date.getNo());
				Integer date_int = Integer.valueOf(date.replace("/", ""));
				sb.append(list.get(SyaryoElements.Order.Company.getNo()));
				sb.append(",");
				sb.append(syaryo.getName());
				sb.append(",");
				sb.append(syaryo.getType());
				sb.append(",");
				sb.append(syaryo.getMachine());
				sb.append(",");
				try{
					sb.append(gyousyuCode.floorEntry(date_int).getValue());
				}catch(NullPointerException e){
					sb.append(gyousyuCode.higherEntry(date_int).getValue());
				}
				sb.append(",");
				sb.append(syaryo.getAge(date));
				sb.append(",");
				try{
					sb.append(smr.floorEntry(date_int).getValue());
				}catch(NullPointerException e){
					sb.append(smr.higherEntry(date_int).getValue());
				}
				sb.append(",");
				sb.append(list.get(SyaryoElements.Order.ID.getNo()));
				sb.append(",");
				sb.append(list.get(SyaryoElements.Order.FLAG.getNo()));
				sb.append(",");
				sb.append(list.get(SyaryoElements.Order.Invoice.getNo()));
				sb.append(",");
				sb.append(error.floorEntry(date_int).toString().split(",")[0]);
				sb.append(",");
				sb.append(error.floorEntry(date_int).toString().split(",")[1]);
				sb.append(",");
				sb.append(list.get(SyaryoElements.Order.Summary.getNo()));
				sb.append("\n");
			}
			csv.println(sb.toString());
			syaryo.compress(false);
		}
	}
}
