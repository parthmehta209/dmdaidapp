package com.example.reports;
import com.example.dmdaid.R;
import com.example.dmdaid.R.id;
import com.example.dmdaid.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ReportListAdapter extends ArrayAdapter<String> {
  private final Context context;
  private final String[] values;

  public ReportListAdapter(Context context, String[] values) {
    super(context, R.layout.rowlayout, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
    TextView textView1 = (TextView) rowView.findViewById(R.id.textView1);
    TextView textView2 = (TextView) rowView.findViewById(R.id.textView2);
    textView1.setText(values[position]);
    textView2.setText("05/01/2014");
    
    return rowView;
  }
} 