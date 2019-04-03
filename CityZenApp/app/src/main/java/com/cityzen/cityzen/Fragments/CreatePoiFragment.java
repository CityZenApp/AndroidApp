package com.cityzen.cityzen.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Fragments.PoiFeature.FeatureSelectedCallback;
import com.cityzen.cityzen.Fragments.PoiFeature.PoiFeatureDialogFragment;
import com.cityzen.cityzen.Fragments.Timer.TimeCallback;
import com.cityzen.cityzen.Fragments.Timer.TimePickerFragment;
import com.cityzen.cityzen.Models.OsmFeature;
import com.cityzen.cityzen.Network.ChangesetCallback;
import com.cityzen.cityzen.Network.CreateChangesetTask;
import com.cityzen.cityzen.Network.CreatePoiTask;
import com.cityzen.cityzen.Network.OsmOperationCallback;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.Development.AppToast;
import com.cityzen.cityzen.Utils.MapUtils.MapUtils;
import com.cityzen.cityzen.Utils.MapUtils.OsmTags;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import de.westnordost.osmapi.OsmConnection;

public class CreatePoiFragment extends Fragment implements TimeCallback, FeatureSelectedCallback {

    private OsmConnection osm;
    private String openChangesetId;
    private OsmFeature poiFeature = null;

    private enum TimeFor {
        OPENING,
        CLOSING
    }

    private String[] dropdownItems;


    private double latitude;
    private double longitude;
    private MapView map;
    private Toolbar toolbar;
    private Switch hoursSwitch24_7;
    private LinearLayout extendedOpeningHoursContainer;
    private TextView openingHours;
    private TextView closingHours;
    //week checkboxes
    private CheckBox checkboxMonday;
    private CheckBox checkboxTuesday;
    private CheckBox checkboxWednesday;
    private CheckBox checkboxThursday;
    private CheckBox checkboxFriday;
    private CheckBox checkboxSaturday;
    private CheckBox checkboxSunday;
    private CheckBox checkboxAll;

    private TimeFor timeFor = null;

    private TextView createPoiFeature;
    private EditText createName;
    private EditText createStreet;
    private EditText createHouseNumber;
    private EditText createPostCode;
    private EditText createCity;
    private EditText createPhone;
    private EditText createWebsite;
    private EditText createEmail;
    private LinearLayout createInternetContainer;
    private Spinner createInternet;

    public CreatePoiFragment() {
        // Required empty public constructor
    }

    public static CreatePoiFragment newInstance(double latitude, double longitude) {
        CreatePoiFragment fragment = new CreatePoiFragment();
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("latitude");
            longitude = getArguments().getDouble("longitude");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_poi, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get OSM credentials data
        osm = ((MainActivity) getActivity()).getOsm();

        mapSetup();
        setupView();
        openingHoursLayoutSetup();
    }

    @Override
    public void onTimeReceived(int hourOfDay, int minutes) {
        String h = String.valueOf(hourOfDay);
        String m = String.valueOf(minutes);
        if (h.length() == 1)
            h = "0" + h;
        if (m.length() == 1)
            m = "0" + m;

        if (timeFor != null)
            if (timeFor == TimeFor.OPENING)
                openingHours.setText(h + ":" + m);
            else
                closingHours.setText(h + ":" + m);
    }

    @Override
    public void onFeatureSelected(OsmFeature osmFeature) {
        poiFeature = osmFeature;
        createPoiFeature.setText(osmFeature.getKey() + " " + osmFeature.getValue());
        if (!canHaveInternet(osmFeature))
            createInternetContainer.setVisibility(View.GONE);//hide the internet if poi does not have
        else
            createInternetContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // deactivate map
        if (map != null)
            map.onDetach();
    }

    private void mapSetup() {
        map = (MapView) getActivity().findViewById(R.id.createPoiMap);

        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);

        // add multi-touch capability
        map.setMultiTouchControls(true);

        // add compass to map
        CompassOverlay compassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // get map controller
        IMapController controller = map.getController();

