package com.lcl.mynote.database;

import android.util.Log;

import com.lcl.mynote.model.Values;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    private static String driver = "com.mysql.jdbc.Driver";//mysql驱动
    private static String url = "jdbc:mysql://192.168.127.1:3306/notepad";//mysql数据库连接url172.20.10.2   10.0.2.2
    private static String user = "root";
    private static String password = "123456";
    DBHelper myDb;


    private static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(driver);//指定驱动
            conn = DriverManager.getConnection(url, user, password);//获取连接

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    //插入数据
    public void insertNote(String title, String content, String datetime) {

        Connection conn = getConnection();
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "insert into note(title,content,datetime) values(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, datetime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public void clearNote() {
        Connection conn = getConnection();
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "truncate table note";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    public List<Values> ShowAll() {
        myDb = new DBHelper(null);//没有context传入空行不行？bu行！
        List<Values> valuesList = new ArrayList<>();
        Connection conn = getConnection();
        Statement st = null;
        try {
            st = conn.createStatement();
            String sql = "select * from note";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                String datetime = rs.getString("datetime");
                Log.d("DBUtils", title);
//            SQLiteDatabase db = myDb.getWritableDatabase();//这里面myDb对象为空！
//            ContentValues values = new ContentValues();
//            values.put(DBHelper.TITLE,title);
//            values.put(DBHelper.CONTENT,content);
//            values.put(DBHelper.TIME,datetime);
//            String sql2 = "delete from 'notes'";//SQLite并不支持TRUNCATE TABLE语句
//            db.execSQL(sql2);
//            db.insert(DBHelper.TABLE,null,values);
                Values values = new Values();//这地方好像没啥用 这是添加集合的 我得先弄到sqlite数据库里！开始想法不对 只想着把数据从mysql中遍历出来 然后直接显示在listview里！你起码得在sqlite里吧
                values.setId(id);
                values.setTitle(title);     //要不逻辑就很不正确 比如显示后删除怎么搞 是删除哪里的呢？本身就没在sqlite你还想删哪？难道隔空删mysql？存到sqlite后就好办了 大不了我再 跳转自身嘛！
                values.setContent(content); //不就刷新了嘛！
                values.setTime(datetime);
                valuesList.add(values);//添加进集合
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return valuesList;
    }
}