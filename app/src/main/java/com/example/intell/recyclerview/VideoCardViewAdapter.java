package com.example.intell.recyclerview;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.intell.ui.PlayActivity;
import com.example.intell.R;
import com.example.intell.entry.Video;
import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class VideoCardViewAdapter extends RecyclerView.Adapter<VideoCardViewAdapter.ViewHolder> {
    private static final String TAG = "VideoCardViewAdapter";
    public static final String APP_KEY = "APP_KEY";
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String PLAY_URL = "PLAY_URL";
    public static final String GLOBAL_AREA_DOMAIN = "GLOBAL_AREA_DOMAIN";

    private Context mContext;
    public static EZUIPlayer mEZUIPlayer;
    public static List<EZUIPlayer> mEZUIPlayerList = new ArrayList<>();

    private String mAppKey;
    private String mAccessToken;
    private String mUrl;

    private ArrayList<Video> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements EZUIPlayer.EZUIPlayerCallBack {
        private final ImageView videoImage;
        private final TextView videoTitle;
        private Context context;
        private Application application;

        /**
         *  开发者申请的Appkey
         */
        private String appkey;
        /**
         *  授权accesstoken
         */
        private String accesstoken;
        /**
         *  播放url：ezopen协议
         */
        private String playUrl;

        public ViewHolder(View v) {
            super(v);
            context = v.getContext();
            try {
                application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
//            appkey = intent.getStringExtra(APP_KEY);
//            accesstoken = intent.getStringExtra(ACCESS_TOKEN);
//            playUrl = intent.getStringExtra(PLAY_URL);

            videoImage = itemView.findViewById(R.id.video_image);
            videoTitle = itemView.findViewById(R.id.video_title);
            mEZUIPlayer = itemView.findViewById(R.id.video);
            mEZUIPlayerList.add(mEZUIPlayer);
            mEZUIPlayer.setLoadingView(initProgressBar());
            mEZUIPlayer.setRatio(16*1.0f/9);
        }

        public TextView getVideoTitle() {
            return videoTitle;
        }

        public ImageView getVideoImage() {
            return videoImage;
        }

        /**
         * 创建加载view
         * @return
         */
        private View initProgressBar() {
            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setBackgroundColor(Color.parseColor("#000000"));
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(lp);
            RelativeLayout.LayoutParams rlp= new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
            ProgressBar mProgressBar = new ProgressBar(context);
            mProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.progress));
            relativeLayout.addView(mProgressBar,rlp);
            return relativeLayout;
        }

        /**
         * 准备播放资源参数
         */
        private void preparePlay(String appkey, String accesstoken, String playUrl){
            //设置debug模式，输出log信息
            EZUIKit.setDebug(true);
            //appkey初始化
            EZUIKit.initWithAppKey(application, appkey);
            System.out.println("hahahah" + appkey);

            //设置授权accesstoken
            EZUIKit.setAccessToken("at.9d5vrd5x9aehav378f9bau1z8iktgvmk-5tprnfhzbn-02w545x-byw5dewyj");

            //设置播放资源参数
            mEZUIPlayer.setCallBack(this);
            mEZUIPlayer.setUrl(playUrl);
            mEZUIPlayer.startPlay();
        }

        @Override
        public void onPlaySuccess() {
//            Log.d(TAG,"onPlaySuccess");
        }

        @Override
        public void onPlayFail(EZUIError ezuiError) {
//            Log.d(TAG,"onPlayFail");
        }

        @Override
        public void onVideoSizeChange(int i, int i1) {
//            Log.d(TAG,"onVideoSizeChange");
        }

        @Override
        public void onPrepared() {
//            Log.d(TAG,"onPrepared");
        }

        @Override
        public void onPlayTime(Calendar calendar) {
//            Log.d(TAG,"onPlayTime");
        }

        @Override
        public void onPlayFinish() {
            Log.d(TAG,"onPlayFinish");
            mEZUIPlayer.stopPlay();
            mEZUIPlayer.releasePlayer();
        }

    }



    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public VideoCardViewAdapter(Context context, ArrayList<Video> dataSet) {
        this.mContext = context;
        this.mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_card, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        int pos = viewHolder.getAdapterPosition();
        viewHolder.getVideoTitle().setText(mDataSet.get(position).getName());
//        viewHolder.getVideoImage().setImageResource(mDataSet.get(position).getImage());
        viewHolder.getVideoImage().setBackgroundColor(mDataSet.get(position).getImage());
        System.out.println("nijun" + mDataSet.get(position).getAccessToken());
        viewHolder.preparePlay(mDataSet.get(position).getAppKey(),
                mDataSet.get(position).getAccessToken(), mDataSet.get(position).getSurl());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppKey = mDataSet.get(pos).getAppKey();
                mAccessToken = mDataSet.get(pos).getAccessToken();
                mUrl = mDataSet.get(pos).getUrl();
                System.out.println("nijunde" + mDataSet.get(pos).getAccessToken());
                EZUIPlayer.EZUIKitPlayMode mode = null;
                mode = EZUIPlayer.getUrlPlayType(mUrl);
                if (mode == EZUIPlayer.EZUIKitPlayMode.EZUIKIT_PLAYMODE_LIVE) {
                    //直播预览 启动播放页面
                    Log.d(TAG, "Element " + pos + " set.");
                    PlayActivity.startPlayActivity(mContext, mAppKey, mAccessToken, mUrl);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static void removeAllEZUIPlayer() {
        for (EZUIPlayer ezuiPlayer : mEZUIPlayerList) {
            ezuiPlayer.stopPlay();
            ezuiPlayer.releasePlayer();
        }
    }
}