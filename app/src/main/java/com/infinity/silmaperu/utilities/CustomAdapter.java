package com.infinity.silmaperu.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.infinity.silmaperu.R;
import com.infinity.silmaperu.domain.ListModel;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ListModel> implements View.OnClickListener {

    Context mContext;
    String outOf;
    int percentage;
    private ArrayList<ListModel> dataSet;
    private int lastPosition = -1;

    public CustomAdapter(ArrayList<ListModel> data, Context context) {
        super(context, R.layout.level_row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        ListModel dataModel = (ListModel) object;

        switch (v.getId()) {
            case R.id.level_row_id:
                Snackbar.make(v, "Release date " + dataModel.getLevel(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.level_row_item, parent, false);
            viewHolder.listImage = (ImageView) convertView.findViewById(R.id.list_image);
            viewHolder.listLevel = (TextView) convertView.findViewById(R.id.list_level);
            viewHolder.listOutOf = (TextView) convertView.findViewById(R.id.list_out_of);
            viewHolder.listPercentage = (TextView) convertView.findViewById(R.id.list_percentage);
            viewHolder.listProgressBar = (ProgressBar) convertView.findViewById(R.id.list_progressBar);
            viewHolder.constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.level_row_id);
            viewHolder.unlockMessage = (TextView) convertView.findViewById(R.id.unlock_message);
            viewHolder.unlockSymbol = (ImageView) convertView.findViewById(R.id.lock_white);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "SilmaPeru" + File.separator + "level_" + dataModel.getLevel() + ".png");

        //viewHolder.listImage.setImageDrawable(dataModel.getLevel());
        viewHolder.listLevel.setText("Level " + dataModel.getLevel());
        viewHolder.listImage.setImageBitmap(bmp);

        if (dataModel.isLockStatus()) {
            viewHolder.listPercentage.setVisibility(View.GONE);
            viewHolder.listProgressBar.setVisibility(View.GONE);
            viewHolder.listOutOf.setVisibility(View.GONE);
            viewHolder.unlockSymbol.setVisibility(View.VISIBLE);
            viewHolder.unlockMessage.setVisibility(View.VISIBLE);
            String message = "";
            if (dataModel.getToUnlock() == 1) {
                message = "Answer " + dataModel.getToUnlock() + " movie to unlock";
            } else {
                message = "Answer " + dataModel.getToUnlock() + " movies to unlock";
            }
            viewHolder.unlockMessage.setText(message);
        } else {
            percentage = Math.round(((float) dataModel.getTotalDone() / (float) dataModel.getTotal()) * 100);
            viewHolder.listPercentage.setText(percentage + "%");
            viewHolder.listProgressBar.setProgress(percentage);
            outOf = dataModel.getTotalDone() + "/" + dataModel.getTotal();
            viewHolder.listOutOf.setText(outOf);
        }

        //int resID = mContext.getResources().getIdentifier(buttonID, "id", mContext.getPackageName());

        int levelRes = dataModel.getLevel();

        int darkRes = mContext.getResources().getIdentifier("colorProgressDarker_" + levelRes, "color", mContext.getPackageName());
        int lightRes = mContext.getResources().getIdentifier("colorProgress_" + levelRes, "color", mContext.getPackageName());
        int outerRes = mContext.getResources().getIdentifier("colorOuter_" + levelRes, "color", mContext.getPackageName());


        LayerDrawable shape = (LayerDrawable) viewHolder.listProgressBar.getProgressDrawable();
        Drawable dark = shape.findDrawableByLayerId(R.id.progress_dark);
        Drawable light = shape.findDrawableByLayerId(R.id.progress_light);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dark.setTint(mContext.getResources().getColor(darkRes));
            light.setTint(mContext.getResources().getColor(lightRes));
        }
        Drawable constraintLayoutBackground = viewHolder.constraintLayout.getBackground();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            constraintLayoutBackground.setTint(mContext.getResources().getColor(outerRes));
        }


        return convertView;
    }

    private static class ViewHolder {
        ImageView listImage;
        ProgressBar listProgressBar;
        TextView listLevel;
        TextView listOutOf;
        TextView listPercentage;
        TextView unlockMessage;
        ImageView unlockSymbol;
        ConstraintLayout constraintLayout;
    }

}
