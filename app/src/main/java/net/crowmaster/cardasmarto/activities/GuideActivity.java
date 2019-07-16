package net.crowmaster.cardasmarto.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;

import net.crowmaster.cardasmarto.R;

/**
 * Created by root on 6/16/16.
 * This activity is launched after user touches "How it works" option in drawer menu and
 * this activity was designated to show some general information about the process to the user.
 */
public class GuideActivity extends AppIntro2 {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        addSlide(AppIntro2Fragment.newInstance("Monitoring car", "Kid plays and we gather data using sensor" +
                " in chip placed on the car.",
                R.drawable.vintage_365191_640, Color.parseColor("#b2a242")));
        addSlide(AppIntro2Fragment.newInstance("Data collection",
                "Next the data is transmitted to the device and we analyze it to detect signs of autism.", R.drawable.analyze_guide, Color.parseColor("#90a4ae")));

        addSlide(AppIntro2Fragment.newInstance("Even more!",
                "You can use this application to sync your information with our website and do much more!",
                        R.drawable.kids2, Color.parseColor("#b27a42")));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
