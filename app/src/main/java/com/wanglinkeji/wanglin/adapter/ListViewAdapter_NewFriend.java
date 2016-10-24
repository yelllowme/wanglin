package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.model.NewFriendInfoModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.HttpUtil;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;
import com.wanglinkeji.wanglin.util.WanglinHttpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/30.
 * 新的朋友ListViewAdapter
 */

public class ListViewAdapter_NewFriend extends BaseAdapter {

    private List<NewFriendInfoModel> list_newFriend;

    private Context context;

    private int resource;

    public ListViewAdapter_NewFriend(List<NewFriendInfoModel> list_newFriend, Context context, int resource){
        this.list_newFriend = list_newFriend;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_newFriend.size();
    }

    @Override
    public Object getItem(int i) {
        return list_newFriend.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int posiiton, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(context).inflate(resource, null);
        CircleImageView imageView_header = (CircleImageView)view.findViewById(R.id.imageview_newFriendItem_header);
        TextView textView_friendName = (TextView)view.findViewById(R.id.textview_newFriendItem_friendName);
        TextView textView_friendMessage = (TextView)view.findViewById(R.id.textview_newFriendItem_friendMessage);
        TextView textView_accept = (TextView)view.findViewById(R.id.textview_newFriendItem_acceptFriend);
        TextView textView_refuse = (TextView)view.findViewById(R.id.textview_newFriendItem_refuse);
        LinearLayout layout_details = (LinearLayout)view.findViewById(R.id.layout_listViewItem_newFriendDetails);

        ViewGroup.LayoutParams params = layout_details.getLayoutParams();
        params.height = OtherUtil.dip2px(context, 75);
        params.width = WangLinApplication.screen_Width;

        textView_friendName.setText(list_newFriend.get(posiiton).getNewFriendName());
        textView_friendMessage.setText(list_newFriend.get(posiiton).getNewFriendMessage());
        textView_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accecptNewFriend(context, list_newFriend.get(posiiton).getInfoId());
                list_newFriend.remove(posiiton);
                ListViewAdapter_NewFriend.this.notifyDataSetChanged();
            }
        });
        textView_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refuseNewFriend(context, list_newFriend.get(posiiton).getInfoId());
                list_newFriend.remove(posiiton);
                ListViewAdapter_NewFriend.this.notifyDataSetChanged();
            }
        });
        return view;
    }

    private void accecptNewFriend(final Context context, int msgId){
        Map<String, String> params = new HashMap<>();
        params.put("MsgId", String.valueOf(msgId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.acceptNewFriend_url, params, DBUtil.getLoginUser().getToken(), "yellow_acceptNewFriend",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        boolean isSuccess = false;
                        try {
                            isSuccess = jsonObject_response.getBoolean("Data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isSuccess == true){
                            Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectingError() {

                    }

                    @Override
                    public void onDisconnectError() {

                    }
                });
    }

    private void refuseNewFriend(final Context context, int msgId){
        Map<String, String> params = new HashMap<>();
        params.put("MsgId", String.valueOf(msgId));
        HttpUtil.sendVolleyStringRequest_Post(context, HttpUtil.refuseNewFriend_url, params, DBUtil.getLoginUser().getToken(), "yellow_refuseNewFriend",
                new WanglinHttpResponseListener() {
                    @Override
                    public void onSuccessResponse(JSONObject jsonObject_response) {
                        boolean isSuccess = false;
                        try {
                            isSuccess = jsonObject_response.getBoolean("Data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (isSuccess == true){
                            Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onConnectingError() {

                    }

                    @Override
                    public void onDisconnectError() {

                    }
                });
    }
}
