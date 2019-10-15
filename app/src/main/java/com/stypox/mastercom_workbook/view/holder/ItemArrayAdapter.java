package com.stypox.mastercom_workbook.view.holder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class ItemArrayAdapter<T> extends ArrayAdapter<T> {
    private int resource;
    private ItemHolderFactory factory;

    public ItemArrayAdapter(@NonNull Context context, int resource, @NonNull List<T> objects, ItemHolderFactory factory) {
        super(context, resource, objects);
        this.resource = resource;
        this.factory = factory;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = (ItemHolder) factory.buildItemHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.updateItemData(getItem(position));
        return convertView;
    }
}
