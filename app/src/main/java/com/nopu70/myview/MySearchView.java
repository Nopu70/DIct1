package com.nopu70.myview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.nopu70.dict.Dictionary;
import com.nopu70.dict.R;
import com.nopu70.fragment.menu.LeftMenuActivity;

import java.util.ArrayList;


/**
 * Created by Nopu70 on 2016/1/30.
 */
public class MySearchView extends LinearLayout{



    ImageButton search;
    AutoCompleteTextView input;
    LinearLayout layout;
    BaiduASRDigitalDialog baiduASRDigitalDialog;
    ArrayAdapter adapter;
    String[] list;
    Context context;
    boolean auto = true;
    int id = 0;
    FocusHasReceiver receiver;
    public static String str;

    boolean focus = true, has = false;

    public MySearchView(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_view, this);
        search = (ImageButton) findViewById(R.id.searchIb);
        input = (AutoCompleteTextView) findViewById(R.id.inputEdit);
        layout = (LinearLayout) findViewById(R.id.searchView);

        input.setThreshold(1);
        list = new String[10];

        setSearchView(focus, has);

        Bundle params = new Bundle();
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, "D4eWYVmfcHG6B85bwnKYEiYY");
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, "hXgx8X5FWwLHvDj2WZorDGYrsemGGYbS");
        params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, VoiceRecognitionConfig.LANGUAGE_ENGLISH);

        baiduASRDigitalDialog = new BaiduASRDigitalDialog(context, params);
        baiduASRDigitalDialog.setDialogRecognitionListener(new DialogRecognitionListener() {
            @Override
            public void onResults(Bundle result) {
                ArrayList<String> rs = result != null ? result.getStringArrayList(RESULTS_RECOGNITION) : null;

                if (rs == null) {
                    return;
                } else {
                    String[] str = (String[]) rs.toArray(new String[rs.size()]);
                    String reS = "";
                    for (String s : str) {
                        reS += s;
                    }
                    input.setText(reS);
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    return;
                }
                int pos = s.charAt(s.length() - 1) - 'a';
                if (pos >= 0 && pos < 26 && auto) {
                    ArrayList<String> l = Dictionary.tree.hasPrefix(s.toString());
                    if (l != null) {
                        list = (String[]) l.toArray(new String[l.size()]);
                        adapter = new ArrayAdapter(context, android.R.layout.simple_expandable_list_item_1, list);
                        input.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    auto = false;
                }
                str = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                focus = true;
                input.setCursorVisible(true);
                if (TextUtils.isEmpty(input.getText()) || input.getText().toString() == "") {
                    input.setCursorVisible(false);
                    has = false;
                } else {
                    has = true;
                }
                setSearchView(focus, has);
            }
        });

        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id != 0) {
                    id = 0;
                    input.setFocusable(true);
                    input.setFocusableInTouchMode(true);
                    input.requestFocus();
                    input.findFocus();
                    focus = true;

                }else if (TextUtils.isEmpty(input.getText())){
                    baiduASRDigitalDialog.show();
                }
                Intent intent = new Intent("com.nopu70.action.SEARCH");
                intent.putExtra("word", input.getText().toString());
                context.sendOrderedBroadcast(intent, null);
            }
        });

        receiver = new FocusHasReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.nopu70.leftmenu.CLICKITEM");
        context.registerReceiver(receiver, filter);
    }

    public void setSearchView(boolean focus, boolean has){
        if (focus==true&&has==true){
            layout.setBackground(getResources().getDrawable(R.drawable.abkgd));
            search.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        } else if (focus==true&&has==false){
            layout.setBackground(getResources().getDrawable(R.drawable.abkgd));
            search.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_voice));
        } else if (focus==false&&has==true){
            layout.setBackground(getResources().getDrawable(R.color.white));
            search.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        } else if (focus==false&&has==false){
            layout.setBackground(getResources().getDrawable(R.color.white));
            search.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        }
    }

    public class FocusHasReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            id = intent.getIntExtra("position", 0);
            if (id != 0) {
                input.setFocusable(false);
                input.setText("");
                focus = false;
                has = false;
            } else {
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                input.requestFocus();
                input.findFocus();
                focus = true;
                has = false;
            }
            setSearchView(focus, has);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        context.unregisterReceiver(receiver);
    }
}
