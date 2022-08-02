package com.example.intell.recyclerview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.intell.R;
import com.example.intell.entry.Module;
import com.example.intell.ui.MainActivity;
import com.example.intell.ui.PDFViewActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class WorkshopCardViewAdapter extends RecyclerView.Adapter<WorkshopCardViewAdapter.ViewHolder> {
    private static final String TAG = "WordshopCardViewAdapter";

    private Context mContext;

    private String mAppKey;
    private String mAccessToken;
    private String mUrl;

    private ArrayList<Module> mDataSet;

    final int REQUEST_CODE = 1;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView moduleImage;
        private final TextView moduleTitle;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            moduleImage = itemView.findViewById(R.id.module_image);
            moduleTitle = itemView.findViewById(R.id.module_title);
        }

        public TextView getModuleTitle() {
            return moduleTitle;
        }

        public ImageView getModuleImage() {
            return moduleImage;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public WorkshopCardViewAdapter(Context context, ArrayList<Module> dataSet) {
        this.mContext = context;
        this.mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.module_card_icon, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        int pos = viewHolder.getAdapterPosition();
        viewHolder.getModuleTitle().setText(mDataSet.get(position).getName());
        viewHolder.getModuleImage().setBackgroundResource(mDataSet.get(position).getImage());
//        viewHolder.getModuleImage().setBackgroundColor(mDataSet.get(position).getImage());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = view.findViewById(R.id.module_title);
                if (tv.getText().equals(view.getContext().getResources().getString(R.string.environment_monitor))) {
//                    Toast.makeText(view.getContext(), R.string.environment_monitor, Toast.LENGTH_SHORT).show();
//                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_environmentFragment);
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_environmentActivity);
                }
                if (tv.getText().equals(view.getContext().getResources().getString(R.string.review_form))) {
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_reviewFormActivity_);
                }
                if (tv.getText().equals("采样方案检查表")) {
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_reviewForm31Activity_);
                }
                if (tv.getText().equals("现场采样检查表")) {
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_reviewForm32Activity_);
                }
                if (tv.getText().equals("检验检测检查表")) {
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_reviewForm33Activity_);
                }
                if (tv.getText().equals("审核记录表")) {
                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_reviewForm34Activity_);
                }
                if (tv.getText().equals("预览PDF")) {

                    showPdfDialog(view);
//                    Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_PDFViewActivity_);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void showPdfDialog(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(view.getContext())
                .setTitle("预览PDF")
                .setMessage("是否打开系统目录预览PDF？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Navigation.findNavController(view).navigate(R.id.action_homeScreen_to_PDFViewActivity_);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog.show();
    }

}
