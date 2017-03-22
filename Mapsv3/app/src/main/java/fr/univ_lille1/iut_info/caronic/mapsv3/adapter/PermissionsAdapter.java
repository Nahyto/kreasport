package fr.univ_lille1.iut_info.caronic.mapsv3.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.MainActivity;
import fr.univ_lille1.iut_info.caronic.mapsv3.R;

/**
 * Created by Chris on 22-Mar-17.
 */

public class PermissionsAdapter extends ArrayAdapter<PermissionsRequestModel> {


    private final Activity activity;
    private final List<PermissionsRequestModel> permissionsList;

    private class ViewHolder {
        private TextView tvTitle;
        private Button btApprove;
        private TextView tvDesciprtion;

        public ViewHolder(View convertView) {
            tvTitle = (TextView) convertView.findViewById(R.id.permission_request_title);
            btApprove = (Button) convertView.findViewById(R.id.permission_request_button);
            tvDesciprtion = (TextView) convertView.findViewById(R.id.permission_request_description);
        }
    }

    public PermissionsAdapter(Activity activity, List<PermissionsRequestModel> permissionsList) {
        super(activity, 0, permissionsList);
        this.activity = activity;
        this.permissionsList = permissionsList;
    }

    @Override
    /**
     * Use ViewHolder pattern for better performance
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder = null;
        PermissionsRequestModel model = permissionsList.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(activity);
            convertView = inflater.inflate(R.layout.permission_request_layout, parent, false);

            mViewHolder = new ViewHolder(convertView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String title =model.getTitle();
        mViewHolder.tvTitle.setText(title);

        View.OnClickListener clickListener = getClickListenerForPermissionRequest(model, mViewHolder);
        if (clickListener != null) {
            mViewHolder.btApprove.setOnClickListener(clickListener);
        }

        String description = model.getDescription();
        if (description != null) {
            mViewHolder.tvDesciprtion.setText(description);
        }



        return convertView;
    }

    private View.OnClickListener getClickListenerForPermissionRequest(PermissionsRequestModel requestModel, ViewHolder mViewHolder) {
        switch (requestModel.getPERMISSION_REQUEST_CODE()) {
            case MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.askSpecificPermission(activity, MainActivity.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                };
            case MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                return new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.askSpecificPermission(activity, MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    }
                };
            default:
                return null;
        }
    }
}
