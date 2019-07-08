package purdue.edu.bicker_quicker;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AnimationHandler {
    public static void slide_down(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v) {

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_in_left(Context ctx, View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_left);
        if(a != null) {
            a.reset();
            if(view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_in_right(Context ctx, View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_right);
        if(a != null) {
            a.reset();
            if(view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_out_left(Context ctx, View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_out_left);
        if(a != null) {
            a.reset();
            if(view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }
    }

    public static void slide_out_right(Context ctx, View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_out_right);
        a.setDuration(500);
        view.startAnimation(a);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        }, 500);


        /*if(a != null) {
            a.reset();
            if(view != null) {
                view.clearAnimation();
                view.startAnimation(a);
            }
        }*/


    }

    public static void select_blue(Button leftVote, Button rightVote, View view, Activity c) {
        LinearLayout closed_bicker_holder = view.findViewById(R.id.closed_bicker_holder);
        LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);
        AnimationDrawable blueAnimation;
        AnimationDrawable purpleAnimation;
        leftVote.setBackgroundResource(R.drawable.blue_select_blue_animation);
        rightVote.setBackgroundResource(R.drawable.purple_select_blue_animation);
        blueAnimation = (AnimationDrawable) leftVote.getBackground();
        purpleAnimation = (AnimationDrawable) rightVote.getBackground();
        blueAnimation.setEnterFadeDuration(800);
        purpleAnimation.setEnterFadeDuration(800);
        blueAnimation.start();
        purpleAnimation.start();
        leftVote.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(open_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, open_bicker_holder);
                }
                else if(closed_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, closed_bicker_holder);
                }
                else {
                    Log.d("AnimationHandler.java", "ERROR: Neither closed/open_bicker_holder is shown");
                    Toast.makeText(c, "ERROR: Neither closed/open_bicker_holder is shown", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }

    public static void select_purple(Button leftVote, Button rightVote, View view, Activity c) {
        LinearLayout closed_bicker_holder = view.findViewById(R.id.closed_bicker_holder);
        LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);
        AnimationDrawable blueAnimation;
        AnimationDrawable purpleAnimation;
        leftVote.setBackgroundResource(R.drawable.blue_select_purple_animation);
        rightVote.setBackgroundResource(R.drawable.purple_select_purple_animation);
        blueAnimation = (AnimationDrawable) leftVote.getBackground();
        purpleAnimation = (AnimationDrawable) rightVote.getBackground();
        blueAnimation.setEnterFadeDuration(800);
        purpleAnimation.setEnterFadeDuration(800);
        blueAnimation.start();
        purpleAnimation.start();
        leftVote.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(open_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, open_bicker_holder);
                }
                else if(closed_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, closed_bicker_holder);
                }
                else {
                    Log.d("AnimationHandler.java", "ERROR: Neither closed/open_bicker_holder is shown");
                    Toast.makeText(c, "ERROR: Neither closed/open_bicker_holder is shown", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }

    public static void select_abstain(Button leftVote, Button rightVote, View view, Activity c) {
        LinearLayout closed_bicker_holder = view.findViewById(R.id.closed_bicker_holder);
        LinearLayout open_bicker_holder = view.findViewById(R.id.open_bicker_holder);
        AnimationDrawable blueAnimation;
        AnimationDrawable purpleAnimation;
        leftVote.setBackgroundResource(R.drawable.blue_select_abstain_animation);
        rightVote.setBackgroundResource(R.drawable.purple_select_abstain_animation);
        blueAnimation = (AnimationDrawable) leftVote.getBackground();
        purpleAnimation = (AnimationDrawable) rightVote.getBackground();
        blueAnimation.setEnterFadeDuration(800);
        purpleAnimation.setEnterFadeDuration(800);
        blueAnimation.start();
        purpleAnimation.start();
        leftVote.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(open_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, open_bicker_holder);
                }
                else if(closed_bicker_holder.isShown()) {
                    AnimationHandler.slide_out_right(c, closed_bicker_holder);
                }
                else {
                    Log.d("AnimationHandler.java", "ERROR: Neither closed/open_bicker_holder is shown");
                    Toast.makeText(c, "ERROR: Neither closed/open_bicker_holder is shown", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);
    }
}
