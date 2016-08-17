package capstone.com.cybertracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import capstone.com.cybertracker.R;
import capstone.com.cybertracker.models.Observation;

/**
 * Created by Arjel on 7/20/2016.
 */

public class ObservationListAdapter extends ArrayAdapter<Observation> {

    private final Context context;
    private final List<Observation> values;

    public ObservationListAdapter(Context context, List<Observation> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_observation, parent, false);
        TextView txtObservationType = (TextView) rowView.findViewById(R.id.txt_observation_type);
        txtObservationType.setText(values.get(position).getObservationType().getLabel());
        return rowView;
    }

}
