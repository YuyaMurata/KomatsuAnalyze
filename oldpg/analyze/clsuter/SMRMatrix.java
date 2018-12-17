/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyze.clsuter;

import file.CSVFileReadWrite;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import json.JsonToSyaryoObj;
import obj.SyaryoElements;
import obj.SyaryoObject1;

/**
 *
 * @author ZZ17390
 */
public class SMRMatrix {

	public static Object[][] create(String filename) {
		Map<String, SyaryoObject1> syaryoMap = new JsonToSyaryoObj().reader(filename);
		int n = syaryoMap.values().stream()
			.map(s -> s.getAge(s.getSMR().keySet().toArray(new String[s.getSMR().size()])[s.getSMR().size() - 1]))
			.map(s -> Integer.valueOf(s))
			.max(Comparator.naturalOrder()).get();
		int m = syaryoMap.size();
		Object[][] mat = new Object[n + 2][m + 1];

		System.out.println("CreateMatrix " + n + " x " + m);

		mat[0][0] = "";

		for (int k = 0; k < n + 2; k++) {
			for (int l = 0; l < m + 1; l++) {
				mat[k][l] = "";
			}
			if (0 < k) {
				mat[k][0] = k;
			}
		}

		int i = 1;
		for (String syaryo : syaryoMap.keySet()) {
			mat[0][i] = syaryo;
			Map<String, List> smrMap = syaryoMap.get(syaryo).getSMR();
			int numAge = 1;
			for (String date : smrMap.keySet()) {
				Integer age = Integer.valueOf(syaryoMap.get(syaryo).getAge(date));
				if (age < 0 || !smrMap.get(date).get(SyaryoElements.SMR.Company.getNo()).toString().contains("komtrax")) {
					continue;
				}
				mat[age + 1][i] = smrMap.get(date).get(SyaryoElements.SMR._SMR.getNo());
				//mat[age + 1][i] = diffValue(syaryoMap.get(syaryo), date);
				numAge = age + 1;
			}

			//check
			/*for(int x=1; x < numAge; x++)
				if(mat[x][i].equals(""))
				mat[x][i] = 0;
			 */
			//interp
			List<String> interX = new ArrayList<>();
			String temp = "0";
			String pare = "0";
			Boolean flg = true;
			for (int x = 1; x < numAge + 1; x++) {
				if (mat[x][i].equals("")) {
					if (flg) {
						pare = temp;
						flg = false;
					}
				} else {
					if (!flg) {
						interX.add(pare + "," + String.valueOf(x));
						flg = true;
					}
				}
				temp = String.valueOf(x);
			}

			System.out.println(syaryo + ":" + interX + ":" + pare);

			for (String interval : interX) {
				Integer[] x = {Integer.valueOf(interval.split(",")[0]), Integer.valueOf(interval.split(",")[1])};
				Integer[] y;
				if (x[0] == 0) {
					y = new Integer[]{0, Double.valueOf(mat[x[1]][i].toString()).intValue()};
				} else {
					y = new Integer[]{Double.valueOf(mat[x[0]][i].toString()).intValue(), Double.valueOf(mat[x[1]][i].toString()).intValue()};
				}

				for (int k = x[0] + 1; k < x[1]; k++) {
					mat[k][i] = linearInterpolation(x, y, k);
				}
			}

			//Syaryo increment
			i++;
		}

		//diff
		/*for (int k = 2; k < mat.length; k++) {
			for (int l = 1; l < mat[k].length; l++) {
				if (mat[k][l].toString().equals("")) {
					mat[k - 1][l] = "";
					continue;
				}
				//System.out.println("mat[" + k + "][" + l + "]:" + mat[k][l]);
				mat[k - 1][l] =Double.valueOf(mat[k][l].toString()).intValue() - Double.valueOf(mat[k - 1][l].toString()).intValue();
			}
		}*/

		return mat;
	}

	public static void printMat(Object[][] mat, String filename) {
		try (PrintWriter pw = CSVFileReadWrite.writer("SMR_"+filename.replace("form.json", "mat.csv"))) {
			for (int i = 0; i < mat.length - 1; i++) {
				List str = new ArrayList();
				for (int j = 0; j < mat[i].length; j++) {
					str.add(mat[i][j]);
				}
				System.out.println(i + ":" + str);
				pw.println(str.stream().map(s -> s.toString()).collect(Collectors.joining(",")));
				//System.out.println("");
			}
		}
	}

	private static String diffValue(SyaryoObject1 syaryo, String date) {
		return syaryo.diffSMR(date);
	}

	//線形補間
	private static String linearInterpolation(Integer[] x, Integer[] y, Integer targetX) {
		Integer interValue = y[0] + (y[1] - y[0]) * (targetX - x[0]) / (x[1] - x[0]);
		return interValue.toString();
	}

	public static void main(String[] args) {
		String filename = "syaryo_obj_WA470_form.json";
		Object[][] mat = create("json\\"+filename);
		printMat(mat, filename);
	}
}
