package com.cityzen.cityzen.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.Fragments.Timer.TimeCallback;
import com.cityzen.cityzen.Fragments.Timer.TimePickerFragment;
import com.cityzen.cityzen.Models.ParcelablePOI;
import com.cityzen.cityzen.Network.ChangesetCallback;
import com.cityzen.cityzen.Network.CreateChangesetTask;
import com.cityzen.cityzen.Network.OsmOperationCallback;
import com.cityzen.cityzen.Network.UpdatePoiTask;
import com.cityzen.cityzen.R;
import com.cityzen.cityzen.Utils.Development.AppToast;
import com.cityzen.cityzen.Utils.DeviceUtils.DeviceUtils;
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
import de.westnordost.osmapi.map.MapDataDao;
import de.westnordost.osmapi.map.data.Node;


public class EditPoiFragment extends Fragment implements TimeCallback {
    private OsmConnection osm;
    private String[] dropdownItems;
    private LinearLayout editInternetContainer;
    private LinearLayout editExistingOpeningHoursContainer;
    private TextView editExistingOpeningHours;
    private Button editExistingOpeningHoursButton;
    private View openingHoursMainContainer;
    private Node editNode;
    private String openChangesetId = null;

    private enum TimeFor {
        OPENING,
        CLOSING
    }

    private MapView map;
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

    private EditText editName;
    private TextView editStreet;
    private TextView editCity;
    private EditText editPostcode;
    private EditText editHouseNumber;
    private EditText editPhone;
    private EditText editWebsite;
    private EditText editEmail;
    private Spinner editInternet;

    private EditPoiFragment.TimeFor timeFor = null;

    private ParcelablePOI POI;
    private Toolbar toolbar;

    //strings used to extract data from POI tags
    private String name = null;
    private String housenumber = null;
    private String postcode = null;
    private String street = null;
    private String city = null;
    private String phone = null;
    private String website = null;
    private String email = null;
    private String internet_access = null;//yes|no
    private String opening_hours = null;

    public EditPoiFragment() {
        // Required empty public constructor
    }

    public static EditPoiFragment newInstance(ParcelablePOI poi) {
        EditPoiFragment fragment = new EditPoiFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_poi, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get OSM credentials data
        osm = ((MainActivity) getActivity()).getOsm();

        showLoadingScreen();
        mapSetup();
        openingHoursLayoutSetup();
        setupView();
        loadNodeData();
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

        //check if the app goes back to MapFragment
        //if MapFragment is open close any routing
        MapFragment MapFragment = (MapFragment) getActivity().getSupportFragmentManager().findFragmentByTag("MapFragment");
        if (MapFragment != null) {
            ((MainActivity) getActivity()).showNavigation();
        }
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
            if (timeFor == EditPoiFragment.TimeFor.OPENING)
                openingHours.setText(h + ":" + m);
            else
                closingHours.setText(h + ":" + m);
    }

