/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

/**
 *
 * @author kaeru
 */
public class OnoMethod {
	//WGS84
	private static final Double A = 6378137.0;	//赤道半径
	private static final Double F = 1/298.257222101;	//扁平率
	private static final Double B = A * (1.0 - F);		//極半径
	
	/**
	 * 小野の公式
	 * 小野房吉. (1984). 電波航法の新しい測位原理と測位精度の評価. 航海, 79, 35-40.
	 * http://www2.nc-toyama.ac.jp/~mkawai/lecture/sailing/geodetic/geosail.html
	 */
	
	public Double distanceOno(Double lat1, Double lon1, Double lat2, Double lon2){
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
		
		return distance / 1000;
	}
	
	public static void main(String[] args) {
		OnoMethod lambert = new OnoMethod();
		Double lat1 = 35.658572;
		Double lon1 = 139.745411;
		Double lat2 = 48.858377;
		Double lon2 = 2.294503;
		
		System.out.println(lambert.distanceOno(lat1, lon1, lat2, lon2));
		//GoogleMap 9719.48km
	}
}
