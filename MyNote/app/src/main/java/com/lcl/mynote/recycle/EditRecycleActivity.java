package com.lcl.mynote.recycle;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lcl.mynote.R;
import com.lcl.mynote.database.DBHelper;
import com.lcl.mynote.model.Values;

public class EditRecycleActivity extends AppCompatActivity {
    private Button btnRestore;
    private Button btnClear;
    private Button btnBack;
    private TextView showTime;
    private TextView showContent;
    private TextView showTitle;
    DBHelper myDb;
    Values values;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recycle);
        myDb = new DBHelper(this);
        init();
    }

    public void init() {

        btnClear = findViewById(R.id.show_clear);
        btnRestore = findViewById(R.id.show_restore);
        btnBack = findViewById(R.id.show_back);
        showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showContent = findViewById(R.id.show_content);
        Intent intent = this.getIntent();


        if (intent != null) {

            showTime.setText(intent.getStringExtra(DBHelper.TIME));
            showTitle.setText(intent.getStringExtra(DBHelper.TITLE));
            showContent.setText(intent.getStringExtra(DBHelper.CONTENT));
            id = intent.getStringExtra(DBHelper.ID);
        }
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDb.getWritableDatabase();
                db.delete(DBHelper.TABLE2, DBHelper.ID + "=?", new String[]{id});
                Intent intent = new Intent(EditRecycleActivity.this, RecycleActivity.class);
                startActivity(intent);
                Toast.makeText(EditRecycleActivity.this, "已删除", Toast.LENGTH_LONG).show();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRecycleActivity.this, RecycleActivity.class);
                startActivity(intent);
            }
        });
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();
                String title = showTitle.getText().toString();
                String content = showContent.getText().toString();
                String time = showTime.getText().toString();
                values.put(DBHelper.TITLE, title);
                values.put(DBHelper.CONTENT, content);
                values.put(DBHelper.TIME, time);
                db.insert(DBHelper.TABLE, null, values);//恢复到notes表
                db.delete(DBHelper.TABLE2, DBHelper.ID + "=?", new String[]{id});//在history表中删除
                Intent intent = new Intent(EditRecycleActivity.this, RecycleActivity.class);
                startActivity(intent);
                Toast.makeText(EditRecycleActivity.this, "已成功恢复数据", Toast.LENGTH_LONG).show();
            }
        });
    }
}
