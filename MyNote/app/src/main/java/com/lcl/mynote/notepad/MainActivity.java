package com.lcl.mynote.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lcl.mynote.R;
import com.lcl.mynote.database.DBHelper;
import com.lcl.mynote.music.MusicActivity;
import com.lcl.mynote.about.HelpActivity;
import com.lcl.mynote.database.DBUtils;
import com.lcl.mynote.map.BDPositionActivity;
import com.lcl.mynote.model.Values;
import com.lcl.mynote.baidupage.BaiduPage;
import com.lcl.mynote.recycle.RecycleActivity;
import com.lcl.mynote.translate.TransActivity;

import java.io.FileInputStream;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

//import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    public static final int UPDATE_ITEM = 1;

    DBHelper myDb;
    private ListView lv_note;
    private FloatingActionButton fab;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.backup:  //第一个参数需要传入一个View，可以是界面当中的任意一个View控件，Snackbar会自动根据这个控件找到最外层的布局来显示
                Snackbar.make(fab, "您要将数据备份吗？", Snackbar.LENGTH_SHORT)
                        .setAction("备份", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MyThread myThread = new MyThread();
                                new Thread(myThread).start();
                                Toast.makeText(MainActivity.this, "数据已备份", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                break;
            case R.id.delete:
                Intent intent = new Intent(MainActivity.this, RecycleActivity.class);
                startActivity(intent);
                break;
            case R.id.baidu:
                Intent intent1 = new Intent(MainActivity.this, BaiduPage.class);
                startActivity(intent1);
                break;
            case R.id.lookup:
                Intent intent2 = new Intent(MainActivity.this, TransActivity.class);
//                Intent intent2 = new Intent(MainActivity.this, QueryActivity.class);
                startActivity(intent2);
                break;
            case R.id.map:
                Intent intent5 = new Intent(MainActivity.this, BDPositionActivity.class);
                startActivity(intent5);
                break;
            case R.id.user:
                String mes = "";
                try {
                    FileInputStream fi_input;
                    fi_input = openFileInput("user_mes.txt");
                    byte[] buffer = new byte[fi_input.available()];
                    fi_input.read(buffer);
                    mes = new String(buffer);
                    Log.d("TAG", "用户信息" + mes);
                    fi_input.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, mes, Toast.LENGTH_SHORT).show();
                break;

            case R.id.music:
                Intent intent_m = new Intent(MainActivity.this, MusicActivity.class);
                startActivity(intent_m);
                break;
            case R.id.restore:
                MyThread2 myThread2 = new MyThread2();
                new Thread(myThread2).start();
                Toast.makeText(MainActivity.this, "恢复成功", Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent3);
//                refresh();
                break;
            case R.id.about:
                Intent intent6 = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent6);
            default:
        }
        return true;
    }
    public void refresh() {
        onCreate(null);
    }
    class MyThread implements Runnable {
        @Override
        public void run() {
            SQLiteDatabase db = myDb.getReadableDatabase();

            //查询数据库中的数据
            Cursor cursor = db.query(DBHelper.TABLE, null, null,
                    null, null, null, null);
            DBUtils dbUtils = new DBUtils();
            dbUtils.clearNote();
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast()) {//判断当前指针是否已经到最后一条记录的下一个若是则返回true

                    String title = cursor.getString(cursor.getColumnIndex(DBHelper.TITLE));
                    String content = cursor.getString(cursor.getColumnIndex(DBHelper.CONTENT));
                    String datetime = cursor.getString(cursor.getColumnIndex(DBHelper.TIME));

                    cursor.moveToNext();
                    Log.d("MainActivity", title);
                    Log.d("MainActivity", content);
                    Log.d("MainActivity", datetime);

                    dbUtils.insertNote(title, content, datetime);

                }
            }
        }
    }


    class MyThread2 implements Runnable{
        @Override
        public void run() {
            List<Values> myValuesList;
            myValuesList = new DBUtils().ShowAll();//遍历后mysql以对象数组的形式传过来了 但应如何将对象数组插入sqlite呢？

            System.out.println("mysql数据"+myValuesList);

            SQLiteDatabase db = myDb.getWritableDatabase();
            db.delete(DBHelper.TABLE,null,null);
//            ContentValues contentValues;

            for(Values v:myValuesList){
                System.out.println("第一个");
//                contentValues = new ContentValues();
                String title= v.getTitle();
                String content=v.getContent();
                String time= v.getTime();
//                contentValues.put(DBHelper.TITLE,title);
//                contentValues.put(DBHelper.CONTENT,content);
//                contentValues.put(DBHelper.TIME,time);
//                Log.i("contentValues", time, null);
//                db.insert(DBHelper.TABLE,null,contentValues);
                Object[] data =new Object[]{v.getId(),v.getTitle(),v.getContent(),v.getTime()};
                db.execSQL("insert into notes values(?,?,?,?)",data);
//                Looper.prepare();
//                Toast.makeText(MainActivity.this,"恢复成功", Toast.LENGTH_LONG).show();
//                Looper.loop();
//                Intent intent = new Intent(AddNoteActivity.this,MainActivity.class);
//                startActivity(intent);
            }
//            db.close();
            System.out.println("保存成功");



            List<Values> valuesList = new ArrayList<>();//泛型用法 这里valuesList是个对象数组 只能“add”Value对象
//            SQLiteDatabase db2 = myDb.getReadableDatabase();

            //查询数据库中的数据
            Cursor cursor = db.query(DBHelper.TABLE, null, null,
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

                    //将values对象存入list对象数组中
                    valuesList.add(values);
                    cursor.moveToNext();
                }
            }
            cursor.close();
            System.out.println("sqlite数据："+valuesList);
            db.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    } // 通过哪一个资源来创建菜单 让菜单显示出来

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将的实例传入这样既使用了toolbar又让外观和功能与actionbar一致

        myDb = new DBHelper(this);
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//将的实例传入这样既使用了toolbar又让外观和功能与actionbar一致

        myDb = new DBHelper(this);
        init();
    }

    public void init() {

        fab = findViewById(R.id.fab);
        lv_note = findViewById(R.id.lv_note);
        List<Values> valuesList = new ArrayList<>();//泛型用法 这里valuesList是个对象数组 只能“add”Value对象
        SQLiteDatabase db = myDb.getReadableDatabase();

        //查询数据库中的数据
        Cursor cursor = db.query(DBHelper.TABLE, null, null,
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

                //将values对象存入list对象数组中
                valuesList.add(values);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        //设置list组件adapter
        final MyBaseAdapter myBaseAdapter = new MyBaseAdapter(valuesList, this, R.layout.note_item);
        lv_note.setAdapter(myBaseAdapter);

        //按钮点击事件
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        //单击查询
        lv_note.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                Values values = (Values) lv_note.getItemAtPosition(position);
                intent.putExtra(DBHelper.TITLE, values.getTitle().trim());
                intent.putExtra(DBHelper.CONTENT, values.getContent().trim());
                intent.putExtra(DBHelper.TIME, values.getTime().trim());
                intent.putExtra(DBHelper.ID, values.getId().toString().trim());
                startActivity(intent);
            }
        });


        //长按删除
        lv_note.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Values values = (Values) lv_note.getItemAtPosition(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否删除?")
                        .setPositiveButton("确认删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = myDb.getWritableDatabase();
                                        db.delete(DBHelper.TABLE, DBHelper.ID + "=?", new String[]{String.valueOf(values.getId())});
                                        //contentvalues是内容值 而values是javabean对象 下面是为了删除的同时添加到最近删除
                                        ContentValues contentvalues = new ContentValues();
                                        contentvalues.put(DBHelper.TITLE, values.getTitle());
                                        contentvalues.put(DBHelper.CONTENT, values.getContent());
                                        contentvalues.put(DBHelper.TIME, values.getTime());
                                        db.insert(DBHelper.TABLE2, null, contentvalues);

                                        db.close();
                                        myBaseAdapter.removeItem(position);
                                        lv_note.post(new Runnable() { //使用Runnalbe 把这个加入到消息队列里面去
                                            @Override
                                            public void run() {
                                                myBaseAdapter.notifyDataSetChanged();
                                            }//方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容
                                        });
                                        //MainActivity.this.onResume();
                                    }
                                })
                        .setNegativeButton("不删除", null).show();
                return true;
            }
        });
    }

    class MyBaseAdapter extends BaseAdapter {  //定义的适配器 继承基类适配器

        private List<Values> valuesList;
        private Context context;
        private int layoutId;

        public MyBaseAdapter(List<Values> valuesList, Context context, int layoutId) {
            this.valuesList = valuesList; //构造函数
            this.context = context;
            this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int position) {
            if (valuesList != null && valuesList.size() > 0)
                return valuesList.get(position);
            else
                return null;   //通过这个方法获取当前项的实例
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override//此方法子项在屏幕内会被调用
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.note_item, parent,
                        false);//通过LayoutInflater的inflate方法映射一个自己定义的Layout布局xml加载
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.content = convertView.findViewById(R.id.tv_content);
                viewHolder.time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String title = valuesList.get(position).getTitle();
            String content = valuesList.get(position).getContent();
            viewHolder.title.setText(title);
            viewHolder.content.setText(content);
            viewHolder.time.setText(valuesList.get(position).getTime());
            return convertView;
        }

        public void removeItem(int position) {
            this.valuesList.remove(position);
        }

    }

    class ViewHolder {
        TextView title;
        TextView content;
        TextView time;
    }
   /* class RefActivity extends AppCompatActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Intent intent = new Intent(RefActivity.this, MainActivity.class);
        }
    }*/
}


