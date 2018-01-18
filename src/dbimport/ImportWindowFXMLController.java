/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbimport;

import file.CSVFileReadWrite;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import file.CSVFileReadWrite;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * FXML Controller class
 *
 * @author ZZ17390
 */
public class ImportWindowFXMLController implements Initializable {

    @FXML
    private TextField key_setting;
    @FXML
    private ChoiceBox<?> table_select;
    @FXML
    private CheckBox all_checkd;
    @FXML
    private TextField join_text;
    @FXML
    private Button sql_check_button;
    @FXML
    private Button start_button;
    @FXML
    private ProgressBar start_progress;
    @FXML
    private Label key_check_boolean;
    
    private Map map = new LinkedHashMap();
    @FXML
    private TableColumn<?, ?> table_field_list;
    @FXML
    private TableColumn<?, ?> table_name_list;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        String path = "resource\\importset\\layout\\enable";
        File[] flist = (new File(path)).listFiles();
        
        List list = table_select.getItems();
        for (File f : flist) {
            String tableName = f.getName().replace("Layout_", "");
            list.add(tableName);
            
            TableContent content = new TableContent(tableName);
            try(BufferedReader csv =  new BufferedReader(new FileReader(f))){
                String line = csv.readLine();
                while((line = csv.readLine()) != null){
                    String field = line.split(",")[2];
                    String name = line.split(",")[1];
                    content.add(field, name);
                }
            } catch (FileNotFoundException ex) {
            } catch (IOException ex) {
            }
            
            map.put(tableName, content);
        }
        
        testprint(map);
        eventSettings();
    }

    private void eventSettings(){
        table_select.getSelectionModel().selectedIndexProperty().addListener(
            (ov, old, current) -> {
                // リスト・ビュー内の選択項目を出力
                onChangeTable(current.intValue());
            }
        );
    }


    @FXML
    private void onChecked(ActionEvent event) {
    }

    @FXML
    private void onSQLCheckButton(ActionEvent event) {
    }

    @FXML
    private void onStartButton(ActionEvent event) {
    }

    private void onChangeTable(int id) {
        System.out.println(table_select.getItems().get(id));
    }
    
    
    private void testprint(Map testmap){
        testmap.entrySet().stream().forEach(System.out::println);
    }
}
