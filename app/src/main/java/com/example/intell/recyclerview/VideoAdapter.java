package com.example.intell.recyclerview;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.intell.PlayActivity;
import com.example.intell.R;
import com.example.intell.entry.Video;
import com.ezvizuikit.open.EZUIPlayer;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private static final String TAG = "VideoAdapter";

    private Context mContext;

    private String mAppKey;
    private String mAccessToken;
    private String mUrl;


    private ArrayList<Video> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView locationNameTextView;
        private final TextView videoDetailText;
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

                }
            });
            locationNameTextView = (TextView) v.findViewById(R.id.location_name_text);
            videoDetailText = (TextView) v.findViewById(R.id.video_detail_text);
            imageView = (ImageView) v.findViewById(R.id.video_screenshot_image);
        }

        public TextView getLocationNameTextView() {
            return locationNameTextView;
        }
        public TextView getVideoDetailText() {
            return videoDetailText;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public VideoAdapter(Context context, ArrayList<Video> dataSet) {
        this.mContext = context;
        this.mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.video_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
        int pos = viewHolder.getAdapterPosition();
                // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getLocationNameTextView().setText(mDataSet.get(position).getName());
        viewHolder.getVideoDetailText().setText(mDataSet.get(position).getDetail());
        viewHolder.getImageView().setImageResource(R.drawable.video1);
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
