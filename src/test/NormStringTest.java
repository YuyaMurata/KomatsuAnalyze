/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import param.KomatsuUserParameter;

/**
 *
 * @author kaeru
 */
public class NormStringTest {
	public static void main(String[] args) {
		String[] pid = new String[]{"08085-00001","ABCD", "SYEO-10-76453", "NSYESYEO-10TO-76453", "NYEO-10-76453", "SYEO-TO10-73", "エンジン", "中古エンジン再生", "E/G", "エンジンオイル", "E/G OIL"};
		
		Pattern p1 = Pattern.compile("^*YEO-[*[^TO]]");
		Pattern p2 = Pattern.compile("^*YEO-TO*");
		Pattern p3 = Pattern.compile("^\\d{5}-\\d{5}");
                Pattern p4 = Pattern.compile("^[0-9A-Z]{2,}-[0-9A-Z]{2,}-[0-9A-Z]{4,}$");
		
		/*for(String p : pid){
			Matcher m = p1.matcher(p);
			Matcher m2 = p2.matcher(p);
                        Matcher m3 = p3.matcher(p);
                        Matcher m4 = p4.matcher(p);
			
			System.out.println("m1="+m.find()+" m2="+m2.find()+" m3="+m3.find()+" m4="+m4.find());
		}*/
                
                System.out.println("ABCD".matches("^[0-9A-Z]{2,}-[0-9A-Z]{2,}-[0-9A-Z]{4,}$"));
                System.out.println("NSYESYEO-10TO-76453".matches("^[0-9A-Z]{2,}-[0-9A-Z]{2,}-[0-9A-Z]{4,}$"));
                System.out.println("NSYESYEO-10TO-73".matches("^[0-9A-Z]{2,}-[0-9A-Z]{2,}-[0-9A-Z]{4,}$"));
                
                for(String p : pid){
                    System.out.println(p+"="+p.matches("^.*(エンジン|E/G).*$"));
		}
                
                //Test
               // KomatsuUserParameter.PC200_MAINTEPARTS_DEF.entrySet().stream().forEach(s ->{
                //    System.out.println("SYEO-10-76453".matches(s.getKey()));
                //});
	}
}
