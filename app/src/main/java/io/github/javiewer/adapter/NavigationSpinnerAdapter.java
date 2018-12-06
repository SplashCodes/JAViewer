package io.github.javiewer.adapter;

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.javiewer.R;

/**
 * Project: JAViewer
 */

public class NavigationSpinnerAdapter<T> extends ArrayAdapter<T> {

    private final LayoutInflater mLayoutInflater;

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource) {
        this(context, resource, 0, new ArrayList<T>());
    }

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        this(context, resource, textViewResourceId, new ArrayList<T>());
    }

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull T[] objects) {
        this(context, resource, 0, Arrays.asList(objects));
    }

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull T[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));
    }

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        this(context, resource, 0, objects);
    }

    public NavigationSpinnerAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<T> objects) {
        super(context, resource, textViewResourceId, objects);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
            view = mLayoutInflater.inflate(R.layout.view_drop_down, parent, false);
            view.setTag("DROPDOWN");
        }

        TextView textView = (TextView) view.findViewById(R.id.dropdown_text);
        textView.setText(getItem(position).toString());

        return view;
    }
}