        GeoPoint position = new GeoPoint(latitude, longitude);
        controller.setCenter(position);
        controller.setZoom(18);
        MapUtils.addMarker(getActivity(), map, latitude, longitude);
    }

    private void setupView() {
        createPoiFeature = (TextView) getActivity().findViewById(R.id.createPoiFeature);
        createPoiFeature.setOnClickListener(clickListener);
        createName = (EditText) getActivity().findViewById(R.id.createName);
        createStreet = (EditText) getActivity().findViewById(R.id.createStreet);
        createHouseNumber = (EditText) getActivity().findViewById(R.id.createHouseNumber);
        createPostCode = (EditText) getActivity().findViewById(R.id.createPostCode);
        createCity = (EditText) getActivity().findViewById(R.id.createCity);
        createPhone = (EditText) getActivity().findViewById(R.id.createPhone);
        createWebsite = (EditText) getActivity().findViewById(R.id.createWebsite);
        createEmail = (EditText) getActivity().findViewById(R.id.createEmail);
        createInternetContainer = (LinearLayout) getActivity().findViewById(R.id.createInternetContainer);
        createInternet = (Spinner) getActivity().findViewById(R.id.createInternet);
        dropdownItems = new String[]{"", getString(R.string.yes), getString(R.string.no), getString(R.string.wifi), getString(R.string.wired), getString(R.string.wlan)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        createInternet.setAdapter(adapter);

        TextView osmCopyright = (TextView) getActivity().findViewById(R.id.osmCopyrightCreatePoi);
        osmCopyright.setText(Html.fromHtml(getString(R.string.osm_copyright)));
        toolbar = (Toolbar) getActivity().findViewById(R.id.createPoiToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        toolbar.inflateMenu(R.menu.update_poi);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!allFieldsValid()) return true;
                createPoi();
                return true;
            }
        });
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.createPoiFeature:
                    featureSelectorDialog();
                    break;
                default:
                    break;
            }
        }
    };

    private void openingHoursLayoutSetup() {
        hoursSwitch24_7 = (Switch) getActivity().findViewById(R.id.hoursSwitch24_7);
        hoursSwitch24_7.setChecked(false);
        extendedOpeningHoursContainer = (LinearLayout) getActivity().findViewById(R.id.extendedOpeningHoursContainer);
        extendedOpeningHoursContainer.setVisibility(View.VISIBLE);
        openingHours = (TextView) getActivity().findViewById(R.id.openingHours);
        closingHours = (TextView) getActivity().findViewById(R.id.closingHours);

        //week CheckBoxes
        checkboxMonday = (CheckBox) getActivity().findViewById(R.id.checkboxMonday);
        checkboxTuesday = (CheckBox) getActivity().findViewById(R.id.checkboxTuesday);
        checkboxWednesday = (CheckBox) getActivity().findViewById(R.id.checkboxWednesday);
        checkboxThursday = (CheckBox) getActivity().findViewById(R.id.checkboxThursday);
        checkboxFriday = (CheckBox) getActivity().findViewById(R.id.checkboxFriday);
        checkboxSaturday = (CheckBox) getActivity().findViewById(R.id.checkboxSaturday);
        checkboxSunday = (CheckBox) getActivity().findViewById(R.id.checkboxSunday);
        checkboxAll = (CheckBox) getActivity().findViewById(R.id.checkboxAll);
        checkboxMonday.setOnClickListener(openingHoursViewListener);
        checkboxTuesday.setOnClickListener(openingHoursViewListener);
        checkboxWednesday.setOnClickListener(openingHoursViewListener);
        checkboxThursday.setOnClickListener(openingHoursViewListener);
        checkboxFriday.setOnClickListener(openingHoursViewListener);
        checkboxSaturday.setOnClickListener(openingHoursViewListener);
        checkboxSunday.setOnClickListener(openingHoursViewListener);
        checkboxAll.setOnClickListener(openingHoursViewListener);

        hoursSwitch24_7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //do stuff when Switch is ON
                    extendedOpeningHoursContainer.setVisibility(View.GONE);
                } else {
                    //do stuff when Switch if OFF
                    extendedOpeningHoursContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        openingHours.setOnClickListener(openingHoursViewListener);
        closingHours.setOnClickListener(openingHoursViewListener);

    }

    View.OnClickListener openingHoursViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.openingHours:
                    timeFor = TimeFor.OPENING;
                    showTimePickerDialog();
                    break;
                case R.id.closingHours:
                    timeFor = TimeFor.CLOSING;
                    showTimePickerDialog();
                    break;

                //week checkboxes
                case R.id.checkboxMonday:
                    if (checkboxMonday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxTuesday:
                    if (checkboxTuesday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxWednesday:
                    if (checkboxWednesday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxThursday:
                    if (checkboxThursday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxFriday:
                    if (checkboxFriday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxSaturday:
                    if (checkboxSaturday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxSunday:
                    if (checkboxSunday.isChecked())
                        if (allDaysAreSelected())
                            checkboxAll.setChecked(true);
                        else
                            checkboxAll.setChecked(false);
                    else
                        checkboxAll.setChecked(false);
                    break;
                case R.id.checkboxAll:
                    if (checkboxAll.isChecked())
                        setWeekCheckboxes();
                    else
                        clearWeekCheckboxes();
                    break;
                default:
                    break;
            }
        }
    };

    private void clearWeekCheckboxes() {
        checkboxMonday.setChecked(false);
        checkboxTuesday.setChecked(false);
        checkboxWednesday.setChecked(false);
        checkboxThursday.setChecked(false);
        checkboxFriday.setChecked(false);
        checkboxSaturday.setChecked(false);
        checkboxSunday.setChecked(false);
        checkboxAll.setChecked(false);
    }

    private void setWeekCheckboxes() {
        checkboxMonday.setChecked(true);
        checkboxTuesday.setChecked(true);
        checkboxWednesday.setChecked(true);
        checkboxThursday.setChecked(true);
        checkboxFriday.setChecked(true);
        checkboxSaturday.setChecked(true);
        checkboxSunday.setChecked(true);
        checkboxAll.setChecked(true);
    }

    private boolean allDaysAreSelected() {
        if (checkboxMonday.isChecked() &&
                checkboxTuesday.isChecked() &&
                checkboxWednesday.isChecked() &&
                checkboxThursday.isChecked() &&
                checkboxFriday.isChecked() &&
                checkboxSaturday.isChecked() &&
                checkboxSunday.isChecked())
            return true;
        return false;
    }

    public void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    public void featureSelectorDialog() {
        DialogFragment dialogFragment = PoiFeatureDialogFragment.newInstance();
        dialogFragment.setTargetFragment(this, 1);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "PoiFeatureDialogFragment");
    }


    private void closeFragment() {
        ((MainActivity) getActivity()).showNavigationNoFab();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(CreatePoiFragment.this)
                .commit();

        //Close keyboard
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * To check wherever the new POI can have internet or not
     * E.g. Bus Station can't have internet, but a cafe can
     * more features need to be added later
     */
    private boolean canHaveInternet(OsmFeature feature) {
        return feature.getValue().equals("bar") ||
                feature.getValue().equals("cafe") ||
                feature.getValue().equals("pub") ||
                feature.getValue().equals("hotel") ||
                feature.getValue().equals("hostel") ||
                feature.getValue().equals("guest_house") ||
                feature.getValue().equals("mobile_phone") ||
                feature.getValue().equals("restaurant") ||
                feature.getValue().equals("fast_food") ||
                feature.getValue().equals("bus_station") ||
                feature.getValue().equals("transportation")//for airports, train station, etc
                ;
    }

    /**************************************Functions to extract the opening hours string from the form*************************************************/
    /**
     * Return the string of the POI opening hours
     *
     * @return null :  if no days selected
     * 24/7 : if hoursSwitch24_7 is checked
     * R.string.add_opening_hours -> No opening hours selected
     * R.string.add_closing_hours -> No closing hours selected
     * Returns a valid opening_hours formant for OSM
     */
    private String filterOpeningHours() {
        String opening_hours = null;
        if (hoursSwitch24_7.isChecked()) return "24/7";
        String days = "";
        if (!areAnyDaysSelected()) return null;
        //filter which days of the week
        if (getLastSelectedDay().equals(getFirstSelectedDay())) {
            //only one day selected
            days = getFirstSelectedDay();
        } else {
            days = getFirstSelectedDay() + "-" + getLastSelectedDay();
        }
        if (openingHours.getText().equals("--:--")) {
            return getString(R.string.add_opening_hours);
        }
        if (closingHours.getText().equals("--:--")) {
            return getString(R.string.add_closing_hours);
        }
        //get opening_hours
        opening_hours = days + " " + openingHours.getText() + ":" + closingHours.getText();
        return opening_hours;
    }

    private String getFirstSelectedDay() {
        String firstDay = "";
        if (checkboxMonday.isChecked())
            firstDay += "Mo";
        else if (checkboxTuesday.isChecked())
            firstDay += "Tu";
        else if (checkboxWednesday.isChecked())
            firstDay += "We";
        else if (checkboxThursday.isChecked())
            firstDay += "Th";
        else if (checkboxFriday.isChecked())
            firstDay += "Fr";
        else if (checkboxSaturday.isChecked())
            firstDay += "Sa";
        else if (checkboxSunday.isChecked())
            firstDay += "Su";
        return firstDay;
    }

    private String getLastSelectedDay() {
        String lastDay = "";
        if (checkboxSunday.isChecked())
            lastDay += "Su";
        else if (checkboxSaturday.isChecked())
            lastDay += "Sa";
        else if (checkboxFriday.isChecked())
            lastDay += "Fr";
        else if (checkboxThursday.isChecked())
            lastDay += "Th";
        else if (checkboxWednesday.isChecked())
            lastDay += "We";
        else if (checkboxTuesday.isChecked())
            lastDay += "Tu";
        else if (checkboxMonday.isChecked())
            lastDay += "Mo";
        return lastDay;
    }

    private boolean allFieldsValid() {
        //needs better regex validation
        if (poiFeature == null || createPoiFeature.getText().equals(getString(R.string.select_amenity_type))) {
            new AppToast(getActivity()).centerViewToast(getString(R.string.select_amenity_type_to_continue));
            return false;
        }
        if (createWebsite.getText().length() > 0 && !createWebsite.getText().toString().contains(".")) {
            createWebsite.requestFocus();
            createWebsite.setError(getString(R.string.not_valid_website));
            return false;
        }
        if (createEmail.getText().length() > 0 && !createEmail.getText().toString().contains("@")) {
            createEmail.requestFocus();
            createEmail.setError(getString(R.string.not_valid_email));
            return false;
        }
        return true;
    }

    private boolean areAnyDaysSelected() {
        if (checkboxMonday.isChecked() || checkboxTuesday.isChecked() || checkboxWednesday.isChecked() || checkboxThursday.isChecked() || checkboxFriday.isChecked() || checkboxSaturday.isChecked() || checkboxSunday.isChecked())
            return true;
        return false;
    }

    /**
     * Creating POi
     */

    /********************************************Create Changeset ************************************************************/

    private void createPoi() {
        if (openChangesetId != null)
            publishNode(openChangesetId);//a changeset is currently opened
        else
            new CreateChangesetTask(getActivity(), ((MainActivity) getActivity()).getOsm(), createChangesetEditTags(), new ChangesetCallback() {
                @Override
                public void onChangesetCreated(String changesetId) {
                    openChangesetId = changesetId;
                    publishNode(openChangesetId);
                }

                @Override
                public void onFailure(String errorMessage) {
                    if (errorMessage == null)
                        new AppToast(getActivity()).centerViewToast((getString(R.string.no_internet_to_update_poi)));
                    else if (errorMessage.equals("Couldn't authenticate you")) {
                        new AppToast(getActivity()).centerViewToast((getString(R.string.session_expired_toast)));
                        ((MainActivity) getActivity()).recheckAuthentication();
                    }
                }
            }).execute();
    }

    //changeset tags
    private Map<String, String> createChangesetEditTags() {
        Map<String, String> tags = new HashMap<>();
        tags.put("created_by", osm.getUserAgent());
        tags.put("comment", "Creating new " + createPoiFeature.getText());
        return tags;
    }


    /*************************************Submit changes to OSM**************************************************************/
    private void publishNode(String changesetId) {
        new CreatePoiTask(getActivity(), osm, changesetId, createSubmitPoiTags(), latitude, longitude, new OsmOperationCallback() {
            @Override
            public void osmOperationSuccessful(String response) {
                //successful created
                new AppToast(getActivity()).centerViewToast(poiFeature.getValue() + " " + getString(R.string.created_succesfully));
                closeFragment();
            }

            @Override
            public void onFailure(String errorMessage) {
                openChangesetId = null;//clear changeset
                new AppToast(getActivity()).centerViewToast(getString(R.string.an_error_occurred));
            }
        }).execute();
    }

    //node tags to submit to OSM
    private Map<String, String> createSubmitPoiTags() {
        if (poiFeature == null) return null;
        Map<String, String> tags = new HashMap<>();

        tags.put(poiFeature.getKey(), poiFeature.getValue());
        if (createName.getText().toString().length() > 0)//update name
            tags.put("name", createName.getText().toString());
        if (createHouseNumber.getText().toString().length() > 0)
            tags.put("addr:housenumber", createHouseNumber.getText().toString());
        if (createPostCode.getText().toString().length() > 0)
            tags.put("addr:postcode", createPostCode.getText().toString());
        if (createStreet.getText().toString().length() > 0)
            tags.put("addr:street", createStreet.getText().toString());
        if (createCity.getText().toString().length() > 0)
            tags.put("addr:city", createCity.getText().toString());
        if (createPhone.getText().toString().length() > 0)
            tags.put("phone", createPhone.getText().toString());
        if (createWebsite.getText().toString().length() > 0)
            tags.put("website", createWebsite.getText().toString());
        if (createEmail.getText().toString().length() > 0)
            tags.put("email", createEmail.getText().toString());
        if (createInternetContainer.getVisibility() == View.VISIBLE) {
            if (createInternet.getSelectedItemPosition() != 0)
                tags.put("internet_access", dropdownItems[createInternet.getSelectedItemPosition()].toLowerCase());
        }
        String openingHours = filterOpeningHours();
        if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
            tags.put(OsmTags.OPENING_HOURS, openingHours);
        return tags;
    }
}
