package jb.moovie.search;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import java.util.Objects;

import jb.moovie.R;
import jb.moovie.search.details.DetailsFragment;

import static jb.moovie.search.SearchVolley.DetailsInterface;
import static jb.moovie.search.SearchVolley.filmSearch;
import static jb.moovie.search.SearchVolley.getMoviesList;

public class SearchFragment extends Fragment {

    private View view = null;
    private EditText searchField;
    private ConstraintLayout constraintLayout;
    private ListView listView;
    private MovieAdapter mAdapter = null;
    private ViewGroup transitionsContainer = null;
    private ConstraintSet originalConstraints = new ConstraintSet();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fragment, container, false);

        searchField = view.findViewById(R.id.search_bar);
        transitionsContainer = view.findViewById(R.id.search_fragment);
        listView = view.findViewById(R.id.movies_list);
        constraintLayout = (ConstraintLayout) transitionsContainer;
        originalConstraints.clone(constraintLayout);

        search();

        return view;
    }

    //TODO MAKE UPDATES WORK AS INTENDED
    private void search() {
        searchField.addTextChangedListener(new TextWatcher() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAdapter != null) mAdapter.clear();
                filmSearch(s, getContext());
                if (s.length() == 0) {
                    adjustView(false);
                    mAdapter.clear();
                    listView.setAdapter(null);
                } else {
                    adjustView(true);
                    detailsListeners();
                }
                mAdapter = new MovieAdapter(Objects.requireNonNull(getContext()), getMoviesList());
                listView.setAdapter(mAdapter);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void detailsListeners() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idx) {
                SearchVolley searchVolley = new SearchVolley(view.getTag().toString(), getContext());
                searchVolley.setCustomObjectListener(new DetailsInterface() {
                    @Override
                    public void detailsSet() {
                        DetailsFragment detailsFragment = new DetailsFragment();
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, detailsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        });
    }

    private void adjustView(boolean b) {
        Display display = Objects.requireNonNull(getActivity()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        ViewGroup.LayoutParams params = searchField.getLayoutParams();

        TransitionManager.beginDelayedTransition(transitionsContainer,
                new ChangeBounds().setPathMotion(new ArcMotion()).setDuration(500));

        if (b) {
            constraintSet.clear(R.id.search_bar, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintLayout);
            params.width = (int) (size.x * 0.95);
        } else {
            originalConstraints.applyTo(constraintLayout);
            params.width = (int) (size.x * 0.65);
        }
        searchField.setLayoutParams(params);
    }

}