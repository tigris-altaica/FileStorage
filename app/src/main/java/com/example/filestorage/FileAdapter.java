package com.example.filestorage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private final ArrayList<String> localDataSet;

    interface OnItemCheckListener {
        void onItemCheck(String item);
        void onItemUncheck(String item);
    }
    private final OnItemCheckListener onItemClick;

    private boolean resetAll = false;

    public void resetAll(){
        resetAll = true;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final CheckBox isSelected;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            isSelected = view.findViewById(R.id.isSelected);
        }

        public TextView getTextView() {
            return name;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            isSelected.setOnClickListener(onClickListener);
        }
    }

    public FileAdapter(ArrayList<String> dataSet, OnItemCheckListener onItemCheckListener) {
        localDataSet = dataSet;
        onItemClick = onItemCheckListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.file_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String file = localDataSet.get(position);
        viewHolder.getTextView().setText(file);

        if (resetAll)
            viewHolder.isSelected.setChecked(false);

        viewHolder.setOnClickListener(v -> {
            if (viewHolder.isSelected.isChecked()) {
                onItemClick.onItemCheck(file);
            } else {
                onItemClick.onItemUncheck(file);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
