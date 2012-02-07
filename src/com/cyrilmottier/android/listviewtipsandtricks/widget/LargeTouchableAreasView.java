/*
 * Copyright (C) 2012 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cyrilmottier.android.listviewtipsandtricks.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyrilmottier.android.listviewtipsandtricks.R;
import com.cyrilmottier.android.listviewtipsandtricks.view.TouchDelegateGroup;

/**
 * A simple layout containing a {@link TextView} as well as two checkboxes: one
 * to select the row and another one to star it.
 * 
 * @author Cyril Mottier
 */
public class LargeTouchableAreasView extends LinearLayout {

    private static final int TOUCH_ADDITION = 20;
    private static final int COLOR_SELECT_AREA = Color.argb(50, 255, 0, 0);
    private static final int COLOR_STAR_AREA = Color.argb(50, 0, 0, 255);

    /**
     * @author Cyril Mottier
     */
    public interface OnLargeTouchableAreasListener {
        /**
         * Called when the selection state changed
         * 
         * @param view The {@link LargeTouchableAreasView} whose selection state
         *            changed
         * @param selected The new selection state
         */
        void onSelected(LargeTouchableAreasView view, boolean selected);

        /**
         * Called when the selection starred changed
         * 
         * @param view The {@link LargeTouchableAreasView} whose starred state
         *            changed
         * @param selected The new starred state
         */
        void onStarred(LargeTouchableAreasView view, boolean starred);
    }

    /**
     * An association of a color and a Rect that will be used only for debug
     * purposes. This will let us draw a color over the area that forward
     * MotionEvent to a delegate View.
     * 
     * @author Cyril Mottier
     */
    private static class TouchDelegateRecord {
        public Rect rect;
        public int color;

        public TouchDelegateRecord(Rect _rect, int _color) {
            rect = _rect;
            color = _color;
        }
    }

    private final ArrayList<TouchDelegateRecord> mTouchDelegateRecords = new ArrayList<LargeTouchableAreasView.TouchDelegateRecord>();
    private final Paint mPaint = new Paint();

    private ImageButton mSelectButton;
    private ImageButton mStarButton;
    private TextView mTextView;

    private TouchDelegateGroup mTouchDelegateGroup;
    private OnLargeTouchableAreasListener mOnLargeTouchableAreasListener;

    private int mTouchAddition;

    private boolean mIsStarred;
    private boolean mIsSelected;

    private int mPreviousWidth = -1;
    private int mPreviousHeight = -1;

    public LargeTouchableAreasView(Context context) {
        super(context);
        init(context);
    }

    public LargeTouchableAreasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        setOrientation(LinearLayout.HORIZONTAL);
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        mTouchDelegateGroup = new TouchDelegateGroup(this);
        mPaint.setStyle(Style.FILL);

        final float density = context.getResources().getDisplayMetrics().density;
        mTouchAddition = (int) (density * TOUCH_ADDITION + 0.5f);

        LayoutInflater.from(context).inflate(R.layout.large_touchable_areas_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mSelectButton = (ImageButton) findViewById(R.id.btn_select);
        mSelectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemViewSelected(!mIsSelected);
                if (mOnLargeTouchableAreasListener != null) {
                    mOnLargeTouchableAreasListener.onSelected(LargeTouchableAreasView.this, mIsSelected);
                }
            }
        });

        mStarButton = (ImageButton) findViewById(R.id.btn_star);
        mStarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemViewStarred(!mIsStarred);
                if (mOnLargeTouchableAreasListener != null) {
                    mOnLargeTouchableAreasListener.onStarred(LargeTouchableAreasView.this, mIsStarred);
                }
            }
        });

        mTextView = (TextView) findViewById(R.id.content);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        final int width = r - l;
        final int height = b - t;

        /*
         * We can't use onSizeChanged here as this is called before the layout
         * of child View is actually done ... Because we need the size of some
         * child children we need to check for View size change manually
         */
        if (width != mPreviousWidth || height != mPreviousHeight) {

            mPreviousWidth = width;
            mPreviousHeight = height;

            mTouchDelegateGroup.clearTouchDelegates();

            //@formatter:off
            addTouchDelegate(
                    new Rect(0, 0, mSelectButton.getWidth() + mTouchAddition, height),
                    COLOR_SELECT_AREA,
                    mSelectButton);
            
            addTouchDelegate(
                    new Rect(width - mStarButton.getWidth() - mTouchAddition, 0, width, height),
                    COLOR_STAR_AREA,
                    mStarButton);
            //@formatter:on

            setTouchDelegate(mTouchDelegateGroup);
        }
    }

    private void addTouchDelegate(Rect rect, int color, View delegateView) {
        mTouchDelegateGroup.addTouchDelegate(new TouchDelegate(rect, delegateView));
        mTouchDelegateRecords.add(new TouchDelegateRecord(rect, color));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        for (TouchDelegateRecord record : mTouchDelegateRecords) {
            mPaint.setColor(record.color);
            canvas.drawRect(record.rect, mPaint);
        }
        super.dispatchDraw(canvas);
    }

    /**
     * Register a listener to be notified of changes on this item view.
     * 
     * @param listener The listener to set
     */
    public void setOnLargeTouchableAreasListener(OnLargeTouchableAreasListener listener) {
        mOnLargeTouchableAreasListener = listener;
    }

    /**
     * Returns the underlying {@link TextView}.
     */
    public TextView getTextView() {
        return mTextView;
    }

    /**
     * Select/unselect the view.
     * 
     * @param selected The new selection state
     */
    public void setItemViewSelected(boolean selected) {
        if (mIsSelected != selected) {
            mIsSelected = selected;
            mSelectButton.setImageResource(mIsSelected ? R.drawable.btn_check_on_normal : R.drawable.btn_check_off_normal);
        }
    }

    /**
     * Star/unstar the view.
     * 
     * @param starred The new starred state
     */
    public void setItemViewStarred(boolean starred) {
        if (mIsStarred != starred) {
            mIsStarred = starred;
            mStarButton.setImageResource(mIsStarred ? R.drawable.btn_star_on_normal : R.drawable.btn_star_off_normal);
        }
    }
}
