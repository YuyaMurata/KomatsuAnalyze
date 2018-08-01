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
import json.SyaryoToZip3;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class AccidentData {

    private static String kisy = "PC138US";

    public static void main(String[] args) {
        String filename = "syaryo\\syaryo_obj_" + kisy + "_form.bz2";
        Map<String, SyaryoObject4> syaryoMap = new SyaryoToZip3().read(filename);

        String outputname = "accident_" + kisy + ".csv";
        try (PrintWriter csv = CSVFileReadWrite.writer(outputname)) {
            extractAccident(syaryoMap, csv);
        }
    }

    public static void extractAccident(Map<String, SyaryoObject4> syaryoMap, PrintWriter csv) {
        int cnt = 0;

        csv.println("SID,SMR,金額,概要");
        for (SyaryoObject4 syaryo : syaryoMap.values()) {

            cnt++;
            if (syaryo.get("受注") == null) {
                System.out.println(syaryo.getName());
                continue;
            }

            Map<String, List> accident = syaryo.get("受注").entrySet().stream()
                .filter(s -> s.getValue().get(10).toString().contains("横転")
                || s.getValue().get(10).toString().contains("水没")
                || s.getValue().get(10).toString().contains("横転")
                || s.getValue().get(10).toString().contains("転倒"))
                .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));

            if (accident == null || accident.isEmpty() || syaryo.get("KOMTRAX_SMR") == null) {
                continue;
            }

            TreeMap<Integer, String> smr = new TreeMap();
            for (String date : syaryo.get("KOMTRAX_SMR").keySet()) {
                smr.put(Integer.valueOf(date.replace("/", "").split("#")[0]),
                    (String) syaryo.get("KOMTRAX_SMR").get(date).get(0));
            }

            StringBuilder sb = new StringBuilder();
            for (String sbn : accident.keySet()) {
                List<String> list = accident.get(sbn);
                String date = list.get(4);
                Integer date_int = Integer.valueOf(date.replace("/", ""));
                sb.append(syaryo.getName());
                sb.append(",");
                try {
                    sb.append(smr.floorEntry(date_int).getValue());
                } catch (NullPointerException e) {
                    sb.append(smr.higherEntry(date_int).getValue());
                }
                sb.append(",");
                sb.append(list.get(0));
                sb.append(",");
                sb.append(sbn);
                sb.append(",");
                sb.append(list.get(13));
                sb.append(",");
                    sb.append(list.get(10));
                sb.append("\n");
            }
            csv.println(sb.toString());
        }
    }
}
