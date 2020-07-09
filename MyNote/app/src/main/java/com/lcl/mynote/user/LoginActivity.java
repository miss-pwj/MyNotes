package com.lcl.mynote.user;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lcl.mynote.R;
import com.lcl.mynote.notepad.MainActivity;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username, paswd;
    private Button registerBtn, showBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    //组件初始化方法
    private void init() {
        username = (EditText) findViewById(R.id.username);
        paswd = (EditText) findViewById(R.id.paswd);
        registerBtn = (Button) findViewById(R.id.register);
        showBtn = (Button) findViewById(R.id.show);
        //按钮添加监听
        registerBtn.setOnClickListener(this);
        showBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String name_str = username.getText().toString();
        String paswd_str = paswd.getText().toString();
        switch (view.getId()) {
            case R.id.register:
                //获取输入的用户名和密码

                boolean flag = save_userMes(LoginActivity.this, name_str, paswd_str);
                FileOutputStream fi_out;
                try {
                    //保存输入的数据
                    String user_mes = "用户名为：" + name_str + "\n" + "密码为：" + paswd_str;
                    fi_out = openFileOutput("user_mes.txt", MODE_PRIVATE);
                    fi_out.write(user_mes.getBytes());
                    fi_out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (flag) {
                    Toast.makeText(LoginActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.show:
                Map<String, String> user = getuser_mes(LoginActivity.this);
                if (user != null) {
                    String username = user.get("username");
                    String paswd = user.get("paswd");
                    Log.d("tag", name_str + "," + paswd_str);
                    Log.d("tag", username + "," + paswd);
                    if (username.equals(name_str) && paswd.equals(paswd_str)) {
                        Toast.makeText(LoginActivity.this, "用户名为：" + username + "\n" + "密码为：" + paswd, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this, "用户名或密码错误！\n\t\t\t请重新输入", Toast.LENGTH_SHORT).show();

                    }
                }

                break;
            default:
                break;
        }

    }

    //保存用户名和密码，并且生成user_mes.xml文件的方法
    private boolean save_userMes(Context context, String username, String paswd) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_mes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("paswd", paswd);
        editor.commit();
        return true;
    }

    //从user_mes.xml文件中取出用户名和密码的方法
    private Map<String, String> getuser_mes(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_mes", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        String paswd = sharedPreferences.getString("paswd", null);
        Map<String, String> user = new HashMap<String, String>();
        user.put("username", username);
        user.put("paswd", paswd);
        return user;
    }
}
