/*
 * Copyright (C) 2011 Cyril Mottier (http://www.cyrilmottier.com)
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

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static com.cyrilmottier.android.listviewtipsandtricks.data.Cheeses.CHEESES;

/**
 * Demo how to add clickable accessories to itemviews. The itemview layout is
 * based on a LinearLayout containing a {@link CheckBox}, a {@link TextView} and
 * a {@link Button}.
 * 
 * @author Cyril Mottier
 */
public class AccessoriesListActivity extends ListActivity {

    private static final String STAR_STATES = "listviewtipsandtricks:star_states";

    private AccessoriesAdapter mAdapter;
    private boolean[] mStarStates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The following code allows the Activity to restore its state after it
        // has been killed by the system (low memory condition, configuration
        // change, etc.)
        if (savedInstanceState != null) {
            mStarStates = savedInstanceState.getBooleanArray(STAR_STATES);
        } else {
            mStarStates = new boolean[CHEESES.length];
        }
        
        mAdapter = new AccessoriesAdapter();
        setListAdapter(mAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBooleanArray(STAR_STATES, mStarStates);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        showMessage(getString(R.string.you_want_info_about_format, CHEESES[position]));
    }

    /**
     * A pretty basic ViewHolder used to keep references on children
     * {@link View}s.
     * 
     * @author Cyril Mottier
     */
    private static class AccessoriesViewHolder {
        public CheckBox star;
        public TextView content;
    }

    /**
     * The Adapter used in the demonstration.
     * 
     * @author Cyril Mottier
     */
    private class AccessoriesAdapter extends BaseAdapter {

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

            AccessoriesViewHolder holder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.accessories_item, parent, false);

                holder = new AccessoriesViewHolder();
                holder.star = (CheckBox) convertView.findViewById(R.id.btn_star);
                holder.content = (TextView) convertView.findViewById(R.id.content);

                ((Button) convertView.findViewById(R.id.btn_buy)).setOnClickListener(mBuyButtonClickListener);

                convertView.setTag(holder);
            } else {
                holder = (AccessoriesViewHolder) convertView.getTag();
            }

            /*
             * The Android API provides the OnCheckedChangeListener interface
             * and its onCheckedChanged(CompoundButton buttonView, boolean
             * isChecked) method. Unfortunately, this implementation suffers
             * from a big problem: you can't determine whether the checking
             * state changed from code or because of a user action. As a result
             * the only way we have is to prevent the CheckBox from callbacking
             * our listener by temporary removing the listener.
             */
            holder.star.setOnCheckedChangeListener(null);
            holder.star.setChecked(mStarStates[position]);
            holder.star.setOnCheckedChangeListener(mStarCheckedChanceChangeListener);

            holder.content.setText(CHEESES[position]);

            return convertView;
        }
    }

    /**
     * Quickly shows a message to the user using a {@link Toast}.
     * 
     * @param message The message to show
     */
    private void showMessage(String message) {
        Toast.makeText(AccessoriesListActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private OnClickListener mBuyButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = getListView().getPositionForView(v);
            if (position != ListView.INVALID_POSITION) {
                showMessage(getString(R.string.you_want_to_buy_format, CHEESES[position]));
            }
        }
    };

    private OnCheckedChangeListener mStarCheckedChanceChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int position = getListView().getPositionForView(buttonView);
            if (position != ListView.INVALID_POSITION) {
                mStarStates[position] = isChecked;
            }
        }
    };
}
