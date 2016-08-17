package capstone.com.cybertracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.Patrol;

/**
 * Created by Arjel on 7/16/2016.
 */

public class PatrolListAdapter extends ArrayAdapter<Patrol> {

    private final Context context;
    private final List<Patrol> values;

    public PatrolListAdapter(Context context, List<Patrol> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_patrol, parent, false);
        TextView txtPatrolName = (TextView) rowView.findViewById(R.id.txt_patrol_name);
        txtPatrolName.setText(values.get(position).getName());

        TextView txtPatrolStatus = (TextView) rowView.findViewById(R.id.txt_patrol_status);
        txtPatrolStatus.setText(values.get(position).getStatus().getLabel());

        TextView txtPatrollerName = (TextView) rowView.findViewById(R.id.txt_patroller_name);
        txtPatrollerName.setText("Patroller Name: " + values.get(position).getPatrollerName());

        return rowView;
    }
}



