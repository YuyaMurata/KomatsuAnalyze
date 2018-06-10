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

public class JRITest {

    public static void main(String[] args) {
        Rengine engine = new Rengine(new String[]{"--no-save"}, false, null);
        REXP result = engine.eval("x <- matrix(c(12,32,25,30), nrow=2)");
        System.out.println(result.toString());
        double[][] d = result.asMatrix();
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[i].length; j++) {
                System.out.print(d[i][j] + " ");
            }
            System.out.println("");
        }

        result = engine.eval("res <- fisher.test(x)");
        System.out.println(result.asVector().at(0).asDouble());

        engine.end();
    }
}
