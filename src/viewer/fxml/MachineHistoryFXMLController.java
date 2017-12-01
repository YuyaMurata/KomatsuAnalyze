package viewer.fxml;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import json.JsonToSyaryoObj;
import obj.Element;
import obj.SyaryoElements;
import obj.SyaryoObject;
import viewer.csv.CSVViewerOutput;
import viewer.filter.DataRuleFilter;

/**
 *
 * @author kaeru
 */
public class MachineHistoryFXMLController implements Initializable {

    @FXML
    private VBox root_vbox;
    @FXML
    private MenuBar fileMenu;
    @FXML
    private SplitPane history_splitpane;
    @FXML
    private ListView<?> machineList;

    private Map<String, SyaryoObject> syaryoMap = new HashMap();
    @FXML
    private Label history_label;
    @FXML
    private ListView<Item> pList;
    @FXML
    private ListView<Item> cList;
    private Map<String, ObservableList<Item>> cMap;
    @FXML
    private TableView sampleView;
    @FXML
    private Button applyButton;

    private SyaryoObject syaryo;
    @FXML
    private Button csv;
    @FXML
    private CheckBox allCheck;
    @FXML
    private ChoiceBox<String> csvOutputForm;
    @FXML
    private Label csvWorkingLabel;
    @FXML
    private TextField conditionField;
    private DataRuleFilter filter;
    @FXML
    private CheckBox machineAllCkeck;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //File Read
        String fileName = "json\\syaryo_obj_WA470_form.json";
        syaryoMap = new JsonToSyaryoObj().reader(fileName);
        syaryo = syaryoMap.values().stream().findFirst().get();

        //ViewData Initialize
        machineListInitialize();
        machineDataListInitialize();

