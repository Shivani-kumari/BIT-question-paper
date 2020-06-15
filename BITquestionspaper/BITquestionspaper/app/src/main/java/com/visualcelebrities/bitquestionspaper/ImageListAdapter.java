package com.visualcelebrities.bitquestionspaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {

    public List<String> imageTitleList;
    public List<String> imageUrlList;
    public List<String> imageFileNameList;
    Context context;

    public ImageListAdapter(List<String> imageTitleList, List<String> imageUrlList, List<String> imageFileNameList, Context context) {
        this.context = context;
        this.imageTitleList = imageTitleList;
        this.imageUrlList = imageUrlList;
        this.imageFileNameList = imageFileNameList;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
//        public LinearLayout linearLayout;
        public ImageView imageView;
        public TextView textView;
        public ImageViewHolder(LinearLayout v) {
            super(v);
//            linearLayout = v;
            textView = v.findViewById(R.id.title_image_item_view);
            imageView = v.findViewById(R.id.image_image_item_view);
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


//        ImageView i = (ImageView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.image_item, parent, false);

        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image, parent, false);
//        ...
        ImageViewHolder vh = new ImageViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.textView.setText(imageTitleList.get(position));
//        holder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
//        holder.imageView.setText(imageTitleList.get(position));
        Picasso.get().load(imageUrlList.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof BCA){
//                    BCA c = (BCA)context;
//                    c.downloadFile();
                    ((BCA) context).downloadFile(context, imageFileNameList.get(position), "", DIRECTORY_DOWNLOADS, imageUrlList.get(position));
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });}

                if(context instanceof BTECH){
                    ((BTECH) context).downloadFile(context, imageFileNameList.get(position), "", DIRECTORY_DOWNLOADS, imageUrlList.get(position));
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return imageTitleList.size();
    }
}
