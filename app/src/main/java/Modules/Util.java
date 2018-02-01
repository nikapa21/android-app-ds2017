package Modules;

import com.google.android.gms.maps.model.LatLng;
import com.itshareplus.googlemapdemo.MapsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Chord.Pair;

/**
 * Created by nikos on 02/01/2018.
 */

public class Util {

    public static void parseJSon(DirectionFinderListener listener, Map<Pair, String> resultWithoutCached, Map<Pair, String> pairCachedDestinations) throws JSONException {

        List<Route> routes = new ArrayList<Route>();
        List<String> dataFiles = new ArrayList<>();

        if (resultWithoutCached != null){
            dataFiles.addAll(resultWithoutCached.values());
        }

        if (pairCachedDestinations != null){
            dataFiles.addAll(pairCachedDestinations.values());
        }

        for(String data : dataFiles){
            System.out.println("Data to be parsed " + data);
            if (data == null)
                return;

            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonRoutes = jsonData.getJSONArray("routes");

            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                Route route = new Route();

                JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                route.distance = new Distance(jsonDistance.getString("text"), jsonDistance.getInt("value"));
                route.duration = new Duration(jsonDuration.getString("text"), jsonDuration.getInt("value"));
                route.endAddress = jsonLeg.getString("end_address");
                route.startAddress = jsonLeg.getString("start_address");
                route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                route.points = decodePolyLine(overview_polylineJson.getString("points"));

                routes.add(route);
            }

        }
        System.out.println("Returning number of routes " + routes.size());
        listener.onDirectionFinderSuccess(routes);
    }

    private static List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
}
