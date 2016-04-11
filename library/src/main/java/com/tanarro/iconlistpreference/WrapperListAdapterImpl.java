package com.tanarro.iconlistpreference;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Created by flocsy@gmail.com on 2016-03-03
 */
class WrapperListAdapterImpl implements WrapperListAdapter {
    final private ListAdapter mOrigAdapter;

    public WrapperListAdapterImpl(final ListAdapter origAdapter) {
        mOrigAdapter = origAdapter;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mOrigAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return getWrappedAdapter().areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(final int position) {
        return getWrappedAdapter().isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(final DataSetObserver observer) {
        getWrappedAdapter().registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(final DataSetObserver observer) {
        getWrappedAdapter().unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return getWrappedAdapter().getCount();
    }

    @Override
    public Object getItem(final int position) {
        return getWrappedAdapter().getItem(position);
    }

    @Override
    public long getItemId(final int position) {
        return getWrappedAdapter().getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return getWrappedAdapter().hasStableIds();
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        return getWrappedAdapter().getView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(final int position) {
        return getWrappedAdapter().getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return getWrappedAdapter().getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return getWrappedAdapter().isEmpty();
    }
}
