/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package file;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class UserDefinedFile {
    public static List<String> filter(String filename){
        List filterList = new ArrayList();
        try(BufferedReader br = CSVFileReadWrite.reader(filename)){
            String line;
            while((line =br.readLine()) != null){
                filterList.addAll(Arrays.asList(line.split(",")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if(filterList.isEmpty())
            return null;
        return filterList;
    }
    
    public static Map<String, Object> evalMatrix(String filename){
        List<List<String>> lines = new ArrayList();
        try(BufferedReader br = CSVFileReadWrite.reader(filename)){
            String line;
            while((line =br.readLine()) != null){
                lines.add(Arrays.asList(line.split(",")));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        if(lines.isEmpty())
            return null;
        
        double[][] matrix = new double[lines.get(0).size()-1][lines.size()-1];
        List headers = new ArrayList();
        //部品コード名
        headers.add(lines.get(0).subList(1, lines.get(0).size()));
        List header = new ArrayList();
        for(int i=1; i < lines.size(); i++){
            //車両名
            header.add(lines.get(i).get(0));
            
            //データ
            for(int j = 1; j < lines.get(0).size(); j++){
                matrix[j-1][i-1] = Double.valueOf(lines.get(i).get(j));
            }
        }
        
        headers.add(header);
        
        Map map = new HashMap();
        map.put("headers", headers);
        map.put("data", matrix);
        
        return map;
    }
}
