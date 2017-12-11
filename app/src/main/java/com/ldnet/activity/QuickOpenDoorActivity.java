package com.ldnet.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.amap.api.maps.model.Text;
import com.ldnet.goldensteward.R;

/**
 * Created by lee on 2017/8/1.
 */
public class QuickOpenDoorActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar bar=getActionBar();
        bar.hide();

//        String name=getIntent().getStringExtra("name");
//        TextView text=(TextView)findViewById(R.id.text);
//        text.setText(name);

    }
}
