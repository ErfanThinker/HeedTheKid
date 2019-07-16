package net.crowmaster.cardasmarto.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.adapters.HistoryDebugAdapter;
import net.crowmaster.cardasmarto.adapters.HistoryRowAdapter;
import net.crowmaster.cardasmarto.entities.HistoryDetailedEntity;
import net.crowmaster.cardasmarto.utils.DBHelper;
import net.crowmaster.cardasmarto.widgets.DividerItemDecoration;

import java.util.ArrayList;

public class DebugFragment extends Fragment implements HistoryRowAdapter.IOnListItemClickListener, HistoryDebugAdapter.IOnListItemClickListener /*implements LoaderManager.LoaderCallbacks<Cursor>*/ {
    private final int LOADER_ID = 2077;
    RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    TextView emptyTv;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

    }

    private class AsyncItemsLoader extends AsyncTask<Activity, Void, ArrayList<HistoryDetailedEntity>> {

        @Override
        protected ArrayList<HistoryDetailedEntity> doInBackground(Activity... activities) {
            return new DBHelper(activities[0]).getRecentRecords();
        }

        @Override
        protected void onPostExecute(ArrayList<HistoryDetailedEntity> historyDetailedEntity) {
            super.onPostExecute(historyDetailedEntity);
            HistoryDebugAdapter mAdapter = new HistoryDebugAdapter(historyDetailedEntity, DebugFragment.this);
            mAdapter.setHasStableIds(false);

            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setAdapter(mAdapter);

            if (historyDetailedEntity.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                emptyTv.setVisibility(View.VISIBLE);
            }
            else {
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyTv.setVisibility(View.GONE);
            }
        }
    }

//    @Override
//    public void onDetach() {
//        getLoaderManager().destroyLoader(LOADER_ID);
//        super.onDetach();
//    }
//
//    @NonNull
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
//        return new CursorLoader(getActivity(),
//                DebugListContentProvider.Constants.DebugURL, null, null, null, DBContract.DataTable.COLUMN_SERVER_TIME + " DESC LIMIT 100");
//    }
//
//    @Override
//    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
//        new HistoryRecordLoader( data).execute();
//    }
//
//    @Override
//    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
////        adapter.setData(null);
//    }
//
//    private class HistoryRecordLoader  extends AsyncTask<Void,Void, ArrayList<HistoryDetailedEntity>> {
//        Cursor cursor;
//        public HistoryRecordLoader(Cursor c){
//            cursor = c;
//        }
//
//        @Override
//        protected ArrayList<HistoryDetailedEntity> doInBackground(Void... voids) {
//            //Log.e("doInBack", Integer.toString(cursor.getCount()));
//            ArrayList<HistoryDetailedEntity> records=new ArrayList<HistoryDetailedEntity>();
//            if(cursor!=null && !cursor.isClosed() && cursor.moveToFirst()) {
//                int[] indices = new int[]{(cursor.getColumnIndex(DBContract.DataTable.COLUMN_CLIENT_TIME)),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_SERVER_TIME),
//                        cursor.getColumnIndex(DBContract.DataTable.BATTERY_LVL),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_X),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_Y),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_Z),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_1),
//                        cursor.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_2),
//                };
//                while (!cursor.isAfterLast()) {
//                    records.add(new HistoryDetailedEntity(cursor.getLong(indices[0]),
//                            cursor.getLong(indices[1]),
//                            cursor.getLong(indices[2]),
//                            cursor.getLong(indices[3]),
//                            cursor.getLong(indices[4]),
//                            cursor.getLong(indices[5]),
//                            cursor.getLong(indices[6]),
//                            cursor.getLong(indices[7])));
//                    cursor.moveToNext();
//                }
//
//            }
//            return records;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<HistoryDetailedEntity> itemList) {
//            super.onPostExecute(itemList);
//            // show in the view
//        }
//    }
}
