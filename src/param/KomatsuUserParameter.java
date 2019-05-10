/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import file.ListToCSV;
import java.util.Map;
import file.MapToJSON;
import java.util.Arrays;
import java.util.List;
import param.obj.UserPartsObject;

/**
 *
 * @author ZZ17390
 */
public interface KomatsuUserParameter {
    //車両分析用
    public static String AZ_PATH = "settings\\analizer\\";
    
    public static String DATE_FORMAT = "yyyyMMdd";
    
    //部品コードの再定義
    public static Map<String, String> PC200_MAINPARTS_DEF = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainparts_define.json");
    public static Map<String, String> PC200_MAINPARTS_DEFNAME = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainparts_definename.json");
    
    //メンテナンス
    public static Map<String, String> PC200_MAINTEPARTS_DEF = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_define.json");
    public static Map<String, String> PC200_MAINTEPARTS_DEFNAME = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_definename.json");
    public static Map<String, String> PC200_MAINTEPARTS_INTERVAL = new MapToJSON().toMap(AZ_PATH+"user\\PC200_mainteparts_interval.json");
    public static UserPartsObject PC200_USERPARTS_DEF = new UserPartsObject(AZ_PATH+"user\\PC200_parts_userdefine.json");
    public static Map<String, String> PC200_PERIODSERVICE = new MapToJSON().toMap(AZ_PATH+"user\\PC200_定期サービス.json");
    
    //事故リスト
    public static Map<String, String> PC200_ACCIDENT = ListToCSV.toMap(AZ_PATH+"user\\PC200_事故車両推定リスト.csv", 1, 0);
    
    //アタッチメント購入リスト
    public static Map<String, String> PC200_ATTACHEMENT = ListToCSV.toMap(AZ_PATH+"user\\PC200_アタッチメント除外リスト.csv", 1, 0);
    
    //評価行列
    public static String PC200_PARTS_EVAL_FILE="PC200_partscd_evalarray.csv";
    public static String PC200_KMERR_EVAL_FILE="PC200_kmerrcd_evalarray.csv";
    
    //EasyViewer
    public static String SYARYOOBJECT_FDPATH = "syaryo\\";
    public static String ICON_IMG = "icon\\syaryo_obj.png";
    
    //Python
    public static String PYTHONE_PATH = "py\\";
    public static Map<String, String> GRAPH_PY = new MapToJSON().toMap(AZ_PATH+"define\\graph_relate_pyfile.json");
    public static String GRAPH_TEMP_FILE = PYTHONE_PATH+"csv\\graph_temp.csv";
    public static String DETECT_TEMP_FILE = PYTHONE_PATH+"csv\\detect_temp.csv";
    public static String DETECT_PY = PYTHONE_PATH+"detect_abnomaly.py";
    
    //分析用 (将来的にはユーザーが定義できるようにする)
    //オールサポート対象 パワーライン
    public static Map POWERLINE = new MapToJSON().toMap(AZ_PATH+"index\\allsupport_index.json");
    
    //事故抽出
    public static List ACCIDENT_WORDS = ListToCSV.toList(AZ_PATH+"define\\accident_words.csv");
    
    //PC200 KR
    public static Map PC_KR_SMASTER = new MapToJSON().toMap(AZ_PATH+"define\\PC200_KR_SyaryoMaster.json");
    
     //データ順序
    public static List<String> DATA_ORDER = Arrays.asList(new String[]{
        "生産",
        "仕様",
        "詳細",
        "出荷",
        "新車",
        "中古車",
        "顧客",
        "SMR",
        "受注",
        "作業",
        "部品",
        "コマツケア",
        "コマツケア前受け金",
        "オールサポート",
        "KOMTRAX_ACT_DATA",
        "KOMTRAX_SMR",
        "KOMTRAX_GPS",
        "KOMTRAX_FUEL_CONSUME",
        "KOMTRAX_ERROR",
        "KOMTRAX_ATT",
        "KOMTRAX_DIG",
        "KOMTRAX_HOIST",
        "KOMTRAX_BREAKER",
        "KOMTRAX_RELIEF",
        "KOMTRAX_PUMP",
        "KOMTRAX_RUNNING",
        "KOMTRAX_ODOMETER",
        "LOADMAP_SMR",
        "LOADMAP_実エンジン回転VSエンジントルク",
        "LOADMAP_作業機操作状況",
        "LOADMAP_ポンプ圧_MAX",
        "LOADMAP_ポンプ圧_F",
        "LOADMAP_ポンプ圧_R",
        "LOADMAP_作業モード選択状況",
        "LOADMAP_エンジン水温VS作動油温",
        "LOADMAP_ST_USERTIME",
        "LOADMAP_ポンプ斜板_F",
        "LOADMAP_ポンプ斜板_R",
        "LOADMAP_可変マッチング",
        "廃車",
        "経歴"
    });
}
