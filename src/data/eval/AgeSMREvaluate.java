/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import data.time.TimeSeriesObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import obj.SyaryoLoader;

/**
 *
 * @author ZZ17807
 */
public class AgeSMREvaluate extends EvaluateTemplate{
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static String KISY = "PC200";
    public static int maxdatalength = 0;
    private int datalength = 0;
    
    
    public AgeSMREvaluate() {
    }
    
    /*public static void main(String[] args) {
        LOADER.setFile(KISY+"_loadmap");
        
        //クラスタリング結果の読み込み
        List<String> clus = ListToCSV.toList(KomatsuUserParameter.AZ_PATH+"user\\PC200_クラスタリング結果.csv");
        clus.remove(0);
        
        //特定のサービスを排除
        SyaryoAnalizer.rejectSettings(true, true, true);
        
        try (PrintWriter pw = CSVFileReadWrite.writerSJIS("PC200_suvivaldata.csv")) {
            //pw.println("SID,メンテナンス評価,使われ方評価,ADMIT_D,FOLD_D,Y,SMR,FSTAT,AGE,ACCIDENT");
            pw.println("SID,メンテナンス評価,使われ方評価,SBN,"+String.join(",", LOADER.indexes("受注"))+",DATE,SMR,SMR2");
            for(String c : clus){
                String sid = c.split(",")[0];
                SyaryoObject s = LOADER.getSyaryoMap().get(sid);
                
                try (SyaryoAnalizer a = new SyaryoAnalizer(s, true)) {                    
                    Integer accident = a.numAccident > 0 ? 1 : 0;
                    
                    TimeSeriesObject t = new TimeSeriesObject(a, "受注", "");
                    
                    if (!t.series.isEmpty()) {
                        //System.out.println(t.sid+":"+t.series);
                        Map.Entry smr = a.getDateToSMR(t.series.get(0));
                        //pw.println(c + "," + a.lifestart + "," + t.series.get(0) + "," + smr.getKey() + "," + smr.getValue() + ",1,"+(a.currentAge_day/365)+","+accident);
                        pw.println(c+","+t.getFirstOrder(a) + "," +t.series.get(0)+","+ smr.getKey() + "," + smr.getValue());    
                    }else{
                        Map.Entry smr = a.getDateToSMR(a.lifestop);
                        //pw.println(c + "," + a.lifestart + "," + a.lifestop + "," + smr.getKey() + "," + smr.getValue() + ",0,"+(a.currentAge_day/365)+","+accident);
                    }
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }*/

    @Override
    public Map<String, List<String>> aggregate(SyaryoAnalizer s) {
        Map<String, List<String>> data = new HashMap<>();
        TimeSeriesObject t = new TimeSeriesObject(s, "受注", "");
        
        //日付リスト
        data.put("受注", new ArrayList<>(t.series.values()));
        
        //ヘッダのための情報取得
        if(t.series.size() > maxdatalength)
            maxdatalength = data.get("受注").size();
        
        
        return data;
    }

    @Override
    public Map<String, Double> normalize(SyaryoAnalizer s, String key, Map<String, List<String>> aggregatedata) {
        Map norm = IntStream.range(0, aggregatedata.get(key).size()).boxed()
                            .collect(Collectors.toMap(
                                    i -> i.toString(), 
                                    i -> s.getDateToSMR(aggregatedata.get(key).get(i)).getValue().doubleValue(),
                                    (h1, h2) -> h2,
                                    LinkedHashMap::new
                                )
                            );
        
        return norm;
    }
    
    @Override
    public Map<String, List<Double>> getClusterData(String key){
        Map<String, List<Double>> data = _eval.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> new ArrayList<>(e.getValue().values())
                ));

        return data;
    }
    
    //Test
    public static void main(String[] args) {
        LOADER.setFile("PC200_form");
        SyaryoAnalizer.rejectSettings(false, false, false);
        SyaryoAnalizer s =  new SyaryoAnalizer(LOADER.getSyaryoMap().get("PC200-10-450635"), true);

        AgeSMREvaluate agesmr = new AgeSMREvaluate();

        Map<String, List<String>> data = agesmr.getdata(s);
        Map<String, Double> result = agesmr.evaluate("受注", s);

        data.entrySet().stream().forEach(d -> {
            System.out.println(d.getKey());
            System.out.println("  " + d.getValue());
            System.out.println("  " + result);
        });
        
        //クラスタ用データ
        System.out.println("\n"+agesmr.header("受注"));
        System.out.println(agesmr.getClusterData("受注"));
    }
}
