package fr.univ_lille1.iut_info.caronic.mapsv3.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity;
import fr.univ_lille1.iut_info.caronic.mapsv3.R;

/**
 * Created by Chris on 22-Mar-17.
 */

public class PermissionsAdapter extends BaseAdapter implements ListAdapter {


    private final Activity activity;
    private final List<PermissionsRequestModel> permissionsList;

    public PermissionsAdapter(Activity activity, List<PermissionsRequestModel> permissionsList) {
        super();
        this.activity = activity;
        this.permissionsList = permissionsList;
    }

    @Override
    public int getCount() {
        return permissionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return permissionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(activity);

        View row = inflater.inflate(R.layout.permission_request_layout, parent, false);

        TextView tv = (TextView) row.findViewById(R.id.permission_request_title);
        tv.setText(permissionsList.get(position).getTitle());

        Button bt = (Button) row.findViewById(R.id.permission_request_button);
        bt.setText("Request the permission");
        switch (permissionsList.get(position).getPERMISSION_REQUEST_CODE()) {
            case MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.askSpecificPermission(activity, MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
                break;
            case MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.askSpecificPermission(activity, MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                });
                break;
            default:
                break;
        }


        return row;
    }
}
