/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ZZ17390
 */
public class DTWMatrix {
    public static void main(String[] args) {
        Object[][] mat = SMRMatrix.create("json\\syaryo_obj_WA470_form.json");
        
        int dtwsize = mat[0].length;
        Object[][] dtw_mat = new Object[dtwsize][dtwsize];
        
        //Initialize
        for(int i=1; i < dtwsize; i++)
            dtw_mat[i][0] = mat[0][i];
        for(int i=1; i < dtwsize; i++)
            dtw_mat[0][i] = mat[0][i];
        
        //transverse variable
        List<Integer>[] tmat = new ArrayList[dtwsize-1];
        for(int i=1; i < dtwsize; i++){
            List<Integer> timerec = new ArrayList();
            for(int j = 1; j < mat.length; j++){
                if(mat[j][i].toString().equals(""))
                    break;
                timerec.add(Integer.valueOf(mat[j][i].toString()));
            }
            //最新SMRを削除
            timerec.remove(timerec.size()-1);        
            tmat[i-1] = timerec;
        }
        
        //test
        System.out.println(dtw_mat[0][1]+":"+tmat[0]);
        
        //Calc
        for(int i=1; i < dtwsize; i++){
            for(int j=1; j < dtwsize; j++){
                int s = Math.min(tmat[i-1].size(), tmat[j-1].size());
                dtw_mat[i][j] = DTW.distance(tmat[i-1].subList(0, s).toArray(new Integer[s]), tmat[j-1].subList(0, s).toArray(new Integer[s]));
            }
            
            System.out.println(i+"/"+dtwsize+" 行処理.");
        }
        
        printMat(dtw_mat);
    }
    
    public static void printMat(Object[][] mat) {
        try (PrintWriter pw = CSVFileReadWrite.writer("test_dtwmat.csv")) {
            for (int i = 0; i < mat.length; i++) {
                List str = new ArrayList();
                for (int j = 0; j < mat[i].length; j++) {
                    if(mat[i][j] == null)
                        str.add("");
                    else
                        str.add(mat[i][j]);
                }
                System.out.println(i + ":" + str);
                pw.println(str.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
                //System.out.println("");
            }
        }
    }
}
