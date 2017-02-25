package com.nopu70.myadapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nopu70.dict.R;
import com.nopu70.tools.Word;

import java.util.List;
import java.util.Map;

/**
 * Created by nopu70 on 16-3-1.
 */
public class WorldsAdapter extends RecyclerView.Adapter{

    public interface OnRecyclerViewListener{
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }

    private LayoutInflater inflater;
    List<Map<String, String>> words;
    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener){
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public WorldsAdapter(List<Map<String, String>> words){
        this.words = words;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recucler_item, null);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WordViewHolder wordViewHolder = (WordViewHolder)holder;
        wordViewHolder.position = position;
        Map<String, String> w = words.get(position);
        wordViewHolder.word.setText(w.get("word"));
        wordViewHolder.tranWord.setText(w.get("detail"));
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        TextView word, tranWord;
        View root;
        int position;
        public WordViewHolder(View itemView) {
            super(itemView);
            word = (TextView)itemView.findViewById(R.id.recucler_item_word);
            tranWord = (TextView)itemView.findViewById(R.id.recucler_item_tran_word);
            root = itemView.findViewById(R.id.recucler_item_root);
            root.setOnClickListener(this);
            root.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null!=onRecyclerViewListener){
                onRecyclerViewListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null!=onRecyclerViewListener){
                onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }
}

