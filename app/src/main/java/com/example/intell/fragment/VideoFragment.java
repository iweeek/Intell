package com.example.intell.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intell.R;
import com.example.intell.entry.AccessToken;
import com.example.intell.entry.Video;
import com.example.intell.recyclerview.VideoCardViewAdapter;
import com.example.intell.recyclerview.VideoGridItemDecoration;

import java.util.ArrayList;


public class VideoFragment extends Fragment {

    private static final String TAG = "VideoFragment";

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Video> mDataset;
//    protected VideoAdapter mAdapter;
    protected VideoCardViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println(TAG + ": onPause...");
        VideoCardViewAdapter.removeAllEZUIPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println(TAG + ": onStop...");
//        VideoCardViewAdapter.mEZUIPlayer.stopPlay();
//        VideoCardViewAdapter.mEZUIPlayer.releasePlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_recycler_view);

//       1. setRecyclerViewLayoutManager();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));

//       1. mAdapter = new VideoAdapter(getActivity(), mDataset);
        mAdapter = new VideoCardViewAdapter(getActivity(), mDataset);
        mRecyclerView.setAdapter(mAdapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.video_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.video_grid_spacing_small);
        mRecyclerView.addItemDecoration(new VideoGridItemDecoration(largePadding, smallPadding));

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

        ArrayList<Integer> images = new ArrayList<>();
        images.add(getContext().getResources().getColor(R.color.purple_200));
        images.add(getContext().getResources().getColor(R.color.purple_500));
        images.add(getContext().getResources().getColor(R.color.purple_700));
        images.add(getContext().getResources().getColor(R.color.teal_200));
        images.add(getContext().getResources().getColor(R.color.teal_700));
        images.add(getContext().getResources().getColor(R.color.silver));
        images.add(getContext().getResources().getColor(R.color.teal));
        images.add(getContext().getResources().getColor(R.color.purple));
//        images.add(R.drawable.ic_7);

        for (int i = 0; i < 7; i++) {
            video = new Video();
            video.setVid(i);
            video.setName(name.get(i));
            video.setSerialNumber(serialNumber.get(i));
            video.setImage(images.get(i));
            video.setAppKey("df21b714ee1a4941984137eae76e1245");
            video.setAccessToken(getTokenBySharedPreferences());
            video.setUrl("ezopen://open.ys7.com/"+ video.getSerialNumber() +"/1.hd.live");
            video.setSurl("ezopen://open.ys7.com/"+ video.getSerialNumber() +"/1.live");
            mDataset.add(video);
        }
    }

    private String getTokenBySharedPreferences() {
        SharedPreferences sp = getContext().getSharedPreferences(AccessToken.ACCESS_TOKEN, Context.MODE_PRIVATE);
        String token = sp.getString("token", null);
        long expireTime = sp.getLong("expireTime", 0);
        return token;
    }
}