package com.wanglinkeji.wanglin.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.activity.BigImage_ChatActivity;
import com.wanglinkeji.wanglin.activity.NineImage_BigActivity;
import com.wanglinkeji.wanglin.customerview.CircleImageView;
import com.wanglinkeji.wanglin.customerview.recordbutton.MediaPlayerManager;
import com.wanglinkeji.wanglin.model.ChatItemMoeld;
import com.wanglinkeji.wanglin.model.SwpeingImageModel;
import com.wanglinkeji.wanglin.util.DBUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/21.
 * 聊天界面ListViewAdapter
 */

public class ListViewAdapter_Chat extends BaseAdapter {

    private List<ChatItemMoeld> list_chatItem;

    private Context context;

    private int resource;

    //显示图片的配置
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.shape_rectangle_black_noborder_nocorner)
            .showImageOnFail(R.drawable.shape_rectangle_gray_noborder_nocorner)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public ListViewAdapter_Chat(List<ChatItemMoeld> list_chatItem, Context context, int resource){
        this.list_chatItem = list_chatItem;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return list_chatItem.size();
    }

    @Override
    public Object getItem(int i) {
        return list_chatItem.get(i);
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
            holder.textView_date = (TextView)view.findViewById(R.id.textview_chatItem_date);
            holder.layout_friend = (LinearLayout)view.findViewById(R.id.layout_chatItem_fromFriend);
            holder.layout_me = (LinearLayout)view.findViewById(R.id.layout_chatItem_fromMe);
            holder.imageView_friendHeader = (CircleImageView)view.findViewById(R.id.imageview_chatItem_friendHeader);
            holder.textView_friendNickName = (TextView)view.findViewById(R.id.textview_chatItem_friendNickName);
            holder.textView_friendContent = (TextView)view.findViewById(R.id.textview_chatItem_friendContent);
            holder.imageView_myHeader = (CircleImageView)view.findViewById(R.id.imageview_chatItem_myHeader);
            holder.textView_myNickName = (TextView)view.findViewById(R.id.textview_chatItem_myNickName);
            holder.textView_myContent = (TextView)view.findViewById(R.id.textview_chatItem_myContent);
            holder.progressBar_messageState = (ProgressBar) view.findViewById(R.id.progressBar_loading_chatItem_myContent);
            holder.imageView_sendFailed = (ImageView)view.findViewById(R.id.imageview_chatItem_sendFailed);
            holder.textView_friednVoice = (TextView)view.findViewById(R.id.textview_chatItem_friendVoice);
            holder.imageView_friendImage = (ImageView)view.findViewById(R.id.imageview_chatItem_friendImage);
            holder.textView_myVoice = (TextView)view.findViewById(R.id.textview_chatItem_myVoice);
            holder.imageView_myImage = (ImageView)view.findViewById(R.id.imageview_chatItem_myImage);
            holder.textView_friendVoiceLength = (TextView)view.findViewById(R.id.textview_chatItem_friendVoiceLength);
            holder.imageView_friendVoiceIsRead = (ImageView)view.findViewById(R.id.imageview_chatItem_friendVoiceIsRead);
            holder.textView_myVoiceLength = (TextView)view.findViewById(R.id.textview_chatItem_myVoiceLength);
            holder.layout_friendVoice = (LinearLayout)view.findViewById(R.id.layout_chatItem_friendVoice);
            holder.textView_friednVoiceSpace = (TextView)view.findViewById(R.id.textview_chatItem_friendVoiceSpace);
            holder.layout_myVoice = (LinearLayout)view.findViewById(R.id.layout_chatItem_myVoice);
            holder.textView_myVoiceSpace = (TextView)view.findViewById(R.id.textview_chatItem_myVoiceSpace);
            view.setTag(holder);
        }else {
            holder = (viewHolder)view.getTag();
        }
        //如果消息来自好友
        if (list_chatItem.get(position).getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_FRIEND){
            //设置显示“对方”或者显示“我”
            holder.layout_friend.setVisibility(View.VISIBLE);
            holder.layout_me.setVisibility(View.GONE);
            //设置显示昵称
            holder.textView_friendNickName.setText(list_chatItem.get(position).getFriendNickName());
            //设置聊天内容
            //判断内容类型
            //文本消息
            if (list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_TEXT){
                holder.textView_friendContent.setVisibility(View.VISIBLE);
                holder.layout_friendVoice.setVisibility(View.GONE);
                holder.imageView_friendImage.setVisibility(View.GONE);
                holder.imageView_friendVoiceIsRead.setVisibility(View.GONE);
                holder.textView_friendVoiceLength.setVisibility(View.GONE);
                holder.textView_friendContent.setText(list_chatItem.get(position).getFriendContent());

            //语音消息
            }else if (list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_VOICE){
                holder.textView_friendContent.setVisibility(View.GONE);
                holder.layout_friendVoice.setVisibility(View.VISIBLE);
                holder.imageView_friendImage.setVisibility(View.GONE);
                holder.textView_friendVoiceLength.setVisibility(View.VISIBLE);
                if (list_chatItem.get(position).getVoiceReadState() == ChatItemMoeld.VOICE_READ_STATE_HAS_READ){
                    holder.imageView_friendVoiceIsRead.setVisibility(View.INVISIBLE);
                }else if (list_chatItem.get(position).getVoiceReadState() == ChatItemMoeld.VOICE_READ_STATE_NOT_READ){
                    holder.imageView_friendVoiceIsRead.setVisibility(View.VISIBLE);
                }
                holder.textView_friendVoiceLength.setText(String.valueOf(list_chatItem.get(position).getVoiceLength()) + "''");
                ViewGroup.LayoutParams params = holder.textView_friednVoiceSpace.getLayoutParams();
                params.width = WangLinApplication.screen_Width / 2 /60 * list_chatItem.get(position).getVoiceLength();
                holder.layout_friendVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBUtil.readVoice(list_chatItem.get(position).getVoiceUrl());
                        if (list_chatItem.get(position).getVoiceReadState() == ChatItemMoeld.VOICE_READ_STATE_NOT_READ){
                            list_chatItem.get(position).setVoiceReadState(ChatItemMoeld.VOICE_READ_STATE_HAS_READ);
                            holder.imageView_friendVoiceIsRead.setVisibility(View.INVISIBLE);
                        }
                        if (list_chatItem.get(position).getVoicePlayState() == ChatItemMoeld.VOICE_PLAY_STATE_START){
                            holder.textView_friednVoice.setBackgroundResource(R.mipmap.voice_stop_icon__friend);
                            list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                            MediaPlayerManager.pause();
                            MediaPlayerManager.release();
                        }else if (list_chatItem.get(position).getVoicePlayState() == ChatItemMoeld.VOICE_PLAY_STATE_STOP){
                            list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_START);
                            holder.textView_friednVoice.setBackgroundResource(R.drawable.anim_chat_voice_friend);
                            AnimationDrawable animation = (AnimationDrawable)holder.textView_friednVoice.getBackground();
                            animation.start();
                            MediaPlayerManager.playSound(list_chatItem.get(position).getVoiceUrl(), new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    holder.textView_friednVoice.setBackgroundResource(R.mipmap.voice_stop_icon__friend);
                                    list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                                }
                            });
                        }
                    }
                });

            //图片消息
            }else if(list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_IMAGE){
                holder.textView_friendContent.setVisibility(View.GONE);
                holder.layout_friendVoice.setVisibility(View.GONE);
                holder.imageView_friendImage.setVisibility(View.VISIBLE);
                holder.imageView_friendVoiceIsRead.setVisibility(View.GONE);
                holder.textView_friendVoiceLength.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(list_chatItem.get(position).getImageUrl(), holder.imageView_friendImage, options);
                holder.imageView_friendImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<SwpeingImageModel> list_images = new ArrayList<SwpeingImageModel>();
                        SwpeingImageModel swpeingImageModel = new SwpeingImageModel();
                        swpeingImageModel.setUrl(list_chatItem.get(position).getImageUrl());
                        list_images.add(swpeingImageModel);
                        SwpeingImageModel.list_nineImage = list_images;
                        SwpeingImageModel.current_position = 0;
                        Intent intent = new Intent(context, NineImage_BigActivity.class);
                        context.startActivity(intent);
                    }
                });
            }
        //如果消息来自我
        }else if (list_chatItem.get(position).getMessageFrom() == ChatItemMoeld.MESSAGE_FROM_ME){
            //设置显示“对方”或者显示“我”
            holder.layout_friend.setVisibility(View.GONE);
            holder.layout_me.setVisibility(View.VISIBLE);
            //设置显示昵称
            holder.textView_myNickName.setText(list_chatItem.get(position).getMyNickName());
            //设置聊天内容
            //判断内容类型
            //文本消息
            if (list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_TEXT){
                holder.textView_myContent.setVisibility(View.VISIBLE);
                holder.layout_myVoice.setVisibility(View.GONE);
                holder.imageView_myImage.setVisibility(View.GONE);
                holder.textView_myVoiceLength.setVisibility(View.GONE);
                holder.textView_myContent.setText(list_chatItem.get(position).getMyContent());

            //语音消息
            }else if (list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_VOICE){
                holder.textView_myContent.setVisibility(View.GONE);
                holder.layout_myVoice.setVisibility(View.VISIBLE);
                holder.imageView_myImage.setVisibility(View.GONE);
                holder.textView_myVoiceLength.setVisibility(View.VISIBLE);
                holder.textView_myVoiceLength.setText(String.valueOf(list_chatItem.get(position).getVoiceLength()) + "''");
                ViewGroup.LayoutParams params = holder.textView_myVoiceSpace.getLayoutParams();
                params.width = WangLinApplication.screen_Width / 2 /60 * list_chatItem.get(position).getVoiceLength();
                holder.layout_myVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (list_chatItem.get(position).getVoicePlayState() == ChatItemMoeld.VOICE_PLAY_STATE_START){
                            holder.textView_myVoice.setBackgroundResource(R.mipmap.voice_stop_icon_me);
                            list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                            MediaPlayerManager.pause();
                            MediaPlayerManager.release();
                        }else if (list_chatItem.get(position).getVoicePlayState() == ChatItemMoeld.VOICE_PLAY_STATE_STOP){
                            list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_START);
                            holder.textView_myVoice.setBackgroundResource(R.drawable.anim_chat_voice_me);
                            AnimationDrawable animation = (AnimationDrawable)holder.textView_myVoice.getBackground();
                            animation.start();
                            //判断本地录音是否存在，存在则播放本地录音，不存在则播放网络声音
                            File file = new File(list_chatItem.get(position).getLocalVoicePath());
                            if (file.isFile() && file.exists()){
                                MediaPlayerManager.playSound(list_chatItem.get(position).getLocalVoicePath(), new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        holder.textView_myVoice.setBackgroundResource(R.mipmap.voice_stop_icon_me);
                                        list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                                    }
                                });
                            }else {
                                MediaPlayerManager.playSound(list_chatItem.get(position).getVoiceUrl(), new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        holder.textView_myVoice.setBackgroundResource(R.mipmap.voice_stop_icon_me);
                                        list_chatItem.get(position).setVoicePlayState(ChatItemMoeld.VOICE_PLAY_STATE_STOP);
                                    }
                                });
                            }
                        }
                    }
                });

            //图片消息
            }else if(list_chatItem.get(position).getMessageType() == ChatItemMoeld.MESSAGE_TYPE_IMAGE){
                holder.textView_myContent.setVisibility(View.GONE);
                holder.layout_myVoice.setVisibility(View.GONE);
                holder.imageView_myImage.setVisibility(View.VISIBLE);
                holder.textView_myVoiceLength.setVisibility(View.GONE);
                //判断本地图片是否存在，存在则显示本地图片，不存在则加载网络图片
                File file = new File(list_chatItem.get(position).getImageLocalPath());
                if (file.isFile() && file.exists()){
                    //用ImageLoader显示照片
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(ImageDownloader.Scheme.FILE.wrap(list_chatItem.get(position).getImageLocalPath()), holder.imageView_myImage,options);
                }else {
                    ImageLoader.getInstance().displayImage(list_chatItem.get(position).getImageUrl(), holder.imageView_myImage, options);
                }
                holder.imageView_myImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, BigImage_ChatActivity.class);
                        intent.putExtra("path", list_chatItem.get(position).getImageLocalPath());
                        context.startActivity(intent);
                    }
                });
            }

            //设置消息发送状态图标
            if (list_chatItem.get(position).getMessageSendState() == ChatItemMoeld.MESSAGE_SEND_STATE_ING){
                holder.progressBar_messageState.setVisibility(View.VISIBLE);
                holder.imageView_sendFailed.setVisibility(View.GONE);
            }else if (list_chatItem.get(position).getMessageSendState() == ChatItemMoeld.MESSAGE_SEND_STATE_FINISH){
                holder.progressBar_messageState.setVisibility(View.GONE);
                holder.imageView_sendFailed.setVisibility(View.GONE);
            }else if (list_chatItem.get(position).getMessageSendState() == ChatItemMoeld.MESSAGE_SEND_STATE_FAILED){
                holder.progressBar_messageState.setVisibility(View.GONE);
                holder.imageView_sendFailed.setVisibility(View.VISIBLE);
            }
        }
        if (list_chatItem.get(position).isShowDate() == true){
            holder.textView_date.setVisibility(View.VISIBLE);
        }else {
            holder.textView_date.setVisibility(View.GONE);
        }
        holder.textView_date.setText(list_chatItem.get(position).getShowDate());
        return view;
    }

    private class viewHolder{
        TextView textView_date;
        LinearLayout layout_friend;
        LinearLayout layout_me;
        CircleImageView imageView_friendHeader;
        TextView textView_friendNickName;
        TextView textView_friendContent;
        LinearLayout layout_friendVoice;
        TextView textView_friednVoice;
        TextView textView_friednVoiceSpace;
        TextView textView_friendVoiceLength;
        ImageView imageView_friendVoiceIsRead;
        ImageView imageView_friendImage;
        CircleImageView imageView_myHeader;
        TextView textView_myNickName;
        TextView textView_myContent;
        LinearLayout layout_myVoice;
        TextView textView_myVoice;
        TextView textView_myVoiceSpace;
        TextView textView_myVoiceLength;
        ImageView imageView_myImage;
        ProgressBar progressBar_messageState;
        ImageView imageView_sendFailed;
    }
}
