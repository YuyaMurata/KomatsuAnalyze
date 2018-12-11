/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.form;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import file.UserDefinedFile;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.LoadSyaryoObject;
import obj.SyaryoObject4;

/**
 *
 * @author ZZ17390
 */
public class SMRDataFormalize {

    private static String KISY = "PC200";

    //異常値候補
    static List<String> svErrValue = Arrays.asList(new String[]{"99", "999", "9999"});

    public static Map<String, List<String>> formSMR(Map<String, List<String>> data, int idx) {

        //異常値の除去
        Map<String, List<String>> f = data.entrySet().stream()
            .filter(e -> !svErrValue.contains(e.getValue().get(idx)))
            .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        return null;
    }

    public static List<String> formKMSMR(Map<String, List> data, int idx) {
        if (data == null) {
            return null;
        }

        List<String> out = new ArrayList<>();

        Deque<String> q = new ArrayDeque(10);
        int cnt = 0;
        Integer before = 0;
        for (String date : data.keySet()) {
            Integer v = Integer.valueOf(data.get(date).get(idx).toString());

            if (!q.isEmpty()) {
                //if (before > v) {//下がる範囲の抽出テスト
                int day = SyaryoAnalizer.time(q.peekLast(), date);
                int limit = day==1?30:24;
                if (Math.abs(before - v)/day > limit) { //上がる範囲の抽出テスト
                    out.add(q.peekLast() + "[" + before + "]:" + date + "[" + v + "]");
                    for (String d : q) {
                        out.add(d + "," + data.get(d).get(idx));
                    }
                    cnt = 10;
                }

            }

            if (cnt > 0) {
                out.add(date + "," + v);
                cnt--;
            }

            before = v;
            q.offerLast(date);
            if (q.size() > 10) {
                q.pollFirst();
            }
        }

        return !out.isEmpty() ? out : null;
    }

    public static void main(String[] args) {

        List<String> downErrorSyaryo = UserDefinedFile.filter("error_proc\\pc200_komtrax_smr_down_10.csv");
        System.out.println(downErrorSyaryo);

        Map<String, SyaryoObject4> syaryoMap = LoadSyaryoObject.load(KISY + "_km_form.bz2");

        Map<String, List> outMap = new TreeMap<>();

        syaryoMap.values().stream().forEach(s -> {
            if (!downErrorSyaryo.contains(s.name)) {

                List<String> str = formKMSMR(s.get("KOMTRAX_SMR"), 0);
                if (str != null) {
                    outMap.put(s.name, str);
                    System.out.println(s.name);
                    //str.stream().forEach(System.out::println);
                    //System.out.println("");
                }
            }
        });

        try (PrintWriter pw = CSVFileReadWrite.writer(KISY + "_KOMTRAX_SMR_異常値_10系列.csv")) {
            int max = outMap.values().stream().mapToInt(s -> s.size()).max().getAsInt();

            //Header
            pw.println(String.join(",,", outMap.keySet()));

            //Data
            for (int i = 0; i < max; i++) {
                List<String> line = new ArrayList<>();
                for (List<String> s : outMap.values()) {
                    if (i < s.size()) {
                        String d = s.get(i);

                        if (d.contains(":")) {
                            line.add(d + ",");
                        } else {
                            line.add(d);
                        }
                    } else {
                        line.add(",");
                    }
                }
                pw.println(String.join(",", line));
            }
        }
    }
}
