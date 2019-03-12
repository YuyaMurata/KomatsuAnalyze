/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package param;

import file.ListToCSV;
import index.SyaryoObjectElementsIndex;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import file.MapToJSON;
import obj.LoadSyaryoObject;

/**
 *
 * @author zz17390
 */
public interface KomatsuDataParameter {

    public static String[] KISY_LIST = new String[]{
        //"PC138US",
        //"PC78US",
        "PC200"
    //"PC200LC", "PC210", "PC210LC",
    //"HB205", "HB205LC", "HB215", "HB215LC",
    //"PC228US", "PC228USLC",
    //"WA100",
    //"WA470",
    //"PC10MR", "PC10UU", "PC18MR", "PC20MR", "PC20UU"
    };

    public static Map joinMap = new HashMap() {
        {
            put("PC200", new String[]{"8", "8N1", "10"});
            put("PC138US", new String[]{"2", "8", "10"});
        }
    };
	
    //Create Syaryo Object
    public static String TEMPLATE_PATH = "template\\";
    public static String MIDDLEDATA_PATH = "middle\\";
    public static String OBJECT_PATH = "syaryo\\";
    public static String INDEX_PATH = "index\\";
    public static String SHUFFLE_FORMAT_PATH = "index\\shuffle_format.json";
    public static String SETTING_GETDATA_PATH = "index\\syaryo_data_index.csv";
    public static String SETTING_GECUSTTDATA_PATH = "index\\customer_data_index.csv";
    public static String CUSTOMER_INDEX_PATH = "index\\customer_data_index.json";
    public static String PC_ERRORFLG_INDEX_PATH = "index\\pc_errorflg_index.json";
    public static String HONSYA_INDEX_PATH = "index\\honsya_index.json";
    public static String SUMMARY_PATH = "summary\\";
    public static String PRODUCT_SOURCE = "index\\product_201703.csv";

    //EasyViewer
    public static String SYARYOOBJECT_FDPATH = "syaryo\\";
    public static String SETTING_DATAFILETER_PATH = "index\\easyviwer_datafilter.csv";
    public static String ICON_IMG = "icon\\syaryo_obj.png";
	
    //Python
    public static String PYTHONE_PATH = "py\\";
    public static Map<String, String> GRAPH_PY = new MapToJSON().toMap("define\\graph_relate_pyfile.json");
    public static String GRAPH_TEMP_FILE = PYTHONE_PATH+"csv\\graph_temp.csv";
    
    public static String DETECT_TEMP_FILE = PYTHONE_PATH+"csv\\detect_temp.csv";
    public static String DETECT_PY = PYTHONE_PATH+"detect_abnomaly.py";

    //Layout
    //public static Map<String, List> DATALAYOUT_INDEX = SyaryoObjectElementsIndex.getInstance().getIndex();
    public static String LAYOUT_FORMAT_PATH = "index\\obj_layout_format.json";

    //Formalize
    public static String PRODUCT_INDEXPATH = "index\\product_index.json";
    public static String DATE_FORMAT = "yyyyMMdd";

    //Exporter
    public static String EXPORT_PATH = "export\\";

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
    //KUECコード
    public static List KUEC_LIST = ListToCSV.toList("define\\kuec_custercd.csv");

    //R
    public static String R_FUNC_PATH = "R\\KMRFunction.R";

    //事故抽出
    public static List ACCIDENT_WORDS = ListToCSV.toList("define\\accident_words.csv");

    //エラー紐付け処理時のデータソース
    public static String[] ERR_SOURCE = new String[]{"kom_order", "parts", "work_info", "service"};
    public static String ERR_DATAPROCESS_PATH = "error_proc\\";

    //認証関連
    public static String AUTH_PATH = "index\\autholize.json";

    //分析用 (将来的にはユーザーが定義できるようにする)
    //オールサポート対象 パワーライン
    public static Map POWERLINE = new MapToJSON().toMap("index\\allsupport_index.json");

    //KOMTRAXエラーコードのフラグの定義
    public static Map PC_ERROR = new MapToJSON().toMap(PC_ERRORFLG_INDEX_PATH);

    //サブキーの変換が必要なものの対応表
    public static List TRANS_DATE = Arrays.asList(new String[]{"受注", "作業", "部品"});

    //名称定義
    public static String PC_KMERR_DEFNAME_INDEX_PATH = "define\\PC200_Komtrax_Error_name.json";
    public static Map PC_KMERR_EDEFNAME = new MapToJSON().toMap(PC_KMERR_DEFNAME_INDEX_PATH);
    public static String PC_PID_SYSDEFNAME_INDEX_PATH = "define\\PC200_PartsID_SystemDefine_name.json";
    public static Map PC_PID_SYSDEFNAME = new MapToJSON().toMap(PC_PID_SYSDEFNAME_INDEX_PATH);
    public static String WORK_DEVID_DEFNAME_INDEX_PATH = "define\\WorkDevID_Define_name.json";
    public static Map WORK_DEVID_DEFNAME = new MapToJSON().toMap(WORK_DEVID_DEFNAME_INDEX_PATH);

    //PC200 KR
    public static String PC_KR_SMASTER_INDEX_PATH = "define\\PC200_KR_SyaryoMaster.json";
    public static Map PC_KR_SMASTER = new MapToJSON().toMap(PC_KR_SMASTER_INDEX_PATH);
}
