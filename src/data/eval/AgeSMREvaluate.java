/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.survival.KaplanMeier;
import data.time.TimeSeriesObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class AgeSMREvaluate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    public Map<String, List<String>> _eval;
    public static Map<String, List<String>> _header;

    public AgeSMREvaluate() {
        setHeader();
    }

    private void setHeader() {
        _header = new HashMap<>();
        _header.put("Age/SMR",
                Arrays.asList(new String[]{"メンテナンスCID", "使われ方CID", "ADMIT_D", "FOLD_D", "Y", "SMR", "FSTAT", "AGE"})
        );
    }

    public Map<String, List<String>> evaluate(Map<String, List<Integer>> cidresults, String key, String target) {
        Map<String, List<String>> map = new TreeMap<>();
        cidresults.entrySet().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(LOADER.getSyaryoMap().get(s.getKey()), true)) {
                map.put(a.name, new ArrayList<>());
                List<Integer> cids = s.getValue();
                
                TimeSeriesObject t = new TimeSeriesObject(a, key, target);
                
                if (!t.series.isEmpty()) {
                    //System.out.println(t.sid+":"+t.series);
                    Map.Entry smr = a.getDateToSMR(t.firstService());
                    map.get(a.name).addAll(Arrays.asList(new String[]{cids.get(0).toString(), cids.get(1).toString(), a.lifestart, t.firstService(), smr.getKey().toString(), smr.getValue().toString(),"1",String.valueOf(a.currentAge_day / 365)}));
                } else {
                    Map.Entry smr = a.getDateToSMR(a.lifestop);
                    map.get(a.name).addAll(Arrays.asList(new String[]{cids.get(0).toString(), cids.get(1).toString(),a.lifestart, a.lifestop, smr.getKey().toString(), smr.getValue().toString(),"0",String.valueOf(a.currentAge_day / 365)}));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(0);
            }
        });
        
        _eval = map;
        
        //Test
        new KaplanMeier().analize(_eval);
        
        return map;
    }
}
