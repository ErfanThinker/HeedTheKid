package net.crowmaster.cardasmarto.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.entities.HistorySimpleEntity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by root on 7/16/16.
 */

/**
 * Responsible for generating rows for history fragment {@link net.crowmaster.cardasmarto.fragments.HistoryFragment}
 */
public class HistoryRowAdapter extends RecyclerView.Adapter<HistoryRowAdapter.ViewHolder> {

    private ArrayList<HistorySimpleEntity> items;
    IOnListItemClickListener onListItemClickListener;
    private Calendar mCalendar;

    public interface IOnListItemClickListener{
        void onClickDetected(View view);
    }

    public HistoryRowAdapter(ArrayList<HistorySimpleEntity> items
            , IOnListItemClickListener onListItemClickListener){
        this.items = items;
        this.onListItemClickListener = onListItemClickListener;
        this.mCalendar = Calendar.getInstance();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_history, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClickListener.onClickDetected(view);
            }
        });

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistorySimpleEntity item = items.get(position);
        int h   = (int)(item.getDuration() /3600000);
        int m = (int)(item.getDuration() - h*3600000)/60000;
        int s= (int)(item.getDuration() - h*3600000- m*60000)/1000 ;
        String hh = h < 10 ? "0"+h: h+"";
        String mm = m < 10 ? "0"+m: m+"";
        String ss = s < 10 ? "0"+s: s+"";
        this.mCalendar.setTimeInMillis(item.getSessionSerial());
        holder.title.setText("Date: " + this.mCalendar.getTime().toString() + "\n" +
                "Child Name: " + item.getchildName() + "\nDuration : " + hh+":"+mm+":"+ss);
        holder.rootView.setTag(item.getSessionSerial());

    }

    @Override
    public int getItemCount() {
        return (items==null) ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public CircularImageView icon;
        public CardView rootView;
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.msg_tv);
            icon = view.findViewById(R.id.row_icon);
            rootView = view.findViewById(R.id.row_root);
        }
    }
}
