package fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.maps.other.MapOptions;
import fr.univ_lille1.iut_info.caronic.mapsv3.other.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSearch extends Fragment {

    private static final String KEY_MAP_OPTIONS = "map_options";
    ;
    private MapView mMapView;
    private MapOptions mMapOptions;
    private MyLocationNewOverlay mLocationNewOverlay;

    public FragmentSearch() {
        // Required empty public constructor
    }

    public static FragmentSearch newInstance(MapOptions mMapOptions) {
        FragmentSearch fragment = new FragmentSearch();

        Bundle args = new Bundle();
        args.putSerializable(KEY_MAP_OPTIONS, mMapOptions);

        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_search, container, false);

        addmap(view, inflater);

        addCreateListener(view);

        return view;
    }

    private void addCreateListener(View view) {

        final EditText etTitre = (EditText) view.findViewById(R.id.Titre);
        final EditText etDescription = (EditText) view.findViewById(R.id.Description);
        final EditText etLatitude = (EditText) view.findViewById(R.id.latitude);
        final EditText etLongtitude = (EditText) view.findViewById(R.id.longitude);


        Button creatButton = (Button) view.findViewById(R.id.fragment_search_create);
        creatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titre = etTitre.getText().toString();
                String description = etDescription.getText().toString();
                String latitudeString = etLatitude.getText().toString();
                String longitudeString = etLongtitude.getText().toString();

                double lat;
                double lng;
                try {
                    lat = Double.parseDouble(latitudeString);
                    lng = Double.parseDouble(longitudeString);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "cannot convert", Toast.LENGTH_SHORT).show();
                    return;
                }

                GeoPoint point = new GeoPoint(lat, lng);

            }
        });

    }

    private void addmap(View view, LayoutInflater inflater) {
        RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.SearchView);
        Configuration.getInstance().setMapViewHardwareAccelerated(true);

        MapView mMapView = new MapView(inflater.getContext());

        mMapOptions = (MapOptions) getArguments().getSerializable(KEY_MAP_OPTIONS);
        Utils.goThroughOptions(getContext(), mMapView, mMapOptions);
        rl.addView(mMapView);
    }

}
