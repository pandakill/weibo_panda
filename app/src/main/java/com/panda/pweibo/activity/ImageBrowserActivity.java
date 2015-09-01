package com.panda.pweibo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.panda.pweibo.R;
import com.panda.pweibo.adapter.ImageBrowserAdapter;
import com.panda.pweibo.models.PicUrls;
import com.panda.pweibo.models.Status;
import com.panda.pweibo.utils.ToastUtils;

import java.util.ArrayList;

/**
 * 图片浏览activity
 *
 * Created by Administrator on 2015/09/01.
 */
public class ImageBrowserActivity extends BaseActivity implements View.OnClickListener {

    private Status              mStatus;
    private int                 mPosition;
    private ImageBrowserAdapter mAdapter;
    private ArrayList<PicUrls>  mImgUrls;

    private ViewPager           pwb_vp_image_brower;
    private TextView            pwb_tv_image_index;
    private Button              pwb_btn_save;
    private Button              pwb_btn_original_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_browser);

        initData();
        initView();
        setData();
    }

    private void initData() {
        mStatus = (Status) getIntent().getSerializableExtra("status");
        mPosition = getIntent().getIntExtra("position", 0);
        // 获取图片数据集合(单图也有对应的集合,集合的size为1)
        mImgUrls = mStatus.getPic_ids();
    }

    private void initView() {
        pwb_vp_image_brower = (ViewPager) findViewById(R.id.pwb_vp_image_brower);
        pwb_tv_image_index = (TextView) findViewById(R.id.pwb_tv_image_index);
        pwb_btn_save = (Button) findViewById(R.id.pwb_btn_save);
        pwb_btn_original_image = (Button) findViewById(R.id.pwb_btn_orgin);

        pwb_btn_save.setOnClickListener(this);
        pwb_btn_original_image.setOnClickListener(this);
    }

    private void setData() {
        mAdapter = new ImageBrowserAdapter(this, mImgUrls, mImageLoader);
        pwb_vp_image_brower.setAdapter(mAdapter);

        final int size = mImgUrls.size();
        int initPosition = Integer.MAX_VALUE / 2 / size * size + mPosition;

        if(size > 1) {
            pwb_tv_image_index.setVisibility(View.VISIBLE);
            pwb_tv_image_index.setText((mPosition+1) + "/" + size);
        } else {
            pwb_tv_image_index.setVisibility(View.GONE);
        }

        pwb_vp_image_brower.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                int index = arg0 % size;
                pwb_tv_image_index.setText((index+1) + "/" + size);

                PicUrls pic = mAdapter.getPic(arg0);
                pwb_btn_original_image.setVisibility(pic.IsShowOriImg() ?
                        View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

        pwb_vp_image_brower.setCurrentItem(initPosition);
    }

    @Override
    public void onClick(View v) {
        PicUrls picUrl = mAdapter.getPic(pwb_vp_image_brower.getCurrentItem());

        switch (v.getId()) {
            case R.id.pwb_btn_save:
                Bitmap bitmap = mAdapter.getBitmap(pwb_vp_image_brower.getCurrentItem());

                boolean showOriImag = picUrl.IsShowOriImg();
                String fileName = "img-" + (showOriImag?"ori-" : "mid-") + picUrl.getImageId();

                String title = fileName.substring(0, fileName.lastIndexOf("."));
                String insertImage = MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, title, "pandaWBImage");
                if(insertImage == null) {
                    ToastUtils.showToast(ImageBrowserActivity.this, "图片保存失败", Toast.LENGTH_SHORT);
                } else {
                    ToastUtils.showToast(ImageBrowserActivity.this, "图片保存成功", Toast.LENGTH_SHORT);
                }
                break;

            case R.id.pwb_btn_orgin:
                picUrl.setShowOriImg(true);
                mAdapter.notifyDataSetChanged();
                pwb_btn_original_image.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }
}
