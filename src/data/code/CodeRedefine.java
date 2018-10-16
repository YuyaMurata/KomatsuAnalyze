/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.code;

import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class CodeRedefine {
    /**
     * 主要部品のみ (品番体系で解釈可能)
     * 車体部品 : BXX <- XXX-○○-XXXXX
     * エンジン部品 : EXX <- 6XX-○○X-XXXX 6XXX-○○-○XXX
     * 付属部品    : AXX <- XXX-8○○-XXXX, XXXX-9○○-XXXX
     * 未使用 サービス　<- 7XX-XXX-XXXX
     * @param origin code
     * @return  redefined code
     */
    public static String partsCDRedefine(String origin){
        String define = "";
        
        //汎用部品除外
        if(origin.split("-").length < 3)
            return null;
        
        String kind = origin.split("-")[0];
        String dev = origin.split("-")[1];
        String indv = origin.split("-")[2];
        
        
        //未使用除外
        if(kind.charAt(0) == '7')
            return null;
        
        //主要部品
        if(kind.charAt(0) == '6'){
            define = "E";
            if(kind.length() == 4)
                dev = dev.charAt(0)+ "1" + indv.charAt(0);
            else
                dev = dev.substring(0,2);
            
        }else if(dev.length() == 3 && (dev.charAt(0) == '8' || dev.charAt(0) == '9'))
            define = "A";
        else
            define = "B";
        
        //装置
        while(dev.length() < 3)
            dev = "0"+dev;
        define = define + dev;
        
        //ユーザー定義による部品定義
        String redefine = KomatsuUserParameter.PC_PARTS_REDEF.get(define);
        
        //Check
        //codeCheck(origin, define);
        
        //return define;
        return redefine;
    }
    
    private static void codeCheck(String origin, String define){
        System.out.println(define+":"+origin);
    }
}
