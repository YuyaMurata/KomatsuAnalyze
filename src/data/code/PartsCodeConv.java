/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.code;

import java.util.List;
import java.util.Optional;
import obj.SyaryoLoader;
import param.KomatsuUserParameter;

/**
 * 品番の変換プログラム
 * @author ZZ17390
 */
public class PartsCodeConv {
    public static String partsConv(SyaryoLoader LOADER, String sid, String id, List<String> parts){
        String comp = parts.get(LOADER.index("部品", "会社CD"));
        String pid = parts.get(LOADER.index("部品", "HNBN"));
        String pn = parts.get(LOADER.index("部品", "BHN_NM"));
        String ppr = parts.get(LOADER.index("部品", "SKKG"));
        
        //ユーザー定義
        String ucvparts = uconv(sid, comp, id, pid, pn);
        if(ucvparts != null)
            return ucvparts;
        
        //自動定義
        String cvparts = conv(pid, pn, ppr);
        
        return cvparts;
    }
    
    public static String uconv(String sid, String comp, String id, String pid, String name) {
        String define = userPartsDefineCode(sid, comp, id, pid, name);
        return define;
    }
    
    public static String conv(String pid, String name, String price) {
        String define = null;
        
        //品番判断
        
        //コマツ品番ではない
        if (!pid.contains("-"))
            define = "UNKNOWN";
        
        //KES
        if (define == null) 
            define = kesPartsDefineCode(pid);
        
        //メンテナンス
        if(define == null)
            define = maintePartsDefineCode(pid);
        
        //主要部品
        if(define == null)
            define = mainPartsDefineCode(pid);
        
        if(define == null)
            return "UNKNOWN";
        else
            return define;
    }

    /**
     * 主要部品の変換 (品番体系で解釈可能) 
     * 車体部品 : BXXX <- XXX-○○-XXXXX
     * エンジン部品 : EXXX <- 6XX-○○X-XXXX 6XXX-○○-○XXX 
     * 付属部品 : AXXX <- XXX-8○○-XXXX, XXXX-9○○-XXXX 未使用
     * サービス : SVXXX　<- 7XX-XXX-XXXX
     *
     * origin code @return redefined code
     * @param origin
     * @return 
     */
    public static String mainPartsDefineCode(String origin) {
        String define = "";

        //主要部品以外除外
        if (!origin.matches("^[0-9]{1}[0-9A-Z]{2,4}-[0-9]{2,3}-[0-9A-Z]{4,}$")) {
            return null;
        }
        
        String kind = origin.split("-")[0];
        String dev = origin.split("-")[1];
        String indv = origin.split("-")[2];

        //主要部品
        if (kind.charAt(0) == '6') {
            define = "E";
            if (kind.length() == 4) {
                dev = dev.charAt(0) + "1" + indv.charAt(0);
            } else {
                dev = dev.substring(0, 2);
            }
        } else if (kind.charAt(0) == '7') {
            define = "SV";
        } else if (dev.length() == 3 && (dev.charAt(0) == '8' || dev.charAt(0) == '9')) {
            define = "A";
        } else {
            define = "B";
        }

        //装置
        while (dev.length() < 3) {
            dev = "0" + dev;
        }
        define = define + dev;

        //ユーザー定義による部品定義
        //String redefine = KomatsuUserParameter.PC200_MAINPARTS_DEF.get(define);
        //Check
        //codeCheck(origin, define);
        //return define;
        return define;
    }
    
    //メンテナンス部品の変換
    public static String maintePartsDefineCode(String origin) {
        Optional<String> define = KomatsuUserParameter.PC200_MAINTEPARTS_DEF.entrySet().stream()
                .filter(p -> origin.matches(p.getKey()))
                .map(p -> p.getValue())
                .findFirst();

        if (define.isPresent()) {
            return define.get();
        } else {
            return null;
        }
    }
    
    //KES部品の変換
    public static String kesPartsDefineCode(String origin){
        if(origin.matches("^[0-9A-Z]{5}-[0-9A-Z]{5}$")){
            return "KES";
        }else
            return null;
    }
    
    //ユーザー定義
    public static String userPartsDefineCode(String sid, String comp, String psbn, String pid, String name){
        if(KomatsuUserParameter.PC200_USERPARTS_DEF.check(sid))
            return KomatsuUserParameter.PC200_USERPARTS_DEF.defineName(sid, new String[]{comp,psbn.split("#")[0],pid,name});
        else
            return null;
    }

    private static void codeCheck(String origin, String define) {
        System.out.println(define + ":" + origin);
    }
}
