/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import data.code.PartsCodeConv;
import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import file.SyaryoToCompress;
import obj.SyaryoObject;
import param.KomatsuDataParameter; 

/**
 *
 * @author ZZ17390
 */
public class CodeHistgram {

    private static final String exportFile = "ExportData_PC200_ALL.json";
    private static final Integer[] range = 
        new Integer[]{0,1000,5000,10000,20000,30000,40000,50000,60000,70000,80000,90000,100000,200000,300000,400000,500000,600000,700000,800000,900000,1000000};
    
    public static void main(String[] args) {
        Map<String, SyaryoObject> syaryoMap = new SyaryoToCompress().readJSON(exportFile);

        SyaryoObject dataHeader = syaryoMap.get("_headers");
        syaryoMap.remove("_headers");
        System.out.println(dataHeader.dump());

        //部品フィルタ
        AnalizeDataFilter.partsdatafilter(syaryoMap, dataHeader);

        partscd(syaryoMap, dataHeader);
    }

    private static void partscd(Map<String, SyaryoObject> syaryoMap, SyaryoObject dataHeader) {
        if (dataHeader.get("部品") == null) {
            System.err.println("Do not exported KOMPAS PARTS DATA!");
            return;
        }
        
        Map defMap = KomatsuDataParameter.PC_PARTS_EDEFNAME;
        
        int hnbn = dataHeader.get("部品").get("部品").indexOf("HNBN");
        int bname = dataHeader.get("部品").get("部品").indexOf("BHN_NM");
        int quant = dataHeader.get("部品").get("部品").indexOf("JISI_SU");
        int price = dataHeader.get("部品").get("部品").indexOf("SKKG");
        
        Map<Integer, List<Integer>> allparts = new TreeMap<>();
        
        try (PrintWriter csv = CSVFileReadWrite.writer("PC200_parts_hist_data.csv")) {
            csv.println("ユーザー再定義,品番,品名,金額");

            //品番 抽出
            syaryoMap.values().stream().forEach(s -> {
                if (s.get("部品") == null) {
                    return;
                }

                List<String> parts = s.get("部品").entrySet().stream()
                    .filter(d -> PartsCodeConv.mainPartsDefineCode((String) d.getValue().get(hnbn)) != null)
                    .map(d -> PartsCodeConv.mainPartsDefineCode((String) d.getValue().get(hnbn))
                    + "," + d.getValue().get(hnbn)
                    + "," + d.getValue().get(bname)
                    + "," + (Integer.valueOf(d.getValue().get(price).toString()) / Integer.valueOf(d.getValue().get(quant).toString())))
                    .collect(Collectors.toList());
                
                //ヒストグラム作成用
                parts.stream().forEach(p ->{
                    Integer pname = Integer.valueOf(p.split(",")[0]);
                    Integer pprice = Integer.valueOf(p.split(",")[3]);
                    
                    if(allparts.get(pname) == null)
                        allparts.put(pname, new ArrayList<>());
                    
                    allparts.get(pname).add(pprice);
                });
                
                parts.stream().forEach(csv::println);
            });
        }
        
        
        try (PrintWriter csv = CSVFileReadWrite.writer("PC200_parts_hist_graph.csv")) {
            int s = -1;
            //headers
            csv.println("データ区間,ALL,"+allparts.keySet().stream().map(cd -> cd.toString()).collect(Collectors.joining(",")));
            
            for(int i=0; i < range.length; i++){
                List freq = new ArrayList();
                
                //rank
                final int rank = range[i];
                final int prevrank = s;
                freq.add(String.valueOf(rank));

                //all
                freq.add(String.valueOf(allparts.values().stream().map(d -> d.stream().filter(de -> (prevrank < de && de <= rank)).count()).mapToLong(cnt -> cnt).sum()));
                
                //再定義コード
                for(Integer cd : allparts.keySet())
                    freq.add(String.valueOf(allparts.get(cd).stream().filter(de -> (prevrank < de && de <= rank)).count()));
                
                s = rank;
                
                csv.println(String.join(",", freq));
            }
        }
    }
}
