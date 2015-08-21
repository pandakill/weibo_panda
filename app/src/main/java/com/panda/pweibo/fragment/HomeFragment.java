package com.panda.pweibo.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.panda.pweibo.R;
import com.panda.pweibo.activity.MainActivity;


/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends Fragment {

    private View view;

    protected MainActivity activity;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

        activity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void initView() {
        view = View.inflate(activity, R.layout.fragment_home, null );


    }
}
