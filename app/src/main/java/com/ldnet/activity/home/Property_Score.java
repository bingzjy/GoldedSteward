package com.ldnet.activity.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

/**
 * Created by lee on 2016/8/1.
 */
public class Property_Score extends BaseActionBarActivity {

    private ImageButton btn_back;
    private TextView tv_main_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_score);
        findView();
    }

    public void findView(){
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_main_title = (TextView) findViewById(R.id.tv_page_title);
        tv_main_title.setText("评分");
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }
    }
}
