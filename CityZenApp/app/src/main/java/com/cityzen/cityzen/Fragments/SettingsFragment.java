package com.cityzen.cityzen.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cityzen.cityzen.Activities.MainActivity;
import com.cityzen.cityzen.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    public interface getUserInfo {
        void onResponse(String displayName);

        void onFailure();
    }

    Button authenticateToOSM;
    TextView loginUsername;
    private String userName = null;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginUsername = (TextView) getActivity().findViewById(R.id.loginUsername);
        authenticateToOSM = (Button) getActivity().findViewById(R.id.authenticateToOSM);
        authenticateToOSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName == null)
                    ((MainActivity) getActivity()).loginToOSM();
                else
                    ((MainActivity) getActivity()).logoutFromOSM();
            }
        });
        loadUsername();
    }


    public void loadUsername() {
        ((MainActivity) getActivity()).logUserInfo(new getUserInfo() {
            @Override
            public void onResponse(final String displayName) {
                userName = displayName;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginUsername.setText(loginUsername.getText() + " " + displayName);
                        loginUsername.setVisibility(View.VISIBLE);
                        authenticateToOSM.setVisibility(View.VISIBLE);
                        authenticateToOSM.setText(getText(R.string.logout));

                    }
                });
            }

            @Override
            public void onFailure() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginUsername.setVisibility(View.GONE);
                            authenticateToOSM.setVisibility(View.VISIBLE);
                            authenticateToOSM.setText(getText(R.string.login_to_osm));
                        }
                    });
                } catch (Exception ignored) {
                }
            }
        });
    }
}
