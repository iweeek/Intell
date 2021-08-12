package com.example.intell.videoscreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intell.R;
import com.example.intell.entry.Video;
import com.example.intell.recyclerview.VideoAdapter;

import java.util.ArrayList;

public class VideoRecyclerViewFragment extends Fragment {

    private static final String TAG = "VideoFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Video> mDataset;
    protected VideoAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        rootView.setTag(TAG);

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_recycler_view);

        setRecyclerViewLayoutManager();

        mAdapter = new VideoAdapter(getActivity(), mDataset);
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        return rootView;
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        mDataset = new ArrayList<>();
        Video video = null;
        ArrayList<String> serialNumber = new ArrayList<>();
        // TODO modify this to get data by network
        serialNumber.add("F70152535");
        serialNumber.add("G08488600");
        serialNumber.add("G17269888");
        serialNumber.add("G17269901");
        serialNumber.add("G17269914");
        serialNumber.add("G17269927");
        serialNumber.add("G17272514");

        ArrayList<String> name = new ArrayList<>();
        name.add("项目部及外围");
        name.add("项目部大门");
        name.add("厂房东门");
        name.add("项目部北1");
        name.add("厂房西门");
        name.add("厂房1");
        name.add("项目部北2");
        for (int i = 0; i < 7; i++) {
            video = new Video();
            video.setVid(i);
            video.setName(name.get(i));
            video.setSerialNumber(serialNumber.get(i));
            video.setAppKey("df21b714ee1a4941984137eae76e1245");
            video.setAccessToken("at.6ao5gajp1u29zeg312v67hd32pfjj28s-41wrv9zha2-1pd4j8x-0bggjsvju");
            video.setUrl("ezopen://open.ys7.com/"+ video.getSerialNumber() +"/1.hd.live");
            mDataset.add(video);
        }
    }
}