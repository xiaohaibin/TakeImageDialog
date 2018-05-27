package com.stx.xhb.library;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * BaseDialogFragment
 */
public class BaseDialogFragment extends DialogFragment {

    protected View contentView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setGravity(getGravity());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        dialog.setContentView(contentView, getLayoutParams());
        return dialog;
    }

    public int getGravity() {
        return Gravity.CENTER;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams((ScreenUtil.getScreenWidth(getActivity())) - ScreenUtil.dp2px(getActivity(), 100),
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setContentView(@LayoutRes int layoutId) {
        contentView = LayoutInflater.from(getActivity()).inflate(layoutId, null);
    }

    public View findViewById(@IdRes int id) {
        return contentView.findViewById(id);
    }

    /**
     * 省略findViewById的强制类型转换
     *
     * @param id
     * @param <T>
     * @return
     */
    public <T extends View> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    protected void findViews() {
    }

    protected void initViews() {
    }

    protected void registerViews() {
    }

}
