/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package viewer.easy;

import file.CSVFileReadWrite;
import param.KomatsuDataParameter;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import obj.SyaryoLoader;
import obj.SyaryoObject;
import viewer.service.ButtonService;

/**
 * FXML Controller class
 *
 * @author kaeru_yuya
 */
public class EasyViewerFXMLController implements Initializable {

    @FXML
    private MenuBar menu;
    @FXML
    private MenuItem import_json;
    @FXML
    private ListView<?> keylist;
    private Accordion viewarea;
    
    private static SyaryoLoader LOADER = SyaryoLoader.getInstance();
    private Map<String, SyaryoObject> syaryoMap;

    @FXML
    private Label id_label;
    @FXML
    private ComboBox<String> datafilter;
    @FXML
    private MenuItem rightClick_copy;
    @FXML
    private Label number_syaryo_label;
    @FXML
    private ToggleButton filter_button;
    @FXML
    private ToggleButton order_button;
    @FXML
    private Label index_number_label;
    @FXML
    private Button graph_button;
    @FXML
    private ComboBox<String> graph_menu;

    private SyaryoObject currentSyaryo;
    private String currentFile;

    @FXML
    private MenuItem smr_plot;
    @FXML
    private MenuItem smr_hist;

    @FXML
    private TextField searchBox;
    @FXML
    private Button searchBtn;
    @FXML
    private VBox dataBox;
    @FXML
    private ProgressIndicator fileProgress;
    @FXML
    private MenuItem showJSON;
    @FXML
    private Label amountData;
    @FXML
    private Button csv_download;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        syaryoMap = new HashMap();
        machineListInitialize();
        graphMenuSettings();
        initializeAccordion(KomatsuDataParameter.DATA_ORDER);
        
