package com.kabanietzsche.admin.calc2;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;

    public HistoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        ArrayList<ResultModel> results = (ArrayList<ResultModel>) ResultModel.listAll(ResultModel.class);

        recyclerView = (RecyclerView) v.findViewById(R.id.history_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        HistoryAdapter adapter = new HistoryAdapter(results);
        recyclerView.setAdapter(adapter);

        return v;
    }

}
