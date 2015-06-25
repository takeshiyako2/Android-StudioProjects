package com.example.yako.tabactivitytest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewUtil {
    public static final void cleanupView(View view) {
        if (view instanceof ImageButton) {
            ImageButton imageButton = (ImageButton) view;
            imageButton.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageButton.setBackground(null);
            } else {
                imageButton.setBackgroundDrawable(null);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackground(null);
            } else {
                imageView.setBackgroundDrawable(null);
            }
        } else if (view instanceof SeekBar) {
            SeekBar seekBar = (SeekBar) view;
            seekBar.setProgressDrawable(null);
            seekBar.setThumb(null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                seekBar.setBackground(null);
            } else {
                seekBar.setBackgroundDrawable(null);
            }
        }

        if (view instanceof ViewGroup) {
            ViewGroup viewGroupg = (ViewGroup) view;
            int size = viewGroupg.getChildCount();
            for (int i = 0; i < size; i++) {
                cleanupView(viewGroupg.getChildAt(i));
            }
        }
    }

    public static final void setBackgroundDrawable(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static final int dpToPx(Context context, int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static final int pxToDp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale);
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {

            return View.generateViewId();

        }
    }
}