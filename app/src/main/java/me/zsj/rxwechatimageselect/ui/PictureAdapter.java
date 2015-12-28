package me.zsj.rxwechatimageselect.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import me.zsj.rxwechatimageselect.R;

/**
 * Created by zsj on 2015/12/20 0020.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {

    private Context mContext;
    private RequestManager requestManager;
    private List<String> mPictureList;

    private List<String> mSelectPicture = new ArrayList<>();

    private onPictureClickListener onPictureClickListener;

    public void setOnPictureClickListener(PictureAdapter.onPictureClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    public PictureAdapter(Context context, List<String> pictureList) {
        this.mContext = context;
        this.mPictureList = pictureList;
        this.requestManager = Glide.with(context);
    }

    @Override
    public PictureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PictureHolder holder = new PictureHolder(
            LayoutInflater.from(mContext).inflate(R.layout.item_picture, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final PictureHolder holder, final int position) {
        holder.imageView.setOriginalSize(50, 50);
        final String picturePath = mPictureList.get(position);
        requestManager.load(mPictureList.get((mPictureList.size() - position - 1)))
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setListener(holder, picturePath);
            }
        });

        if (mSelectPicture.contains(picturePath)) {
           showPictureSelect(holder);
        }else {
            pictureUnSelect(holder);
        }
    }

    @Override
    public int getItemCount() {
        return mPictureList.size();
    }

    private void setListener(PictureHolder holder, String picturePath) {
        if (mSelectPicture.contains(picturePath)) {
            pictureUnSelect(holder);
            mSelectPicture.remove(picturePath);
        } else {
            showPictureSelect(holder);
            mSelectPicture.add(picturePath);
        }

        if (onPictureClickListener != null)
            onPictureClickListener.onPictureClick(mSelectPicture);

    }

    private void showPictureSelect(PictureHolder holder) {
        holder.pictureContent.setBackgroundColor(Color.YELLOW);
        holder.imageViewCheck.setImageResource(R.mipmap.checkbox_marked);
    }

    private void pictureUnSelect(PictureHolder holder) {
        holder.pictureContent.setBackgroundColor(Color.parseColor("#616161"));
        holder.imageViewCheck.setImageResource(R.mipmap.ic_checkbox_blank_grey600_24dp);
    }

    class PictureHolder extends RecyclerView.ViewHolder {

        PictureView imageView;
        FrameLayout pictureContent;
        ImageView imageViewCheck;

        public PictureHolder(View itemView) {
            super(itemView);
            imageView = (PictureView) itemView.findViewById(R.id.iv_picture);
            pictureContent = (FrameLayout) itemView.findViewById(R.id.fl_picture_content);
            imageViewCheck = (ImageView) itemView.findViewById(R.id.image_check);
        }
    }

    public interface onPictureClickListener {
        void onPictureClick(List<String> picturePaths);
    }

}
