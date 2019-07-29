package au.net.winehound.ui.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.CloseableIterator;
import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.net.winehound.LogTags;
import au.net.winehound.R;
import au.net.winehound.domain.Winery;
import au.net.winehound.domain.WineryLightweight;
import au.net.winehound.domain.webservice.WineryPage;
import au.net.winehound.service.LocationHelper;
import au.net.winehound.service.WineHoundService;
import au.net.winehound.ui.WineryActivity;
import au.net.winehound.ui.views.WineryMapMarker;
import au.net.winehound.ui.views.WineryMapMarker_;

@EFragment
public class WineriesMapFragment extends Fragment {

    private Map<Integer, Marker> displayingWineires = new HashMap<Integer, Marker>();
    private final Map<Marker, Integer> wineriesByMarker = new HashMap<Marker, Integer>();
    private final Map<Integer, Bitmap> wineryLogos = new HashMap<Integer, Bitmap>();

    private View mapProgress;
    private MapView mapView;

//    private boolean isVisible;
    private LocationHelper locationHelper = new LocationHelper();
    private LatLngBounds lastBounds;

    @Bean
    protected WineHoundService service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_wineries_map, container, false);
        mapProgress =  view.findViewById(R.id.wineries_map_progress);
        mapView = (MapView)view.findViewById(R.id.wineries_map_view);

        // Why do we pass in a new bundle??? Because if we pass in the savedInstanceState, we get an
        // exception java.lang.RuntimeException: Parcelable encounteredClassNotFoundException reading a Serializable object (name = au.net.winehound.service.WineHoundService$SearchOrder)
        // Which comes out of the maps API. WTF!?
        mapView.onCreate(new Bundle());

        // Setup the map view
        final GoogleMap map = mapView.getMap();
        if(map != null){
            map.setMyLocationEnabled(true);
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLngBounds bounds  = map.getProjection().getVisibleRegion().latLngBounds;
                    lastBounds = bounds;

                    Log.i(LogTags.UI, "Camera changed to " + bounds);

                    // Show the wineries we have in the DB for this view
                    startCalculateMapResults(bounds, map, displayingWineires);

                    // Start downloading from the webservice
                    downloadMap(map, bounds, "");
                }
            });
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    WineryMapMarker markerView = WineryMapMarker_.build(getActivity());
                    // Fined the markers winery
                    Winery winery = service.getWinery(wineriesByMarker.get(marker));
                    markerView.setWinery(winery, locationHelper.getLastLocation());

                    return markerView;
                }
            });
            map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    // Fined the markers winery
                    Winery winery = service.getWinery(wineriesByMarker.get(marker));
                    getActivity().startActivity(WineryActivity.start(getActivity(), winery));

                }
            });
        }
        else{
            Log.e(LogTags.UI, "GoogleMap not present!");
        }


        return view;
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    protected void showPreCalculatedMapResults(GoogleMap map, List<WineryLightweight> toAdd, Map<Integer, Marker> toRemove, Map<Integer, Marker> newMarkers){
        if(!isVisible()){
            // Quick exit - we are no longer showing
            return;
        }

        MapsInitializer.initialize(getActivity());

        // Add all the new ones
        for(WineryLightweight winery : toAdd){
            BitmapDescriptor pinIcon = getBitmapDescriptor(winery.getTier(), wineryLogos.get(winery.getId()), getResources());

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(winery.getLatitude(), winery.getLongitude()))
                    .title(winery.getName())
                    .icon(pinIcon));
            newMarkers.put(winery.getId(), marker);
            wineriesByMarker.put(marker, winery.getId());
        }


        // Now remove all the ones that have gone off the screen
        for (Marker removeMarker: toRemove.values()){
            removeMarker.remove();
            if(wineriesByMarker.containsKey(removeMarker)){
                int wineryID = wineriesByMarker.remove(removeMarker);
                wineryLogos.remove(wineryID);
            }
        }
        displayingWineires = newMarkers;
    }

    @UiThread
    protected void showMapProgress(){
        mapProgress.setVisibility(View.VISIBLE);
    }

    @UiThread
    protected void hideMapProgress(){
        mapProgress.setVisibility(View.GONE);
    }

    @Background(serial = "mapMarkers")
    protected void downloadMap(GoogleMap map, LatLngBounds bounds, String fromName){
        Log.i(LogTags.UI, "Fetching wineries from " + fromName + " for bounds " + bounds);

        // Unwind our recursion - if the bounds have changed, stop fetching from this lot
        if(bounds != lastBounds){
            return;
        }
        showMapProgress();

        // Do the WS call
        WineryPage page;
        try{
            page = service.downloadAndSaveWineries(bounds, fromName);
        }
        catch(Exception e){
            Log.e(LogTags.UI, "Error downloading wineries", e);

            // No network then we are done!  Outta here ...
            hideMapProgress();
            return;
        }

        // Stop our recursion - if we got nothing, we are past the last page
        if(page.getWineries().isEmpty() || page.getWineries().size() == 1){
            hideMapProgress();
            return;
        }

        // show results
        calculateMapResults(bounds, map, new HashMap<Integer, Marker>(displayingWineires));

        // Recurse - get the next lot of wineries
        Winery lastWinery = page.getWineries().get(page.getWineries().size() - 1);
        if(isVisible()){
            downloadMap(map, bounds, lastWinery.getName());
        }
    }


    @Background
    protected void startCalculateMapResults(LatLngBounds bounds, GoogleMap map, Map<Integer, Marker> currentMarkers){
        calculateMapResults(bounds, map, currentMarkers);
    }

    private void calculateMapResults(LatLngBounds bounds, GoogleMap map, Map<Integer, Marker> currentMarkers){
        Log.i(LogTags.UI, "Starting to calculate view results for " + bounds);

        Map<Integer, Marker> toRemove = currentMarkers;
        List<WineryLightweight> toAdd = new ArrayList<WineryLightweight>();
        Map<Integer, Marker> newMarkers = new HashMap<Integer, Marker>();


        CloseableIterator<WineryLightweight> it = service.getWineryPlaceMarkers(bounds);
        while(it.hasNext()){
            // If the bounds have changed, stop fetching from this lot.  We can stop now, there is
            // no need to do extra work
            if(bounds != lastBounds){
                return;
            }

            WineryLightweight winery = it.next();

            if(currentMarkers.containsKey(winery.getId())){
                // if its already showing, leave it
                newMarkers.put(winery.getId(), currentMarkers.get(winery.getId()));
                toRemove.remove(winery.getId());

            }
            else{
                // If they are gold plus level, we have to download their logo so we can show it in
                if(winery.getTier() == Winery.Tier.GoldPlus && winery.getLogoUrl() != null && !winery.getLogoUrl().isEmpty()){
                    try {
                        Bitmap bm = Picasso.with(getActivity()).load(winery.getLogoUrl()).get();
                        wineryLogos.put(winery.getId(), bm);
                    } catch (IOException e) {
                        Log.e(LogTags.UI, "Error downloading winery logo from " + winery.getLogoUrl());
                    }
                }

                toAdd.add(winery);
            }
        }

        Log.i(LogTags.UI, "Calculated there are " + toAdd.size() + " markers to add and " + toRemove.size() + " markers to remove");
        showPreCalculatedMapResults(map, toAdd, toRemove, newMarkers);
    }

    /**
     * Move the map to display the given co-ordinates
     *
     * @param latitude
     * @param longitude
     * @param zoomLevel
     */
    public void display(double latitude, double longitude, float zoomLevel){
        GoogleMap map = mapView.getMap();
        if(map != null){
            MapsInitializer.initialize(getActivity());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoomLevel));
        }
    }



    /**
     * Converts the map to a static image which can be clicked on
     *
     * @param listener
     */
    public void setStaticMap(final GoogleMap.OnMapClickListener listener){
        GoogleMap map = mapView.getMap();
        if(map != null) {

            UiSettings settings = map.getUiSettings();
            settings.setAllGesturesEnabled(false);
            settings.setCompassEnabled(false);
            settings.setZoomControlsEnabled(false);
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    listener.onMapClick(marker.getPosition());
                    return true;
                }
            });

            map.setMyLocationEnabled(false);
            map.setOnMapClickListener(listener);
        }
    }


    @Override
    public void onCreate(Bundle instanceState){
        super.onCreate(new Bundle());
        locationHelper.onCreate(getActivity());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
        locationHelper.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        mapView.onResume();
//        isVisible = true;
    }

    @Override
    public void onPause(){
        super.onPause();
        mapView.onPause();
//        isVisible = false;
    }

    private static BitmapDescriptor getBitmapDescriptor(Winery.Tier tier, Bitmap wineryLogo, Resources resources) {
        // Figure out what icon to use
        BitmapDescriptor pinIcon;

        if (tier == Winery.Tier.Bronze) {
            pinIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_bronze);
        } else if (tier == Winery.Tier.Silver) {
            pinIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_silver);
        } else if (tier == Winery.Tier.Gold) {
            pinIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_gold_default);
        } else if (tier == Winery.Tier.GoldPlus && wineryLogo != null) {

            // Load up our images
            Bitmap iconBm = BitmapFactory.decodeResource(resources, R.drawable.map_pin_gold_logo).copy(Bitmap.Config.ARGB_8888, true);
            float paddingWidth = resources.getDimension(R.dimen.gold_logo_pin_border);

            // Resize the winery logo to fit centered inside a square logoWidth in size
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, wineryLogo.getWidth(), wineryLogo.getHeight()),
                    new RectF(paddingWidth, paddingWidth, iconBm.getWidth() - paddingWidth, iconBm.getWidth() - paddingWidth), Matrix.ScaleToFit.CENTER);

            Bitmap scaledLogoBm = Bitmap.createBitmap(iconBm.getWidth(), iconBm.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledLogoBm);
            canvas.drawBitmap(wineryLogo, m, null);

            // Paint the winery logo onto the placemark icon
            canvas = new Canvas(iconBm);
            Paint p = new Paint();
            BitmapShader bmShader = new BitmapShader(scaledLogoBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            p.setShader(bmShader);
            canvas.drawRoundRect(new RectF(paddingWidth, paddingWidth, iconBm.getWidth() - paddingWidth, iconBm.getWidth() - paddingWidth),
                    iconBm.getWidth() / 2, iconBm.getWidth() / 2, p);

            pinIcon = BitmapDescriptorFactory.fromBitmap(iconBm);
        } else {
            pinIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_pin_basic);
        }
        return pinIcon;
    }

}
