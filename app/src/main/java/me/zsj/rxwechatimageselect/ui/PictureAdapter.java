package me.zsj.rxwechatimageselect.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import me.zsj.rxwechatimageselect.listener.OnPictureClickListener;

/**
 * Created by zsj on 2015/12/20 0020.
 */
public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureHolder> {

    private Context mContext;
    private RequestManager requestManager;
    private List<String> mPictureAlbumList;
    private Toolbar mToolbar;
    private int mSelectPictureCount = 0;

    private List<String> mSelectPicture = new ArrayList<>();

    private OnPictureClickListener onPictureClickListener;

    public void setOnPictureClickListener(OnPictureClickListener onPictureClickListener) {
        this.onPictureClickListener = onPictureClickListener;
    }

    public PictureAdapter(Context context, List<String> pictureList, Toolbar toolbar) {
        this.mContext = context;
        this.mPictureAlbumList = pictureList;
        this.requestManager = Glide.with(context);
        this.mToolbar = toolbar;
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
        final String picturePath = mPictureAlbumList.get(position);
        requestManager.load(mPictureAlbumList.get((mPictureAlbumList.size() - position - 1)))
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
        return mPictureAlbumList.size();
    }

    private void setListener(PictureHolder holder, String picturePath) {
        if (mSelectPicture.contains(picturePath)) {
            pictureUnSelect(holder);
            mSelectPicture.remove(picturePath);
            mSelectPictureCount -= 1;
        } else {
            showPictureSelect(holder);
            mSelectPicture.add(picturePath);
            mSelectPictureCount += 1;
        }

        if (onPictureClickListener != null)
            onPictureClickListener.onPictureClick(mSelectPicture);

        setTitle(mSelectPictureCount);
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
        private PictureView imageView;
        private FrameLayout pictureContent;
        private ImageView imageViewCheck;

        public PictureHolder(View itemView) {
            super(itemView);
            imageView = (PictureView) itemView.findViewById(R.id.iv_picture);
            pictureContent = (FrameLayout) itemView.findViewById(R.id.fl_picture_content);
            imageViewCheck = (ImageView) itemView.findViewById(R.id.image_check);
        }
    }

    private void setTitle(int selectPictureCount) {
        if (selectPictureCount == 0) {
            mToolbar.setTitle(mContext.getResources().getString(R.string.app_name));
        }else {
            mToolbar.setTitle(selectPictureCount + "/" + mPictureAlbumList.size());
        }
    }

}
