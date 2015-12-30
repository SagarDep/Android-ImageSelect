package me.zsj.rxwechatimageselect.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import me.zsj.rxwechatimageselect.R;
import me.zsj.rxwechatimageselect.ScreenUtils;

/**
 * Created by zsj on 2015/12/22 0022.
 */
public class PictureListLayoutAnimation implements View.OnClickListener{

    private Context mContext;
    private View mPictureListView;
    private FrameLayout mCurrentPathLayout;
    private RecyclerView mRecyclerView;

    private ObjectAnimator mOpenAnimation;
    private ObjectAnimator mCloseAnimation;

    private OnShowPictureListListener onShowPictureListListener;


    private boolean mIsClose = true;
    private static final int VIEW_HEIGHT = 0;
    private static final int ANIMATION_DURATION = 300;
    private static final String ANIMATION_ACTION = "translationY";

    public PictureListLayoutAnimation(PictureListBuilder builder) {
        this.mContext = builder.context;
        this.mPictureListView = builder.pictureListView;
        this.mRecyclerView = (RecyclerView) this.mPictureListView.findViewById(R.id.recyclerView_picture_list);
        this.mCurrentPathLayout = builder.currentPathLayout;
        this.mOpenAnimation = buildOpenAnimation();
        this.mCloseAnimation = buildCloseAnimation();
        this.onShowPictureListListener = builder.onShowPictureListListener;
        if (mIsClose) {
            mPictureListView.setVisibility(View.GONE);
        }
        mCurrentPathLayout.setOnClickListener(this);
    }

    private ObjectAnimator buildOpenAnimation() {
        ObjectAnimator openAnimation = ObjectAnimator.ofFloat(mPictureListView,
                ANIMATION_ACTION, ScreenUtils.getHeight(mContext), VIEW_HEIGHT);
        openAnimation.setDuration(ANIMATION_DURATION);
        openAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mPictureListView.setVisibility(View.VISIBLE);
            }
        });
        return openAnimation;
    }

    private ObjectAnimator buildCloseAnimation() {
        ObjectAnimator closeAnimation = ObjectAnimator.ofFloat(mPictureListView,
                ANIMATION_ACTION, VIEW_HEIGHT, ScreenUtils.getHeight(mContext));
        closeAnimation.setDuration(ANIMATION_DURATION);
        closeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPictureListView.setVisibility(View.GONE);
            }
        });
        return closeAnimation;
    }

    @Override
    public void onClick(View v) {
        if (mIsClose) {
            mIsClose = false;
            open();
        }else {
            mIsClose = true;
            close();
        }
    }

    private void open() {
        if (onShowPictureListListener != null)
            onShowPictureListListener.showPictureList(mRecyclerView, mIsClose);
        mOpenAnimation.start();
    }

    public void close() {
        mCloseAnimation.start();
    }

    public static class PictureListBuilder {
        private View pictureListView;
        private FrameLayout currentPathLayout;
        private Context context;
        private OnShowPictureListListener onShowPictureListListener;

        public PictureListBuilder(Context context, View pictureListView, FrameLayout currentPathLayout) {
            this.context = context;
            this.pictureListView = pictureListView;
            this.currentPathLayout = currentPathLayout;
        }

        public PictureListLayoutAnimation build() {
            return new PictureListLayoutAnimation(this);
        }

        public PictureListBuilder setOnShowPictureListListener(OnShowPictureListListener onShowPictureListListener) {
            this.onShowPictureListListener = onShowPictureListListener;
            return this;
        }
    }

    public interface OnShowPictureListListener {
        void showPictureList(RecyclerView recyclerView, boolean isAnimationClose);
    }
}
