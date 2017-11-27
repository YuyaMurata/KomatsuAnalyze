/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import jsc.contingencytables.ContingencyTable2x2;
import jsc.contingencytables.FishersExactTest;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author ZZ17390
 */
public class SFET {
	Rengine engine = new Rengine(new String[]{"--no-save"}, false, null);
	
	private Double[] quartile(Map<Integer, Double> divTimeData, int w) {
		Double[] output = new Double[3];
		Double[] y = divTimeData.values().toArray(new Double[divTimeData.size()]);
		Arrays.sort(y);

		if (w % 4 == 0) {
			output[0] = y[w / 4 - 1];
			output[2] = y[(3 * w) / 4 - 1];
		} else {
			output[0] = y[w / 4 - 1] / 2 + y[w / 4] / 2;
			output[2] = y[(3 * w) / 4 - 1] / 2 + y[(3 * w) / 4] / 2;
		}
		if (w % 2 == 0) {
			output[1] = y[w / 2 - 1] / 2 + y[w / 2] / 2;
		} else {
			output[1] = y[(w - 1) / 2];
		}
		/*System.out.print("y=");
		for(double x : y){
			System.out.print(x +" ");
		}
		System.out.println("");
		System.out.print("o=");
		for(double o : output){
			System.out.print(o +" ");
		}
		System.out.println("");*/

		return output;
	}

	private int[][] count(Map<Integer, Double> divTimeData, int th, Double xth) {
		int[][] output = new int[2][2];
		//System.out.println(th);
		for (Integer t : divTimeData.keySet()) {
			if (t < th && divTimeData.get(t) >= xth) // a
			{
				output[0][0]++;
			} else if (t >= th && divTimeData.get(t) >= xth) // b
			{
				output[1][0]++;
			} else if (t < th && divTimeData.get(t) < xth) // c
			{
				output[0][1]++;
			} else // d
			{
				output[1][1]++;
			}
		}
		return output;
	}

	TreeMap<Integer, Double> map = new TreeMap<>();

	public SFET(TreeMap<Integer, Double> timeData, int w, double alpha) {
		PrintWriter pw = CSVFileReadWrite.writer("test_SFET.csv");
		for (int t = 0; t < (timeData.size() - w+1); t++) {
			int tr = t + w - 1;
			System.out.println("tr="+tr);
			for (int th = t; th < tr; th++) {
				//System.out.println("th="+th);
				Double[] xth = quartile(timeData.subMap(t, tr), w);
				for (int i = 0; i < 3; i++) {
					int[][] contTable = count(timeData.subMap(t, tr), th, xth[i]);
					//double p = new Combinations(i, i)
					//System.out.println("a=" + contTable[0][0] + " b=" + contTable[1][0] + " c=" + contTable[0][1] + " d=" + contTable[1][1]);
					REXP result = engine.eval("x <- matrix(c("+contTable[0][0]+","+contTable[1][0]+","+contTable[0][1]+","+contTable[1][1]+"), nrow=2)");
					//printMat(result);
					Double p = engine.eval("fisher.test(x)").asVector().at(0).asDouble();
					Integer s = contTable[0][0] * contTable[1][0] - contTable[0][1] * contTable[1][1];
					if (p <= alpha) {
						pw.println("t_th=," + th + " ,s=" + s + " ,P=," + p);
						//map.put(th, p);
					}
				}
				//System.exit(0);
			}
		}
		engine.end();
	}
	
	public void printMat(REXP result){
		double[][] d = result.asMatrix();
		for(int i=0; i < d.length; i++){
			for(int j =0; j < d[i].length; j++){
				System.out.print(d[i][j]+" ");
			}
			System.out.println("");
		}
	}

	public Double getTSP(Integer t) {
		if (map.get(t) != null) {
			return map.get(t);
		} else {
			return 1d;
		}
	}
}
