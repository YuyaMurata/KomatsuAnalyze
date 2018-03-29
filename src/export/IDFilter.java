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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class IDFilter {
    public static void main(String[] args) {
        String filterCSVFile = "device_smr_year_KM_PC138US.csv";
        int id_no = 1;
        String filterIDs = "max_smr_time_PC138US_filter5000.csv";
        
        try (PrintWriter csv = CSVFileReadWrite.writer("filter_"+filterCSVFile)) {
            try(BufferedReader br = CSVFileReadWrite.reader(filterCSVFile)){
                try(BufferedReader ids = CSVFileReadWrite.reader(filterIDs)){
                    List<String> id = ids.lines().collect(Collectors.toList());
                    
                    String line;
                    csv.println(br.readLine());
                    while((line = br.readLine()) != null){
                        System.out.println(line);
                        if(line.equals("")) continue;
                        String csvid = line.split(",")[id_no];
                        if(id.contains(csvid))
                            csv.println(line);
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}
