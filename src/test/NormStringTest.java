/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kaeru
 */
public class NormStringTest {
	public static void main(String[] args) {
		String[] pid = new String[]{"ABCD", "SYEO-10-76453", "SYEO-10TO-76453", "NYEO-10-76453", "SYEO-TO10-76453"};
		
		Pattern p1 = Pattern.compile("^*YEO-[*[^TO]]");
		Pattern p2 = Pattern.compile("^*YEO-TO*");
		
		for(String p : pid){
			Matcher m = p1.matcher(p);
			Matcher m2 = p2.matcher(p);
			
			System.out.println("m1="+m.find()+" m2="+m2.find());
		}
	}
}
