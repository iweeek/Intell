package com.example.intell.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.intell.ui.PlayActivity;
import com.example.intell.R;
import com.example.intell.entry.Video;
import com.ezvizuikit.open.EZUIPlayer;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class VideoCardViewAdapter extends RecyclerView.Adapter<VideoCardViewAdapter.ViewHolder> {
    private static final String TAG = "VideoCardViewAdapter";

    private Context mContext;

    private String mAppKey;
    private String mAccessToken;
    private String mUrl;

    private ArrayList<Video> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView videoImage;
        private final TextView videoTitle;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            videoImage = itemView.findViewById(R.id.video_image);
            videoTitle = itemView.findViewById(R.id.video_title);
        }

        public TextView getVideoTitle() {
            return videoTitle;
        }

        public ImageView getVideoImage() {
            return videoImage;
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
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAppKey = mDataSet.get(pos).getAppKey();
                mAccessToken = mDataSet.get(pos).getAccessToken();
                mUrl = mDataSet.get(pos).getUrl();
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
}
