package cz.zemankrystof.izastavka;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.annotations.Expose;

import java.util.Arrays;
import java.util.List;

import cz.zemankrystof.izastavka.data.Constants;
import cz.zemankrystof.izastavka.data.model.Datum;
import cz.zemankrystof.izastavka.data.model.SurroundingVeh;
import cz.zemankrystof.izastavka.data.remote.ApiUtils;
import cz.zemankrystof.izastavka.data.remote.StopsService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Fragment containing all map functionality
 */
public class MapFragment extends Fragment {

    Handler refreshVehicleHandler;
    Runnable refreshVehicleRunnable;

    public static MapFragment newInstance() {
        MapFragment myFragment = new MapFragment();
       /* Bundle args = new Bundle();
        args.putString("defaultURL", Constants.item1URL);
        myFragment.setArguments(args);*/
        return myFragment;
    }

    public MapFragment() {
    }

    MapView mapView;
    private GoogleMap googleMap;
    private View rootView;
    private StopsService service;
    private Call<SurroundingVeh> call;

    View customMarkerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, null);

        service = ApiUtils.getStopsService();
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * Load mapVioew and setup base location + camera position
         */
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                double lattitude = 49.200047;
                double longitude = 16.607923;

//                double lattitude = 49.191579;
//                double longitude = 16.612641;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lattitude, longitude)).zoom(15).build();
                googleMap.moveCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                startRefreshVehiclefHandler();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        startRefreshVehiclefHandler();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d("MapFragment", "onDestroy");
        super.onDestroy();
        mapView.onDestroy();
        if (call != null) {
            call.cancel();
        }
        if (refreshVehicleHandler != null){
            refreshVehicleHandler.removeCallbacks(refreshVehicleRunnable);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (call != null) {
            call.cancel();
        }
        if (refreshVehicleHandler != null){
            refreshVehicleHandler.removeCallbacks(refreshVehicleRunnable);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Load all surrounding vehicles from API and show them as markers on map
     */
    public void loadSurroundingVehicles(){
        call = service.getVehicles(Constants.stopId);
        call.enqueue(new Callback<SurroundingVeh>() {
            @Override
            public void onResponse(Call<SurroundingVeh> call, Response<SurroundingVeh> response) {
                if(response.body() != null) {
                    if (response.body().getData() != null) {
                        Log.d("SURROUNDING", "" + response.body().getLastUpdate());
                        googleMap.clear();
                        List<Datum> data = response.body().getData();
                        Log.d("Lines", "" + data.size());
                        for (int i = 0; i < data.size(); i++) {
                            Datum it = data.get(i);
                            double latitude = it.getLat();
                            double longitude = it.getLng();
                            Integer rotation = it.getBearing();
                            String lineName;
                            if (it.getLineName() == null) {
                                lineName = it.getLineID().toString();
                            } else {
                                lineName = it.getLineName();
                            }


                            //customMarkerView = (getLayoutInflater().inflate(R.layout.arrow0, null));

                            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(lineName + " " + rotation);


                            //marker.rotation(rotation);
                            if (getMarkerBitmapFromView(customMarkerView, lineName, rotation) != null) {
                                marker.icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(customMarkerView, lineName, rotation)));
                                marker.anchor(0.5f, 0.5f);
                            }
                            /*IconGenerator iconFactory = new IconGenerator(getContext());
                            iconFactory.setTextAppearance(R.style.myStyleText);
                            iconFactory.setBackground(getActivity().getDrawable(R.drawable.vehicle_arrow));
                            iconFactory.setContentRotation(0);
                            marker.icon(BitmapDescriptorFactory.fromResource(R.layout.line));*/
                            //marker.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(it.getLineID().toString())));

                            googleMap.addMarker(marker);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SurroundingVeh> call, Throwable t) {

            }
        });
    }

    /**
     * @param view is custom marker layout which we will convert into bitmap.
     * @param lineName is the line name which you want to show in marker.
     * @return
     */
    private Bitmap getMarkerBitmapFromView(View view, String lineName, int rotation) {

        TextView textTv;
        ImageView imageView;

        try {
            switch (rotation) {
                case 0:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow0, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText0));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView0);
                    break;

                case 45:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow45, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText45));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView45);
                    break;

                case 90:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow90, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText90));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView90);
                    break;

                case 135:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow135, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText135));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView135);
                    break;

                case 180:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow180, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText180));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView180);
                    break;

                case 225:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow225, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText225));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView225);
                    break;

                case 270:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow270, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText270));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView270);
                    break;

                case 315:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow315, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText315));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView315);
                    break;

                default:
                    customMarkerView = (getLayoutInflater().inflate(R.layout.arrow0, null));
                    textTv = ((TextView) customMarkerView.findViewById(R.id.arrowText0));
                    imageView = customMarkerView.findViewById(R.id.arrowImageView0);
                    break;
            }
            textTv.setText(lineName);

            if (lineName.contentEquals("1")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineOne));
            } else if (lineName.contentEquals("2")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineTwo));
            } else if (lineName.contentEquals("3")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineThree));
            } else if (lineName.contentEquals("4")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineFour));
            } else if (lineName.contentEquals("5")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineFive));
            } else if (lineName.contentEquals("6")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineSix));
            } else if (lineName.contentEquals("7")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineSeven));
            } else if (lineName.contentEquals("8")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineEight));
            } else if (lineName.contentEquals("9")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineNine));
            } else if (lineName.contentEquals("10")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineTen));
            } else if (lineName.contentEquals("11")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineEleven));
            } else if (lineName.contentEquals("12")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineTwelve));
            } else if (lineName.contentEquals("13")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineThirteen));
            } else if (lineName.contentEquals("14")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineFourteen));
            } else if (lineName.contains("N")) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineNight));
                textTv.setTextColor(getResources().getColor(R.color.white));
            } else if (isTroley(lineName)) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineTroley));
                textTv.setTextColor(getResources().getColor(R.color.yellow));
            } else if (isMhd(lineName)) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineMhd));
            } else if (isNight(lineName)) {
                imageView.setImageTintList(getResources().getColorStateList(R.color.lineNight));
                textTv.setTextColor(getResources().getColor(R.color.yellow));
            } else {
                imageView.setImageTintList(getResources().getColorStateList(R.color.DeepSkyBlue));
            }

            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        }catch(Exception e){
            return null;
        }
    }

    private boolean isTroley(String toCheck){
        for (int i = 24; i < 40; i++) {
            if (toCheck.contains("" + i)){
                return true;
            }
        }
        return false;
    }

    private boolean isMhd(String toCheck){
        for (int i = 40; i < 89; i++) {
            if (toCheck.contains("" + i) && !(toCheck.contains("E"))){
                return true;
            }
        }
        return false;
    }

    private boolean isNight(String toCheck){
        for (int i = 89; i < 100; i++) {
            if (toCheck.contains("" + i)){
                return true;
            }
        }
        return false;
    }

    public void startRefreshVehiclefHandler(){
        refreshVehicleHandler = new Handler();
        refreshVehicleRunnable = new Runnable() {
            @Override
            public void run() {
                loadSurroundingVehicles();
                refreshVehicleHandler.postDelayed(refreshVehicleRunnable,5000);
                Log.d("MapFragment","Refreshing vehicles!");
            }
        };

        refreshVehicleHandler.post(refreshVehicleRunnable);
    }
}
