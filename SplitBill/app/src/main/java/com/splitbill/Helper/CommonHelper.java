package com.splitbill.Helper;

import android.content.Context;
import android.widget.Toast;

public class CommonHelper {

    public static CommonHelper commonHelper = new CommonHelper();

    public CommonHelper() {
    }

    public static CommonHelper getInstance( ) {
        return commonHelper;
    }

    public void showMessageShort(Context context , String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void showMessageLong(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
