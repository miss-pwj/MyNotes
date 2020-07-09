package com.lcl.mynote.about;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lcl.mynote.notepad.MainActivity;
import com.lcl.mynote.R;


public class HelpActivity extends AppCompatActivity {
    private Button btnBACK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        btnBACK = findViewById(R.id.BACK);
        btnBACK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(intent4);
            }
        });

    }
}
