package me.zsj.rxwechatimageselect.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import me.zsj.rxwechatimageselect.R;
import me.zsj.rxwechatimageselect.model.Picture;

/**
 * Created by zsj on 2015/12/25 0025.
 */
public class PictureListAdapter extends RecyclerView.Adapter<PictureListAdapter.PictureListHolder>{

    private List<Picture> mPictureList;
    private Context mContext;
    private OnPictureListItemClickListener OnPictureListItemClickListener;

    public void setOnPictureListItemClickListener(OnPictureListItemClickListener OnPictureListItemClickListener) {
        this.OnPictureListItemClickListener = OnPictureListItemClickListener;
    }

    public PictureListAdapter(Context context, List<Picture> pictureList) {
        this.mContext = context;
        this.mPictureList = pictureList;

    }

    @Override
    public PictureListAdapter.PictureListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PictureListHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_picture_list, parent, false));
    }

    @Override
    public void onBindViewHolder(PictureListAdapter.PictureListHolder holder, int position) {
        Picture picture = mPictureList.get(position);
        holder.picture = picture;
        String picturePath = picture.getPictureDir().getAbsolutePath();
        int pathIndex = picturePath.lastIndexOf("/");

        holder.textCurrentDir.setText(picturePath.substring(pathIndex).substring(1));
        holder.textPictureSize.setText(picture.getPictureCount() + "张");
        Glide.with(mContext)
                .load(picture.getFirstPath())
                .centerCrop()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPictureList.size();
    }

    class PictureListHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView imageView;
        TextView textPictureSize;
        TextView textCurrentDir;
        LinearLayout pictureListLayout;

        Picture picture;

        public PictureListHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_picture_list);
            textCurrentDir = (TextView) itemView.findViewById(R.id.text_current_dir);
            textPictureSize = (TextView) itemView.findViewById(R.id.text_picture_size);
            pictureListLayout = (LinearLayout) itemView.findViewById(R.id.picture_list_layout);
            pictureListLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (OnPictureListItemClickListener != null)
                OnPictureListItemClickListener.onPictureListClick(picture);
        }
    }

    public interface OnPictureListItemClickListener {
        void onPictureListClick(Picture picture);
    }
}
