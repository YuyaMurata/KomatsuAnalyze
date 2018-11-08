/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.service;

import java.util.List;
import java.util.Map;
import javafx.concurrent.Task;
import viewer.graph.TimeSpreadChart;

/**
 *
 * @author ZZ17390
 */
public class ButtonTask extends Task<Void>{

    @Override
    protected Void call() throws Exception {
        System.out.println("Current Syaryo is "+ButtonService.syaryo.getName()+" Menu="+ButtonService.smenu);
        
        if(ButtonService.syaryo == null){
            System.out.println("Current Syaryo is Null! Menu="+ButtonService.smenu);
            return null;
        }
        
        Map<String, List> map = ButtonService.syaryo.get(ButtonService.smenu);

        if(map == null)
            return null;
        
        ButtonService.syaryo.startHighPerformaceAccess();
        new TimeSpreadChart().graph(ButtonService.smenu, ButtonService.syaryo);
        ButtonService.syaryo.stopHighPerformaceAccess();
            
        return null;
    }
}
