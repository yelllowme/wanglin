package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.model.UserFriendModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/23.
 * @ 好友界面， @ 小区好友
 */

public class ListViewAdapter_AtUser_AtHouseEstate extends BaseAdapter implements SectionIndexer {

    private List<UserFriendModel> list_friend;

    private Context context;

    private int resource;

    public ListViewAdapter_AtUser_AtHouseEstate(List<UserFriendModel> list_friend, Context context, int resource){
        this.list_friend = list_friend;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_friend.size();
    }

    @Override
    public Object getItem(int i) {
        return list_friend.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        TextView textView_sortLetter = (TextView)view.findViewById(R.id.textview_listviewItem_AtHouseEstate_sortLetter);
        ImageView imageView_header = (ImageView)view.findViewById(R.id.imageview_listviewItem_AtHouseEstate_header);
        TextView textView_name = (TextView)view.findViewById(R.id.textview_listviewItem_AtHouseEstate_name);

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            textView_sortLetter.setVisibility(View.VISIBLE);
            textView_sortLetter.setText(list_friend.get(position).getSortLetters());
        }else{
            textView_sortLetter.setVisibility(View.INVISIBLE);
        }

        textView_name.setText(list_friend.get(position).getName());
        return view;
    }


    //根据ListView的当前位置获取分类的首字母的Char ascii值
    @Override
    public int getSectionForPosition(int i) {
        return list_friend.get(i).getSortLetters().charAt(0);
    }

    //根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
    @Override
    public int getPositionForSection(int section){
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list_friend.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }


     //提取英文的首字母，非英文字母用#代替。
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }
}
