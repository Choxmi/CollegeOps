package com.choxmi.collegeops;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by choslk on 3/19/2018.
 */

public class BeforeLoginActivity extends Activity {
    Button loginBtn;
    SharedPreferences sf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);
        sf = getSharedPreferences("vitam",MODE_PRIVATE);

        loginBtn = (Button)findViewById(R.id.beforeLoginBtn);

        if(!sf.getString("id","").equals("")){
            Intent intent = new Intent(BeforeLoginActivity.this, MainActivity.class);
            startActivity(intent);
        }else{
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BeforeLoginActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
