package com.stypox.mastercom_workbook.view.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemArrayAdapter<D, H extends ItemHolder<D>> extends RecyclerView.Adapter<H> {
    private int resource;
    private List<D> dataItems;
    private ItemHolderFactory<H> factory;

    public ItemArrayAdapter(int resource, List<D> dataItems, ItemHolderFactory<H> factory) {
        this.resource = resource;
        this.dataItems = dataItems;
        this.factory = factory;
    }

    @NonNull
    @Override
    public H onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return factory.buildItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull H holder, int position) {
        D dataItem = dataItems.get(position);
        holder.updateItemData(dataItem);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }

    public void sort(Comparator<D> comparator) {
        Collections.sort(dataItems, comparator);
        notifyDataSetChanged();
    }
}
