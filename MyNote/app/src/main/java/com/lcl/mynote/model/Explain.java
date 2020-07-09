package com.lcl.mynote.model;

/**
 * Created by lcl on 2020/6/20.
 */

public class Explain {
    String[] explains;

    public String[] getExplains() {
        return explains;
    }

    public String getStrings(String[] strings){
        String str = "";
        for (String s:strings
             ) {
            str += s+"\n";
        }
        return str;
    }
}
