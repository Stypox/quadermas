package com.stypox.mastercom_workbook.view.holder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemArrayAdapter<D> extends RecyclerView.Adapter<ItemHolder<D>> {
    public interface OnItemClickListener<D> {
        void onClick(D dataItem);
    }


    private int resource;
    private List<D> dataItems;
    private ItemHolderFactory<D> factory;
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
