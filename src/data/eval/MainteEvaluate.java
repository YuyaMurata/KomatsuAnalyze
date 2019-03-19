/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

import analizer.SyaryoAnalizer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import obj.SyaryoLoader;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class MainteEvaluate {

    private static Map<String, String> index = KomatsuUserParameter.PC200_MAINTEPARTS_DEFNAME;
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private static List<String> _header;

    public static List<String> header() {
        return _header;
    }

    //SMRを期間で分割するメソッドは未実装であるため期間は-1で利用する
    public static Map<String, Double> aggregate(SyaryoAnalizer s, String sd, String fd) {
        Map<String, Double> map = new LinkedHashMap<>();
        
        if(_header == null)
            _header = index.values().stream().distinct().collect(Collectors.toList());
        
        if (s.get().get("受注") == null) {
            return null;
        }
        
        //初期化
        for (String h : header()) {
            map.put(h, 0d);
        }

        //評価期間
        Integer st = !sd.equals("-1") ? Integer.valueOf(sd) : Integer.valueOf(s.lifestart);
        Integer sp = !fd.equals("-1") ? Integer.valueOf(fd) : Integer.valueOf(s.lifestop);

        int d_idx = LOADER.index("受注", "ODDAY");
        int pd_idx = LOADER.index("受注", "SGYO_KTICD");
        int p_idx = LOADER.index("部品", "HNBN");
        int p_price_idx = LOADER.index("部品", "SKKG");
        s.get().get("受注").entrySet().stream().filter(odr
            -> (st <= Integer.valueOf(odr.getValue().get(d_idx)) //評価期間でフィルタリング
            && Integer.valueOf(odr.getValue().get(d_idx)) <= sp))
            .forEach(odr -> {

                //定期点検
                String pd_m = index.get(odr.getValue().get(pd_idx));
                if (pd_m != null) {
                    map.put(pd_m, map.get(pd_m) + 1);
                }

                //定期交換品
                Map<String, List<String>> parts = s.getSBNParts(odr.getKey());
                if (parts != null) {
                    //1作番の各メンテナンス合計部品金額
                    Map<String, Integer> price = new HashMap<>();

                    //品番用のカウントロジック
                    parts.values().stream()
                        .filter(p -> mainteCheck(p.get(p_idx)))
                        .forEach(p -> {
                            String m = mainteDefName(p.get(p_idx));
                            if (price.get(m) == null) {
                                price.put(m, 0);
                            }
                            price.put(m, price.get(m) + (Integer.valueOf(p.get(p_price_idx))));
                        });
                    
                    //金額で判定
                    price.entrySet().stream().forEach(p -> {
                        if (p.getKey().equals("パワーラインオイル")) {
                            if (p.getValue() >= 40000) {
                                map.put(p.getKey(), map.get(p.getKey()) + 1);
                            }
                        } else {
                            map.put(p.getKey(), map.get(p.getKey()) + 1);
                        }

                    });
                }
            });

        return map;
    }

    public static Boolean mainteCheck(String pid) {
        if (pid.contains("SYEO-T") || pid.contains("NYEO-T")) {
            return true;
        } else if (pid.contains("SYEO") || pid.contains("NYEO")) {
            return true;
        } else if (index.get(pid) != null) {
            return true;
        }

        return false;
    }

    public static String mainteDefName(String pid) {
        if (pid.contains("SYEO-T") || pid.contains("NYEO-T")) {
            return "パワーラインオイル";
        } else if (pid.contains("SYEO") || pid.contains("NYEO")) {
            return "エンジンオイル";
        } else {
            return index.get(pid);
        }
    }

    //正規化
    public static Map<String, Double> nomalize(Map<String, Double> agmap, int termsmr, double termday) {
        if(agmap == null)
            return null;
            
        Map<String, Double> map = new LinkedHashMap<>();
        agmap.entrySet().stream().forEach(ag -> {
            map.put(ag.getKey(), eval(ag.getKey(), ag.getValue(), termsmr, termday / 365d));
        });

        return map;
    }

    private static Double eval(String s, double num, int smr, double y) {
        //経年の数値を切捨て
        //y = Math.floor(y);

        if (smr < 0) //異常値は評価しない
        {
            return 0d;
        }

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
    private static Double period(String sk, double num, int smr, double y) {
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
    private static Double parts(String hnbn, double num, int smr, double y) {
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
