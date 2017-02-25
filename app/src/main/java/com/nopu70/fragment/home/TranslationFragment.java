package com.nopu70.fragment.home;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nopu70.dict.R;
import com.nopu70.myadapter.MyPageAdapter;
import com.nopu70.myview.TSView;
import com.nopu70.myview.TWView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nopu70 on 15-12-21.
 */
public class TranslationFragment extends Fragment {

    private View view = null;

    TabLayout tabLayout;
    ViewPager viewPager;
    List<String> strList = new ArrayList<String>();
    List<View> viewList = new ArrayList<View>();
    TSView tsView;
    TWView twView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        tsView = new TSView(activity);
        twView = new TWView(activity);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.translation_fragment, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);

        viewList.add(twView);
        viewList.add(tsView);

        strList.add("单词查询");
        strList.add("句子查询");

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setSelectedTabIndicatorHeight(1);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.black));
        MyPageAdapter adapter = new MyPageAdapter(viewList, strList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

        return view;


    }
}
