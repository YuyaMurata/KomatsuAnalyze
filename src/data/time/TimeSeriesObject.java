/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.time;

import analizer.SyaryoAnalizer;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuUserParameter;
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17807
 */
public class TimeSeriesObject {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static UserPartsObject PARTS = KomatsuUserParameter.PC200_USERPARTS_DEF;

    public String sid;
    public String key;
    public String target;
    public List<String> series;

    public TimeSeriesObject(SyaryoObject s, String key, String target) {
        this.sid = s.name;
        this.key = key;
        this.target = target;
        this.series = toSeries(s, key, target);
    }

    private List<String> toSeries(SyaryoObject s, String key, String target) {
        List<String> t;
        if (key.equals("受注")) {
            t = partsToSeries(s, target);
        } else {
            t = komtraxErrorToSeries(s, key, target);
        }

        return t;
    }

    private List<String> partsToSeries(SyaryoObject s, String target) {
        List<String> t = new ArrayList<>();
        if (PARTS.check(s.name)) {
            t = PARTS.index.get(s.name).entrySet().stream() //ユーザー定義の部品
                    .filter(e -> e.getValue().equals(target)) //ターゲット以外の部品は除去
                    .map(e -> e.getKey()[1]) //ユーザー定義部品の作番情報
                    .map(sbn -> s.get("受注").get(sbn).get(LOADER.index("受注", "SGYO_KRDAY"))) //作業完了日
                    .collect(Collectors.toList());
        }

        return t;
    }

    private List<String> komtraxErrorToSeries(SyaryoObject s, String key, String target) {
        List<String> t = new ArrayList<>();
        s.startHighPerformaceAccess();
        if (s.get(key) != null) {
            t = s.get(key).entrySet().stream()
                    .filter(e -> e.getValue().get(LOADER.index(key, "ERROR_CODE")).equals(target))
                    .map(e -> e.getKey())
                    .distinct()
                    .collect(Collectors.toList());
        }
        s.stopHighPerformaceAccess();

        return t;
    }

    //test
    public static void main(String[] args) throws Exception {
        LOADER.setFile("PC200_form");

        //事故車両の除外
        Map<String, SyaryoObject> syaryos = new TreeMap<>();
        for (SyaryoObject s : LOADER.getSyaryoMap().values()) {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                if (a.numAccident == 0) {
                    syaryos.put(a.name, a.get());
                }
            }
        }

        Map<String, List<String>> test = new HashMap();
        syaryos.values().stream().forEach(s -> {
            TimeSeriesObject t = new TimeSeriesObject(s, "受注", "オルタネータ");
            if (t.series != null) {
                test.put(t.sid, t.series);
            }
        });
        System.out.println("オルタネータ　時系列");
        //test.entrySet().stream().map(e -> e.getKey()+","+e.getValue()).forEach(System.out::println);

        Map<String, List<String>> test2 = new HashMap();
        syaryos.values().stream().forEach(s -> {
            TimeSeriesObject t = new TimeSeriesObject(s, "KOMTRAX_ERROR", "AB00KE");
            if (t.series != null) {
                test2.put(t.sid, t.series);
            }
        });
        System.out.println("\nKOMTRAX_ERR[AB00KE]　時系列");
        //test2.entrySet().stream().map(e -> e.getKey()+","+e.getValue()).forEach(System.out::println);

        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("alt_ak_check.csv")) {
            List<String> sids = new ArrayList<>(test.keySet());
            sids.addAll(new ArrayList(test2.keySet()));
            sids = sids.stream().distinct().sorted().collect(Collectors.toList());

            pw.println("SID,オルタネータ,AB00KE");
            for (String sid : sids) {
                String cnt = null;
                String flg = "0";
                if (test2.get(sid) != null) {
                    String ed = test2.get(sid).get(test2.get(sid).size() - 1);
                    if (test.get(sid) != null && ed != null) {
                        try (SyaryoAnalizer a = new SyaryoAnalizer(syaryos.get(sid), true)) {
                            String sd = test.get(sid).get(test.get(sid).size() - 1);
                            int diff = a.getDateToSMR(sd).getValue() - a.getDateToSMR(ed.split("#")[0]).getValue();
                            flg = (0 <= diff && diff <= 30) ? "1" : "0";
                        }
                    }
                    cnt = syaryos.get(sid).get("KOMTRAX_ERROR").get(ed).get(LOADER.index("KOMTRAX_ERROR", "VALUE"));
                }
                pw.println(sid + "," + (test.get(sid) != null ? test.get(sid).size() : "0") + "," + (test2.get(sid) != null ? cnt : "0") + "," + flg);
            }
            pw.println("TOTAL," + sids.size());
        }
    }
}
