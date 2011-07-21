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

import com.cyrilmottier.android.listviewtipsandtricks.data.Cheeses;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Demo how to correctly handle the empty cas with {@link ListView} /
 * {@link AdapterView}.
 * 
 * @author Cyril Mottier
 */
public class EmptyListActivity extends ListActivity {

    private static final String EMPTY[] = {};
    private static final String CHEESES[] = Cheeses.CHEESES;

    private CheeseAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_list);

        mAdapter = new CheeseAdapter(CHEESES);
        setListAdapter(mAdapter);
    }

    public void onSetEmpty(View v) {
        mAdapter.changeData(EMPTY);
    }

    public void onSetData(View v) {
        mAdapter.changeData(CHEESES);
    }

    private class CheeseAdapter extends BaseAdapter {

        private String[] mData;

        public CheeseAdapter(String[] data) {
            mData = data;
        }

        public void changeData(String[] data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.text_item, parent, false);
            }

            ((TextView) convertView).setText(getItem(position));

            return convertView;
        }
    }
}
