package com.nopu70.fragment.menu;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.nopu70.dict.R;
import com.nopu70.myview.TWView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 15-12-21.
 */
public class LeftMenuActivity extends LinearLayout {

    public interface OnMenuClickListener{
        public void showHomePage(int page);
    }

    OnMenuClickListener omcl;

    private int[] menu_imgId = {R.drawable.left_translation, R.drawable.left_reading,
            R.drawable.left_daily_sentance, R.drawable.left_wordsbook,
            R.drawable.left_offdic, R.drawable.left_setting};
    private String[] menus = new String[]{"翻译", "记忆", "每日一句", "生词本", "词典", "设置"};

    private ListView menuList = null;
    GoToSearchViewReceiver receiver;
    Context context;

    public int itemId = 0;


    public LeftMenuActivity(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        omcl = (OnMenuClickListener)context;

        LayoutInflater.from(context).inflate(R.layout.left_menu, this);

        menuList = (ListView)findViewById(R.id.menu_list);

        List<Map<String, Object>> item_list = new ArrayList<Map<String, Object>>();

        for (int i=0; i<menu_imgId.length; i++){
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("imgId", menu_imgId[i]);
            item.put("menus", menus[i]);
            item_list.add(item);
        }

        SimpleAdapter adapter = new SimpleAdapter(getContext(), item_list, R.layout.menu_item,
                new String[]{"imgId","menus"}, new int[]{R.id.img, R.id.menus});
        menuList.setAdapter(adapter);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position+""+itemId);
                if (itemId == position) {
                    return;
                } else {
                    omcl.showHomePage(position);
                    itemId = position;
                }
                Intent intent = new Intent("com.nopu70.leftmenu.CLICKITEM");
                intent.putExtra("position", position);
                context.sendBroadcast(intent);
            }
        });

        receiver = new GoToSearchViewReceiver();
        IntentFilter filter = new IntentFilter(TWView.SEARCH);
        filter.setPriority(20);
        context.registerReceiver(receiver, filter);
    }

    public class GoToSearchViewReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            menuList.performItemClick(menuList, 0, menuList.getItemIdAtPosition(0));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(receiver);
    }
}