        //CSVOutputForm Initialize
        csvOutputFormInitialize();
    }

    public void machineListInitialize() {
        //Add ListView
        ObservableList machines = machineList.getItems();

        int i = 0;
        for (Object name : syaryoMap.keySet()) {
            machines.add((++i) + " : " + name);
        }

        //Event
        machineList.getSelectionModel().selectedIndexProperty().addListener(
            (ov, old, current) -> {
                // リスト・ビュー内の選択項目を出力
                machineHistorySelected(current.intValue());
            }
        );
    }

    public void machineDataListInitialize() {
        //ListViewSettings
        cMap = new HashMap<>();
        for (String prefName : SyaryoElements.list) {
            //parent
            Item pref = new Item(0, prefName, false);
            pList.getItems().add(pref);

            //child
            ListView<Item> list = new ListView();
            for (Element e : SyaryoElements.map.get(prefName)) {
                if (e.getText().contains("format")) {
                    continue;
                }

                Item pref2 = new Item(e.getNo(), e.getText(), false);
                list.getItems().add(pref2);
            }
            cMap.put(prefName, list.getItems());
        }
        pList.setCellFactory(CheckBoxListCell.forListView((Item item) -> item.onProperty()));

        //Selected SyaryoElement-parent
        pList.getSelectionModel().selectedIndexProperty().addListener(
            (ov, old, current) -> {
                // リスト・ビュー内の選択項目を出力
                String name = pList.getItems().get(current.intValue()).toString();
                elementSelected(name);
            }
        );
    }

    public void csvOutputFormInitialize() {
        ObservableList<String> options
            = FXCollections.observableArrayList(
                "None",
                "Time");
        csvOutputForm.getItems().addAll(options);
        csvOutputForm.getSelectionModel().select(0);
    }

    //Selected Machine
    public void machineHistorySelected(Integer index) {
        System.out.println("Selection in the listView is : " + index);
        String name = machineList.getItems().get(index).toString().split(" : ")[1];
        history_label.setText(name);
        syaryo = syaryoMap.get(name);
    }

    //Selected SyaryoElements
    public void elementSelected(String name) {
        cList.setItems(cMap.get(name));
        cList.setCellFactory(CheckBoxListCell.forListView((Item item) -> item.onProperty()));
    }

    Map<String, Integer> selectData;

    @FXML
    private void applyAction(ActionEvent event) {
        System.out.println("Apply!");
        selectData = new LinkedHashMap();

        sampleView.getColumns().clear();

        ObservableList<ObservableList> data = FXCollections.observableArrayList();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add(syaryo.getName());

        List<TableColumn> column = new ArrayList<>();
        column.add(createColumn("機種・型・機番", 0));
        selectData.put("機種・型・機番", 0);

        int n = 0;
        for (Item item : pList.getItems()) {
            //System.out.println(item.getName());
            if (!item.isOn()) {
                continue;
            }
            for (Item item2 : cMap.get(item.getName())) {
                if (item2.isOn()) {
                    //System.out.println("("+item2.getIndex()+","+item2.name+","+item2.isOn()+"),");
                    n++;
                    final int idx = n;
                    String colName = item.getName() + "." + item2.getName();
                    column.add(createColumn(colName, idx));
                    selectData.put(colName, item2.getIndex());
                    list.add(syaryo.getCol(item.getName(), item2.getIndex()).get(0));
                }
            }
        }
        data.add(list);
        sampleView.getColumns().addAll(column.toArray(new TableColumn[column.size()]));

        sampleView.setItems(data);
    }

    public TableColumn createColumn(String header, int index) {
        TableColumn tc = new TableColumn(header);
        tc.setCellValueFactory(
            new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                return new SimpleStringProperty(param.getValue().get(index).toString());
            }
        });

        return tc;
    }

    @FXML
    private void writeCSV(ActionEvent event) {
        //Update
        applyAction(null);

        //Initialize
        csvWorkingLabel.setText("Start : CSV 出力");

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");

        List syaryos = new ArrayList();
        String filename;
        if (machineAllCkeck.isSelected()) {
            syaryos = syaryoMap.values().stream().collect(Collectors.toList());
            filename = syaryo.getMachine() + "_" + sdf.format(now) + ".csv";
        } else {
            syaryos.add(syaryo);
            filename = syaryo.getName() + "_" + sdf.format(now) + ".csv";
        }
        
        String[] condition = conditionField.getText().split(",");
        filter = new DataRuleFilter();
        if(condition.length > 0){
            for(String c : condition){
                filter.setRule(c);
            }
        }

        //Select Form
        int n = 0;
        if (csvOutputForm.getSelectionModel().isSelected(0)) {
            n = CSVViewerOutput.none("None_" + filename, filter, selectData, syaryos);
        } else if (csvOutputForm.getSelectionModel().isSelected(1)) {
            n = CSVViewerOutput.time("Time_" + filename, filter, selectData, syaryos);
        } else {
            System.out.println("Not select output form.");
        }

        csvWorkingLabel.setText("Stop : " + n + "行 CSV 出力");
    }

    @FXML
    private void allChecked(ActionEvent event) {
        if (allCheck.isSelected()) {
            for (Item item : pList.getItems()) {
                item.setOn(true);
                elementSelected(item.getName());
                for (Item item2 : cList.getItems()) {
                    item2.setOn(true);
                }
            }
        } else {
            for (Item item : pList.getItems()) {
                item.setOn(false);
                elementSelected(item.getName());
                for (Item item2 : cList.getItems()) {
                    item2.setOn(false);
                }
            }
        }
    }

    static class Item {

        private final StringProperty name = new SimpleStringProperty();
        private final BooleanProperty on = new SimpleBooleanProperty();
        private final Integer index;

        public Item(Integer index, String name, boolean on) {
            this.index = index;
            setName(name);
            setOn(on);
        }

        public final StringProperty nameProperty() {
            return this.name;
        }

        public final Integer getIndex() {
            return this.index;
        }

        public final String getName() {
            return this.nameProperty().get();
        }

        public final void setName(final String name) {
            this.nameProperty().set(name);
        }

        public final BooleanProperty onProperty() {
            return this.on;
        }

        public final boolean isOn() {
            return this.onProperty().get();
        }

        public final void setOn(final boolean on) {
            this.onProperty().set(on);
        }

        @Override
        public String toString() {
            return getName();
        }

    }
}
