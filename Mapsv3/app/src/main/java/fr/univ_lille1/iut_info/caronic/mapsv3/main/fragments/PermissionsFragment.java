package fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity;
import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.adapter.PermissionsAdapter;
import fr.univ_lille1.iut_info.caronic.mapsv3.adapter.PermissionsRequestModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends Fragment {


    private final String LOG = PermissionsFragment.class.getSimpleName();

    private static final String KEY_PERMISSIONS_REQUESTED = "mapsv3.permissions_requested";
    public static Type permissionsListType = new TypeToken<ArrayList<Integer>>(){}.getType();

    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permissions, container, false);

        ListView listView = (ListView) view.findViewById(R.id.fragment_permissions_list_view);

        String permissionsRequestJson = getArguments().getString(KEY_PERMISSIONS_REQUESTED);
        ArrayList<Integer> permisisonsIntList = new Gson().fromJson(permissionsRequestJson, permissionsListType);
        List<PermissionsRequestModel> permissionsList = new ArrayList<>();

        for (Integer perm : permisisonsIntList) {
            PermissionsRequestModel model;
            switch (perm) {
                case MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    model = new PermissionsRequestModel("Request write external storage", MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    permissionsList.add(model);
                    break;
                case MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    model = new PermissionsRequestModel("Request access fine location", MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    permissionsList.add(model);
                    break;
                default:
                    break;
            }
        }

        PermissionsAdapter adapter = new PermissionsAdapter(getActivity(), permissionsList);

        listView.setAdapter(adapter);

        return view;

    }

    public static PermissionsFragment newInstance(List<Integer> permissionsToRequest) {
        PermissionsFragment permissionsFragment = new PermissionsFragment();
        Bundle args = new Bundle();
        String permissionsString = new Gson().toJson(permissionsToRequest, permissionsListType);
        args.putSerializable(KEY_PERMISSIONS_REQUESTED, permissionsString);
        permissionsFragment.setArguments(args);
        return permissionsFragment;
    }

}
