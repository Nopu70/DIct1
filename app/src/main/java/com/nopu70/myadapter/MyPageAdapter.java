package com.nopu70.myadapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Nopu70 on 2016/1/26.
 */
public class MyPageAdapter extends PagerAdapter {

    List<View> mViewList;
    List<String> mStrList;

    public MyPageAdapter(List<View> viewList, List<String> strList){
        this.mViewList = viewList;
        this.mStrList = strList;
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mStrList.get(position);
    }
}
