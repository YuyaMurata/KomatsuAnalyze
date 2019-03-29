/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import file.ListToCSV;
import java.util.Map;
import file.MapToJSON;
import java.util.List;

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
    public static Map<String, String> PC200_MAINPARTS_DEF = new MapToJSON().toMap("user\\PC200_mainparts_define.json");
    public static Map<String, String> PC200_MAINPARTS_DEFNAME = new MapToJSON().toMap("user\\PC200_mainparts_definename.json");
    
    //メンテナンス
    public static Map<String, String> PC200_MAINTEPARTS_DEF = new MapToJSON().toMap("user\\PC200_mainteparts_define.json");
    public static Map<String, String> PC200_MAINTEPARTS_DEFNAME = new MapToJSON().toMap("user\\PC200_mainteparts_definename.json");
    public static Map<String, Integer> PC200_MAINTEPARTS_INTERVAL = new MapToJSON().toMap("user\\PC200_mainteparts_interval.json");
    
    //評価行列
    public static String PC200_PARTS_EVAL_FILE="PC200_partscd_evalarray.csv";
    public static String PC200_KMERR_EVAL_FILE="PC200_kmerrcd_evalarray.csv";
    
    //削除車両
    public static List<String> PC200_REJECT_LIST = ListToCSV.toList("user\\PC200_rejectSID.csv");
    public static List<String> DEALER_REJECT_LIST = ListToCSV.toList("define\\reject_サブディーラ担当コード.csv");
}
