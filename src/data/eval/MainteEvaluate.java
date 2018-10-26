/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.eval;

/**
 *
 * @author ZZ17390
 */
public class MainteEvaluate {
    
    public static Double eval(String s, int num, int smr, double y){
        Double d = period(s, num, smr, y);
        if(d == null)
            d = parts(s, num, smr, y);
        
        return d;
    }
    
    //定期メンテ評価
    private static Double period(String sk, int num, int smr, double y) {
        double d = 0;
        switch (sk) {
            case "特定自主検査":
                d = (double) num / y;
                break;
            case "新車巡回":
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
                    double s = (smr / 500d) > 4 ? 4 : smr / 500d;
                    if(s >= 1)
                        d = (double) num / s;
                }
                break;
            case "エンジンオイル":
                double s = (smr / 500d);
                d = num / s;
                break;
        }
        
        return d;
    }

    //交換部品
    private static Double parts(String hnbn, int num, int smr, double y) {
        String p = hnbn.replace(" ", "");
        double d = 0;
        
        //500時間交換品
        if (p.equals("エンジンオイルフィルタ") || p.equals("燃料プレフィルタ") || p.equals("作動油タンクブリザード")) {
            d = num / (smr / 500d);
        }

        //1000時間
        if(p.equals("作動油フィルタ") ||  p.equals("燃料メインフィルタ")){
            d = num / (smr / 1000d);
        }
        
        //1年
        if(p.equals("エアコン内気フィルタ") || p.equals("エアコン外気フィルタ")){
            d = num / (y / 1d);
        }
        
        return d;
    }
}
