package com.innov8.memeit.CustomClasses;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;
import android.widget.TextView;

import com.innov8.memeit.R;

/**
 * Created by Biruk on 5/26/2018.
 */

public class CustomDialog {
    Activity activity;
    Dialog dialog;
    String message;

    public CustomDialog(Activity activity, String message) {
        this.activity = activity;
        this.message = message;
    }

    public void show() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        TextView text = (TextView) dialog.findViewById(R.id.loadingText);
        text.setText(message);

        dialog.show();

    }
    public void hide(){
        dialog.hide();
    }


}
