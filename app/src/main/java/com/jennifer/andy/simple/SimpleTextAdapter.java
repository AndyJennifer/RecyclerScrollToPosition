package com.jennifer.andy.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Author:  andy.xwt
 * Date:    2018/6/20 15:49
 * Description:
 */

public class SimpleTextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> list;
    private Context mContext;

    public SimpleTextAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.list = list;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SimpleTextHolder(LayoutInflater.from(mContext).inflate(R.layout.item_simple_text, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SimpleTextHolder textHolder = (SimpleTextHolder) holder;
        textHolder.mTextView.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private static class SimpleTextHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        SimpleTextHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_text);
        }
    }


}
