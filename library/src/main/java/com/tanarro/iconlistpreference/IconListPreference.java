package com.tanarro.iconlistpreference;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * based on
 * http://stackoverflow.com/questions/4549746/custom-row-in-a-listpreference
 * 
 * @author atanarro
 * 
 */
public class IconListPreference extends ListPreference {

    private Drawable mIcon;
    private IconListPreferenceScreenAdapter iconListPreferenceAdapter = null;
    private Context mContext;
    private LayoutInflater mInflater;
    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private int[] mEntryIcons = null;

    public IconListPreference(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconListPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs);
//        setLayoutResource(R.layout.preference_icon);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconListPreference, defStyle, 0);
        setIcon(a.getDrawable(R.styleable.IconListPreference_prefIcon));

        int entryIconsResId = a.getResourceId(R.styleable.IconListPreference_entryIcons, -1);
        if (entryIconsResId != -1) {
            setEntryIcons(entryIconsResId);
        }
        mInflater = LayoutInflater.from(context);

        a.recycle();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            final String persistedValue = getPersistedString((String)defaultValue);
            setValue(persistedValue);
        } else {
            setValue((String)defaultValue);
        }
        final int index = getValueIndex();
        if (index > -1) {
            setValueIndex(index);
        }
    }

    /**
     * Sets the value to the given index from the entry values.
     *
     * @param index The index of the value to set.
     */
    @Override
    public void setValueIndex(final int index) {
        super.setValueIndex(index);
        if (mEntryIcons != null) {
            setIcon(mEntryIcons[index]);
        }
    }

    private int getValueIndex() {
        return findIndexOfValue(getValue());
    }

    @Override
    public void onBindView(final View view) {
        super.onBindView(view);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            final ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
            if (imageView != null && mIcon != null) {
                imageView.setImageDrawable(mIcon);
            }
        }
    }

//    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void setIcon(final Drawable icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setIcon(icon);
        } else {
            if ((icon == null && mIcon != null) || (icon != null && !icon.equals(mIcon))) {
                mIcon = icon;
                notifyChanged();
            }
        }
    }

    public Drawable getIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return super.getIcon();
        } else {
            return mIcon;
        }
    }

    public void setEntryIcons(int[] entryIcons) {
        mEntryIcons = entryIcons;
        final int index = getValueIndex();
        if (index > -1) {
            setValueIndex(index);
        }
    }

    public void setEntryIcons(int entryIconsResId) {
        TypedArray icons_array = mContext.getResources().obtainTypedArray(entryIconsResId);
        int[] icon_ids_array = new int[icons_array.length()];
        for (int i = 0; i < icons_array.length(); i++) {
            icon_ids_array[i] = icons_array.getResourceId(i, -1);
        }
        setEntryIcons(icon_ids_array);
        icons_array.recycle();
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        entries = getEntries();
        entryValues = getEntryValues();

        if (entries.length != entryValues.length) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        if (mEntryIcons != null && entries.length != mEntryIcons.length) {
            throw new IllegalStateException("IconListPreference requires the icons entries array be the same length than entries or null");
        }

        iconListPreferenceAdapter = new IconListPreferenceScreenAdapter(mContext);

        if (mEntryIcons != null) {
            builder.setAdapter(iconListPreferenceAdapter, null);
        }
    }

    private class IconListPreferenceScreenAdapter extends BaseAdapter {
        public IconListPreferenceScreenAdapter(Context context) {

        }

        public int getCount() {
            return entries.length;
        }

        class CustomHolder {
            private TextView text = null;
            private RadioButton rButton = null;

            CustomHolder(View row, int position) {
                text = (TextView) row.findViewById(R.id.image_list_view_row_text_view);
                text.setText(entries[position]);

                rButton = (RadioButton) row.findViewById(R.id.image_list_view_row_radio_button);
                rButton.setId(position);
                rButton.setClickable(false);
                rButton.setChecked(getValueIndex() == position);

                if (mEntryIcons != null) {
                    text.setText(" " + text.getText());
                    text.setCompoundDrawablesWithIntrinsicBounds(mEntryIcons[position], 0, 0, 0);
                }
            }
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            CustomHolder holder = null;
            final int p = position;
            row = mInflater.inflate(R.layout.image_list_preference_row, parent, false);
            holder = new CustomHolder(row, position);

            row.setTag(holder);

            // row.setClickable(true);
            // row.setFocusable(true);
            // row.setFocusableInTouchMode(true);
            row.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.requestFocus();

                    Dialog mDialog = getDialog();
                    mDialog.dismiss();

                    IconListPreference.this.callChangeListener(entryValues[p]);
                    setValueIndex(p);
                }
            });

            return row;
        }

    }

}
