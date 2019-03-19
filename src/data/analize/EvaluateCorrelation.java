/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data.analize;

import file.CSVFileReadWrite;
import file.UserDefinedFile;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TestUtils;
import param.KomatsuDataParameter;
import param.KomatsuUserParameter;

/**
 *
 * @author ZZ17390
 */
public class EvaluateCorrelation {

    private static Map evalPartsMap;
    private static Map evalKMErrMap;
    //private static String filename = "PC200_correlationMap_parts_kme.csv";
    //private static String filename = "PC200_rank_correlationMap_parts_kme.csv";
    private static String filename = "PC200_rank_correlationMap_kme.csv";
    //private static String filename = "PC200_rank_correlationMap_parts.csv";
    private static Map defpMap = KomatsuUserParameter.PC200_MAINPARTS_DEFNAME;
    private static Map defeMap = KomatsuDataParameter.PC_KMERR_EDEFNAME;

    public static void main(String[] args) {
        settings();

        //List partsHeader = ((List<List>) evalPartsMap.get("headers")).get(0);
        //double[][] partsData = (double[][]) evalPartsMap.get("data");
        //List kmerrHeader = ((List<List>) evalKMErrMap.get("headers")).get(0);
        //double[][] kmerrData = (double[][]) evalKMErrMap.get("data");
        
        //System.out.println("t-"+partsHeader);
        //test(partsHeader.indexOf("9"), partsHeader.indexOf("11"), partsData);
        
        //calcMatrix();
        //rankCalcMatrix();
        //rankCalcMatrix(((List<List>) evalPartsMap.get("headers")).get(0), defpMap, (double[][]) evalPartsMap.get("data"));
        rankCalcMatrix(((List<List>) evalKMErrMap.get("headers")).get(0), defeMap, (double[][]) evalKMErrMap.get("data"));
    }
    
    private static void rankCalcMatrix(List header, Map defMap, double[][] data){
        
        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            for (int i = 0; i < header.size(); i++) {
                
                for (int j = i+1; j < header.size()-1; j++) {
                    double d = correlation(data[i], data[j]);
                    if(Math.abs(d) >= 0.7d){
                        final int x = i;
                        final int x2 = j;
                        
                        List<String> cor = new ArrayList();
                        cor.add(header.get(i).toString()+"("+defMap.get(header.get(i).toString().replace(" ", ""))+")");
                        cor.add(String.valueOf(IntStream.range(0, data[i].length).map(y -> Double.valueOf(data[x][y]).intValue()).sum()));
                        cor.add(header.get(j).toString()+"("+defMap.get(header.get(j).toString().replace(" ", ""))+")");
                        cor.add(String.valueOf(IntStream.range(0, data[i].length).map(y -> Double.valueOf(data[x2][y]).intValue()).sum()));
                        cor.add(String.valueOf(d));
                        cor.add(String.valueOf(test(i, j, data)));
                        
                        pw.println(String.join(",", cor));
                    }
                }
            }
        }
    }
    
    private static void rankCalcMatrix(){
        List partsHeader = ((List<List>) evalPartsMap.get("headers")).get(0);
        double[][] partsData = (double[][]) evalPartsMap.get("data");
        List kmerrHeader = ((List<List>) evalKMErrMap.get("headers")).get(0);
        double[][] kmerrData = (double[][]) evalKMErrMap.get("data");
        
        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            for (int i = 0; i < kmerrHeader.size(); i++) {
                for (int j = 0; j < partsHeader.size(); j++) {
                    double d = correlation(kmerrData[i], partsData[j]);
                    if(Math.abs(d) >= 0.7){
                        final int x = i;
                        final int x2 = j;
                        
                        List<String> cor = new ArrayList();
                        cor.add(kmerrHeader.get(i).toString()+"("+defeMap.get(kmerrHeader.get(i).toString().replace(" ", ""))+")");
                        cor.add(String.valueOf(IntStream.range(0, kmerrData[i].length).map(y -> Double.valueOf(kmerrData[x][y]).intValue()).sum()));
                        cor.add(partsHeader.get(j).toString()+"("+defpMap.get(partsHeader.get(j).toString().replace(" ", ""))+")");
                        cor.add(String.valueOf(IntStream.range(0, partsData[j].length).map(y -> Double.valueOf(partsData[x2][y]).intValue()).sum()));
                        cor.add(String.valueOf(d));
                        cor.add(String.valueOf(test(kmerrData[x], partsData[x2])));
                        
                        pw.println(String.join(",", cor));
                    }
                }
            }
        }
    }
    
    private static void calcMatrix(){
        List partsHeader = ((List<List>) evalPartsMap.get("headers")).get(0);
        double[][] partsData = (double[][]) evalPartsMap.get("data");
        List kmerrHeader = ((List<List>) evalKMErrMap.get("headers")).get(0);
        double[][] kmerrData = (double[][]) evalKMErrMap.get("data");
        
        try (PrintWriter pw = CSVFileReadWrite.writer(filename)) {
            pw.println(","+String.join(",", partsHeader));
            for (int i = 0; i < kmerrHeader.size(); i++) {
                List<String> cor = new ArrayList();
                cor.add(kmerrHeader.get(i).toString());
                
                for (int j = 0; j < partsHeader.size(); j++) {
                    cor.add(String.valueOf(correlation(kmerrData[i], partsData[j])));
                }
                
                pw.println(String.join(",", cor));
            }
        }
    }

    public static void settings() {
        evalPartsMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_PARTS_EVAL_FILE);
        evalKMErrMap = UserDefinedFile.evalMatrix(KomatsuUserParameter.PC200_KMERR_EVAL_FILE);
    }

    public static double correlation(double[] x, double[] y) {
        return new PearsonsCorrelation().correlation(x, y);
    }
    
    public static double test(int x, int y, double[][] data){
        double t = TestUtils.kolmogorovSmirnovTest(data[x], data[y]);
        //System.out.println(t);
        return t;
    }
    
    public static double test(double[] data, double[] data2){
        double t = TestUtils.kolmogorovSmirnovTest(data, data2);
        //System.out.println(t);
        return t;
    }

    public static void testPrint() {
        //Test
        List<List> headers = (List<List>) evalPartsMap.get("headers");
        double[][] data = (double[][]) evalPartsMap.get("data");

        System.out.println("parts_header:" + headers.get(0).get(1));
        System.out.println(Arrays.toString(data[1]));

        headers = (List<List>) evalKMErrMap.get("headers");
        data = (double[][]) evalKMErrMap.get("data");

        System.out.println("kmerr_header:" + headers.get(0).get(12));
        System.out.println(Arrays.toString(data[12]));
    }
}
