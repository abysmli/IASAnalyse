package net.icedeer.abysmli.iasanalyse.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.view.ComponentsContainer;

import java.util.List;

/**
 * Created by Li, Yuan on 18.06.15.
 * All Right reserved!
 */
class ComponentsViewAdapter extends ArrayAdapter<ComponentsContainer> {

    public ComponentsViewAdapter(Context context, List<ComponentsContainer> items) {
        super(context, R.layout.components_view, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.components_view, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ComponentsContainer item = getItem(position);
        viewHolder.ivIcon.setImageDrawable(item.icon);
        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvDescription.setText(item.description);

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvDescription;
    }
}