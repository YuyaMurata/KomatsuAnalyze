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
    
    public static Double eval(String s, int num, int smr, int y){
        Double d = period(s, num, smr, y);
        if(d == null)
            d = parts(s, num, smr, y);
        
        return d;
    }
    
    //定期メンテ評価
    private static Double period(String sk, int num, int smr, int y) {
        switch (sk) {
            case "特定自主検査":
                return (double) num / y;
            case "新車巡回":
                double yrate = 3;
                if(y <= 2)
                    yrate = 3 * y / 2;
                return (double) num / yrate;
            case "コマツケア":
                if (y > 4) {
                    return (double) num / 4d;
                } else {
                    double s = (smr / 500d) > 4 ? 4 : smr / 500d;
                    return (double) num / s;
                }
            case "エンジンオイル":
                return num / (smr / 500d);
        }
        
        return null;
    }

    //交換部品
    private static Double parts(String hnbn, int num, int smr, int y) {
        String p = hnbn.replace(" ", "");
        
        //500時間交換品
        if (p.equals("エンジンオイルフィルタ") || p.equals("燃料プレフィルタ") || p.equals("作動油タンクブリザード")) {
            return num / (smr / 500d);
        }

        //1000時間
        if(p.equals("作動油フィルタ") ||  p.equals("燃料メインフィルタ")){
            return num / (smr / 1000d);
        }
        
        //1年
        if(p.equals("エアコン内気フィルタ") || p.equals("エアコン外気フィルタ")){
            return num / (y / 1d);
        }
        
        return -1d;
    }
}
