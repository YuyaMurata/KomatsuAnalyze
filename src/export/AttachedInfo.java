/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package export;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AttachedInfo {
    private static String csvFile = "ExportData_PC200_ALL.csv";
    public static void main(String[] args) {
        workname(csvFile);
    }
    
    private static void workname(String file){
        Map devName = KomatsuDataParameter.WORK_DEVID_DEFNAME;
        try(PrintWriter csv = CSVFileReadWrite.writer(csvFile+"_work_id_name.csv")){
            try(BufferedReader br = CSVFileReadWrite.reader(csvFile)){
                String line = br.readLine();
                csv.println(line+",devID,devName");
                while((line = br.readLine()) != null){
                    if(line.split(",")[3].length() > 3){
                        String devID = line.split(",")[3].substring(0, 4);
                        line = line+","+devID+","+devName.get(devID);
                    }else{
                        line = line+",None,None";
                    }
                    csv.println(line);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
