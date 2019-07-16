package net.crowmaster.cardasmarto.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.entities.HistoryDetailedEntity;

import java.util.ArrayList;
import java.util.Calendar;

public class HistoryDebugAdapter extends RecyclerView.Adapter<HistoryDebugAdapter.ViewHolder> {

    private ArrayList<HistoryDetailedEntity> items;
    IOnListItemClickListener onListItemClickListener;
    private Calendar mCalendar;

    public interface IOnListItemClickListener{
        void onClickDetected(View view);
    }

    public HistoryDebugAdapter(ArrayList<HistoryDetailedEntity> items
            , IOnListItemClickListener onListItemClickListener){
        this.items = items;
        this.onListItemClickListener = onListItemClickListener;
        this.mCalendar = Calendar.getInstance();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.row_debug_history, parent, false);
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
        HistoryDetailedEntity item = items.get(position);
//        int h   = (int)(item.getDuration() /3600000);
//        int m = (int)(item.getDuration() - h*3600000)/60000;
//        int s= (int)(item.getDuration() - h*3600000- m*60000)/1000 ;
//        String hh = h < 10 ? "0"+h: h+"";
//        String mm = m < 10 ? "0"+m: m+"";
//        String ss = s < 10 ? "0"+s: s+"";
        this.mCalendar.setTimeInMillis(item.getServerTime());
        holder.timeTV.setText(String.format("Time: %s", this.mCalendar.getTime().toString()));
        holder.batteryTV.setText("Battery lvl: " + item.getBatteryLvl());
        holder.batteryTV.setText("Acc_x: " + item.getAcX() + " , Acc_y: " + item.getAcY() + " , Acc_z: " + item.getAcZ());
        holder.encoderTV.setText("Encoder1: " + item.getEncoder1() + " , Encoder2: " + item.getEncoder2());
//        holder.rootView.setTag(item.getSessionSerial());

    }

    @Override
    public int getItemCount() {
        return (items==null) ? 0 : items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView timeTV;
        public TextView batteryTV;
        public TextView accTV;
        public TextView encoderTV;
        public CircularImageView icon;
        public CardView rootView;
        public ViewHolder(View view) {
            super(view);
            timeTV = (TextView) view.findViewById(R.id.time_tv);
            batteryTV = (TextView) view.findViewById(R.id.battery_tv);
            accTV = (TextView) view.findViewById(R.id.acc_tv);
            encoderTV = (TextView) view.findViewById(R.id.encoder_tv);
            icon = (CircularImageView) view.findViewById(R.id.row_icon);
            rootView = (CardView) view.findViewById(R.id.row_root);
        }
    }
}
