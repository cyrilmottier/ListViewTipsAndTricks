package com.cyrilmottier.android.listviewtipsandtricks.util;

import java.lang.ref.WeakReference;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * A particular {@link AsyncQueryHandler} allowing clients to be notified via a
 * listener. The {@link NotifyingAsyncQueryHandler} also make sure no strong
 * reference is kept on the given listener.
 * 
 * @author Cyril Mottier
 */
public class NotifyingAsyncQueryHandler extends AsyncQueryHandler {

    private WeakReference<NotifyingAsyncQueryListener> mListener;

    /**
     * Client may use this to listen to completed query operations.
     * 
     * @author Cyril Mottier
     */
    public interface NotifyingAsyncQueryListener {
        void onQueryComplete(int token, Object cookie, Cursor cursor);
    }

    public NotifyingAsyncQueryHandler(ContentResolver resolver, NotifyingAsyncQueryListener listener) {
        super(resolver);
        setQueryListener(listener);
    }

    /**
     * Assign the given {@link NotifyingAsyncQueryListener}.
     */
    public void setQueryListener(NotifyingAsyncQueryListener listener) {
        mListener = (listener != null) ? new WeakReference<NotifyingAsyncQueryListener>(listener) : null;
    }
    
    public void clearQueryListener() {
        mListener = null;
    }

    public void startQuery(Uri uri, String[] projection) {
        startQuery(-1, null, uri, projection, null, null, null);
    }

    public void startQuery(Uri uri, String[] projection, String sortOrder) {
        startQuery(-1, null, uri, projection, null, null, sortOrder);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        final NotifyingAsyncQueryListener listener = (mListener == null) ? null : mListener.get();
        if (listener != null) {
            listener.onQueryComplete(token, cookie, cursor);
        } else if (cursor != null) {
            cursor.close();
        }
    }
}
