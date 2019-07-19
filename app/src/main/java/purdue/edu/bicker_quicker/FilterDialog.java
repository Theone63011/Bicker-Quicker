package purdue.edu.bicker_quicker;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AlertDialogLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class FilterDialog extends AppCompatDialogFragment {

    private Switch showActive;
    private Switch showExpired;
    private EditText keywords;

    private Switch allSwitch;
    private Switch artSwitch;
    private Switch boardGamesSwitch;
    private Switch booksSwitch;
    private Switch comedySwitch;
    private Switch foodSwitch;
    private Switch moviesSwitch;
    private Switch musicSwitch;
    private Switch philosophySwitch;
    private Switch politicsSwitch;
    private Switch relationshipsSwitch;
    private Switch scienceSwitch;
    private Switch sportsSwitch;
    private Switch tvShowsSwitch;
    private Switch videoGamesSwitch;
    private Switch miscellaneousSwitch;

    private FilterDialogListener listener;

    public static final String FILTER_PREFS = "filterPrefs";
    public static final String ALL_PREF = "allPref";
    public static final String ART_PREF = "artPref";
    public static final String BOARDGAMES_PREF = "boardgamesPref";
    public static final String BOOKS_PREF = "booksPref";
    public static final String COMEDY_PREF = "comedyPref";
    public static final String FOOD_PREF = "foodPref";
    public static final String MOVIES_PREF = "moviesPref";
    public static final String MUSIC_PREF = "musicPref";
    public static final String PHILOSOPHY_PREF = "philosophyPref";
    public static final String POLITICS_PREF = "politicsPref";
    public static final String RELATIONSHIPS_PREF = "relationshipsPref";
    public static final String SCIENCE_PREF = "sciencePref";
    public static final String SPORTS_PREF = "sportsPref";
    public static final String TVSHOWS_PREF = "tvShowsPref";
    public static final String VIDEOGAMES_PREF = "videoGamesPref";
    public static final String MISCELLANEOUS_PREF = "miscellaneousPref";

    public static final String ACTIVE_PREF = "activePref";
    public static final String EXPIRED_PREF = "expiredPref";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_dialog, null);
        showActive = view.findViewById(R.id.show_active_filter);
        showExpired = view.findViewById(R.id.show_expired_filter);
        keywords = view.findViewById(R.id.keyword_filter);

        allSwitch = view.findViewById(R.id.allSwitch);
        artSwitch = view.findViewById(R.id.artSwitch);
        boardGamesSwitch = view.findViewById(R.id.boardGamesSwitch);
        booksSwitch = view.findViewById(R.id.booksSwitch);
        comedySwitch = view.findViewById(R.id.comedySwitch);
        foodSwitch = view.findViewById(R.id.foodSwitch);
        moviesSwitch = view.findViewById(R.id.moviesSwitch);
        musicSwitch = view.findViewById(R.id.musicSwitch);
        philosophySwitch = view.findViewById(R.id.philosophySwitch);
        politicsSwitch = view.findViewById(R.id.politicsSwitch);
        relationshipsSwitch = view.findViewById(R.id.relationshipsSwitch);
        scienceSwitch = view.findViewById(R.id.scienceSwitch);
        sportsSwitch = view.findViewById(R.id.sportsSwitch);
        tvShowsSwitch = view.findViewById(R.id.tvShowsSwitch);
        videoGamesSwitch = view.findViewById(R.id.videoGamesSwitch);
        miscellaneousSwitch = view.findViewById(R.id.miscSwitch);

        ArrayList<Switch> switches = new ArrayList<Switch>();
        switches.add(artSwitch);
        switches.add(boardGamesSwitch);
        switches.add(booksSwitch);
        switches.add(comedySwitch);
        switches.add(foodSwitch);
        switches.add(moviesSwitch);
        switches.add(musicSwitch);
        switches.add(philosophySwitch);
        switches.add(politicsSwitch);
        switches.add(relationshipsSwitch);
        switches.add(scienceSwitch);
        switches.add(sportsSwitch);
        switches.add(tvShowsSwitch);
        switches.add(videoGamesSwitch);
        switches.add(miscellaneousSwitch);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        allSwitch.setChecked(sharedPreferences.getBoolean(ALL_PREF, true));
        artSwitch.setChecked(sharedPreferences.getBoolean(ART_PREF, true));
        boardGamesSwitch.setChecked(sharedPreferences.getBoolean(BOARDGAMES_PREF, true));
        booksSwitch.setChecked(sharedPreferences.getBoolean(BOOKS_PREF, true));
        comedySwitch.setChecked(sharedPreferences.getBoolean(COMEDY_PREF, true));
        foodSwitch.setChecked(sharedPreferences.getBoolean(FOOD_PREF, true));
        moviesSwitch.setChecked(sharedPreferences.getBoolean(MOVIES_PREF, true));
        musicSwitch.setChecked(sharedPreferences.getBoolean(MUSIC_PREF, true));
        philosophySwitch.setChecked(sharedPreferences.getBoolean(PHILOSOPHY_PREF, true));
        politicsSwitch.setChecked(sharedPreferences.getBoolean(POLITICS_PREF, true));
        relationshipsSwitch.setChecked(sharedPreferences.getBoolean(RELATIONSHIPS_PREF, true));
        scienceSwitch.setChecked(sharedPreferences.getBoolean(SCIENCE_PREF, true));
        sportsSwitch.setChecked(sharedPreferences.getBoolean(SPORTS_PREF, true));
        tvShowsSwitch.setChecked(sharedPreferences.getBoolean(TVSHOWS_PREF, true));
        videoGamesSwitch.setChecked(sharedPreferences.getBoolean(VIDEOGAMES_PREF, true));
        miscellaneousSwitch.setChecked(sharedPreferences.getBoolean(MISCELLANEOUS_PREF, true));

        showActive.setChecked(sharedPreferences.getBoolean(ACTIVE_PREF, true));
        showExpired.setChecked(sharedPreferences.getBoolean(EXPIRED_PREF, false));

        showActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(ACTIVE_PREF, isChecked);
                edit.commit();
            }
        });
        showExpired.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(EXPIRED_PREF, isChecked);
                edit.commit();
            }
        });



        allSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(ART_PREF, isChecked);
                edit.commit();
                for (Switch e : switches) {
                    e.setChecked(isChecked);
                }
            }
        });

        artSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(ART_PREF, isChecked);
                edit.commit();
            }
        });
        boardGamesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(BOARDGAMES_PREF, isChecked);
                edit.commit();
            }
        });
        booksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(BOOKS_PREF, isChecked);
                edit.commit();
            }
        });
        comedySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(COMEDY_PREF, isChecked);
                edit.commit();
            }
        });
        foodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(FOOD_PREF, isChecked);
                edit.commit();
            }
        });
        moviesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(MOVIES_PREF, isChecked);
                edit.commit();
            }
        });
        musicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(MUSIC_PREF, isChecked);
                edit.commit();
            }
        });
        philosophySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(PHILOSOPHY_PREF, isChecked);
                edit.commit();
            }
        });
        politicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(POLITICS_PREF, isChecked);
                edit.commit();
            }
        });
        relationshipsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(RELATIONSHIPS_PREF, isChecked);
                edit.commit();
            }
        });
        scienceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(SCIENCE_PREF, isChecked);
                edit.commit();
            }
        });
        sportsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(SPORTS_PREF, isChecked);
                edit.commit();
            }
        });
        tvShowsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(TVSHOWS_PREF, isChecked);
                edit.commit();
            }
        });
        videoGamesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(VIDEOGAMES_PREF, isChecked);
                edit.commit();
            }
        });
        miscellaneousSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getContext().getSharedPreferences(FILTER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean(MISCELLANEOUS_PREF, isChecked);
                edit.commit();
            }
        });


        builder.setView(view)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean active = showActive.isChecked();
                        boolean expired = showActive.isChecked();
                        String keys = keywords.getText().toString();
                        ArrayList<String> filteredCategories = new ArrayList<String>();
                        // Add switches turned on to array of switches
                        for (Switch e : switches) {
                            if (!e.isChecked()) {
                                filteredCategories.add(e.getText().toString());
                            }
                        }

                        listener.applyFilter(active, expired, filteredCategories, keys);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+" must implement Filter Dialog Listener");
        }
    }

    public interface FilterDialogListener {
        void applyFilter(boolean showActive, boolean showExpired, ArrayList<String> categories, String keywords);
    }
}
