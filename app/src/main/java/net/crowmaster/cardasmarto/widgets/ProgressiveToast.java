package net.crowmaster.cardasmarto.widgets;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import net.crowmaster.cardasmarto.R;


public class ProgressiveToast {
	static AlertDialog.Builder ToasticalertBuilder;
	static AlertDialog ToasticalertDialog;
    private static ProgressiveToast instance = null;
//	Activity mActivity;
//	public ProgressiveToast(Activity a){
//		mActivity=a;
//	}


    protected ProgressiveToast() {}

    public static ProgressiveToast getInstance() {
        if(instance == null) {
            synchronized (ProgressiveToast.class) {
                if(instance == null)
                    instance = new ProgressiveToast();
            }
        }
        return instance;
    }

    /*
    pass -1 for color if you don't want to specify it
     */
	public void show(Activity a, String message,@Nullable int color){
        if(ToasticalertDialog == null ||
                (ToasticalertDialog != null && !ToasticalertDialog.isShowing())) {
            LayoutInflater inflater = (LayoutInflater) a.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.toast, null);

        /*pb.setIndeterminate(true);
        pb.getIndeterminateDrawable().setColorFilter(0xFF99CC00,
                android.graphics.PorterDuff.Mode.MULTIPLY);*/
            TextView mTextView = (TextView) layout.findViewById(R.id.customProgeressiveToast);
            String toBeShown = (message == null) ? "Please be patient while SSN is interacting with server..." : message;
            mTextView.setText(toBeShown);
            //mTextView.setTypeface(AppConfig.getInstance().getPrimaryTypeface());

            if(color != -1) {
                ProgressBar pb = (ProgressBar) layout.findViewById(R.id.progressBar);
                pb.getIndeterminateDrawable()
                        .setColorFilter(color, PorterDuff.Mode.SRC_IN);
                //mTextView.setTextColor(color);
            }
            ToasticalertBuilder = new AlertDialog.Builder(a);
            ToasticalertBuilder.setCancelable(false);
            ToasticalertDialog = ToasticalertBuilder.setView(layout).create();
            ToasticalertDialog.getWindow().
                    setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            ToasticalertDialog.show();
        }
	}
	public void dismiss(){
		if(ToasticalertDialog!=null &&
                ToasticalertDialog.isShowing())ToasticalertDialog.dismiss();
	}
		
}
