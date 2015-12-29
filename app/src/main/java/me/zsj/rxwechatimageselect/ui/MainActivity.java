package me.zsj.rxwechatimageselect.ui;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.zsj.rxwechatimageselect.R;
import me.zsj.rxwechatimageselect.listener.OnPictureClickListener;
import me.zsj.rxwechatimageselect.listener.OnPictureListItemClickListener;
import me.zsj.rxwechatimageselect.model.Picture;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity
        implements PictureListLayoutAnimation.OnShowPictureListListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private PictureAdapter mPictureAdapter;
    private FrameLayout mPictureLayout;
    private RelativeLayout mPictureListContent;
    private PictureListAdapter mPictureListAdapter;
    private TextView mTextViewPath;


    private HashSet<String> mDirPaths = new HashSet<>();
    private List<Picture> mPictureList = new ArrayList<>();
    private List<String> mPictureListPath = new ArrayList<>();

    private PictureListLayoutAnimation mPictureListLayoutAnimation;
    private boolean mIsAnimationClose = true;

    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mPictureLayout = (FrameLayout) findViewById(R.id.fl_current_picture);
        mPictureListContent = (RelativeLayout) findViewById(R.id.picture_list_content);
        mTextViewPath = (TextView) findViewById(R.id.tv_path);

        setSupportActionBar(mToolbar);
        setupRecyclerView();
        fetchPicture();

        addLayout();
    }

    private void addLayout() {
        final View view = LayoutInflater.from(this).inflate(R.layout.picture_list_layout, null);
        mPictureListContent.addView(view);
        mPictureListLayoutAnimation = new PictureListLayoutAnimation.PictureListBuilder(this, view, mPictureLayout)
                .setOnShowPictureListListener(this)
                .build();
    }

    private void setupRecyclerView() {
        mPictureAdapter = new PictureAdapter(this, mPictureListPath, mToolbar);
        mRecyclerView.setAdapter(mPictureAdapter);
        mPictureAdapter.setOnPictureClickListener(getPictureClickListener());
    }

    private OnPictureClickListener getPictureClickListener() {
        return new OnPictureClickListener() {
            @Override
            public void onPictureClick(List<String> picturePaths) {
                Log.e("picture paths", picturePaths.toString());
            }
        };
    }

    private List<String> getFilePictures(File pictureDir) {
        return Arrays.asList(pictureDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg")
                        || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        }));
    }

    private void fetchPicture() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        mSubscription = loadPicture()
                .map(new Func1<List<Picture>, List<String>>() {
                    @Override
                    public List<String> call(List<Picture> pictureList) {
                        File pictureDir = pictureList.get(0).getPictureDir();
                        createPicturePath(pictureDir, getFilePictures(pictureDir));
                        return mPictureListPath;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> strings) {
                        File file = new File(strings.get(0)).getParentFile();
                        setCurrentPictureDir(file);
                        mPictureAdapter.notifyDataSetChanged();
                    }
                });
    }

    private Observable<List<Picture>> loadPicture() {
        return Observable.create(new Observable.OnSubscribe<List<Picture>>() {
            @Override
            public void call(Subscriber<? super List<Picture>> subscriber) {
                Uri pictureUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(pictureUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_ADDED + " desc");

                while (cursor.moveToNext()) {
                    String firstPath = null;
                    String path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    if (firstPath == null) firstPath = path;
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) continue;

                    String dirPath = parentFile.getAbsolutePath();
                    Picture picture = null;
                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    }else {
                        mDirPaths.add(dirPath);
                        picture = new Picture();
                        picture.setFirstPath(firstPath);
                        picture.setPictureDir(parentFile);
                    }
                    int pictureSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg"))
                                return true;
                            return false;
                        }
                    }).length;
                    picture.setPictureCount(pictureSize);
                    mPictureList.add(picture);
                }
                cursor.close();
                mDirPaths = null;
                subscriber.onNext(mPictureList);
            }
        });
    }

    private void setCurrentPictureDir(File parentFile) {
        String parentPath = parentFile.getAbsolutePath();
        int indexOf = parentFile.getAbsolutePath().lastIndexOf("/");
        String path = parentPath.substring(indexOf).substring(1);
        mTextViewPath.setText("当前相册:" + path);
    }

    private void createPicturePath(File parentFile, List<String> picturePath) {
        for (int i = 0; i < picturePath.size(); i ++) {
            mPictureListPath.add(parentFile.getAbsolutePath() + "/" + picturePath.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.picker_done) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showPictureList(RecyclerView recyclerView, boolean isAnimationClose) {
        mIsAnimationClose = isAnimationClose;
        mPictureListAdapter = new PictureListAdapter(this, mPictureList);
        recyclerView.setAdapter(mPictureListAdapter);

        pictureListClick(mPictureListAdapter);
    }

    private void pictureListClick(PictureListAdapter pictureListAdapter) {
        pictureListAdapter.setOnPictureListItemClickListener(new OnPictureListItemClickListener() {
                    @Override
                    public void onPictureListClick(Picture picture) {
                        mPictureListLayoutAnimation.close();
                        mPictureListPath.clear();
                        createPicturePath(picture.getPictureDir(),
                                getFilePictures(picture.getPictureDir()));
                        setCurrentPictureDir(picture.getPictureDir());
                        mPictureAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!mIsAnimationClose) {
            mPictureListLayoutAnimation.close();
            mIsAnimationClose = true;
            return true;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription.isUnsubscribed())
            mSubscription.unsubscribe();
    }
}
