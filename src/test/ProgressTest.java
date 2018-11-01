/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author Shingo Suzuki
 */
public class ProgressTest extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        HBox box = new HBox(10);
        Button button = new Button("Task start");
        ProgressBar bar = new ProgressBar();
        Label text = new Label("0/100");

        // HBox
        box.setAlignment(Pos.CENTER);
        box.getChildren().addAll(button, bar, text);

        // Button
        button.setOnAction(e -> {
            Task task = getTask();
            bar.progressProperty().unbind();
            bar.progressProperty().bind(task.progressProperty());
            text.textProperty().bind(task.messageProperty());
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(task);
            task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, wse -> executor.shutdown());
            Platform.runLater(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            });
        });

        // ProgressBar
        bar.setProgress(0);
        bar.setPrefWidth(180);

        // Stage
        Scene scene = new Scene(box, 360, 160);
        stage.setScene(scene);
        stage.setTitle("ProgressBar Sample");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Task getTask() {
        return new Task() {
            @Override
            protected String call() throws Exception {
                for (int i = 1; i <= 100; i++) {
                    updateProgress(i, 100);
                    updateMessage(String.format("%d/%d", i, 100));
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                return "Done";
            }
        };
    }
}
