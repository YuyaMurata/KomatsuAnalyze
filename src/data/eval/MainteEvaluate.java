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
        //経年の数値を切捨て
        //y = Math.floor(y);
        
        Double d = period(s, num, smr, y);
        if(d == null)
            d = parts(s, num, smr, y);
        
        //1.0を超える場合は1.0に固定
        if(d != null)
            if(d > 1)
                d = 1d;
        
        return d;
    }
    
    //定期メンテ評価
    private static Double period(String sk, int num, int smr, double y) {
        Double d = null;
        switch (sk) {
            case "特定自主検査":
                if(y < 1)
                    d = 1d;
                else
                    d = (double) num / y;
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
                if(smr > 500){ //500hを超える場合は評価
                    double s = (smr / 500d);
                    d = num / s;
                }else
                    d = 1d;
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
            if(smr > 500)
                d = num / (smr / 500d);
            else
                d = 1;
        }

        //1000時間
        if(p.equals("作動油フィルタ") ||  p.equals("燃料メインフィルタ")){
            if(smr > 1000)
                d = num / (smr / 1000d);
            else
                d = 1;
        }
        
        //5000時間
        if(p.equals("パワーラインオイル")){
            if(smr > 5000)
                d = num / (smr / 5000d);
            else
                d = 1;
        }
        
        //1年
        if(p.equals("エアコン内気フィルタ") || p.equals("エアコン外気フィルタ")){
            if(y >= 1)
                d = num / (y / 1d);
            else
                d = 1;
        }
        
        return d;
    }
}
