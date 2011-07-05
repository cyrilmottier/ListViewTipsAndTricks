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
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cyrilmottier.android.listviewtipsandtricks.util.NotifyingAsyncQueryHandler;
import com.cyrilmottier.android.listviewtipsandtricks.util.NotifyingAsyncQueryHandler.NotifyingAsyncQueryListener;

/**
 * Shows a smart way of handling separators in {@link ListView}s. It also shows
 * some ways to boost your {@link ListView}s using techniques like 'section
 * caching', ViewHolder, CharArrayBuffer, etc.
 * 
 * @author Cyril Mottier
 */
public class SectionedListActivity extends ListActivity implements NotifyingAsyncQueryListener {

    private AudioFilesAdapter mAdapter;
    private NotifyingAsyncQueryHandler mQueryHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new AudioFilesAdapter(this, null);
        setListAdapter(mAdapter);

        // Starts querying the media provider. This is done asynchronously not
        // to possibly block the UI or even worse fire an ANR...
        mQueryHandler = new NotifyingAsyncQueryHandler(getContentResolver(), this);
        mQueryHandler.startQuery(Media.EXTERNAL_CONTENT_URI, AudioFilesQuery.PROJECTION, AudioFilesQuery.SORT_ORDER);
    }

    @Override
    protected void onDestroy() {
        // Clear any strong reference to this Activity
        mQueryHandler.clearQueryListener();
        super.onDestroy();
    }

    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if (cursor != null) {
            startManagingCursor(cursor);
        }
        mAdapter.changeCursor(cursor);
    }

    private static class AudioFilesViewHolder {
        public TextView separator;
        public TextView titleView;
        public CharArrayBuffer titleBuffer = new CharArrayBuffer(128);
        public TextView subtitleView;
        public StringBuilder subtitleBuffer = new StringBuilder();
    }

    private static class AudioFilesAdapter extends CursorAdapter {

        /**
         * State of ListView item that has never been determined.
         */
        private static final int STATE_UNKNOWN = 0;

        /**
         * State of a ListView item that is sectioned. A sectioned item must
         * display the separator.
         */
        private static final int STATE_SECTIONED_CELL = 1;

        /**
         * State of a ListView item that is not sectioned and therefore does not
         * display the separator.
         */
        private static final int STATE_REGULAR_CELL = 2;

        private final CharArrayBuffer mBuffer = new CharArrayBuffer(128);
        private int[] mCellStates;

        public AudioFilesAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            mCellStates = cursor == null ? null : new int[cursor.getCount()];
        }

        @Override
        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
            mCellStates = cursor == null ? null : new int[cursor.getCount()];
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            final AudioFilesViewHolder holder = (AudioFilesViewHolder) view.getTag();

            /*
             * Separator
             */
            boolean needSeparator = false;

            final int position = cursor.getPosition();
            cursor.copyStringToBuffer(AudioFilesQuery.TITLE, holder.titleBuffer);

            switch (mCellStates[position]) {
                case STATE_SECTIONED_CELL:
                    needSeparator = true;
                    break;

                case STATE_REGULAR_CELL:
                    needSeparator = false;
                    break;

                case STATE_UNKNOWN:
                default:
                    // A separator is needed if it's the first itemview of the
                    // ListView or if the group of the current cell is different
                    // from the previous itemview.
                    if (position == 0) {
                        needSeparator = true;
                    } else {
                        cursor.moveToPosition(position - 1);

                        cursor.copyStringToBuffer(AudioFilesQuery.TITLE, mBuffer);
                        if (mBuffer.sizeCopied > 0 && holder.titleBuffer.sizeCopied > 0 && mBuffer.data[0] != holder.titleBuffer.data[0]) {
                            needSeparator = true;
                        }

                        cursor.moveToPosition(position);
                    }

                    // Cache the result
                    mCellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                    break;
            }

            if (needSeparator) {
                holder.separator.setText(holder.titleBuffer.data, 0, 1);
                holder.separator.setVisibility(View.VISIBLE);
            } else {
                holder.separator.setVisibility(View.GONE);
            }

            /*
             * Title
             */
            holder.titleView.setText(holder.titleBuffer.data, 0, holder.titleBuffer.sizeCopied);

            /*
             * Subtitle
             */
            holder.subtitleBuffer.setLength(0);
            final String album = cursor.getString(AudioFilesQuery.ALBUM);
            if (!TextUtils.isEmpty(album)) {
                holder.subtitleBuffer.append(album);
                final String artist = cursor.getString(AudioFilesQuery.ARTIST);
                if (!TextUtils.isEmpty(artist)) {
                    holder.subtitleBuffer.append(" - ");
                    holder.subtitleBuffer.append(artist);
                }
            }
            
            if (TextUtils.isEmpty(holder.subtitleBuffer)) {
                holder.subtitleView.setVisibility(View.GONE);
            } else {
                holder.subtitleView.setVisibility(View.VISIBLE);
                holder.subtitleView.setText(holder.subtitleBuffer);
            }
            
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            View v = LayoutInflater.from(context).inflate(R.layout.audio_list_item, parent, false);

            // The following code allows us to keep a reference on the child
            // views of the item. It prevents us from calling findViewById at
            // each getView/bindView and boosts the rendering code.
            AudioFilesViewHolder holder = new AudioFilesViewHolder();
            holder.separator = (TextView) v.findViewById(R.id.separator);
            holder.titleView = (TextView) v.findViewById(R.id.title);
            holder.subtitleView = (TextView) v.findViewById(R.id.subtitle);

            v.setTag(holder);

            return v;
        }

    }

    /**
     * Keep query data in one place
     * 
     * @author Cyril Mottier
     */
    private interface AudioFilesQuery {
        String[] PROJECTION = {
                Media._ID, Media.TITLE, Media.ALBUM, Media.ARTIST
        };

        int TITLE = 1;
        int ALBUM = 2;
        int ARTIST = 3;

        String SORT_ORDER = Media.TITLE + " ASC";
    }

}
