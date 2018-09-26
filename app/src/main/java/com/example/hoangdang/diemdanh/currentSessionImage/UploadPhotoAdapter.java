package com.example.hoangdang.diemdanh.currentSessionImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoangdang.diemdanh.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by TUNG HIEP on 2/22/2018.
 */

public class UploadPhotoAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<String> PhotoList;

    public UploadPhotoAdapter(Context context, int layout, List<String> photoList) {
        this.context = context;
        this.layout = layout;
        PhotoList = photoList;
    }
    private class ViewHodler
    {
        ImageView imageView;
    }
    @Override
    public int getCount() {
        return PhotoList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHodler viewHodler;
        if(convertView == null)
        {
            viewHodler = new ViewHodler();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            viewHodler.imageView = (ImageView) convertView.findViewById(R.id.lineimageview);
            convertView.setTag(viewHodler);
        }
        else
        {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        Picasso.with(context).load(PhotoList.get(position)).resize(300,300).centerCrop().into(viewHodler.imageView);
        return convertView;
    }
}
