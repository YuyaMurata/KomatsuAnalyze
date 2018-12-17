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
import java.util.logging.Level;
import java.util.logging.Logger;
import file.MapToJSON;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class ProductIndex {
    private static String product_source = KomatsuDataParameter.PRODUCT_SOURCE;
    private static String outpath = KomatsuDataParameter.PRODUCT_INDEXPATH;
    
    public static void main(String[] args) {
        Map<String, String> product = new HashMap();
        
        try(BufferedReader csv = CSVFileReadWrite.reader(product_source)){
            String line;
            while((line = csv.readLine()) != null){
                String id = line.split(",")[0];
                String date = line.split(",")[1]+"01";
                product.put(id, date);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        new MapToJSON().toJSON(outpath, product);
    }
}
