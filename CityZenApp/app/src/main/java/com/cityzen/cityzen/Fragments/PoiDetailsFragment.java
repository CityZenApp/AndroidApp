package com.cityzen.cityzen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.MapUtils.MapUtils;
import com.cityzen.cityzen.Utils.PoiHelper;
import com.cityzen.cityzen.Utils.StorageUtil;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.Locale;
import java.util.Map;

import androidx.fragment.app.DialogFragment;


public class PoiDetailsFragment extends DialogFragment {

    private TextView poiTitleDialog;
    private TextView poiDialogAddress;
    private ImageButton favoriteImageButton;
    private StorageUtil storageUtil;
    private MapView map;
    private ParcelablePOI POI;
    private LinearLayout poiDialogContent;
    private Button poiDialogDirectionsButton;
    private ImageButton poiDialogEdit;
    private ImageButton poiDialogShare;

    public PoiDetailsFragment() {
        // Required empty public constructor
    }

    public static PoiDetailsFragment newInstance(ParcelablePOI poi) {
        PoiDetailsFragment fragment = new PoiDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("POI", poi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            POI = getArguments().getParcelable("POI");

        }
        storageUtil = new StorageUtil(getActivity());
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poi_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewSetup();
        loadDataToUI();
        setupMapPreview(POI.getLatitude(), POI.getLongitude(), POI.getPoiName());
    }

    @Override
    public void onResume() {
        super.onResume();

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(getDialog().getContext(), PreferenceManager.getDefaultSharedPreferences(getDialog().getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // deactivate map
        if (map != null)
            map.onDetach();
    }


    private void viewSetup() {
        TextView osmCopyright = getDialog().findViewById(R.id.osmCopyright);
        osmCopyright.setText(Html.fromHtml(getString(R.string.osm_copyright)));
        poiDialogDirectionsButton = getDialog().findViewById(R.id.poiDialogDirectionsButton);
        favoriteImageButton = getDialog().findViewById(R.id.poiDialogFavorite);
        poiDialogEdit = getDialog().findViewById(R.id.poiDialogEdit);
        poiDialogShare = getDialog().findViewById(R.id.poiDialogShare);
        poiTitleDialog = getDialog().findViewById(R.id.poiTitleDialog);
        poiDialogAddress = getDialog().findViewById(R.id.poiDialogAddress);
        poiDialogContent = getDialog().findViewById(R.id.poiDialogContent);
        ImageButton poiDialogClose = getDialog().findViewById(R.id.poiDialogClose);
        poiDialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (storageUtil.isFavorite(POI))
                    storageUtil.deleteFromFavorites(POI);
                else
                    storageUtil.saveToFavoritesPOI(POI);
                updateFavoriteButton();
            }
        });
        poiDialogDirectionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (POI != null)
                    ((MainActivity) getActivity()).showDirectionsToPoi(POI);
                //Close this fragment
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .remove(PoiDetailsFragment.this)
                        .commit();
            }
        });
        poiDialogEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (POI != null) {
                    ((MainActivity) getActivity()).editPoi(POI);
                    //Close this fragment
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .remove(PoiDetailsFragment.this)
                            .commit();
                }
            }
        });
        poiDialogShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (POI != null) {
                    String geoUri = String.format(
                            Locale.ENGLISH,
                            "geo:%f,%f?z=18",
                            POI.getLatitude(),
                            POI.getLongitude()
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    startActivity(intent);
                }
            }
        });
    }

    private void loadDataToUI() {
        poiTitleDialog.setText(POI.getPoiName());
        poiDialogAddress.setText(PoiHelper.createAddressDisplayString(POI));
        //display tags to view
        if (POI.getTags() != null)
            for (Map.Entry<String, String> tag : POI.getTags().entrySet()) {
                if (!tag.getKey().contains("addr:")
                        && !tag.getKey().contains("name")
                        && !tag.getKey().contains("amenity")
                        && !tag.getKey().contains("shop")
                        && !tag.getKey().contains("historic")
                        && !tag.getKey().contains("tourism")
                        && !tag.getKey().contains("building")
                        && !tag.getKey().contains("wikipedia")
                        && !tag.getKey().contains("wikidata")
                    //add more constraints if necessary to remove form detailed preview
                        )
                    poiDialogContent.addView(
                            inflateRowItem(
                                    tag.getKey().replaceAll("_", " ").substring(0, 1).toUpperCase() + tag.getKey().replaceAll("_", " ").substring(1).toLowerCase(),
                                    tag.getValue()
                            ));
            }
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        //refresh favorites fragment if it is visible
        ((MainActivity) getActivity()).refreshAppNavigation();
        //favorite button setup
        if (storageUtil.isFavorite(POI))
            favoriteImageButton.setImageResource(R.drawable.ic_favorite_poi_true);
        else
            favoriteImageButton.setImageResource(R.drawable.ic_favorite_poi_false);
    }

    private View inflateRowItem(String title, String value) {
        View view;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.detailed_poi_tagitem, null);

        //LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.rowContainer);
        TextView titleTextView = (TextView) view.findViewById(R.id.rowTitle);
        TextView valueTextView = (TextView) view.findViewById(R.id.rowValue);
        titleTextView.setText(title);
        valueTextView.setText(value);
        //Linking content
        if (title.toLowerCase().equals("email") || title.toLowerCase().equals("contact:email")) {
            Linkify.addLinks(valueTextView, Linkify.EMAIL_ADDRESSES);
            valueTextView.setLinksClickable(true);
        }
        if (title.toLowerCase().equals("website") || title.toLowerCase().equals("contact:website")) {
            Linkify.addLinks(valueTextView, Linkify.WEB_URLS);
            valueTextView.setLinksClickable(true);
        }
        if (title.toLowerCase().equals("phone") || title.toLowerCase().equals("phone:mobile") || title.toLowerCase().equals("contact:mobile") || title.toLowerCase().equals("contact:phone")) {
            Linkify.addLinks(valueTextView, Linkify.PHONE_NUMBERS);
            valueTextView.setLinksClickable(true);
        }
        return view;
    }

    private void setupMapPreview(double lat, double lon, String markerTitle) {
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getDialog().getContext(), PreferenceManager.getDefaultSharedPreferences(getDialog().getContext()));

        map = (MapView) getDialog().findViewById(R.id.poiDialogMap);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);

        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getDialog().getContext(), new InternalCompassOrientationProvider(getDialog().getContext()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // get map controller
        IMapController controller = map.getController();
        GeoPoint position = new GeoPoint(lat, lon);
        controller.setCenter(position);
        controller.setZoom(18);

        MapUtils.addMarker(getActivity(), map, lat, lon, markerTitle);
    }
}
