package com.lcl.mynote.recycle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lcl.mynote.R;
import com.lcl.mynote.database.DBHelper;
import com.lcl.mynote.model.Values;
import com.lcl.mynote.notepad.MainActivity;

import java.util.ArrayList;
import java.util.List;


public class RecycleActivity extends AppCompatActivity {
    DBHelper myDb;
    private ListView lvNote;
    private FloatingActionButton done;
    private FloatingActionButton clear;
   // List<Values> valuesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDb = new DBHelper(this);
        setContentView(R.layout.activity_recycle);
        init();
//        Check_Adapter adapter = new Check_Adapter(DeleteActivity.this,valuesList);
//        lvNote.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        myDb = new DBHelper(this);
        setContentView(R.layout.activity_recycle);
        init();

    }

    public void init() {

//        done = findViewById(R.id.done);
        clear = findViewById(R.id.clear);
        lvNote = findViewById(R.id.lisv_note);
        List<Values> valuesList = new ArrayList<>();//泛型用法 这里valuesList是个对象数组 只能“add”Value对象
        SQLiteDatabase db = myDb.getReadableDatabase();

        //查询数据库中的数据
        Cursor cursor = db.query(DBHelper.TABLE2, null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            Values values;
            while (!cursor.isAfterLast()) {//判断当前指针是否已经到最后一条记录的下一个若是则返回true
                //实例化values对象
                values = new Values();

                //把数据库中的一个表中的数据赋值给values
                values.setId(Integer.valueOf(cursor.getString(cursor.getColumnIndex(DBHelper.ID))));
                values.setTitle(cursor.getString(cursor.getColumnIndex(DBHelper.TITLE)));
                values.setContent(cursor.getString(cursor.getColumnIndex(DBHelper.CONTENT)));
                values.setTime(cursor.getString(cursor.getColumnIndex(DBHelper.TIME)));

                //将values对象存入list对象数组中 valueList里面有要显示的数据所有数据 但不能直接显示在listview上 要适配器
                valuesList.add(values);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

//        done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent3 = new Intent(RecycleActivity.this, MainActivity.class);
//                startActivity(intent3);
//            }
//        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RecycleActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否清空最近删除?（清空将无法恢复）")
                        .setPositiveButton("确认清空",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = myDb.getWritableDatabase();
                                        String sql = "delete from 'history'";//SQLite并不支持TRUNCATE TABLE语句
                                        db.execSQL(sql);
                                        Intent intent = new Intent(RecycleActivity.this, RecycleActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(RecycleActivity.this, "已清空最近删除", Toast.LENGTH_LONG).show();
                                    }
                                })
                        .setNegativeButton("不清空",null).show();
            }
        });

        Check_Adapter adapter = new Check_Adapter(RecycleActivity.this,valuesList);
        lvNote.setAdapter(adapter);

        //单击跳转到最近删除页面 可以永久删除或者恢复
        lvNote.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent6 = new Intent(RecycleActivity.this, EditRecycleActivity.class);
                Values values = (Values) lvNote.getItemAtPosition(position);
                intent6.putExtra(DBHelper.TITLE,values.getTitle().trim());
                intent6.putExtra(DBHelper.CONTENT,values.getContent().trim());
                intent6.putExtra(DBHelper.TIME,values.getTime().trim());
                intent6.putExtra(DBHelper.ID,values.getId().toString().trim());
                startActivity(intent6);
            }
        });


    }
    class Check_Adapter extends BaseAdapter {
        private List<Values> mData; //相当于valuelist 一个类型都是对象数组
        private LayoutInflater mInflater;

        public Check_Adapter(Context context, List<Values> data) {
            mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        public void setChecks(List<Values> data) {
            this.mData = data;
        }

        @Override
        public int getCount() {
            if (mData != null && mData.size() > 0)
                return mData.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.recycle_item, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();//通过此方法将缓存的ViewHolder重新取出
            }

            viewHolder.title.setText(mData.get(position).getTitle());
            viewHolder.content.setText(mData.get(position).getContent());
            viewHolder.time.setText(mData.get(position).getTime());

            return convertView;
        }

        class ViewHolder {    //用来对控件实例进行缓存 提高运行效率
            TextView title;
            TextView content;
            TextView time;
        }
    }
}

