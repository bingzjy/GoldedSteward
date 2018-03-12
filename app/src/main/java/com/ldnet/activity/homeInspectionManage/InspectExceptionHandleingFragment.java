package com.ldnet.activity.homeInspectionManage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;


public class InspectExceptionHandleingFragment extends Fragment {


    public InspectExceptionHandleingFragment() {

    }

    public static InspectExceptionHandleingFragment newInstance(){
        return new InspectExceptionHandleingFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inspect_exception_handleing, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "房屋验收-异常：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "房屋验收-异常：" + this.getClass().getSimpleName());
    }

}
