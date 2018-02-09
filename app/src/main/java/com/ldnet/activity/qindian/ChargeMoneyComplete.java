package com.ldnet.activity.qindian;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ldnet.activity.MainActivity;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.goldensteward.R;

/**
 * Created by zjy on 2017/9/5.
 */
public class ChargeMoneyComplete extends BaseActionBarActivity {

    private Button back;
    private TextView title;
    private ImageButton imageBack;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qindian_pay_money_complete);

        back=(Button)findViewById(R.id.btn_back_to_home);
        title=(TextView)findViewById(R.id.tv_page_title);
        imageBack=(ImageButton)findViewById(R.id.btn_back);
        back.setOnClickListener(this);
        imageBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_back:
                try {
                    gotoActivityAndFinish(QinDianMain.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_back_to_home:
                try {
                    gotoActivityAndFinish(MainActivity.class.getName(),null);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}