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

/**
 *
 * @author ZZ17390
 */
public class PowerLineIndex {
    public static void main(String[] args) {
        String filename = "resource\\パワーライン保障_対象装置.csv";
        
        Map map = new HashMap();
        
        try(BufferedReader csv = CSVFileReadWrite.reader(filename)){
            String line;
            while((line = csv.readLine()) != null){
                String[] as = line.split(",");
                map.put(as[0].trim(), as[1]);
            }
            new MapToJSON().write("allsupport_index.json", map);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
