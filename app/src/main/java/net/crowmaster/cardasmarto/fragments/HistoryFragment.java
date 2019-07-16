package net.crowmaster.cardasmarto.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.adapters.HistoryRowAdapter;
import net.crowmaster.cardasmarto.entities.HistorySimpleEntity;
import net.crowmaster.cardasmarto.utils.DBHelper;
import net.crowmaster.cardasmarto.widgets.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by root on 7/16/16.
 */
public class HistoryFragment extends Fragment implements HistoryRowAdapter.IOnListItemClickListener {
    RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    TextView emptyTv;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llm = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.history_lv);
        emptyTv = (TextView) view.findViewById(R.id.empty_histroy_tv);
        llm.setOrientation(RecyclerView.VERTICAL);
        llm.setSmoothScrollbarEnabled(true);

        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        new AsyncItemsLoader().execute(getActivity());

    }

    @Override
    public void onClickDetected(View view) {
        long sessionSerial = (long) view.getTag();
        //((MainActivity)getActivity()).requestHistory(sessionSerial);
    }

    private class AsyncItemsLoader extends AsyncTask<Activity, Void, ArrayList<HistorySimpleEntity>> {

        @Override
        protected ArrayList<HistorySimpleEntity> doInBackground(Activity... activities) {
            return new DBHelper(activities[0]).getSimpleHistoryList();
        }

        @Override
        protected void onPostExecute(ArrayList<HistorySimpleEntity> historySimpleEntities) {
            super.onPostExecute(historySimpleEntities);
            HistoryRowAdapter mAdapter = new HistoryRowAdapter(historySimpleEntities, HistoryFragment.this);
            mAdapter.setHasStableIds(false);

            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setAdapter(mAdapter);

            if (historySimpleEntities.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                emptyTv.setVisibility(View.VISIBLE);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyTv.setVisibility(View.GONE);
            }
        }
    }
}