        //default 動作しないため修正
        //defaultFileLoad(new File("syaryo\\syaryo_obj_PC200_sv_form.bz2"));
    }

    public void machineListInitialize() {
        //Event
        keylist.getSelectionModel().selectedIndexProperty().addListener(
            (ov, old, current) -> {
                // リスト・ビュー内の選択項目を出力
                System.out.println(ov);
                machineHistorySelected(current.intValue());
            }
        );
    }

    //Selected Machine
    public void machineHistorySelected(Integer index) {
        System.out.println("Selection in the listView is : " + index);
        if (index < 0) {
            return;
        }
        String name = keylist.getItems().get(index).toString();

        currentSyaryo = syaryoMap.get(name);
        index_number_label.setText(String.valueOf(index + 1));
        id_label.setText(currentSyaryo.getName());

        //データの設定
        settingData(currentSyaryo);
    }

    private void initializeAccordion(List<String> order) {
        TitledPane[] tps = new TitledPane[order.size()];
        //TextArea[] tes = new TextArea[KomatsuDataParameter.DATA_ORDER.size()];

        int i = 0;
        for (String data : order) {
            TextArea ta = new TextArea();
            ta.prefHeight(250d);
            tps[i] = new TitledPane(data, ta);
            tps[i].setAnimated(false);
            tps[i].setExpanded(false);
            i++;
        }

        dataBox.getChildren().setAll(tps);
        
        //フィルタ設定
        List<String> filter = new ArrayList<>(order);
        filter.set(0, "ALL");
        datafilter.getItems().setAll(filter);
    }

    //アコーディオンの設定
    private void settingData(SyaryoObject syaryo) {
        int i = 0;
        for (Node node : dataBox.getChildren()) {
            TitledPane title = (TitledPane) node;
            String[] str;
            str = textdump(syaryo.get(title.getText().split(" ")[0].replace("×", "")));
            ((TextArea) title.getContent()).setText(str[1]);
            title.setText(check(title.getText(), str));
            i++;
        }
    }

    private String check(String title, String[] datastr) {
        if (datastr[1] == null) {
            return "×" + title.split(" ")[0].replace("×", "");
        } else {
            return title.split(" ")[0].replace("×", "") + " " + datastr[0];
        }
    }

    private String[] textdump(Map<String, List> m) {
        if (m == null) {
            return new String[]{"", null};
        }

        StringBuilder sb = new StringBuilder();
        for (String d : m.keySet()) {
            sb.append(d);
            sb.append(m.get(d));
            sb.append("\n");
        }

        return new String[]{String.valueOf(m.size()), sb.toString()};
    }

    @FXML
    private void loadJSONFile(ActionEvent event) {
        FileChooser filechooser = new FileChooser();
        filechooser.setInitialDirectory(new File(KomatsuDataParameter.SYARYOOBJECT_FDPATH));
        File file = filechooser.showOpenDialog(menu.getScene().getWindow());

        fileLoad(file);
    }
    
    //default 動作しないため修正
    private void defaultFileLoad(File file) {
        if (LOADER.isServerRun() && LOADER.getFilePath().contains("syaryo_obj")) {
            fileLoad(new File("Default File"));
        }else
            fileLoad(file);        
    }
    
    private void fileLoad(File file) {
        if (file != null) {
            id_label.setText(file.getName());
            
            if(file.getName().contains("_mid_")){
                String f = file.getName();
                String od = f.split("_").length < 5?f.split("_")[3].replace(".bz2", ""):f.split("_")[3]+"_"+f.split("_")[4].replace(".bz2", "");
                initializeAccordion(Arrays.asList(new String[]{od}));
            }else{
                initializeAccordion(KomatsuDataParameter.DATA_ORDER);
            }
                
            final ExecutorService exec = Executors.newSingleThreadExecutor();
            Task ft = fileLoadTask(file);
            exec.submit(ft);
            ft.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, wse -> exec.shutdown());
        }

        //Filter Initialize
        initializeFilter(true);
    }

    private Task<Void> fileLoadTask(File file) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> fileProgress.setProgress(-1));
                Platform.runLater(() -> id_label.setText(file.getName()));
                
                //車両マップの読み込み
                LOADER.setFile(file);
                syaryoMap = LOADER.getSyaryoMapWithHeader();
                
                Platform.runLater(() -> updateKeyList(new ArrayList(new TreeSet(syaryoMap.keySet()))));
                Platform.runLater(() -> fileProgress.setProgress(1));
                return null;
            }
        };
    }

    private void updateKeyList(List keys) {
        ObservableList list = FXCollections.observableArrayList(keys);
        keylist.setItems(list);
        number_syaryo_label.setText(keylist.getItems().size() + " / " + syaryoMap.size());
    }

    private void initializeFilter(Boolean f) {
        //Select Filter
        if (f) {
            datafilter.getSelectionModel().select(0);
        }

        //Order Initialize
        order_button.setSelected(false);
        order_button.setText("N");

        //Filter Button Initialize
        filter_button.setSelected(false);
        filter_button.setText("NF");
    }

    private static Map currentFilterMap;

    @FXML
    private void selectFilter(ActionEvent event) {
        System.out.println("Selection in the listView is : " + datafilter.getValue());
        currentFilterMap = null;

        //Filter Button Initialize
        initializeFilter(false);

        //Filter Button
        filter_button.setSelected(true);
        onFilter(event);
    }

    private void selectOrder(ActionEvent event) {
        List list;
        if (datafilter.getValue().equals("ALL") || currentFilterMap == null) {
            return;
        } else if (order_button.isSelected()) {
            list = (List) currentFilterMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue().reversed())
                .map(s -> ((Map.Entry) s).getKey().toString())
                .collect(Collectors.toList());
            order_button.setText("D");
        } else {
            list = (List) currentFilterMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(s -> ((Map.Entry) s).getKey().toString())
                .collect(Collectors.toList());
            order_button.setText("A");
        }
        //更新処理
        updateKeyList(list);
    }

    @FXML
    private void rightClickCopy(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(id_label.getText());
        clipboard.setContent(content);
    }

    private void graphMenuSettings() {
        graph_menu.getItems().addAll(KomatsuDataParameter.GRAPH_PY.keySet());
    }

    @FXML
    private void rightClickCopyList(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        StringBuilder sb = new StringBuilder();
        for (Object id : keylist.getItems()) {
            sb.append(id);
            sb.append("\n");
        }
        content.putString(sb.toString());
        clipboard.setContent(content);
    }

    @FXML
    private void onFilter(ActionEvent event) {
        filtering(datafilter.getValue(), filter_button.isSelected());
    }

    //Curent Filter
    private Map enableFilterMap;
    private Map disableFilterMap;

    private void filtering(String filter, Boolean btnEnDis) {
        if (filter.equals("ALL")) {
            updateKeyList((new ArrayList(new TreeSet(syaryoMap.keySet()))));
            return;
        }

        if (currentFilterMap == null) {
            enableFilterMap = new ConcurrentHashMap();
            disableFilterMap = new ConcurrentHashMap();
            syaryoMap.values().parallelStream()
                .forEach(s -> {
                    Map data = s.get(filter);
                    if (data != null) {
                        enableFilterMap.put(s.getName(), data.size());
                    } else {
                        disableFilterMap.put(s.getName(), 0);
                    }
                });
            
            int cnt = enableFilterMap.values().stream().mapToInt(size -> Integer.valueOf(size.toString())).sum();
            amountData.setText("合計:"+String.valueOf(cnt)+"件");
        }

        if (btnEnDis) {
            currentFilterMap = enableFilterMap;
            filter_button.setText("EN");
        } else {
            currentFilterMap = disableFilterMap;
            filter_button.setText("DS");
        }

        //更新処理
        updateKeyList((new ArrayList(new TreeSet(currentFilterMap.keySet()))));
    }

    @FXML
    private void onOrder(ActionEvent event) {
        selectOrder(event);
    }

    ButtonService service = new ButtonService();

    @FXML
    private void graphAction(ActionEvent event) {
        String v = graph_menu.getValue();
        if (v == null) {
            return;
        }

        System.out.println(v+":"+LOADER.indexes(v));
        service.setInfo(v, LOADER.index(v, "VALUE"), currentSyaryo);
        service.restart();
    }

    @FXML
    private void plotSMR(ActionEvent event) {
        keylist.getItems();
    }

    @FXML
    private void histSMR(ActionEvent event) {
        keylist.getItems();
    }

    @FXML
    private void clickSearch(ActionEvent event) {
        if (searchBox.getText().equals("")) {
            updateKeyList(new ArrayList(new TreeSet(syaryoMap.keySet())));
            return;
        }

        List<String> targets;
        String[] searchWord = searchBox.getText().replace(" ", "").split(",");

        List<String> searchList;
        Map<String, Integer> result = new HashMap<>();

        if (datafilter.getValue().equals("ALL")) {
            //車両名完全一致
            searchList = Arrays.asList(searchWord).stream().filter(s -> syaryoMap.get(s) != null).collect(Collectors.toList());
            if (!searchList.isEmpty()) {
                updateKeyList(searchList);
                return;
            }

            //車両名部分一致
            searchList = Arrays.asList(searchWord).stream()
                .map(s -> syaryoMap.keySet().stream()
                    .filter(s2 -> s2.contains("-"))
                .filter(s2 -> s2.split("-")[2].equals(s))
                .findFirst())
                .filter(f -> f.isPresent())
                .map(f -> f.get())
                .collect(Collectors.toList());
            if (!searchList.isEmpty()) {
                updateKeyList(searchList);
                return;
            }
            
            System.out.println("Not Found "+String.join(",", searchWord));
            updateKeyList(new ArrayList());
            return ;
        }

        
        targets = Arrays.asList(new String[]{datafilter.getValue()});
        searchList = new ArrayList<>(currentFilterMap.keySet());
        

        searchList.parallelStream().forEach(s -> {
            SyaryoObject syaryo = syaryoMap.get(s);

            for (String target : targets) {
                if (syaryo.get(target) == null) {
                    continue;
                }

                syaryo.startHighPerformaceAccess();

                Map<String, Boolean> check = new HashMap<>();
                for (String w : searchWord) {
                    check.put(w, false);
                }

                //System.out.println(s);
                for (String sbn : syaryo.get(target).keySet()) {
                    List data = syaryo.get(target).get(sbn);
                    for (String w : searchWord) {
                        Boolean and = false;

                        if (sbn.split("#")[0].equals(w)) {
                            and = true;
                        } else if (data.contains(w)) {
                            and = true;
                        }

                        if (and) {
                            check.put(w, true);
                        }
                    }
                }

                if (!check.values().stream().filter(c -> !c).findFirst().isPresent()) {
                    result.put(s, 1);
                }

                syaryo.stopHighPerformaceAccess();
            }
        });

        updateKeyList(new ArrayList(new TreeSet(result.keySet())));
    }

    @FXML
    private void showJSON(ActionEvent event) {
        System.out.println(currentSyaryo.dump());
    }

    @FXML
    private void downloadCSV(ActionEvent event) {
        if(currentSyaryo == null)
            return ;
            
        try(PrintWriter pw = CSVFileReadWrite.writer(currentSyaryo.name+".csv")){
            
        }
    }
}