    //load node data, searched items sometimes don't come with tags
    private void loadNodeData() {
        if (DeviceUtils.isInternetConnected(getActivity()))
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    MapDataDao mapDao = new MapDataDao(osm);
                    editNode = mapDao.getNode(POI.getId());
//                    if (editNode != null)
//                        if (editNode.getTags() != null)
//                            for (Map.Entry<String, String> tag : editNode.getTags().entrySet()) {
//                                AppLog.log(tag.getKey() + " " + tag.getValue());
//                            }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    hideLoadingScreen();

                    if (editNode != null) //load data to UI
                        loadNodeInfoToUI(editNode);
                    else {
                        //not a node
                        new AppToast(getActivity()).centerViewToast(getString(R.string.editing_is_not_possible) + " \n" + POI.getPoiName());
                        closeFragment();
                    }
                }
            }.execute();
        else {
            hideLoadingScreen();
            new AppToast(getActivity()).centerViewToast(getString(R.string.offline));
            closeFragment();
        }
    }

    private void loadNodeInfoToUI(Node node) {
        if (node.getTags() != null)
            for (Map.Entry<String, String> tag : node.getTags().entrySet()) {
                if (tag.getKey().equals(OsmTags.OPENING_HOURS)) {
                    opening_hours = tag.getValue();
                    editExistingOpeningHours.setText(opening_hours);
                    openingHoursMainContainer.setVisibility(View.GONE);
                    editExistingOpeningHoursContainer.setVisibility(View.VISIBLE);
                }
                if (tag.getKey().equals("name")) {
                    name = tag.getValue();
                    editName.setText(name);
                }
                if (tag.getKey().equals("addr:housenumber")) {
                    housenumber = tag.getValue();
                    editHouseNumber.setText(housenumber);
                }
                if (tag.getKey().equals("addr:postcode")) {
                    postcode = tag.getValue();
                    editPostcode.setText(postcode);
                }
                if (tag.getKey().equals("addr:street")) {
                    street = tag.getValue();
                    editStreet.setText(street);
                }
                if (tag.getKey().equals("addr:city")) {
                    city = tag.getValue();
                    editCity.setText(city);
                }
                if (tag.getKey().equals("phone")) {
                    phone = tag.getValue();
                    editPhone.setText(phone);
                }
                if (tag.getKey().equals("website")) {
                    website = tag.getValue();
                    editWebsite.setText(website);
                }
                if (tag.getKey().equals("email")) {
                    email = tag.getValue();
                    editEmail.setText(email);
                }
                if (tag.getKey().equals("internet_access")) {
                    internet_access = tag.getValue();
                    if (tag.getValue().equalsIgnoreCase("yes"))
                        editInternet.setSelection(1, true);
                    else if (tag.getValue().equalsIgnoreCase("no"))
                        editInternet.setSelection(2, true);
                    else if (tag.getValue().equalsIgnoreCase("wifi"))
                        editInternet.setSelection(3, true);
                    else if (tag.getValue().equalsIgnoreCase("wired"))
                        editInternet.setSelection(4, true);
                    else if (tag.getValue().equalsIgnoreCase("wlan"))
                        editInternet.setSelection(5, true);
                }
            }
        if (opening_hours == null)
            openingHoursMainContainer.setVisibility(View.VISIBLE);
        else
            openingHoursMainContainer.setVisibility(View.GONE);

    }

    private void setupView() {
        editExistingOpeningHoursContainer = getActivity().findViewById(R.id.editExistingOpeningHoursContainer);
        editExistingOpeningHoursContainer.setVisibility(View.GONE);
        editExistingOpeningHours = getActivity().findViewById(R.id.editExistingOpeningHours);
        editExistingOpeningHoursButton = getActivity().findViewById(R.id.editExistingOpeningHoursButton);
        editExistingOpeningHoursButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editExistingOpeningHoursContainer.setVisibility(View.GONE);
                openingHoursMainContainer.setVisibility(View.VISIBLE);
            }
        });
        editName = getActivity().findViewById(R.id.editName);
        editStreet = (EditText) getActivity().findViewById(R.id.editStreet);
        editCity = (EditText) getActivity().findViewById(R.id.editCity);
        editHouseNumber = getActivity().findViewById(R.id.editHouseNumber);
        editPostcode = getActivity().findViewById(R.id.editPostcode);
        editPhone = getActivity().findViewById(R.id.editPhone);
        editWebsite = getActivity().findViewById(R.id.editWebsite);
        editEmail = getActivity().findViewById(R.id.editEmail);
        editInternetContainer = getActivity().findViewById(R.id.editInternetContainer);
        if (!canHaveInternet()) editInternetContainer.setVisibility(View.GONE);

        editInternet = getActivity().findViewById(R.id.editInternet);
        dropdownItems = new String[]{"", getString(R.string.yes), getString(R.string.no), getString(R.string.wifi), getString(R.string.wired), getString(R.string.wlan)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        editInternet.setAdapter(adapter);

        TextView osmCopyright = getActivity().findViewById(R.id.osmCopyrightEditPoi);
        osmCopyright.setText(Html.fromHtml(getString(R.string.osm_copyright)));
        toolbar = getActivity().findViewById(R.id.editPoiToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        toolbar.inflateMenu(R.menu.update_poi);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!allFieldsValid()) {
                    return true;
                }
                if (!hasChanged()) {
                    closeFragment();
                    return true;
                }
                editPoi();
                return true;
            }
        });
    }

    private void mapSetup() {
        map = getActivity().findViewById(R.id.createPoiMap);
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()));
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setTilesScaledToDpi(true);
        map.setMultiTouchControls(true);

        CompassOverlay compassOverlay = new CompassOverlay(getActivity(), new InternalCompassOrientationProvider(getActivity()), map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        IMapController controller = map.getController();
        GeoPoint position = new GeoPoint(POI.getLatitude(), POI.getLongitude());
        controller.setCenter(position);
        controller.setZoom(18);

        MapUtils.addMarker(getActivity(), map, POI.getLatitude(), POI.getLongitude());
    }

    private void openingHoursLayoutSetup() {
        openingHoursMainContainer = getActivity().findViewById(R.id.openingHoursMainContainer);
        hoursSwitch24_7 = getActivity().findViewById(R.id.hoursSwitch24_7);
        hoursSwitch24_7.setChecked(false);
        extendedOpeningHoursContainer = getActivity().findViewById(R.id.extendedOpeningHoursContainer);
        extendedOpeningHoursContainer.setVisibility(View.VISIBLE);
        openingHours = getActivity().findViewById(R.id.openingHours);
        closingHours = getActivity().findViewById(R.id.closingHours);

        //week CheckBoxes
        checkboxMonday = getActivity().findViewById(R.id.checkboxMonday);
        checkboxTuesday = getActivity().findViewById(R.id.checkboxTuesday);
        checkboxWednesday = getActivity().findViewById(R.id.checkboxWednesday);
        checkboxThursday = getActivity().findViewById(R.id.checkboxThursday);
        checkboxFriday = getActivity().findViewById(R.id.checkboxFriday);
        checkboxSaturday = getActivity().findViewById(R.id.checkboxSaturday);
        checkboxSunday = getActivity().findViewById(R.id.checkboxSunday);
        checkboxAll = getActivity().findViewById(R.id.checkboxAll);
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
                    timeFor = EditPoiFragment.TimeFor.OPENING;
                    showTimePickerDialog();
                    break;
                case R.id.closingHours:
                    timeFor = EditPoiFragment.TimeFor.CLOSING;
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
        return checkboxMonday.isChecked() &&
                checkboxTuesday.isChecked() &&
                checkboxWednesday.isChecked() &&
                checkboxThursday.isChecked() &&
                checkboxFriday.isChecked() &&
                checkboxSaturday.isChecked() &&
                checkboxSunday.isChecked();
    }

    public void showTimePickerDialog() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.setTargetFragment(this, 0);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }


    /**
     * Editing OSM
     */
    /********************************************Create Changeset ************************************************************/
    private void editPoi() {
        if (openChangesetId != null)
            submitEdits(openChangesetId);//a changeset is currently opened
        else
            new CreateChangesetTask(getActivity(), ((MainActivity) getActivity()).getOsm(), createChangesetEditTags(), new ChangesetCallback() {
                @Override
                public void onChangesetCreated(String changesetId) {
                    openChangesetId = changesetId;
                    submitEdits(openChangesetId);
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
        tags.put("comment", buildChangesetComment());
        return tags;
    }

    //check for data changes
    private boolean hasChanged() {
        if (name != null && !name.equalsIgnoreCase(editName.getText().toString()))
            return true;
        if (postcode != null && !postcode.equalsIgnoreCase(editPostcode.getText().toString()))
            return true;
        if (housenumber != null && !housenumber.equalsIgnoreCase(editHouseNumber.getText().toString()))
            return true;
        if (street != null && !street.equalsIgnoreCase(editStreet.getText().toString()))
            return true;
        if (city != null && !city.equalsIgnoreCase(editCity.getText().toString()))
            return true;
        if (phone != null && !phone.equalsIgnoreCase(editPhone.getText().toString()))
            return true;
        if (website != null && !website.equalsIgnoreCase(editWebsite.getText().toString()))
            return true;
        if (email != null && !email.equalsIgnoreCase(editEmail.getText().toString()))
            return true;
        if (opening_hours != null && openingHoursMainContainer.getVisibility() == View.VISIBLE) {
            String openingHours = filterOpeningHours();
            if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
                return true;
        }

        //if no tags found, they are just being added
        if (housenumber == null && editHouseNumber.getText().length() > 0)
            return true;
        if (postcode == null && editPostcode.getText().length() > 0)
            return true;
        if (street == null && editStreet.getText().length() > 0)
            return true;
        if (city == null && editCity.getText().length() > 0)
            return true;
        if (phone == null && editPhone.getText().length() > 0)
            return true;
        if (website == null && editWebsite.getText().length() > 0)
            return true;
        if (email == null && editEmail.getText().length() > 0)
            return true;
        if (opening_hours == null) {
            String openingHours = filterOpeningHours();
            if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
                return true;
        }

        return false;
    }

    //changeset comment builder function
    private String buildChangesetComment() {
        String comment = "Updating";
        if (name != null && !name.equalsIgnoreCase(editName.getText().toString()))
            comment += " name,";
        if (postcode != null && !postcode.equalsIgnoreCase(editPostcode.getText().toString()))
            comment += " postcode,";
        if (housenumber != null && !housenumber.equalsIgnoreCase(editHouseNumber.getText().toString()))
            comment += " housenumber,";
        if (street != null && !street.equalsIgnoreCase(editStreet.getText().toString()))
            comment += " street address,";
        if (city != null && !city.equalsIgnoreCase(editCity.getText().toString()))
            comment += " City,";
        if (phone != null && !phone.equalsIgnoreCase(editPhone.getText().toString()))
            comment += " phone number,";
        if (website != null && !website.equalsIgnoreCase(editWebsite.getText().toString()))
            comment += " website,";
        if (email != null && !email.equalsIgnoreCase(editEmail.getText().toString()))
            comment += " email,";
        if (opening_hours != null && openingHoursMainContainer.getVisibility() == View.VISIBLE) {
            String openingHours = filterOpeningHours();
            if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
                comment += " opening hours,";
        }

        //if no tags found, they are just being added
        if (housenumber == null && editHouseNumber.getText().length() > 0)
            comment += " adding housenumber,";
        if (postcode == null && editPostcode.getText().length() > 0)
            comment += " adding postcode,";
        if (street == null && editStreet.getText().length() > 0)
            comment += " adding street address,";
        if (city == null && editCity.getText().length() > 0)
            comment += " adding city,";
        if (phone == null && editPhone.getText().length() > 0)
            comment += " adding phone,";
        if (website == null && editWebsite.getText().length() > 0)
            comment += " adding website,";
        if (email == null && editEmail.getText().length() > 0)
            comment += " adding email,";
        if (opening_hours == null) {
            String openingHours = filterOpeningHours();
            if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
                comment += " opening hours,";
        }
        return comment.substring(0, (comment.length() - 1));//remove last ","
    }

    /*************************************Submit changes to OSM**************************************************************/
    private void submitEdits(String changesetId) {
        if (editNode != null)
            new UpdatePoiTask(getActivity(), osm, POI, changesetId, createSubmitPoiTags(), new OsmOperationCallback() {
                @Override
                public void osmOperationSuccessful(String response) {
                    //response is the version number of the node incremented by one
                    if (Integer.parseInt(response) == (editNode.getVersion() + 1)) {
                        //successful update
                        new AppToast(getActivity()).centerViewToast(POI.getPoiName() != null ? POI.getPoiName() : "" + " " + getString(R.string.updated_successfully));
                        closeFragment();
                    } else {
                        new AppToast(getActivity()).centerViewToast(getString(R.string.an_error_occurred));
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    openChangesetId = null;//clear changeset
                    new AppToast(getActivity()).centerViewToast(getString(R.string.an_error_occurred));
                }
            }).execute();
        else {
            new AppToast(getActivity()).toast(getString(R.string.an_error_occurred));
            loadNodeData();//reload node data, might have failed due to network
        }
    }

    //node tags to update to OSM
    private Map<String, String> createSubmitPoiTags() {
        Map<String, String> tags;
        if (editNode != null)
            tags = new HashMap<>(editNode.getTags());
        else
            tags = new HashMap<>();

        if (name != null && !editName.getText().toString().equals(name))//update name
            tags.put("name", editName.getText().toString());
        else if (editName.getText().length() > 0)//adding new name
            tags.put("name", editName.getText().toString());

        if (housenumber != null && !editHouseNumber.getText().toString().equals(housenumber))
            tags.put("addr:housenumber", editHouseNumber.getText().toString());
        else if (editHouseNumber.getText().length() > 0)
            tags.put("addr:housenumber", editHouseNumber.getText().toString());

        if (postcode != null && !editPostcode.getText().toString().equals(postcode))
            tags.put("addr:postcode", editPostcode.getText().toString());
        else if (editPostcode.getText().length() > 0)
            tags.put("addr:postcode", editPostcode.getText().toString());

        if (street != null && !editStreet.getText().toString().equals(street))
            tags.put("addr:street", editStreet.getText().toString());
        else if (editStreet.getText().length() > 0)
            tags.put("addr:street", editStreet.getText().toString());

        if (city != null && !editCity.getText().toString().equals(city))
            tags.put("addr:city", editCity.getText().toString());
        else if (editCity.getText().length() > 0)
            tags.put("addr:city", editCity.getText().toString());

        if (phone != null && !editPhone.getText().toString().equals(phone))
            tags.put("phone", editPhone.getText().toString());
        else if (editPhone.getText().length() > 0)
            tags.put("phone", editPhone.getText().toString());

        if (website != null && !editWebsite.getText().toString().equals(website))
            tags.put("website", editWebsite.getText().toString());
        else if (editWebsite.getText().length() > 0)
            tags.put("website", editWebsite.getText().toString());

        if (email != null && !editEmail.getText().toString().equals(email))
            tags.put("email", editEmail.getText().toString());
        else if (editEmail.getText().length() > 0)
            tags.put("email", editEmail.getText().toString());

        if (canHaveInternet()) {
            if (internet_access != null && !dropdownItems[editInternet.getSelectedItemPosition()].equalsIgnoreCase(internet_access) && editInternet.getSelectedItemPosition() != 0)
                tags.put("internet_access", dropdownItems[editInternet.getSelectedItemPosition()].toLowerCase());
            else if (editInternet.getSelectedItemPosition() != 0)
                tags.put("internet_access", dropdownItems[editInternet.getSelectedItemPosition()].toLowerCase());
        }

        if (openingHoursMainContainer.getVisibility() == View.VISIBLE) {
            String openingHours = filterOpeningHours();
            if (openingHours != null && !openingHours.equalsIgnoreCase(getString(R.string.add_opening_hours)) && !openingHours.equalsIgnoreCase(getString(R.string.add_closing_hours)))
                tags.put(OsmTags.OPENING_HOURS, openingHours);
        }

        return tags;
    }


    /**
     * To check wherever the POI can have internet or not
     * E.g. Bus Station can't have internet, but a cafe can
     */
    private boolean canHaveInternet() {
        return POI.getPoiClassType().equals("bar") ||
                POI.getPoiClassType().equals("cafe") ||
                POI.getPoiClassType().equals("pub") ||
                POI.getPoiClassType().equals("hotel") ||
                POI.getPoiClassType().equals("hostel") ||
                POI.getPoiClassType().equals("guest_house") ||
                POI.getPoiClassType().equals("mobile_phone") ||
                POI.getPoiClassType().equals("restaurant") ||
                POI.getPoiClassType().equals("fast_food") ||
                POI.getPoiClassType().equals("bus_station") ||
                POI.getPoiClassType().equals("transportation")//for airports, train station, etc
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
        String opening_hours;
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

    private boolean areAnyDaysSelected() {
        if (checkboxMonday.isChecked() || checkboxTuesday.isChecked() || checkboxWednesday.isChecked() || checkboxThursday.isChecked() || checkboxFriday.isChecked() || checkboxSaturday.isChecked() || checkboxSunday.isChecked())
            return true;
        return false;
    }

    private boolean allFieldsValid() {
        //needs better regex validation
        if (editWebsite.getText().length() > 0 && !editWebsite.getText().toString().contains(".")) {
            editWebsite.requestFocus();
            editWebsite.setError(getString(R.string.not_valid_website));
            return false;
        }
        if (editEmail.getText().length() > 0 && !editEmail.getText().toString().contains("@")) {
            editEmail.requestFocus();
            editEmail.setError(getString(R.string.not_valid_email));
            return false;
        }
        return true;
    }

    private void closeFragment() {
        ((MainActivity) getActivity()).showNavigationNoFab();
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .remove(EditPoiFragment.this)
                .commit();

        //Close keyboard
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void hideLoadingScreen() {
        FrameLayout loaderContainer = getActivity().findViewById(R.id.loadingScreenContainer);
        loaderContainer.setVisibility(View.GONE);
    }

    private void showLoadingScreen() {
        FrameLayout loaderContainer = getActivity().findViewById(R.id.loadingScreenContainer);
        loaderContainer.setVisibility(View.VISIBLE);
    }
}
