package com.tanarro.iconlistpreference;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * based on
 * http://stackoverflow.com/questions/4549746/custom-row-in-a-listpreference
 * 
 * @author atanarro
 * 
 */
public class IconListPreference extends ListPreference {
    public final static int ICON_SIDE_LEFT = 0;
    public final static int ICON_SIDE_RIGHT = 1;
    public final static int ICON_SIDE_START = 2;
    public final static int ICON_SIDE_END = 3;

    private boolean updateIcon;
    private int iconSide;
    private Drawable mIcon;
    private ImageView imageView; // only used if Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
    private Context mContext;
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

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconListPreference, defStyle, 0);
        setIcon(a.getDrawable(R.styleable.IconListPreference_prefIcon));
        setUpdateIcon(a.getBoolean(R.styleable.IconListPreference_updateIcon, true));
        setIconSide(a.getInt(R.styleable.IconListPreference_iconSide, ICON_SIDE_LEFT));

        int entryIconsResId = a.getResourceId(R.styleable.IconListPreference_entryIcons, -1);
        if (entryIconsResId != -1) {
            setEntryIcons(entryIconsResId);
        }

        a.recycle();

//        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                int index = findIndexOfValue(newValue.toString());
//                if (index != -1) {
//                    setValueIndex(index);
//                }
//                return true;
//            }
//        });
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        final int index = getValueIndex();
        if (index > -1) {
            setValueIndex(index);
        }
    }

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

    public void setIcon(int iconResId) {
        iconResId = iconResId != 0 ? iconResId : R.drawable.null_drawable;
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

    public void setUpdateIcon(boolean update) {
        updateIcon = update;
    }

    public void setIconSide(final int side) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            iconSide = side;
        } else {
            if (side == ICON_SIDE_START) {
                iconSide = ICON_SIDE_LEFT;
            } else if (side == ICON_SIDE_END) {
               iconSide = ICON_SIDE_RIGHT;
            } else {
                iconSide = side;
            }
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
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        final CharSequence[] entries = getEntries();
        if (mEntryIcons != null && entries.length != mEntryIcons.length) {
            throw new IllegalStateException("IconListPreference requires the icons entries array be the same length than entries or null");
        }
    }

    @Override
    protected void showDialog(final Bundle state) {
        super.showDialog(state);
        final AlertDialog dialog = (AlertDialog) getDialog();
        final ListView listView = dialog.getListView();
        final ListAdapter adapter = listView.getAdapter();
        final WrapperListAdapterImpl wrapperAdapter = createWrapperAdapter(adapter);

        // Adjust the selection because resetting the adapter loses the selection.
        final int selectedPosition = getValueIndex();
        listView.setAdapter(wrapperAdapter);
        if (selectedPosition != -1) {
            listView.setItemChecked(selectedPosition, true);
            listView.setSelection(selectedPosition);
        }
    }

    public void show() {
        showDialog(null);
    }

    private WrapperListAdapterImpl createWrapperAdapter(final ListAdapter origAdapter) {
        return new IconListPreferenceAdapter(origAdapter);
    }

    class IconListPreferenceAdapter extends WrapperListAdapterImpl {

        public IconListPreferenceAdapter(final ListAdapter origAdapter) {
            super(origAdapter);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private int getViewLayoutDirection(final View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                switch (view.getLayoutDirection()) {
                    case View.LAYOUT_DIRECTION_LTR: return  1;
                    case View.LAYOUT_DIRECTION_RTL: return -1;
                }
//                final int dir = view.getTextDirection();
//                switch (dir) {
//                    case View.TEXT_DIRECTION_INHERIT:          return  0;
//                    case View.TEXT_DIRECTION_FIRST_STRONG:     return  0;
//                    case View.TEXT_DIRECTION_ANY_RTL:          return -1;
//                    case View.TEXT_DIRECTION_LTR:              return  1;
//                    case View.TEXT_DIRECTION_RTL:              return -1;
//                    case View.TEXT_DIRECTION_LOCALE:           return  0;
//                    case View.TEXT_DIRECTION_FIRST_STRONG_LTR: return  1;
//                    case View.TEXT_DIRECTION_FIRST_STRONG_RTL: return -1;
//                }
            }
            return 1;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final View view = super.getView(position, convertView, parent);
            if (mEntryIcons != null) {
                final TextView text = (TextView)view.findViewById(android.R.id.text1);
                text.setCompoundDrawablePadding(24);
//                final int dir = getViewLayoutDirection(text);
//                int actualIconSide = iconSide;
                switch (iconSide) {
                    case ICON_SIDE_START:
                        text.setCompoundDrawablesRelativeWithIntrinsicBounds(mEntryIcons[position], 0, 0, 0);
                        break;
                    case ICON_SIDE_END:
                        text.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, mEntryIcons[position], 0);
                        break;
                    case ICON_SIDE_RIGHT:
                        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, mEntryIcons[position], 0);
                        break;
                    case ICON_SIDE_LEFT:
                    default:
                        text.setCompoundDrawablesWithIntrinsicBounds(mEntryIcons[position], 0, 0, 0);
                        break;
                }
//                switch (iconSide) {
//                    case ICON_SIDE_END:
//                        actualIconSide = dir == 1 ? ICON_SIDE_RIGHT : ICON_SIDE_LEFT;
//                        break;
//                    case ICON_SIDE_START:
//                    case ICON_SIDE_AUTO:
//                        actualIconSide = dir == 1 ? ICON_SIDE_LEFT : ICON_SIDE_RIGHT;
//                        break;
//                    default:
//                }
//                switch (actualIconSide) {
//                    case ICON_SIDE_RIGHT:
//                        text.setCompoundDrawablesWithIntrinsicBounds(0, 0, mEntryIcons[position], 0);
//                        break;
//                    case ICON_SIDE_LEFT:
//                    default:
//                        text.setCompoundDrawablesWithIntrinsicBounds(mEntryIcons[position], 0, 0, 0);
//                        break;
//                }
//                Log.v("DEMO", "defaultLocale: " + Locale.getDefault()
//                        + ", API: " + Build.VERSION.SDK_INT
//                        + ", dir: " + dir
//                        + ", iconSide: " + iconSide
//                        + ", actualIconSide: " + actualIconSide);
            }
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.requestFocus();

                    IconListPreference.this.callChangeListener(getEntryValues()[position]);
                    setValueIndex(position);

                    Dialog mDialog = getDialog();
                    mDialog.dismiss();
                }
            });
            return view;
        }
    }
}
