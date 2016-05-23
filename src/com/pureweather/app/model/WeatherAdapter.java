package com.pureweather.app.model;

import java.util.List;

import com.pureweather.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherAdapter extends ArrayAdapter<Weather> {

	private int resourceID;
	
	public WeatherAdapter(Context context, int resource,List<Weather> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		resourceID = resource;
	}




	@Override 
	public View getView(int position, View convertView, ViewGroup parent){
		View view ;
		Weather info = getItem(position);
		ViewHolder VH;
		if (convertView == null){
			
			view = LayoutInflater.from(getContext()).inflate(resourceID, null);
			VH = new ViewHolder();
			VH.condImage = (ImageView) view.findViewById(R.id.main_city_listview_image);
			VH.cityName = (TextView) view.findViewById(R.id.main_city_listview_city_name);
			VH.condText = (TextView) view.findViewById(R.id.main_city_listview_cond_text);
			VH.tempRange = (TextView) view.findViewById(R.id.main_city_listview_temp_range);
			view.setTag(VH);
		}
		else{
			view = convertView;
			VH = (ViewHolder) view.getTag();
	
		}
		//动态设置图片的方法！！
        try {
            Integer value = cls.getDeclaredField("p"+info.getImageCode()).getInt(null);
            VH.condImage.setImageResource(value);
                        //Log.v("value",value.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            VH.condImage.setImageResource(R.drawable.p999);
        }

		VH.cityName.setText(info.getCityName());
		VH.condText.setText(info.getCondText());
		VH.tempRange.setText(info.getTempRange());
		return view;
		
		
	}
	class ViewHolder{
		ImageView condImage;
		TextView cityName;
		TextView condText;
		TextView tempRange;
	} 	
	Class<com.pureweather.app.R.drawable> cls = R.drawable.class;
	
	
}
