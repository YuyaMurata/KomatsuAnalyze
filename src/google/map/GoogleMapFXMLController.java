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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

        String rule = "";//"WA470-7-10180";
        List<SyaryoObject> syaryoList = syaryoMap.values().stream()
                .filter(s -> s.getName().contains(rule))
                .filter(s -> s.getGPS() != null)
                .limit(5)
                .collect(Collectors.toList());
        System.out.println("GPS車両 : " + syaryoList.size());

        //allPoint(syaryoList);
        timePoint(syaryoList);
    }

    public void allPoint(List<SyaryoObject> syaryoList) {
        //Color
        Random rand = new Random();

        for (SyaryoObject syaryo : syaryoList) {

            System.out.println(syaryo.getName() + ":" + syaryo.getGPS().size());

            //Color
            Integer r = rand.nextInt(256);
            Integer g = rand.nextInt(256);
            Integer b = rand.nextInt(256);
            String color = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);

            //Create GPS Marker
            MarkerOptions mopt = new MarkerOptions();
            InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
            List<Marker> syaryoGPS = new ArrayList<>();
            List<LatLong> syaryoPath = new ArrayList<>();

            int i = 0;
            for (String gpsDate : syaryo.getGPS().keySet()) {
                String[] gps = syaryo.getGPS().get(gpsDate).get(0).toString().split("_");
                LatLong latLong = new LatLong(compValue(gps[0]), compValue(gps[1]));

                syaryoPath.add(latLong);
                List<LatLong> point = new ArrayList<>();
                point.add(latLong);
                point.add(latLong);

                if (i == 0 || i == syaryo.getGPS().size() - 1) {
                    if (i == (syaryo.getGPS().size() - 1)) {
                        //Marker
                        Marker marker = new Marker(mopt);
                        marker.setPosition(latLong);
                        syaryoGPS.add(marker);
                        InfoWindow info = new InfoWindow(infoWindowOptions);
                        info.setContent(syaryo.getName() + "\n" + gpsDate);
                        info.open(map, marker);
                    }

                    gpsPath(point, 15, color);
                } else {
                    gpsPath(point, 8, color);
                }

                i++;
                //System.out.println(gpsDate + ":" + latLong + " (" + i + "/" + syaryo.getGPS().size() + ")");
            }

            gpsPath(syaryoPath, 2, color);
            //Add Marker to Map
            map.addMarkers(syaryoGPS);
        }
    }

    private Map<String, Map<String, Map<String, List>>> timeMap;

    public void timePoint(List<SyaryoObject> syaryoList) {
        Random rand = new Random();
        timeMap = new TreeMap();

        for (SyaryoObject syaryo : syaryoList) {
            System.out.println(syaryo.getName() + ":" + syaryo.getGPS().size());
            //Color
            Integer r = rand.nextInt(256);
            Integer g = rand.nextInt(256);
            Integer b = rand.nextInt(256);
            String color = "#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);

            for (String gpsDate : syaryo.getGPS().keySet()) {
                String[] gps = syaryo.getGPS().get(gpsDate).get(0).toString().split("_");
                LatLong latLong = new LatLong(compValue(gps[0]), compValue(gps[1]));
                String y = gpsDate.substring(0, 4);

                if (timeMap.get(y) == null) {
                    timeMap.put(y, new HashMap<>());
                }

                if (timeMap.get(y).get(syaryo.getName()) == null) {
                    timeMap.get(y).put(syaryo.getName(), new HashMap<>());
                    timeMap.get(y).get(syaryo.getName()).put("date", new ArrayList());
                    timeMap.get(y).get(syaryo.getName()).put("latlong", new ArrayList());

                    //Color
                    timeMap.get(y).get(syaryo.getName()).put("color", new ArrayList());
                    timeMap.get(y).get(syaryo.getName()).get("color").add(color);
                }

                timeMap.get(y).get(syaryo.getName()).get("date").add(gpsDate);
                timeMap.get(y).get(syaryo.getName()).get("latlong").add(latLong);
            }
        }

        //Slider Initialize
        Double first = Double.valueOf(timeMap.keySet().toArray(new String[timeMap.size()])[0]);
        Double last = Double.valueOf(timeMap.keySet().toArray(new String[timeMap.size()])[timeMap.size() - 1]);

        dateSlider.setMin(first);
        dateSlider.setMax(last);
        dateSlider.setMajorTickUnit(1);
        dateSlider.setBlockIncrement(1);
        dateSlider.setShowTickLabels(true);
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

        //System.out.println(sliderMap);
    }

    protected void dateScroll(Double oldv, Double newv) {
        System.out.println("Scroll!" + newv);
        //String oy = sliderMap.floorEntry(oldv).getValue();
        if (oldMapShape != null) {
            for (MapShape old : oldMapShape) {
                map.removeMapShape(old);
            }
            oldMapShape.clear();
        }
        
        for (String sname : timeMap.get(String.valueOf(newv.intValue())).keySet()) {
            Map<String, List> syaryo = timeMap.get(String.valueOf(newv.intValue())).get(sname);
            gpsPath(syaryo.get("latlong"), 2, (String) syaryo.get("color").get(0));
            
            //marker(sname, (LatLong)syaryo.get("latlong").get(syaryo.get("latlong").size()-1), 
            //                (String)syaryo.get("date").get(syaryo.get("date").size()-1)));
        }
    }
    
    public void marker(String name, LatLong latLong, String content){
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

    public double compValue(String str) {
        //変換
        str = str.replace("N", "").replace("S", "-").replace("E", "").replace("W", "-");

        String[] s = str.split("\\.");

        BigDecimal b1 = new BigDecimal(s[0]);
        BigDecimal b2 = new BigDecimal(s[1]).divide(new BigDecimal("60"), 7, RoundingMode.HALF_UP);
        BigDecimal b3 = new BigDecimal(s[2]).divide(new BigDecimal("3600"), 7, RoundingMode.HALF_UP);
        BigDecimal b4 = new BigDecimal(s[3]).divide(new BigDecimal("21600"), 7, RoundingMode.HALF_UP);

        BigDecimal result = b1.add(b2).add(b3).add(b4);

        return result.doubleValue();
    }
}
