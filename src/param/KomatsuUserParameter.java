/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import java.util.Map;
import json.MapIndexToJSON;

/**
 *
 * @author ZZ17390
 */
public interface KomatsuUserParameter {
    public static String PC200_ERRFILTER_FILE="user\\PC200_errorfilter_180828.txt";
    
    //部品フィルタ
    public static String PC200_PARTSFILTER_FILE="user\\PC200_partsfilter_180921.txt";
    public static Integer PARTS_FILTER_PRICE = 0;
    
    //部品コードの再定義
    public static String PC_PARTS_REDEF_INDEX_PATH = "user\\pc200_parts_redefine.json";
    public static Map<String, String> PC_PARTS_REDEF = new MapIndexToJSON().reader(PC_PARTS_REDEF_INDEX_PATH);
    
    //評価行列
    public static String PC200_PARTS_EVAL_FILE="PC200_partscd_evalarray.csv";
    public static String PC200_KMERR_EVAL_FILE="PC200_kmerrcd_evalarray.csv";
}
