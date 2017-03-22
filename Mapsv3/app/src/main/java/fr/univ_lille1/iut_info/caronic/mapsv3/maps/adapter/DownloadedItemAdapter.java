package fr.univ_lille1.iut_info.caronic.mapsv3.maps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.univ_lille1.iut_info.caronic.mapsv3.R;


public class DownloadedItemAdapter extends ArrayAdapter<DownloadedItemModel> {

    private List<DownloadedItemModel> dataSet;
    Context mContext;

    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
    }


    public DownloadedItemAdapter(List<DownloadedItemModel> data, Context context) {
        super(context, R.layout.download_items, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DownloadedItemModel dataModel = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.download_items, parent, false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.downloaded_item_icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.downloaded_item_title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.downloaded_item_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        String size = getContext().getResources().getString(R.string.offline_areas_downloaded_area_size, dataModel.getSize());

        viewHolder.title.setText(dataModel.getTitle());
        viewHolder.description.setText(size);

        // Return the completed view to render on screen
        return convertView;
    }


}
