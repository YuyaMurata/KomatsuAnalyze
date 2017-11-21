/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author ZZ17390
 */
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
public class JRITest 
{
	public static void main(String[] args) 
	{
		Rengine engine = new Rengine(new String[]{"--no-save"}, false, null);
		engine.assign("a", new int[]{36});
		REXP result = engine.eval("sqrt(a)");
		System.out.println(result.asDouble());
		engine.end();
	}
}
