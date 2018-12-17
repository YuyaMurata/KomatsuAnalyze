/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import index.SyaryoObjectElementsIndex;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import json.MapIndexToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ObjectLayoutIndex {
    static Map<String, List> DATA_LAYOUT_INDEX = SyaryoObjectElementsIndex.getInstance().getIndex();    
    public static void main(String[] args) {
        //負荷情報の紐付け
        String[] keys = new String[]{"SMR", "実エンジン回転VSエンジントルク", "作業機操作状況", "ポンプ圧_MAX", "ポンプ圧_F", "ポンプ圧_R", "作業モード選択状況", "エンジン水温VS作動油温", "ST_USERTIME", "ポンプ斜板_F", "ポンプ斜板_R", "可変マッチング"};
        String key = "LOADMAP_";
        for(String k : keys)
            DATA_LAYOUT_INDEX.put(key+k, Arrays.asList(new String[]{"VALUE"}));
        
        new MapIndexToJSON().write(KomatsuDataParameter.LAYOUT_FORMAT_PATH.replace("format", "default_format"), DATA_LAYOUT_INDEX);
    }
}
