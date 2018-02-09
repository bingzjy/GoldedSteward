package com.ldnet.activity.homeInspectionManage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ldnet.goldensteward.R;


public class InspectExceptionCompletedFragment extends Fragment {

    public InspectExceptionCompletedFragment() {
    }

    public static InspectExceptionCompletedFragment newInstance(){
        return new InspectExceptionCompletedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inspect_exception_completed, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
