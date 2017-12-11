package com.ldnet.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.ldnet.activity.base.BaseActionBarActivity;
import com.ldnet.entities.MessageData;
import com.ldnet.entities.User;
import com.ldnet.goldensteward.R;
import com.ldnet.utility.Services;

/**
 * Created by lee on 2016/11/23.
 */
public class MessageDetail extends BaseActionBarActivity {

    private TextView tv_message_detail_title,tv_message_detail_time,tv_message_detail_content,tv_main_title,tvCommName;
    private ImageButton btn_back;
    private MessageData messageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_message_detail);
        findView();
    }


    public void findView(){
        Intent intent = this.getIntent();
        messageData =(MessageData)intent.getSerializableExtra("messageData");
        tv_message_detail_title = (TextView)findViewById(R.id.tv_message_detail_title);
        tv_message_detail_time = (TextView)findViewById(R.id.tv_message_detail_time);
        tv_message_detail_content = (TextView)findViewById(R.id.tv_message_detail_content);
        tvCommName=(TextView)findViewById(R.id.message_detail_comm_name);
        tv_main_title = (TextView)findViewById(R.id.tv_page_title);
        tv_main_title.setText(R.string.fragment_me_message_detail);
        btn_back = (ImageButton) findViewById(R.id.btn_back);
        tv_message_detail_title.setText(messageData.Title);
        tv_message_detail_content.setText(messageData.Content);
        tv_message_detail_time.setText(messageData.Created);
        tvCommName.setText(messageData.CommunityName);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_back){
            finish();
        }
    }
}
