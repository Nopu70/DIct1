package com.nopu70.tools;

import java.util.List;
import java.util.Map;

/**
 * Created by Nopu70 on 2016/2/4.
 */
public class Word {
    public String w;
    public String ph_en = "";
    public String ph_am = "";
    public String ph_en_mp3 = "";
    public String ph_am_mp3 = "";
    public List<Map<String, String>> partList;
    public Map<String, String> exchange;

    public Word(String w, String ph_en, String ph_am, String ph_en_mp3, String ph_am_mp3, List<Map<String, String>> partList, Map<String, String> exchange){
        this.ph_en = ph_en;
        this.ph_am = ph_am;
        this.ph_en_mp3 = ph_en_mp3;
        this.ph_am_mp3 = ph_am_mp3;
        this.partList = partList;
        this.exchange = exchange;
        this.w = w;
    }
}
