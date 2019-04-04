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
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17390
 */
public interface KomatsuUserParameter {
    //車両分析用
    public static String AZ_PATH = "settings\\analizer\\";
    
    //部品コードの再定義
    public static Map<String, String> PC200_MAINPARTS_DEF = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainparts_define.json");
    public static Map<String, String> PC200_MAINPARTS_DEFNAME = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainparts_definename.json");
    
    //メンテナンス
    public static Map<String, String> PC200_MAINTEPARTS_DEF = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_define.json");
    public static Map<String, String> PC200_MAINTEPARTS_DEFNAME = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_definename.json");
    public static Map<String, Integer> PC200_MAINTEPARTS_INTERVAL = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_interval.json");
    public static UserPartsObject PC200_USERPARTS_DEF = new UserPartsObject(AZ_PATH+"user\\PC200_parts_userdefine.json");
    
    //評価行列
    public static String PC200_PARTS_EVAL_FILE="PC200_partscd_evalarray.csv";
    public static String PC200_KMERR_EVAL_FILE="PC200_kmerrcd_evalarray.csv";
}
