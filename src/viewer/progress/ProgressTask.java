/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.progress;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.ProgressBar;
import json.SyaryoToZip3;

/**
 *
 * @author ZZ17390
 */
public class ProgressTask {

    private ProgressBar bar;

    public ProgressTask(ProgressBar bar) {
        this.bar = bar;
    }

    public void start() {
        Task task = getTask();
        bar.progressProperty().unbind();
        bar.progressProperty().bind(task.progressProperty());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(task);
        //task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, wse -> executor.shutdown());
    }

    private Task getTask() {
        return new Task() {
            @Override
            protected String call() throws Exception {
                while (SyaryoToZip3.runnable) {
                    long s = (long) (100 * (double) SyaryoToZip3.availSize / (double) SyaryoToZip3.fileSize);
                    updateProgress(s, 100);
                    TimeUnit.MILLISECONDS.sleep(100);
                    System.out.println(s+"%("+SyaryoToZip3.availSize + ")/" + SyaryoToZip3.fileSize);
                    
                    if (SyaryoToZip3.availSize >= SyaryoToZip3.fileSize) {
                        break;
                    }
                }
                
                updateProgress(100, 100);
                
                System.out.println("Done");
                return "Done";
            }
        };
    }
}
