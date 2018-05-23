/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import java.util.List;

/**
 *
 * @author zz17390
 */
public interface KomatsuDataParameter {
    public static String[] KISY_LIST = new String[]{
        "PC138US",
        "PC78US",
        "PC200", "PC200LC", "PC200SC", "PC210", "PC210LC", 
        "HB205","HB205LC", "HB215", "HB215LC",
        "PC228US", "PC228USLC",
        "WA100",
        "WA470",  
        "PC10MR", "PC10UU", "PC18MR", "PC20MR", "PC20UU"
    };
    
    public static String TEMPLATE_PATH = "template\\";
    public static String MIDDLEDATA_PATH = "middle\\";
    public static String SHUFFLE_FORMAT_PATH = "index\\shuffle_format.json";
    public static String SETTING_GETDATA_PATH = "index\\syaryo_data_index.csv";
}
