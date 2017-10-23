/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package google.map;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
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
        dateSlider.valueProperty().addListener((ObservableValue<? extends Number> 
        observ, Number oldVal, Number newVal) -> {
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
        SyaryoObject syaryo = syaryoMap.get("WA470-7-10180");
        System.out.println(syaryo.getName() + ":" + syaryo.getGPS().size());

        //Create GPS Marker
        MarkerOptions mopt = new MarkerOptions();
        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        List<Marker> syaryoGPS = new ArrayList<>();
        List<LatLong> syaryoPath = new ArrayList<>();
        int i = 0;
        for (String gpsDate : syaryo.getGPS().keySet()) {
            String[] gps = syaryo.getGPS().get(gpsDate).get(0).toString().split("_");
            LatLong latLong = new LatLong(compValue(gps[0]), compValue(gps[1]));
            //Marker marker = new Marker(mopt);
            //marker.setPosition(latLong);
            //syaryoGPS.add(marker);

            /*InfoWindow info = new InfoWindow(infoWindowOptions);
            info.setContent(syaryo.getName()+"\n"+gpsDate);
            info.open(map, marker);*/
            syaryoPath.add(latLong);
            List<LatLong> point = new ArrayList<>();
            point.add(latLong);
            point.add(latLong);
            gpsPath(point, 10);

            i++;
            System.out.println(gpsDate+":"+latLong+" ("+i+"/"+syaryo.getGPS().size()+")");
        }

        gpsPath(syaryoPath, 5);
        
        //Add Marker to Map
        //map.addMarkers(syaryoGPS);
    }
    
    protected void dateScroll(double oldv, double newv){
        System.out.println("Scroll!");
    }
    
    public void gpsPath(List<LatLong> path, int size){
        PolylineOptions line_opt;
        Polyline line;

        line_opt = new PolylineOptions();
        line_opt.path(new MVCArray(path.toArray(new LatLong[path.size()])))
                .clickable(false)
                .draggable(false)
                .editable(false)
                .strokeColor("#ff4500")
                .strokeWeight(size)
                .visible(true);

        line = new Polyline(line_opt);
        map.addMapShape(line);
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
