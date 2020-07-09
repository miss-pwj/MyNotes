package com.lcl.mynote.translate;

/**
 * @author lcl
 * @date 2020/7/4 20:02
 * @description
 */

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.lcl.mynote.R;
import com.lcl.mynote.translate.BaiduTranslate.*;

import java.util.List;

//import com.example.zhangyu.mytranslate.BaiduTranslate.TransApi;
//import com.example.zhangyu.mytranslate.BaiduTranslate.TranslateResult;

public class TransActivity extends AppCompatActivity {


    private Button button;
    private TextView textView;
    private Handler handler = new Handler();
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
        initView();
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String query =editText.getText().toString();
//                final String query = "我爱你\n你爱我吗";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String resultJson = new TransApi().getTransResult(query, "auto", "en");
                        //拿到结果，对结果进行解析。
                        Gson gson = new Gson();
                        TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                        final List<TranslateResult.TransResultBean> trans_result = translateResult.getTrans_result();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                String dst = "";
                                for (TranslateResult.TransResultBean s : trans_result
                                ) {
                                    dst = dst + "\n" + s.getDst();
                                }

                                textView.setText(dst);
                            }
                        });

                    }
                }).start();
            }
        });

    }
}