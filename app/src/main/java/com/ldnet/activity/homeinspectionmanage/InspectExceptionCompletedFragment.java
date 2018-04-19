package com.ldnet.activity.homeinspectionmanage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ldnet.goldensteward.R;
import com.tendcloud.tenddata.TCAgent;


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


    @Override
    public void onResume() {
        super.onResume();
        TCAgent.onPageStart(getActivity(), "房屋验收-异常上报完毕：" + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        TCAgent.onPageEnd(getActivity(), "房屋验收-异常上报完毕：" + this.getClass().getSimpleName());
    }

}
