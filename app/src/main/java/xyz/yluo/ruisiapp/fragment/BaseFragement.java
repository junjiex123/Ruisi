package xyz.yluo.ruisiapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.yluo.ruisiapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragement extends Fragment {


    public BaseFragement() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragement_base, container, false);
    }

}
