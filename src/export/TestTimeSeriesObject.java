/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public class TestTimeSeriesObject {

    static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static void main(String[] args) {
        LOADER.setFile("PC200_form");

        SyaryoObject s = LOADER.getSyaryoMap().get("PC200-8N1-351304");

        try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
            TimeSeriesObject ts = new TimeSeriesObject(a, "受注", "");

            //日付のマージ
            List<String> dates = new ArrayList();
            dates.addAll(new ArrayList(ts.series.values()));

            List<String> km = a.getItems("KOMTRAX_ERROR", LOADER.index("KOMTRAX_ERROR", "ERROR_CODE"));
            Map<String, List<String>> map = new HashMap();
            for (String cd : km) {
                List t = ts.komtraxErrorToSeries(a, cd).keySet().stream().map(d -> d.split("#")[0]).collect(Collectors.toList());
                map.put(cd, t);
                dates.addAll(t);
            }
            
            dates = dates.stream().distinct().sorted().collect(Collectors.toList());

            //output
            try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200-8N1-351304_test_series_smr.csv")) {
                //header
                pw.println("date,sv,"+String.join(",", km));
                
                for (String d : dates) {
                    List<String> out = new ArrayList<>();
                    out.add(a.getDateToSMR(d).getValue().toString());
                    //out.add(d);
                    out.add(ts.series.values().contains(d)?"1":"");
                    out.addAll(km.stream().map(cd -> map.get(cd).contains(d)?"1":"").collect(Collectors.toList()));
                    pw.println(String.join(",", out));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
