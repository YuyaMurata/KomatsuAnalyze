/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17390
 */
public class UseEvaluate {

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, List<String>> _header;
    private static Integer R = 3;
    private Map<String, Map<String, List<Double>>> _eval;

    public UseEvaluate() {
        _header = new HashMap<>();
        _eval = new HashMap<>();
    }
    
    public Map<String, List<String>> header() {
        return _header;
    }
    
    public List<String> longheader() {
        List<String> h = new ArrayList<>();
        int i = 0;
        for(String _h : _header.keySet()){
            h.addAll(_header.get(_h));
            h.set(i, _h+"|"+_header.get(_h).get(0));
            i += _header.get(_h).size();
        }
        
        return h;
    }

    //評価値取得
    public Map<String, Map<String, List<Double>>> evaluate(Map<String, SyaryoObject> map) {
        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, false)) {
                //集約
                Map<String, List<String>> data = aggregate(a);

                //正規化
                Map<String, List<Double>> norm = normalize(a, data);

                _eval.put(a.name, norm);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return _eval;
    }
    
    public Map<String, List<Double>> evaluate(SyaryoObject s) {
        Map<String, List<Double>> eval = new HashMap<>();

        try (SyaryoAnalizer a = new SyaryoAnalizer(s, false)) {
            //集約
            Map<String, List<String>> data = aggregate(a);

            //正規化
            eval = normalize(a, data);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return eval;
    }

    
    //データ取得
    public Map<String, Map<String, List<String>>> getdata(Map<String, SyaryoObject> map) {
        Map<String, Map<String, List<String>>> eval = new TreeMap<>();

        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, false)) {
                //集約
                eval.put(s.name, aggregate(a));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return eval;
    }
    
    public Map<String, List<String>> getdata(SyaryoObject s) {
        Map<String, List<String>> eval = new HashMap<>();
        
        try (SyaryoAnalizer a = new SyaryoAnalizer(s, false)) {
            //集約
            eval = aggregate(a);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return eval;
    }
    
    public Map<String, List<Double>> getClusData(){
        Map<String, List<Double>> data = _eval.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(), 
                            e -> _header.keySet().stream().flatMap(h -> e.getValue().get(h).stream()).collect(Collectors.toList())
                    ));
        
        return data;
    }

    private Map<String, List<Double>> normalize(SyaryoAnalizer a, Map<String, List<String>> rank) {
        Map norm = _header.keySet().stream()
                .collect(Collectors.toMap(
                        h -> h, 
                        h -> a.get(h).keySet().stream()
                                .map(l -> rank.get(h).contains(l) ? 1d : 0d)
                                .collect(Collectors.toList()),
                        (h1, h2) -> h1,
                        LinkedHashMap::new
                ));

        return norm;
    }

    private Map<String, List<String>> aggregate(SyaryoAnalizer a) {
        Map<String, List<String>> data = new HashMap<>();

        data.put("LOADMAP_実エンジン回転VSエンジントルク", engine(a.get("LOADMAP_実エンジン回転VSエンジントルク")));
        data.put("LOADMAP_エンジン水温VS作動油温", temperature(a.get("LOADMAP_エンジン水温VS作動油温")));
        
        if(_header.isEmpty() && !data.containsValue(null)){
            _header.put("LOADMAP_実エンジン回転VSエンジントルク", new ArrayList(a.get("LOADMAP_実エンジン回転VSエンジントルク").keySet()));
            _header.put("LOADMAP_エンジン水温VS作動油温", new ArrayList(a.get("LOADMAP_エンジン水温VS作動油温").keySet()));
        }
        
        return data;
    }

    private List<String> engine(Map<String, List<String>> map) {
        if(map == null)
            return null;
        
        //トルク10の値を削除  0=VALUE
        Map<String, Double> data = map.entrySet().stream()
                .filter(e -> !e.getKey().split("_")[1].contains("10"))
                .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(0))));

        //PEEK DESC RANK
        List<String> peekRank = rank(data, R);

        return peekRank;
    }
    
    private List<String> temperature(Map<String, List<String>> map) {
        if(map == null)
            return null;
        
        //0=VALUE
        Map<String, Double> data = map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> Double.valueOf(e.getValue().get(0))));

        //PEEK DESC RANK
        List<String> peekRank = rank(data, R);

        return peekRank;
    }
    
    private List<String> rank(Map<String, Double> data, int r){
        return data.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(R)
                .map(e -> e.getKey()).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        LOADER.setFile("PC200_loadmap");
        SyaryoObject s = LOADER.getSyaryoMap().get("PC200-10-450635");
        
        UseEvaluate use = new UseEvaluate();
        
        Map<String, List<String>> data = use.getdata(s);
        Map<String, List<Double>> result = use.evaluate(s);
        
        data.entrySet().stream().forEach(d ->{
            System.out.println(d.getKey());
            System.out.println("  "+d.getValue());
            System.out.println("  "+result.get(d.getKey()));
        });
    }
}
