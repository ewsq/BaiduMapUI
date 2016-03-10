package com.ctc.gis;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class GalleryViewPager extends ViewPager {

    private DisplayMetrics displayMetrics;

    public GalleryViewPager(Context context) {
        super(context);
        init();
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        displayMetrics = getContext().getResources().getDisplayMetrics();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels - getPageMargin() * 4, MeasureSpec.AT_MOST);
        
        int height = 0;
        //下面遍历所有child的高度
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);            
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
//            if (getChildCount()==1) height = h ;
//            if (getChildCount()==2 && i==1) height = h ;
//            if (getChildCount()>2 && i==1) height = h ;
//            System.out.println("getcurrentitem:"+getCurrentItem()+" i:"+i+" h:"+h+" height:"+height);
            if (h > height ) // 采用最大的view的高度
                height = h;
        }
 
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    
}
