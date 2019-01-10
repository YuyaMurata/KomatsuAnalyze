/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.easy;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import file.SyaryoToCompress;
import program.r.R;

/**
 *
 * @author kaeru_yuya
 */
public class EasyViewMain extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("EasyViewerFXML.fxml"));
        Scene scene = new Scene(root);

        stage.setTitle("Easy View Ver.0.1a");
        stage.setScene(scene);
        stage.show();
        

        stage.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false) {
                //R.close();
                SyaryoToCompress.runnable = false;
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
