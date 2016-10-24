package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.HousingEstateModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/9.
 * 附近小区列表Adapter
 */
public class ListViewAdapter_NeighborHouseEstate extends BaseAdapter {

    private List<HousingEstateModel> list_houseEstate;

    private Context context;

    private int resource;

    public ListViewAdapter_NeighborHouseEstate(List<HousingEstateModel> list_houseEstate, Context context, int resource){
        this.list_houseEstate = list_houseEstate;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_houseEstate.size();
    }

    @Override
    public Object getItem(int i) {
        return list_houseEstate.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        TextView textView_houseEstateName = (TextView)view.findViewById(R.id.textview_listviewItem_neightorHouseEstate);
        textView_houseEstateName.setText(list_houseEstate.get(position).getName());
        return view;
    }
}
