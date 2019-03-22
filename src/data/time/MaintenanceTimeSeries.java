/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import analizer.SyaryoAnalizer;
import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author kaeru
 */
public class MaintenanceTimeSeries {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile("PC200_form_loadmap");
        SyaryoObject syaryo = LOADER.getSyaryoMap().get("PC200-10-450635");

        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, true)) {
            toTimeSeries(analize);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static List<Integer> toTimeSeries(SyaryoAnalizer s) {
        System.out.println(s.get().name);

        List<String> dsmr = new ArrayList<>();
        String ds = "";
        Integer smr = 550;
        while ((ds = s.getSMRToDate(smr)) != null) {
            dsmr.add(ds);
            smr += 550;
        }

        String b = s.lifestart;
        String a = s.lifestop;

        int idx = LOADER.index("受注", "SGYO_KRDAY");
        int idx2 = LOADER.index("部品", "HNBN");
        int idx3 = LOADER.index("部品", "BHN_NM");
        int idx5 = LOADER.index("部品", "JISI_SU");
        int idx4 = LOADER.index("部品", "SKKG");

        List<String> c = Arrays.asList(new String[]{"A", "B", "E", "M","S","U","TH"});

        Map<String, List<String>> out = new TreeMap();
        List<String> parts = new ArrayList<>();
        //時系列データ生成
        Map<String, List<String>> odr = s.get().get("受注");
        for (String sbn : odr.keySet()) {
            String d = odr.get(sbn).get(idx);
            Map<String, List<String>> p = s.getSBNParts(sbn);

            if (p == null) {
                continue;
            }

            List<String> cnt = c.stream()
                .map(f -> p.values().stream()
                    .map(h -> PartsCodeConv.conv(h.get(idx2), h.get(idx3), h.get(idx4)).charAt(0))
                    .filter(dh -> dh.toString().equals(f)).count())
                .map(dh -> dh.toString())
                .map(dh -> dh.equals("0")?"":dh)
                .collect(Collectors.toList());
            out.put(d, cnt);
            
            //部品情報の取得
            parts.addAll(p.values().stream()
                .map(h -> sbn+","+d+","+h.get(idx2)+","+h.get(idx3)+","+PartsCodeConv.conv(h.get(idx2), h.get(idx3), h.get(idx4))+","+h.get(idx5)+","+h.get(idx4)).collect(Collectors.toList()));
        }

        if (out.get(b) == null) {
            out.put(b, c.stream().map(f -> "").collect(Collectors.toList()));
        }

        if (out.get(a) == null) {
            out.put(a, c.stream().map(f -> "").collect(Collectors.toList()));
        }

        for (String d1 : dsmr) {
            if (out.get(d1) == null) {
                out.put(d1, c.stream().map(f -> f.equals("TH") ? "100" : "").collect(Collectors.toList()));
            } else {
                out.get(d1).set(out.get(d1).size() - 1, "100");
            }
        }

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(s.get().name + "_mante_series.csv")) {
            pw.println("日付," + String.join(",", c));
            out.entrySet().stream()
                    .filter(o -> !o.getKey().equals("None"))
                    .map(o -> toDate(o.getKey()) + "," + String.join(",", o.getValue()))
                    .forEach(pw::println);
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(s.get().name + "_mante_series_detail.csv")) {
            pw.println("日付,作番,品番,品名,変換コード,数量,金額");
            parts.stream()
                    .forEach(pw::println);
        }

        return null;
    }

    private static String toDate(String date) {
        return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }
}
