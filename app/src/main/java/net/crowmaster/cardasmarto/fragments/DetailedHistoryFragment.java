package net.crowmaster.cardasmarto.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by root on 7/17/16.
 */
public class DetailedHistoryFragment extends Fragment {
    private long sessionSerial;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sessionSerial = this.getArguments().getLong("sessionSerial");
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
