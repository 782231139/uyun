package com.uyun.hummer.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.uyun.hummer.R;


/**
 * Created by LIYUN.
 */
public class SelectDialog {
    private Context context;
    private Dialog dialog;
    private android.view.Display display;

    public SelectDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public SelectDialog builder(View view) {
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.ActionDialogStyle);
        dialog.setCancelable(true);
        dialog.setContentView(view);
        dialog.getWindow().setDimAmount(0);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);

        return this;
    }
    public void setCancelable(boolean flag){
        dialog.setCancelable(flag);
    }
    public boolean isShowing(){
        return dialog.isShowing();
    }


    public void show() {
        dialog.show();
    }
    public void diss(){
        dialog.dismiss();
    }

}
