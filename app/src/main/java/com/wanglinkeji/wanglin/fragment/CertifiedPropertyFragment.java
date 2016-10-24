package com.wanglinkeji.wanglin.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanglinkeji.wanglin.R;
import com.wanglinkeji.wanglin.adapter.ListViewAdapter_CertifilerProperty_news;
import com.wanglinkeji.wanglin.adapter.ViewPagerAdapter_CertifilerProperty_ScrollImages;
import com.wanglinkeji.wanglin.customerview.MyListView;
import com.wanglinkeji.wanglin.model.NewsModel;
import com.wanglinkeji.wanglin.util.OtherUtil;
import com.wanglinkeji.wanglin.util.WangLinApplication;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/16.
 * 首页->物业
 */
public class CertifiedPropertyFragment extends Fragment implements View.OnClickListener {

    public static final int IMAGE_COUNT = 300;

    private View view;

    private TextView textView_houseEstateName;

    private ViewPager viewPager_scrollImages;

    private MyListView listView_news;

    private List<NewsModel> list_news;

    //ViewPager List
    private List<View> list_view;

    //图片轮播器表示图片数量的点
    private LinearLayout layout_point;

    //ViewPager图片轮播的Handler
    private ImageHandler handler = new ImageHandler(new WeakReference<CertifiedPropertyFragment>(this));

    //图片轮播器图片List
    private List<Integer> list_image;

    //图片轮播器当前页索引
    private static int viewPager_currentItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_fragment_certified_property, container, false);

        viewInit();
        getUserInfo();
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);//开始轮播
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            default:
                break;
        }
    }

    private void viewInit(){
        textView_houseEstateName = (TextView)view.findViewById(R.id.textview_fragment_CertifiledProperty_houseEstateName);
        listView_news = (MyListView)view.findViewById(R.id.listview_fragment_CertifiledProperty_news);
        viewPager_scrollImages = (ViewPager)view.findViewById(R.id.viewpager_fragment_CertifiledProperty_scrollImages);
    }

    private void getUserInfo(){
        /**
         * 获取小区名
         */
        textView_houseEstateName.setText("网邻");
        /**
         * 获取图片
         */
        //初始化图片list,换成网络图片时记得处理当图片小于三张时出现的bug
        list_image = new ArrayList<Integer>();
        list_image.add(R.mipmap.temp_image_one);
        list_image.add(R.mipmap.temp_image_two);
        list_image.add(R.mipmap.temp_image_three);
        //初始化ViewPager
        list_view = new ArrayList<View>();
        for (int i = 0; i < list_image.size(); i++){
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_viewpager_item_image_carousel, null);
            list_view.add(view);
        }
        //初始化表示图片数量的点
        layout_point = (LinearLayout)view.findViewById(R.id.layout_fragmentCertifiedProperty_ScollImages_point);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WangLinApplication.screen_Width, WangLinApplication.screen_Height/40);
        params.setMargins(8, 0, 8, 0); //设置点之间的间隔
        final ImageView[] images_point = new ImageView[list_image.size()];
        for (int i = 0; i < images_point.length; i++){
            ImageView point = new ImageView(getActivity());
            point.setLayoutParams(params);
            //设置点的大小
            OtherUtil.setViewLayoutParams(point, true, 0, 40);
            //设置点的背景图片
            if (i == 0){
                point.setImageResource(R.drawable.shape_circle_gray666_noborder);
            }else{
                point.setImageResource(R.drawable.shape_circle_white_noborder);
            }

            images_point[i] = point;
            layout_point.addView(point);
        }
        viewPager_scrollImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) { //arg0表示当前position
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
                for (int i = 0; i < list_image.size(); i++){
                    if (i == arg0 % list_image.size()){
                        images_point[i].setImageResource(R.drawable.shape_circle_gray666_noborder);
                    }else{
                        images_point[i].setImageResource(R.drawable.shape_circle_white_noborder);
                    }
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:{
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    }
                    case ViewPager.SCROLL_STATE_IDLE:{
                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    }
                    default:
                        break;
                }
            }
        });

        viewPager_scrollImages.setAdapter(new ViewPagerAdapter_CertifilerProperty_ScrollImages(list_image, list_view));
        viewPager_currentItem = list_image.size() * IMAGE_COUNT/6;

        /**
         * 获取物业新闻
         */
        list_news = new ArrayList<>();
        NewsModel newsModel = new NewsModel();
        newsModel.setImageUrl("http://myt.cjn.cn/yzh/201503/W020150328400491889933.jpg");
        newsModel.setNewsTitle("紧急停电通知，设备正在抢修中，造成不便请谅解");
        newsModel.setNewsSeeCount(123);
        NewsModel newsModel1 = new NewsModel();
        newsModel1.setImageUrl("http://imgs.focus.cn/upload/lz/37984/a_379831433.jpg");
        newsModel1.setNewsTitle("喜大普奔：本小区荣获“全市十佳小区”称号");
        newsModel1.setNewsSeeCount(325);
        NewsModel newsModel2 = new NewsModel();
        newsModel2.setImageUrl("http://img1.cache.netease.com/catchpic/F/F9/F93AB81346B497D08259D030C4F7B11D.jpg");
        newsModel2.setNewsTitle("5单元电梯故障通报");
        newsModel2.setNewsSeeCount(55);
        list_news.add(newsModel);
        list_news.add(newsModel1);
        list_news.add(newsModel2);
        listView_news.setAdapter(new ListViewAdapter_CertifilerProperty_news(list_news, getActivity(), R.layout.layout_listview_item_certifiler_property_news));

    }

    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE  = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT   = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT  = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED  = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 3000;

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<CertifiedPropertyFragment> weakReference;

        protected ImageHandler(WeakReference<CertifiedPropertyFragment> wk){
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CertifiedPropertyFragment fragment = weakReference.get();
            if (fragment == null){
                //Activity已经回收，无需再处理UI了
                return ;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            if (fragment.handler.hasMessages(MSG_UPDATE_IMAGE)){
                fragment.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    viewPager_currentItem++;
                    fragment.viewPager_scrollImages.setCurrentItem(viewPager_currentItem);
                    //准备下次播放
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    viewPager_currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }
}
