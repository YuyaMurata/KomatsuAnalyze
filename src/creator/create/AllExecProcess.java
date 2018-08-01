/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package creator.create;

import param.KomatsuDataParameter;

/**
 *
 * @author zz17390
 */
public class AllExecProcess {

    private static String[] kisyList = KomatsuDataParameter.KISY_LIST;

    public static void main(String[] args) {
        
        //String  kisy = "PC138US";
        for (String kisy : kisyList) {
            Long start = System.currentTimeMillis();
            
            TemplateCreate.create(kisy);
            Long st_temc = System.currentTimeMillis();
            
            TemplateToObjectCreate.create(kisy);
            Long st_temp2obj = System.currentTimeMillis();
            
            TemplateShuffle.create(kisy);
            Long st_shuffle = System.currentTimeMillis();
            
            int n= ObjectsJoiner.create(kisy, true);
            Long st_join = System.currentTimeMillis();
            
            //Time Check
            System.out.println("\n--- "+kisy+" ---\n"+
                                "Total:"+(st_join-start)+"ms\n"+
                                "Syaryo N:"+n+"\n"+
                                "  TemplateCreate:"+(st_temc-start)+"ms\n"+
                                "  TemplateToObjectCreate:"+(st_temp2obj-st_temc)+"ms\n"+
                                "  TemplateShuffle:"+(st_shuffle-st_temp2obj)+"ms\n"+
                                "  ObjectsJoiner:"+(st_join-st_shuffle)+"ms\n"+
                                "---------------\n\n");
        }
    }
}
