package com.panda.pweibo.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.panda.pweibo.R;
import com.panda.pweibo.utils.ImageUtils;
import com.panda.pweibo.widget.WrapHeightGridView;

import java.util.List;

/**
 * 发微博时候的图片适配器
 *
 * Created by Administrator on 2015/9/1:14:04.
 */
public class WriteStatusGridImgsAdapter extends BaseAdapter {

    private Context             mContext;
    private List<Uri>           mUriList;
    private WrapHeightGridView  mGridView;

    public WriteStatusGridImgsAdapter(Context context, List<Uri> uriList, WrapHeightGridView gridView) {
        mContext    =   context;
        mUriList    =   uriList;
        mGridView   =   gridView;
    }

    @Override
    public int getCount() {
        return mUriList.size();
    }

    @Override
    public Uri getItem(int position) {
        return mUriList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_write_grid_image, null);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pwb_iv_image_browser
                = (ImageView) convertView.findViewById(R.id.pwb_iv_image_browser);
        holder.pwb_iv_delete = (ImageView) convertView.findViewById(R.id.pwb_iv_delete);

        int horizontalSpacing = mGridView.getHorizontalSpacing();
        int width = (mGridView.getWidth() - horizontalSpacing * 2
                - mGridView.getPaddingLeft() - mGridView.getPaddingRight()) / 3;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        holder.pwb_iv_image_browser.setLayoutParams(params);

        // set data
        Uri item = getItem(position);
        Bitmap bitmap = ImageUtils.getBitmapFromUri(mContext, item);
        holder.pwb_iv_image_browser.setImageBitmap(bitmap);

        // 点击右上角的删除,则将该图片移除
        holder.pwb_iv_delete.setVisibility(View.VISIBLE);
        holder.pwb_iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUriList.remove(position);
                notifyDataSetChanged();
            }
        });

        // 获取屏幕宽度大小、设置bitmap的宽度
        convertView.setDrawingCacheEnabled(true);
        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();

        // 如果没有调用这个方法，得到的bitmap为null
        convertView.measure(View.MeasureSpec.makeMeasureSpec(display.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(256, View.MeasureSpec.UNSPECIFIED));
        // 设置布局的尺寸和位置
        convertView.layout(0, 0, convertView.getMeasuredWidth(), convertView.getMeasuredHeight());
        // 生成bitmap
        Bitmap bitmap1 = Bitmap.createBitmap(convertView.getWidth(), convertView.getHeight(),
                Bitmap.Config.RGB_565);
        // 利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap1);
        // 把view中的内容绘制在画布上
        convertView.draw(canvas);

        return convertView;
    }

    protected class ViewHolder {
        public ImageView        pwb_iv_image_browser;
        public ImageView        pwb_iv_delete;
    }
}
