package com.stypox.mastercom_workbook.view.holder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemArrayAdapter<D> extends RecyclerView.Adapter<ItemHolder<D>> {
    public interface OnItemClickListener<D> {
        void onClick(D dataItem);
    }


    private final int resource;
    private List<D> dataItems;
    private final ItemHolderFactory<D> factory;
    @Nullable private OnItemClickListener<D> onItemClickListener;


    public ItemArrayAdapter(int resource, List<D> dataItems, ItemHolderFactory<D> factory) {
        this.resource = resource;
        this.dataItems = dataItems;
        this.factory = factory;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<D> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    void onItemClick(D dataItem) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(dataItem);
        }
    }

    public void sort(Comparator<D> comparator) {
        Collections.sort(dataItems, comparator);
        //noinspection NotifyDataSetChanged
        notifyDataSetChanged();
    }

    public void setDataItems(final List<D> dataItems) {
        this.dataItems = dataItems;
        //noinspection NotifyDataSetChanged
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ItemHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        return factory.buildItemHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder<D> holder, int position) {
        D dataItem = dataItems.get(position);
        holder.updateItemData(dataItem);
    }

    @Override
    public int getItemCount() {
        return dataItems.size();
    }
}
