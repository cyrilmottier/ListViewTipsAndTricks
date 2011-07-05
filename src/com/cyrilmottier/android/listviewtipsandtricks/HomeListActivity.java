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

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Lists all ListView tips & tricks available in the AndroidManifest.
 * 
 * @author Cyril Mottier
 */
public class HomeListActivity extends ListActivity {

    private static final String CATEGORY_SAMPLE_CODE = "com.cyrilmottier.android.listviewtipsandtricks.SAMPLE_CODE";

    private ArrayAdapter<Tip> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ArrayAdapter<Tip>(this, android.R.layout.simple_list_item_1);

        // The following code looks for activities with the given category. All
        // activities that responds to the given Intent will be added to the
        // list of all tips & tricks.
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(CATEGORY_SAMPLE_CODE);

        final PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo resolveInfo : resolveInfos) {

            final ActivityInfo ai = resolveInfo.activityInfo;

            String activityName = ai.name;
            int lastIndex = activityName.lastIndexOf(".");
            if (lastIndex > 0 && lastIndex < activityName.length()) {
                activityName = activityName.substring(lastIndex + 1, activityName.length());
            }

            final Intent intent = new Intent();
            intent.setClassName(ai.applicationInfo.packageName, ai.name);

            mAdapter.add(new Tip(activityName, intent));
        }

        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Tip tip = mAdapter.getItem(position);
        if (tip != null) {
            startActivity(tip.intent);
        }
    }
    
    /**
     * Represents a single ListView tip & trick in the list of all tips. A
     * {@link Tip} is basically a title and an Intent to the Activity demo-ing
     * the tip & trick
     * 
     * @author Cyril Mottier
     */
    private static class Tip {
        public String title;
        public Intent intent;

        public Tip(String title, Intent intent) {
            this.title = title;
            this.intent = intent;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
