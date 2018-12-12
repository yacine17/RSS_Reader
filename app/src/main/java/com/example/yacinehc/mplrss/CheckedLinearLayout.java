package com.example.yacinehc.mplrss;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.LinearLayout;

import com.example.yacinehc.mplrss.model.RSS;

import java.io.Serializable;
import java.util.Objects;

public class CheckedLinearLayout extends LinearLayout implements Checkable {
    private boolean checked;
    private RSS rss;

    public CheckedLinearLayout(Context context) {
        super(context);
    }

    public CheckedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CheckedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    public RSS getRss() {
        return rss;
    }

    public void setRss(RSS rss) {
        this.rss = rss;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckedLinearLayout that = (CheckedLinearLayout) o;
        return checked == that.checked &&
                Objects.equals(rss, that.rss);
    }

    @Override
    public int hashCode() {

        return Objects.hash(checked, rss);
    }
}
