/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.index;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import file.MapToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ErrorCodeIndex {
    public static void main(String[] args) {
        String filename = "resource\\code\\PCエラーフラグ.csv";
        Map map = new HashMap();
        
        try(BufferedReader csv = CSVFileReadWrite.readerSJIS(filename)){
            //ヘッダ無視
            String line = csv.readLine();
            
            while((line = csv.readLine()) != null){
                String[] as = line.split(",");
                map.put(as[0].trim(), as[1]);
            }
            new MapToJSON().write(KomatsuDataParameter.PC_ERRORFLG_INDEX_PATH, map);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
