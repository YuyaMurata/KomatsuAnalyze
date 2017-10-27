/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.map;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapShape;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;
import json.JsonToSyaryoObj;
import obj.SyaryoObject;

/**
 * FXML Controller class
 *
 * @author ZZ17390
 */
public class GoogleMapFXMLController implements Initializable {

    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    private DecimalFormat formatter = new DecimalFormat("###.00000");

    @FXML
    private Slider dateSlider;
    @FXML
    private Button runBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        mapView.addMapInializedListener(() -> configureMap());
        dateSlider.valueProperty().addListener((ObservableValue<? extends Number> observ, Number oldVal, Number newVal) -> {
            dateScroll(oldVal.doubleValue(), newVal.doubleValue());
        });
    }

    protected void configureMap() {
        MapOptions mapOptions = new MapOptions();

        //Create Map
        mapOptions.center(new LatLong(35.670889, 139.742127))
                .mapType(MapTypeIdEnum.ROADMAP)
                .zoom(9);
        map = mapView.createMap(mapOptions, false);

        //Get Syaryo Data
        Map<String, SyaryoObject> syaryoMap = new JsonToSyaryoObj().reader("syaryo_obj_WA470_form.json");

        String rule = "WA470-7-10180";
        List<SyaryoObject> syaryoList = syaryoMap.values().stream()
                .filter(s -> s.getName().contains(rule))
                .filter(s -> s.getGPS() != null)
                .collect(Collectors.toList());
        System.out.println("GPS車両 : " + syaryoList.size());

        //allPoint(syaryoList);
        timePoint(syaryoMap.get(rule));

        sliderInitialize();
    }

    public void sliderInitialize() {
        //Slider Initialize
        dateSlider.setMin(data.first());
        dateSlider.setMax(data.last());
        dateSlider.setMajorTickUnit(1);
        dateSlider.setBlockIncrement(1);
        //dateSlider.setShowTickLabels(true);
        dateSlider.setShowTickMarks(true);
        dateSlider.setSnapToTicks(true);
        dateSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double value) {
                return String.valueOf(value.intValue());
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });
    }

    MapPathData data;

    public void timePoint(SyaryoObject syaryo) {
        System.out.println(syaryo.getName() + ":" + syaryo.getGPS().size());
        data = new MapPathData(syaryo.getName());

        for (String gpsDate : syaryo.getGPS().keySet()) {
            if (gpsDate.contains("#")) {
                continue;
            }
            String ymd = gpsDate.split(" ")[0].replace("/", "").substring(0, 8);

            data.addData(Integer.valueOf(ymd), (String) syaryo.getGPS().get(gpsDate).get(0));
        }

    }

    protected void dateScroll(Double oldv, Double newv) {
        System.out.println("Scroll!" + newv);
        //String oy = sliderMap.floorEntry(oldv).getValue();
        if (oldMapShape != null) {
            for (MapShape old : oldMapShape) {
                System.out.println(old);
                map.removeMapShape(old);
            }
            oldMapShape.clear();
        }

        gpsPath(data.getPath(newv.intValue()), 5, data.color);
    }

    //Map process
    public void marker(String name, LatLong latLong, String content) {
        //Create GPS Marker
        MarkerOptions mopt = new MarkerOptions();
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        //Marker
        Marker marker = new Marker(mopt);
        marker.setPosition(latLong);

        InfoWindow info = new InfoWindow(infoWindowOptions);
        info.setContent(name + ":" + content);
        info.open(map, marker);

        map.addMarker(marker);
    }

    private List<MapShape> oldMapShape = new ArrayList<>();

    public void gpsPath(List<LatLong> path, int size, String color) {
        PolylineOptions line_opt;
        Polyline line;

        line_opt = new PolylineOptions();
        line_opt.path(new MVCArray(path.toArray(new LatLong[path.size()])))
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor(color)
                .strokeWeight(size)
                .visible(true);

        line = new Polyline(line_opt);
        map.addMapShape(line);
        oldMapShape.add(line);
    }

    public void gpsPoint(List<LatLong> path, int size, String color, Boolean info) {
        for (LatLong latlong : path) {
            List point = new ArrayList();
            point.add(latlong);

            gpsPath(point, size, color);
        }

        if (info) {
            marker("", path.get(0), color);
        }
    }

    @FXML
    private void runTime(ActionEvent event) {
    }
}
