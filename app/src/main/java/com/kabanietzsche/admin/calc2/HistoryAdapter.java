package com.kabanietzsche.admin.calc2;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<ResultModel> results;

    public HistoryAdapter(ArrayList<ResultModel> results) {
        this.results = results;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryViewHolder historyViewHolder =
                new HistoryViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.history_item, parent, false));

        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ResultModel result = results.get(position);
        String info = result.getUserName() + holder.calculationsTextView.getContext().getString(R.string.pressed)
                + result.getButtonsTapped() + holder.calculationsTextView.getContext().getString(R.string.buttons_and_got_result)
                + Utils.formatResult(result.getResult());
        String calculations = result.getEnteredData();
        holder.infoTextView.setText(info);
        holder.calculationsTextView.setText(calculations);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView infoTextView;
        TextView calculationsTextView;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            infoTextView = (TextView) itemView.findViewById(R.id.history_info_text_view);
            calculationsTextView = (TextView) itemView.findViewById(R.id.history_calculations_text_view);
        }
    }
}
