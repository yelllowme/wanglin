package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.AddHouseEstate_HouseProCerActivity;
import com.wanglinkeji.wanglin.activity.MyHouseEstateActivity;
import com.wanglinkeji.wanglin.model.UserHouseModel;
import com.wanglinkeji.wanglin.model.UserRoomModel;

import java.util.List;

/**
 * Created by Administrator on 2016/9/19.
 * 我的小区列表Adapter
 */
public class ListViewAdapter_MyHouseEstate_houseList extends BaseAdapter {

    private List<UserHouseModel> list_house;

    private Context context;

    private int resource;

    private Handler handler;

    public ListViewAdapter_MyHouseEstate_houseList(List<UserHouseModel> list_house, Context context, int resource, Handler handler){
        this.list_house = list_house;
        this.context = context;
        this.resource = resource;
        this.handler = handler;
    }

    @Override
    public int getCount() {
        return list_house.size();
    }

    @Override
    public Object getItem(int i) {
        return list_house.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final viewHolder holder;
        if (view == null){
            view = LayoutInflater.from(context).inflate(resource, null);
            holder = new viewHolder();
            holder.layout_title = (RelativeLayout)view.findViewById(R.id.layout_myHouseEstateItem_title);
            holder.textView_houseName = (TextView)view.findViewById(R.id.textview_myHouseEstateItem_houseName);
            holder.textView_userIdentity = (TextView)view.findViewById(R.id.textview_myHouseEstateItem_userIdentity);
            holder.imageView_state_default = (ImageView)view.findViewById(R.id.imageview_myHouseEstateItem_state_default);
            holder.imageView_state_checking = (ImageView)view.findViewById(R.id.imageview_myHouseEstateItem_state_checking);
            holder.imageView_state_notPass = (ImageView)view.findViewById(R.id.imageview_myHouseEstateItem_state_notPass);
            holder.imageView_state_notUpload = (ImageView)view.findViewById(R.id.imageview_myHouseEstateItem_state_notUpload);
            holder.textView_houseSite = (TextView)view.findViewById(R.id.textview_myHouseEstateItem_houseSite);
            holder.layout_dialog = (LinearLayout)view.findViewById(R.id.layout_myHouseEstateItem_menuDialog);
            holder.textView_deleteHouse = (TextView)view.findViewById(R.id.textview_myHouseEstateItem_deleteHouse);
            holder.textView_setDefault = (TextView)view.findViewById(R.id.textview_myHouseEstateItem_setDefaultHouse);
            holder.imageView_menuBtn = (ImageView)view.findViewById(R.id.imageview_myHouseEstateItem_menuIcon);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        /**
         * 设置控件初始值
         */
        //右下角对话框
        if (list_house.get(position).isShowDialog() == true){
            holder.layout_dialog.setVisibility(View.VISIBLE);
        }else {
            holder.layout_dialog.setVisibility(View.INVISIBLE);
        }
        holder.textView_houseName.setText(list_house.get(position).getHouseEstateName());
        //设置住房所在位置
        String houseSite = "";
        if (list_house.get(position).getRidgepoleNum() > 0){
            houseSite += list_house.get(position).getRidgepoleNum() + "栋";
        }
        if (list_house.get(position).getUnitNum() > 0){
            houseSite += list_house.get(position).getUnitNum() + "单元";
        }
        houseSite = houseSite + list_house.get(position).getFloorNum() + "楼" + list_house.get(position).getRoomNum() + "号";
        holder.textView_houseSite.setText(houseSite);
        /**
         * 根据用户上传的房屋资料证明和审核状态来设置控件
         */
        //住房审核状态以及设置背景(“业主”和“租户”在审核通过之后背景为蓝色，其他都为灰色)
        //一共有三种状态：
        //审核中：只要绑定了小区，不管是否上传房产证照或者租赁合同
        //已通过
        //未通过

        //身份是业主时：只需要判断房产证的审核状态
        if (list_house.get(position).getUserIdentity() == UserHouseModel.USER_IDENTITY_PROPRIETOR){
            holder.textView_userIdentity.setText("业主");
            switch (list_house.get(position).getIsVerifyHouseCertificate()){
                //未上传 或 已上传 或 审核中，都为审核中状态
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_NOT_UPLOAD:
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_UPLOAD:
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_ING:{
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_gray_noborder_topcorner);
                    holder.imageView_state_default.setVisibility(View.INVISIBLE);
                    holder.imageView_state_checking.setVisibility(View.VISIBLE);
                    holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                    holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    break;
                }
                //审核未通过
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_NOT_PASS:{
                    //设置背景
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_gray_noborder_topcorner);
                    holder.imageView_state_default.setVisibility(View.INVISIBLE);
                    holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                    holder.imageView_state_notPass.setVisibility(View.VISIBLE);
                    holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    break;
                }
                //审核通过
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_HAS_PASS:{
                    //设置背景
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_blue_noborder_topcorner);
                    //设置角标
                    if (list_house.get(position).isDefault() == true){
                        holder.imageView_state_default.setVisibility(View.VISIBLE);
                        holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    }else {
                        holder.imageView_state_default.setVisibility(View.INVISIBLE);
                        holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                default:
                    break;
            }
        //身份是租户时：只需要判断租赁合同的审核状态
        }else if (list_house.get(position).getUserIdentity() == UserHouseModel.USER_IDENTITY_RENTMENT){
            holder.textView_userIdentity.setText("租户");
            switch (list_house.get(position).getIsVerifyRentContract()){
                //未上传 或 已上传 或 审核中，都为审核中状态
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_NOT_UPLOAD:
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_UPLOAD:
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_ING:{
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_gray_noborder_topcorner);
                    holder.imageView_state_default.setVisibility(View.INVISIBLE);
                    holder.imageView_state_checking.setVisibility(View.VISIBLE);
                    holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                    holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    break;
                }
                //未通过
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_NOT_PASS:{
                    //设置背景
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_gray_noborder_topcorner);
                    holder.imageView_state_default.setVisibility(View.INVISIBLE);
                    holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                    holder.imageView_state_notPass.setVisibility(View.VISIBLE);
                    holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    break;
                }
                //已通过
                case UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_HAS_PASS:{
                    //设置背景
                    holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_blue_noborder_topcorner);
                    //设置角标
                    if (list_house.get(position).isDefault() == true){
                        holder.imageView_state_default.setVisibility(View.VISIBLE);
                        holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    }else {
                        holder.imageView_state_default.setVisibility(View.INVISIBLE);
                        holder.imageView_state_checking.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
                        holder.imageView_state_notUpload.setVisibility(View.INVISIBLE);
                    }
                    break;
                }
                default:
                    break;
            }
        //身份为未知时：
        }else if (list_house.get(position).getUserIdentity() == UserHouseModel.USER_IDENTITY_UNKNOW){
            holder.textView_userIdentity.setText("未知");
            holder.layout_title.setBackgroundResource(R.drawable.shape_rectangle_gray_noborder_topcorner);
            holder.imageView_state_default.setVisibility(View.INVISIBLE);
            holder.imageView_state_checking.setVisibility(View.INVISIBLE);
            holder.imageView_state_notPass.setVisibility(View.INVISIBLE);
            holder.imageView_state_notUpload.setVisibility(View.VISIBLE);
        }
        /**
         * 设置点击事件
         */
        holder.imageView_menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_house.get(position).isShowDialog() == true){
                    list_house.get(position).setShowDialog(false);
                    holder.layout_dialog.setVisibility(View.INVISIBLE);
                }else {
                    list_house.get(position).setShowDialog(true);
                    holder.layout_dialog.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.textView_deleteHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.textView_setDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list_house.get(position).getIsVerifyHouseCertificate() == UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_HAS_PASS
                        || list_house.get(position).getIsVerifyRentContract() == UserHouseModel.VERIFY_HOUSE_CERRIFICATE_STATE_HAS_PASS){
                    Message message = new Message();
                    message.what = -111;
                    message.obj = list_house.get(position).getHouseId();
                    handler.sendMessage(message);
                }else {
                    Toast.makeText(context, "请选择审核通过的住房！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.textView_deleteHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message();
                message.what = -222;
                message.obj = list_house.get(position).getHouseId();
                handler.sendMessage(message);
            }
        });
        holder.layout_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddHouseEstate_HouseProCerActivity.class);
                intent.putExtra("whatFrom", AddHouseEstate_HouseProCerActivity.WHAT_FROM_HOUSE_ESTATE);
                intent.putExtra("blockNum", list_house.get(position).getRidgepoleNum());
                intent.putExtra("unitNum", list_house.get(position).getUnitNum());
                intent.putExtra("floorNum", list_house.get(position).getFloorNum());
                intent.putExtra("roomNum", list_house.get(position).getRoomNum());
                intent.putExtra("houseEstateName", list_house.get(position).getHouseEstateName());
                intent.putExtra("houseId", list_house.get(position).getHouseId());
                switch (list_house.get(position).getUserIdentity()){
                    case UserHouseModel.USER_IDENTITY_PROPRIETOR:{
                        intent.putExtra("imageUrl", list_house.get(position).getHouseCertificateImg());
                        break;
                    }
                    case UserHouseModel.USER_IDENTITY_RENTMENT:{
                        intent.putExtra("imageUrl", list_house.get(position).getRentContractImg());
                        break;
                    }
                    case UserHouseModel.USER_IDENTITY_UNKNOW:
                        break;
                    default:
                        break;
                }
                context.startActivity(intent);
            }
        });
        return view;
    }

    private class viewHolder{
        RelativeLayout layout_title;
        TextView textView_houseName;
        TextView textView_userIdentity;
        ImageView imageView_state_default;
        ImageView imageView_state_checking;
        ImageView imageView_state_notPass;
        ImageView imageView_state_notUpload;
        TextView textView_houseSite;
        LinearLayout layout_dialog;
        TextView textView_deleteHouse;
        TextView textView_setDefault;
        ImageView imageView_menuBtn;
    }
}
