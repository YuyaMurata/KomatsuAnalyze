/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 * 車両評価
 *
 * @author ZZ17390
 */
public class EvaluateSyaryoObject {

    public Map<String, Map<String, Map<String, Double>>> eval = new LinkedHashMap<>();
    public String name, company;
    public Integer day, smr, rent;

    //負荷リスト
    private static List<String> USELIST = KomatsuDataParameter.DATA_ORDER.stream()
            .filter(d -> d.contains("LOADMAP_実エンジン回転") || d.contains("LOADMAP_ポンプ圧MAX"))
            .collect(Collectors.toList());

    public EvaluateSyaryoObject(SyaryoObject syaryo) {
        try (SyaryoAnalizer analize = new SyaryoAnalizer(syaryo, false)) {
            this.name = analize.get().name;
            this.company = analize.mcompany;
            this.day = analize.currentAge_day;
            this.smr = analize.maxSMR[3];

            eval.put("use", useData(analize));
            //eval.put("age/smr", agingSMRData(analize));
            eval.put("mainte", mainteData(analize));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(syaryo.name);
            System.exit(0);
        }
    }

    //評価結果をさらに評価する場合に利用
    public void addEval(String data, String kind, Map<String, Double> addData) {
        eval.get(data).put(kind, addData);

        //ヘッダ登録
        switch (data) {
            case "use":
                UseEvaluate.addHeader(kind, new ArrayList<>(addData.keySet()));
        }
    }

    /**
     * 使われ方のデータ 定義 : 累積負荷 / SMR
     *
     */
    private Map<String, Map<String, Double>> useData(SyaryoAnalizer syaryo) {
        //負荷マップを正規化
        Map<String, Map<String, Double>> loadmap = UseEvaluate.nomalize(syaryo, USELIST);
        
        return loadmap;
    }

    /**
     * 経年/SMRのデータ 定義 : v[経年, ACT_SMR]
     */
    private Map agingSMRData(SyaryoAnalizer syaryo) {
        return null;
    }

    /**
     * メンテナンスデータ 定義 : v[エンジンメンテ, 油圧メンテ, 定期メンテ] エンジンメンテ = エンジンOIL交換回数 / (SMR /
     * インターバル) 油圧メンテ = 作動機オイル交換 / (SMR / インターバル) 定期メンテ = 作業形態回数 / (経年 / インターバル)
     */
    private Map<String, Map<String, Double>> mainteData(SyaryoAnalizer syaryo) {
        Map<String, Double> quantity = MainteEvaluate.aggregate(syaryo, "-1", "-1");
        Map<String, Double> quality = MainteEvaluate.nomalize(quantity, smr, day);

        Map<String, Map<String, Double>> mainte = new HashMap<>();
        mainte.put("quantity", quantity);
        mainte.put("quality", quality);

        return mainte;
    }

    //評価結果をリストで取得
    public List<Double> getData(String select, String kind) {
        if (eval.get(select).get(kind) == null) {
            return getHeaderList(select, kind).stream().map(h -> 0d).collect(Collectors.toList());
        }
        return new ArrayList(eval.get(select).get(kind).values());
    }
    
    //内部での定義データ
    public static List<String> getDataList(String select) {
        switch (select) {
            case "mainte":
                return Arrays.asList(new String[]{"quantity", "quality"});
            case "use":
                return USELIST;
        }

        return new ArrayList<>();
    }

    //ヘッダ情報を取得
    public static List<String> getHeaderList(String select, String kind) {
        switch (select) {
            case "mainte":
                return MainteEvaluate.header();
            case "use":
                return UseEvaluate.header(kind);
        }

        return new ArrayList<>(Arrays.asList(new String[]{"None"}));
    }

    //出力用(ヘッダ)
    public static String printh(String select, String kind) {
        return String.join(",", getHeaderList(select, kind));
    }

    //出力用(評価結果)
    public String print(String select, String kind) {
        return getData(select, kind).stream().map(v -> v.toString()).collect(Collectors.joining(","));
    }
}
