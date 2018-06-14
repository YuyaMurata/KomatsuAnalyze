/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author kaeru
 */
public class DistanceCulc {
	//WGS84
	private static final Double A = 6378137.0;	//赤道半径
	private static final Double F = 1/298.257222101;	//扁平率
	private static final Double B = A * (1.0 - F);		//極半径
	
	/**
	 * 
	 * http://www2.nc-toyama.ac.jp/~mkawai/lecture/sailing/geodetic/geosail.html
	 */
	
	public Double onoFormula(Double lat1, Double lon1, Double lat2, Double lon2){
		Double lat1Rad = Math.toRadians(lat1);
		Double lon1Rad = Math.toRadians(lon1);
		Double lat2Rad = Math.toRadians(lat2);
		Double lon2Rad = Math.toRadians(lon2);
		
		//測地緯度　→　化成緯度
		Double p1 = Math.atan(B/A*Math.tan(lat1Rad));
		Double p2 = Math.atan(B/A*Math.tan(lat2Rad));
		
		//Special Distance
		Double sd = Math.acos(Math.sin(p1)*Math.sin(p2) + Math.cos(p1)*Math.cos(p2)*Math.cos(lon1Rad-lon2Rad));
		
		Double c = Math.pow(Math.sin(p1)+Math.sin(p2), 2);
		Double d = Math.pow(Math.sin(p1)-Math.sin(p2), 2);
		
		Double p = (A-B)*(sd-Math.sin(sd))/(4*(1+Math.cos(sd)));
		Double q = (A-B)*(sd+Math.sin(sd))/(4*(1-Math.cos(sd)));
		
		Double distance = A * sd - c*p - d*q;
		
                if(distance.isNaN()) distance = 0d;
                
		return distance;
	}
        
        public static Double compValue(String str) {
		//変換
		str = str.replace("N", "").replace("S", "-").replace("E", "").replace("W", "-");

		String[] s = str.split("\\.");

		BigDecimal b1 = new BigDecimal(s[0]);
		BigDecimal b2 = new BigDecimal(s[1]).divide(new BigDecimal("60"), 7, RoundingMode.HALF_UP);
		BigDecimal b3 = new BigDecimal(s[2]).divide(new BigDecimal("3600"), 7, RoundingMode.HALF_UP);
		BigDecimal b4 = new BigDecimal(s[3]).divide(new BigDecimal("21600"), 7, RoundingMode.HALF_UP);

		BigDecimal result = b1.add(b2).add(b3).add(b4);

		return result.doubleValue();
	}
	
	public static void main(String[] args) {
		DistanceCulc lambert = new DistanceCulc();
		Double lat1 = 35.658572;
		Double lon1 = 139.745411;
		Double lat2 = 48.858377;
		Double lon2 = 2.294503;
		
		System.out.println(lambert.onoFormula(lat1, lon1, lat2, lon2));
		//GoogleMap 9719.48km
                //国土地理院 9743.05km
	}
}
