package fr.univ_lille1.iut_info.caronic.mapsv3.main.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity;
import fr.univ_lille1.iut_info.caronic.mapsv3.R;
import fr.univ_lille1.iut_info.caronic.mapsv3.main.adapter.PermissionsAdapter;
import fr.univ_lille1.iut_info.caronic.mapsv3.main.adapter.PermissionsRequestModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class PermissionsFragment extends Fragment {


    private final String LOG = PermissionsFragment.class.getSimpleName();

    private static final String KEY_PERMISSIONS_REQUESTED = "mapsv3.permissions_requested";
    public static Type permissionsIntListType = new TypeToken<ArrayList<Integer>>(){}.getType();

    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permissions, container, false);

        String permissionsRequestJson = getArguments().getString(KEY_PERMISSIONS_REQUESTED);
        ArrayList<Integer> permissionsIntList = new Gson().fromJson(permissionsRequestJson, permissionsIntListType);

        List<PermissionsRequestModel> permissionsList = new ArrayList<>();

        fillPermissionsList(permissionsIntList, permissionsList);


        PermissionsAdapter adapter = new PermissionsAdapter(getActivity(), permissionsList);
        ListView listView = (ListView) view.findViewById(R.id.fragment_permissions_list_view);
        listView.setAdapter(adapter);

        return view;
    }

    public static PermissionsFragment newInstance(List<Integer> permissionsToRequest) {
        PermissionsFragment permissionsFragment = new PermissionsFragment();

        Bundle args = new Bundle();

        String permissionsString = new Gson().toJson(permissionsToRequest, permissionsIntListType);
        args.putSerializable(KEY_PERMISSIONS_REQUESTED, permissionsString);

        permissionsFragment.setArguments(args);

        return permissionsFragment;
    }

    private void fillPermissionsList(List<Integer> permissionsIntList, List<PermissionsRequestModel> permissionsList) {
        for (Integer perm : permissionsIntList) {
            String title = null;
            int code = -1;
            String description = null;

            switch (perm) {
                case MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                    title = getString(R.string.permission_write_external_storage_title);
                    code = MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;
                    break;
                case MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                    title = getString(R.string.permission_access_fine_location_title);
                    code = MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
                    break;
                default:
                    break;
            }
            if (code != -1 && title != null) {
                PermissionsRequestModel model = new PermissionsRequestModel(code, title, description);
                permissionsList.add(model);
            }
        }
    }

}
