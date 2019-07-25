/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.survival;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author ZZ17807
 */
public class KaplanMeier {

    public void analize(Map<String, List<String>> data) {
        int idx_m = 0;
        int idx_u = 1;
        int idx_smr = 5;
        int idx_fstat = 6;

        //グループ分類
        Map<String, Map<String, List<String>>> group = new HashMap<>();
        data.entrySet().stream().forEach(d -> {
            String sid = d.getKey();
            String key = d.getValue().get(idx_m) + "," + d.getValue().get(idx_u);
            String smr = d.getValue().get(idx_smr);
            String fstat = d.getValue().get(idx_fstat);
            
            if (group.get(key) == null) {
                group.put(key, new HashMap<>());
            }
            group.get(key).put(sid, Arrays.asList(new String[]{smr, fstat}));
        });

        //グループごとの故障率
        group.entrySet().stream().forEach(g -> km(g.getKey(), g.getValue()));
    }
    
    public Map<String, List<String>> km(String gkey, Map<String, List<String>> g) {
        Map<Integer, List<String>> m = new TreeMap<>();
        g.entrySet().stream()
                .forEach(e -> {
                    Integer smr = Integer.valueOf(e.getValue().get(0));
                    if (m.get(smr) == null) {
                        m.put(smr, new ArrayList<>());
                    }
                    m.get(smr).add(e.getKey());
                });

        //故障率
        Map<Integer, Double> fail = new TreeMap<>();
        Long acm = 0L;
        for (Integer smr : m.keySet()) {
            if (fail.get(smr) == null) {
                fail.put(smr, 0d);
            }
            
            Long cnt = m.get(smr).stream()
                    .filter(sid -> g.get(sid).get(1).equals("1"))
                    .count();
            
            Long rem = m.entrySet().stream()
                            .mapToLong(e -> e.getValue().stream()
                                            .filter(sid -> e.getKey() >= smr || g.get(sid).get(1).equals("1"))
                                            .count())
                            .sum();
            
            acm += cnt;
            
            fail.put(smr, acm.doubleValue() / rem.doubleValue());
        }
        
        //Test
        try(PrintWriter pw = CSVFileReadWrite.writerSJIS(gkey+"_test.csv")){
            fail.entrySet().stream().map(df -> df.getKey()+","+df.getValue()).forEach(pw::println);
        }
        
        return null;
    }
}
