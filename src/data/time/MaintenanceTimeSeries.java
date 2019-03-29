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
import param.KomatsuUserParameter;

/**
 *
 * @author kaeru
 */
public class MaintenanceTimeSeries {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, Integer> interval = KomatsuUserParameter.PC200_MAINTEPARTS_INTERVAL;
    
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoObject syaryo = LOADER.getSyaryoMap().get("PC200-8N1-315586");

        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, true)) {
            //toTimeSeries(analize);
            series(analize, interval);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void series(SyaryoAnalizer s, Map<String, Integer> target){
        //各メンテ部品の交換実績を調査
        Map<String, List<String>> mainte = new TreeMap<>();
        List<String> sbns = new ArrayList<>(s.get("受注").keySet());
        sbns.stream()
                .filter(sbn -> s.getSBNParts(sbn) != null)
                .forEach(sbn -> {
                    s.getSBNParts(sbn).values().stream()
                                    .map(p -> PartsCodeConv.partsConv(LOADER, p))
                                    .filter(pdef -> target.get(pdef) != null)
                                    .forEach(pdef -> {
                                        if(mainte.get(pdef) == null)
                                            mainte.put(pdef, new ArrayList<>());
                                        mainte.get(pdef).add(sbn);
                                    });
                });
        
        //重複除去と変換
        Map<String, List<String>> map = new TreeMap<>();
        for(String key : mainte.keySet()){
            List<String> list = mainte.get(key).stream()
                        .distinct()
                        .map(sbn -> s.getDateToSMR(s.get("受注").get(sbn).get(LOADER.index("受注", "SGYO_KRDAY"))).toString())
                        .collect(Collectors.toList());
            map.put(key, list);
        }
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(s.get().name + "_mante_testseries.csv")) {
            pw.println("KEY,SMR系列");
            map.entrySet().stream()
                    .map(m -> m.getKey()+","+m.getValue().stream().map(smr -> smr).collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
    }
    
    private static Map<String, List<Long>> toTimeSeries(SyaryoAnalizer s) {
        System.out.println(s.get().name);

        List<String> dsmr = new ArrayList<>();
        String ds;
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

        List<String> c = Arrays.asList(new String[]{"A", "B", "E", "M", "K", "S", "U", "TH"});

        Map<String, List<Long>> out = new TreeMap();
        List<String> parts = new ArrayList<>();
        
        //時系列データ生成
        Map<String, List<String>> odr = s.get().get("受注");
        for (String sbn : odr.keySet()) {
            String d = odr.get(sbn).get(idx);
            Map<String, List<String>> p = s.getSBNParts(sbn);

            if (p == null) {
                continue;
            }
            
            List<Long> cnt = c.stream()
                .map(f -> p.values().stream()
                    .map(h -> PartsCodeConv.conv(h.get(idx2), h.get(idx3), h.get(idx4)).charAt(0))
                    .filter(dh -> dh.toString().equals(f)).count())
                .collect(Collectors.toList());
            
            if(out.get(d) != null){
                //マージ
                int i=0;
                for(Long n : out.get(d)){
                    cnt.set(i, cnt.get(i++)+n);
                }
            }
            out.put(d, cnt);
            
            //部品情報の取得
            parts.addAll(p.values().stream()
                .map(h -> sbn+","+d+","+h.get(idx2)+","+h.get(idx3)+","+PartsCodeConv.conv(h.get(idx2), h.get(idx3), h.get(idx4))+","+h.get(idx5)+","+h.get(idx4)).collect(Collectors.toList()));
        }

        if (out.get(b) == null) {
            out.put(b, c.stream().map(f -> 0L).collect(Collectors.toList()));
        }

        if (out.get(a) == null) {
            out.put(a, c.stream().map(f -> 0L).collect(Collectors.toList()));
        }

        for (String d1 : dsmr) {
            if (out.get(d1) == null) {
                out.put(d1, c.stream().map(f -> f.equals("TH") ? 100L : 0L).collect(Collectors.toList()));
            } else {
                out.get(d1).set(out.get(d1).size() - 1, 100L);
            }
        }

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS(s.get().name + "_mante_series.csv")) {
            pw.println("日付," + String.join(",", c));
            out.entrySet().stream()
                    //.filter(o -> !o.getKey().equals("None"))
                    .map(o -> toDate(o.getKey()) + "," + o.getValue().stream().map(v -> v==0L?"":v.toString()).collect(Collectors.joining(",")))
                    .forEach(pw::println);
        }
        
        /*try (PrintWriter pw = CSVFileReadWrite.writerSJIS(s.get().name + "_mante_series_detail.csv")) {
            pw.println("日付,作番,品番,品名,変換コード,数量,金額");
            parts.stream()
                    .forEach(pw::println);
        }*/

        return out;
    }

    private static String toDate(String date) {
        if(date.equals("None"))
            return date;
        return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
    }
}
