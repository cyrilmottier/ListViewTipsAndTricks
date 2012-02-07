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
package com.cyrilmottier.android.listviewtipsandtricks;

import static com.cyrilmottier.android.listviewtipsandtricks.data.Cheeses.CHEESES;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.cyrilmottier.android.listviewtipsandtricks.widget.LargeTouchableAreasView;
import com.cyrilmottier.android.listviewtipsandtricks.widget.LargeTouchableAreasView.OnLargeTouchableAreasListener;

/**
 * Shows how to enlarge touchable areas. This is particularly useful to ensure
 * user interaction is correctly taken into account when dealing with tiny
 * touchable areas (checkbox, star, etc.).
 * 
 * @author Cyril Mottier
 */
public class LargeTouchableAreasListActivity extends ListActivity {

    private static final String STAR_STATES = "listviewtipsandtricks:star_states";
    private static final String SELECTION_STATES = "listviewtipsandtricks:selection_states";

    private boolean[] mStarStates;
    private boolean[] mSelectionStates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LargeTouchableAreasAdapter adapter = new LargeTouchableAreasAdapter();

        // The following code allows the Activity to restore its state after it
        // has been killed by the system (low memory condition, configuration
        // change, etc.)
        if (savedInstanceState != null) {
            mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
            mSelectionStates = savedInstanceState.getBooleanArray(SELECTION_STATES);
        } else {
            mStarStates = new boolean[adapter.getCount()];
            mSelectionStates = new boolean[adapter.getCount()];
        }

        setListAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(SELECTION_STATES, mSelectionStates);
        outState.putBooleanArray(STAR_STATES, mStarStates);
    }

    /**
     * The Adapter used in the demonstration.
     * 
     * @author Cyril Mottier
     */
    private class LargeTouchableAreasAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return CHEESES.length;
        }

        @Override
        public String getItem(int position) {
            return CHEESES[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final LargeTouchableAreasView view;

            if (convertView == null) {
                view = (LargeTouchableAreasView) getLayoutInflater().inflate(R.layout.large_touchable_areas_item, parent, false);
                view.setOnLargeTouchableAreasListener(mOnLargeTouchableAreasListener);
            } else {
                view = (LargeTouchableAreasView) convertView;
            }

            view.setItemViewStarred(mStarStates[position]);
            view.setItemViewSelected(mSelectionStates[position]);
            view.getTextView().setText(getItem(position));

            return view;
        }
    }

    private OnLargeTouchableAreasListener mOnLargeTouchableAreasListener = new OnLargeTouchableAreasListener() {

        @Override
        public void onSelected(LargeTouchableAreasView view, boolean selected) {
            final int position = getListView().getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                mSelectionStates[position] = selected;
            }
        }

        @Override
        public void onStarred(LargeTouchableAreasView view, boolean starred) {
            final int position = getListView().getPositionForView(view);
            if (position != ListView.INVALID_POSITION) {
                mStarStates[position] = starred;
            }
        }
    };
}
