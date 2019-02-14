/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class MainteEvaluate {

    private static Map<String, String> index = KomatsuDataParameter.PC_PID_DEFNAME;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();

    public static List<String> header() {
        List<String> h = new ArrayList<>(index.values());
        return h;
    }

    //SMRを期間で分割するメソッドは未実装であるため期間は-1で利用する
    public static Map<String, Integer> aggregate(SyaryoAnalizer s, String sd, String fd) {
        Map<String, Integer> map = new LinkedHashMap<>();

        //初期化
        for (String p : index.keySet()) {
            map.put(index.get(p), 0);
        }

        if (s.get().get("受注") == null) {
            return map;
        }

        //評価期間
        Integer st = !sd.equals("-1") ? Integer.valueOf(sd) : Integer.valueOf(s.lifestart);
        Integer sp = !fd.equals("-1") ? Integer.valueOf(fd) : Integer.valueOf(s.lifestop);

        int d_idx = LOADER.index("受注", "ODDAY");
        int pd_idx = LOADER.index("受注", "SGYO_KTICD");
        int p_idx = LOADER.index("部品", "HNBN");
        s.get().get("受注").entrySet().stream().filter(odr
            -> (st <= Integer.valueOf(odr.getValue().get(d_idx).toString()) //評価期間でフィルタリング
            && Integer.valueOf(odr.getValue().get(d_idx).toString()) <= sp))
            .forEach(odr -> {

                //定期点検
                String pd_m = index.get(odr.getValue().get(pd_idx).toString());
                if (pd_m != null) {
                    map.put(pd_m, map.get(pd_m) + 1);
                }

                //定期交換品
                Map<String, List<String>> parts = s.getSBNParts(odr.getKey());
                if (parts != null) {
                    //品番用のカウントロジック
                    parts.values().stream()
                        .map(p -> p.get(p_idx))
                        .map(p -> (p.contains("SYEO-TO") || p.contains("NYEO-TO")) ? "パワーラインオイル" : p)
                        .map(p -> (p.contains("SYEO") || p.contains("NYEO")) ? "エンジンオイル" : p)
                        .map(p -> index.get(p))
                        .filter(m -> m != null).distinct()
                        .forEach(m -> {
                            map.put(m, map.get(m) + 1);
                        });
                }
            });

        return map;
    }

    public static Map<String, Double> evaluate(Map<String, Integer> agmap, int termsmr, double termday) {
        Map<String, Double> map = new HashMap<>();
        agmap.entrySet().stream().forEach(ag -> {
            map.put(ag.getKey(), eval(ag.getKey(), ag.getValue(), termsmr, termday / 365d));
        });

        return map;
    }

    private static Double eval(String s, int num, int smr, double y) {
        //経年の数値を切捨て
        //y = Math.floor(y);

        Double d = period(s, num, smr, y);
        if (d == null) {
            d = parts(s, num, smr, y);
        }

        //1.0を超える場合は1.0に固定
        if (d != null) {
            if (d > 1) {
                d = 1d;
            }
        }

        return d;
    }

    //定期メンテ評価
    private static Double period(String sk, int num, int smr, double y) {
        Double d = null;
        switch (sk) {
            case "特定自主検査":
                if (y < 1) {
                    d = 1d;
                } else {
                    d = (double) num / y;
                }
                break;
            /*case "新車巡回":
                if(y >= (16/12))
                    d = (double) num / 3;
                else if(y >= (6 / 12))
                    d = (double) num / 2;
                else
                    d = (double) num / 1;
                break;
            case "コマツケア":
                if (y > 4) {
                    d = (double) num / 4d;
                } else {
                    double s;
                    if(y*500d > smr)
                        s = y;
                    else
                        s = (smr / 500d) > 4 ? 4 : smr / 500d;
                    if(s >= 1)
                        d = (double) num / s;
                }
                break;*/
            case "エンジンオイル":
                if (smr > 500) { //500hを超える場合は評価
                    double s = (smr / 500d);
                    d = num / s;
                } else {
                    d = 1d;
                }
                break;
        }

        return d;
    }

    //交換部品
    private static Double parts(String hnbn, int num, int smr, double y) {
        String p = hnbn.replace(" ", "");
        double d = 0;

        //500時間交換品
        if (p.equals("エンジンオイルフィルタ") || p.equals("燃料プレフィルタ") || p.equals("作動油タンクブリーザ")) {
            if (smr > 500) {
                d = num / (smr / 500d);
            } else {
                d = 1;
            }
        }

        //1000時間
        if (p.equals("作動油フィルタ") || p.equals("燃料メインフィルタ")) {
            if (smr > 1000) {
                d = num / (smr / 1000d);
            } else {
                d = 1;
            }
        }

        //5000時間
        if (p.equals("パワーラインオイル")) {
            if (smr > 5000) {
                d = num / (smr / 5000d);
            } else {
                d = 1;
            }
        }

        //1年
        if (p.equals("エアコン内気フィルタ") || p.equals("エアコン外気フィルタ")) {
            if (y >= 1) {
                d = num / (y / 1d);
            } else {
                d = 1;
            }
        }

        return d;
    }
}
