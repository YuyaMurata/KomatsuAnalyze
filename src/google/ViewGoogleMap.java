package google;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;//★
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ViewGoogleMap extends Application implements MapComponentInitializedListener {

    GoogleMapView mapView;
    GoogleMap map;

    @Override
    public void start(Stage stage) throws Exception {

        //Create the JavaFX component and set this as a listener so we know when 
        //the map has been initialized, at which point we can then begin manipulating it.
        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);

        Scene scene = new Scene(mapView);

        stage.setTitle("JavaFX and Google Maps");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void mapInitialized() {
        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        //N36.21.32.010_E136.25.18.061 -> N = '+' E = '+'
        //36 + (21*1/60) + (32*1/3600) + (10*1/216000)
        double la = compValue("N36.21.32.010");
        double lo = compValue("E136.25.18.061");
        System.out.println(la+","+lo);
        LatLong lat = new LatLong(la, lo);
        
        mapOptions.center(lat)
            .mapType(MapTypeIdEnum.ROADMAP) //★
            .overviewMapControl(false)
            .panControl(false)
            .rotateControl(false)
            .scaleControl(false)
            .streetViewControl(false)
            .zoomControl(false)
            .zoom(12);

        map = mapView.createMap(mapOptions);

        //Add a marker to the map
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(lat)
            .visible(Boolean.TRUE)
            .title("My Marker");

        Marker marker = new Marker(markerOptions);

        map.addMarker(marker);

    }
    
    public double compValue(String str){
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

    public static void main(String[] args) {
        launch(args);
    }
}
