/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import obj.LoadSyaryoObject;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import param.KomatsuDataParameter;

/**
 *
 * @author ZZ17390
 */
public class AgentSimulator{

    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private SyaryoObject syaryo;
    private Map<String, List<String>> dateToSBN = new HashMap();
    private BlockingQueue<OneDayForm> dq = new LinkedBlockingDeque(7);;

    public AgentSimulator(SyaryoObject syaryo) {
        this.syaryo = syaryo;
    }

    public void setting() {
        this.syaryo.startHighPerformaceAccess();
        dateToSBN = AgentSettingTools.setDateToSBNs(syaryo);
        this.syaryo.stopHighPerformaceAccess();
    }
}
