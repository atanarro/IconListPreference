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

    private boolean updateIcon;
    private Drawable mIcon;
    private ImageView imageView; // only used if Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
    private Context mContext;
    private LayoutInflater mInflater;
    private CharSequence[] entries;
    private CharSequence[] entryValues;
    private int[] mEntryIcons;

    public IconListPreference(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconListPreference(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            setLayoutResource(R.layout.preference_icon);
        }
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconListPreference, defStyle, 0);
        setIcon(a.getDrawable(R.styleable.IconListPreference_prefIcon));
        updateIcon = a.getBoolean(R.styleable.IconListPreference_updateIcon, true);

        int entryIconsResId = a.getResourceId(R.styleable.IconListPreference_entryIcons, -1);
        if (entryIconsResId != -1) {
            setEntryIcons(entryIconsResId);
        }
        mInflater = LayoutInflater.from(context);

        a.recycle();
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
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
        if (mEntryIcons != null && updateIcon) {
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
            imageView = (ImageView)view.findViewById(android.R.id.icon);
            if (imageView != null && mIcon != null && updateIcon) {
                imageView.setImageDrawable(mIcon);
            }
        }
    }

    public void setIcon(final int iconResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setIcon(iconResId);
        } else {
            setIcon(mContext.getResources().getDrawable(iconResId));
        }
    }

//    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void setIcon(final Drawable icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            super.setIcon(icon);
        } else {
            if ((icon == null && mIcon != null) || (icon != null && !icon.equals(mIcon))) {
                mIcon = icon;
                if (imageView != null && mIcon != null) {
                    imageView.setImageDrawable(mIcon);
                }
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

        if (mEntryIcons != null) {
            final IconListPreferenceScreenAdapter iconListPreferenceAdapter = new IconListPreferenceScreenAdapter();
            builder.setAdapter(iconListPreferenceAdapter, null);
        }
    }

    private class IconListPreferenceScreenAdapter extends BaseAdapter {
        public int getCount() {
            return entries.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        class CustomHolder {
            TextView text;
            RadioButton rButton;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            CustomHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.image_list_preference_row, parent, false);
                holder = new CustomHolder();
                holder.text = (TextView)convertView.findViewById(R.id.image_list_view_row_text_view);
                holder.rButton = (RadioButton)convertView.findViewById(R.id.image_list_view_row_radio_button);
                convertView.setTag(holder);
            } else {
                holder = (CustomHolder)convertView.getTag();
            }

            holder.text.setText(entries[position]);
            if (mEntryIcons != null) {
                holder.text.setText(" " + holder.text.getText());
                holder.text.setCompoundDrawablesWithIntrinsicBounds(mEntryIcons[position], 0, 0, 0);
            }

            holder.rButton.setId(position);
            holder.rButton.setClickable(false);
            holder.rButton.setChecked(getValueIndex() == position);

            // row.setClickable(true);
            // row.setFocusable(true);
            // row.setFocusableInTouchMode(true);
            final int p = position;
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.requestFocus();

                    IconListPreference.this.callChangeListener(entryValues[p]);
                    setValueIndex(p);

                    Dialog mDialog = getDialog();
                    mDialog.dismiss();
                }
            });

            return convertView;
        }

    }

}
