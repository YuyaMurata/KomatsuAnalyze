/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import obj.SyaryoObject;

/**
 *
 * @author ZZ17807
 */
public abstract class EvaluateTemplate {
    public static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static Map<String, List<String>> _header;
    public Map<String, Map<String, Double>> _eval;
    
    public EvaluateTemplate(){
        _header = new HashMap<>();
        _eval = new HashMap<>();
    }
    
    //ヘッダ設定
    public void setHeader(String key, List<String> h){
        if(_header.get(key) == null){
            _header.put(key, h);
        }
    }
    
    //ヘッダ取得
    public List<String> header(String key) {
        return _header.get(key);
    }
    
    public abstract Map<String, List<String>> aggregate(SyaryoAnalizer s);
    
    public abstract Map<String, Double> normalize(SyaryoAnalizer s, String key, Map<String, List<String>> aggregatedata);
    
    public Map<String, Double> evaluate(String key, SyaryoAnalizer s){
        //集約
        Map<String, List<String>> data = aggregate(s);

        //正規化
        Map<String, Double> eval = normalize(s, key, data);
        
        //結果
        _eval.put(s.name, eval);
        
        return eval;
    }
    
    public Map<String, List<String>> getdata(SyaryoAnalizer s){
        //集約
        Map<String, List<String>> data = aggregate(s);
        
        return data;
    }
    
    public Map<String, Map<String, Double>> evaluate(String key, Map<String, SyaryoObject> map){
        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                evaluate(key, a);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return _eval;
    }
    
    //データ取得
    public Map<String, Map<String, List<String>>> getdata(Map<String, SyaryoObject> map){
        Map<String, Map<String, List<String>>> eval = new TreeMap<>();

        map.values().stream().forEach(s -> {
            try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {
                //集約
                eval.put(s.name, getdata(a));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return eval;
    }
    
    public Map<String, List<Double>> getClusterData(String key){
        Map<String, List<Double>> data = _eval.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> header(key).stream().map(h -> e.getValue().get(h)).collect(Collectors.toList())
                ));

        return data;
    }
    
    public abstract Map<String, Integer> scoring(Map<String, Integer> cluster, Map<String, List<Double>> data);
}
