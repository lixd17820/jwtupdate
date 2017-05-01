package com.jwt.update;

import java.util.List;

import com.jwt.update.bean.OneLineSelectBean;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommOnelineImgListAdapter extends ArrayAdapter<OneLineSelectBean> {

	Activity context;

	public CommOnelineImgListAdapter(Activity _context,
			List<OneLineSelectBean> objects) {
		super(_context, R.layout.select_list_item, objects);
		this.context = _context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			row = inflater.inflate(R.layout.select_list_item, null);
		}
		OneLineSelectBean kv = getItem(position);
		if (kv != null) {
			ImageView img = (ImageView) row.findViewById(R.id.imageView1);
			TextView tv1 = (TextView) row.findViewById(R.id.TextView_left);
			tv1.setText(kv.getText1());

			if (kv.isSelect()) {
				img.setImageResource(android.R.drawable.btn_star_big_on);
			} else {
				img.setImageResource(android.R.drawable.btn_star_big_off);
			}
		}
		return row;

	}
}
