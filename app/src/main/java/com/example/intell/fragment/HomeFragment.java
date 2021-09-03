package com.example.intell.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.intell.R;
import com.example.intell.entry.Module;
import com.example.intell.recyclerview.VideoGridItemDecoration;
import com.example.intell.recyclerview.WorkshopCardViewAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    protected MaterialCardView card;

    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected ArrayList<Module> mDataset;
    protected WorkshopCardViewAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.module_recycler_view);

//       1. setRecyclerViewLayoutManager();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));

//       1. mAdapter = new VideoAdapter(getActivity(), mDataset);
        mAdapter = new WorkshopCardViewAdapter(getActivity(), mDataset);
        mRecyclerView.setAdapter(mAdapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.video_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.video_grid_spacing_small);
        mRecyclerView.addItemDecoration(new VideoGridItemDecoration(largePadding, smallPadding));

        return rootView;
    }

    private void initDataset() {
        mDataset = new ArrayList<>();
        Module module = null;
        ArrayList<String> name = new ArrayList<>();
        name.add("消息中心");
        name.add("管理系统");
        name.add("任务");
        name.add("我的账号");
        name.add(getResources().getString(R.string.environment_monitor));


        ArrayList<Integer> images = new ArrayList<>();
        images.add(Color.CYAN);
        images.add(Color.GRAY);
        images.add(Color.GREEN);
        images.add(Color.BLUE);
        images.add(Color.DKGRAY);

//        images.add(getContext().getResources().getColor(R.color.teal_200));
        for (int i = 0; i < 5; i++) {
            module = new Module();
            module.setName(name.get(i));
            module.setImage(images.get(i));
            mDataset.add(module);
        }
    }
}