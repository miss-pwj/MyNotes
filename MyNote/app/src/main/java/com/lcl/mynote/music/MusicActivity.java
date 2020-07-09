package com.lcl.mynote.music;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.lcl.mynote.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MusicActivity extends AppCompatActivity implements View.OnClickListener,
        Runnable, ServiceConnection, SeekBar.OnSeekBarChangeListener {
    private ImageView disc, needle, playingPre, playingPlay, playingNext;
    private ObjectAnimator discAnimation, needleAnimation;//自定义指针和唱盘
    private boolean isPlaying = true;//0,1 判断是否处于播放状态
    //声明服务
    private static final String TAG = MusicActivity.class.getSimpleName();
    private MediaService.MusicController mMusicController;
    //使用方法：mMusicController.play();播放   mMusicController.pause();暂停
    private boolean running;
    private TextSwitcher mSwitcher;
    private SeekBar mSeekBar;

    public MusicActivity(ImageView playingPlay) {
        this.playingPlay = playingPlay;
    }

    public MusicActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        //设置透明栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        //滑动条部分
        mSeekBar = (SeekBar) findViewById(R.id.music_seek_bar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        mSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        Intent intent = new Intent(this, MediaService.class);
        //增加StartService，来增加后台播放功能
        startService(intent);
        // 绑定服务，使用context来绑定
        // 那个界面需要绑定 就用哪个 Activity
        // 参数1：Intent               代表需要绑定哪一个Service
        // 参数2：ServiceConnection    回调接口，可以接收到Service连接成功和断开的回调，成功就可以取到对象。
        // 绑定服务 参数2就是服务和指定的对象绑定在一起
        bindService(intent, this, BIND_AUTO_CREATE);

        //指针和唱片部分
        initViews();//定义背景图
        setAnimations();

    }

    private void initViews() {
        playingPre = (ImageView) findViewById(R.id.playing_pre);
        playingPlay = (ImageView) findViewById(R.id.playing_play);
        playingNext = (ImageView) findViewById(R.id.playing_next);
        disc = (ImageView) findViewById(R.id.disc);
        needle = (ImageView) findViewById(R.id.needle);
        playingPre.setOnClickListener(this);
        playingPlay.setOnClickListener(this);
        playingNext.setOnClickListener(this);
    }

    //动画设置
    private void setAnimations() {
        discAnimation = ObjectAnimator.ofFloat(disc, "rotation", 0, 360);
        discAnimation.setDuration(20000);
        discAnimation.setInterpolator(new LinearInterpolator());
        discAnimation.setRepeatCount(ValueAnimator.INFINITE);

        needleAnimation = ObjectAnimator.ofFloat(needle, "rotation", 0, 25);
        needle.setPivotX(0);
        needle.setPivotY(0);
        needleAnimation.setDuration(800);
        needleAnimation.setInterpolator(new LinearInterpolator());
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.playing_pre://前一曲
                if (discAnimation != null) {
                    discAnimation.end();
                    playing();
                }
                break;
            case R.id.playing_play://播放中
                if (isPlaying) {
                    playing();
                } else {
                    if (needleAnimation != null) {
                        needleAnimation.reverse();
                        needleAnimation.end();
                        mMusicController.pause();
                    }
                    if (discAnimation != null && discAnimation.isRunning()) {
                        discAnimation.cancel();
                        mMusicController.pause();
                        float valueAvatar = (float) discAnimation.getAnimatedValue();
                        discAnimation.setFloatValues(valueAvatar, 360f + valueAvatar);
                    }
                    playingPlay.setImageResource(R.drawable.music_play);
                    isPlaying = true;
                }
                break;
            case R.id.playing_next://下一曲
                if (discAnimation != null) {
                    discAnimation.end();
                    playing();
                }
                break;
            default:
                break;
        }
    }

    //播放：1、播放音乐 2、动画旋转 3、暂停图片切换为播放按钮图片
    private void playing() {
        needleAnimation.start();
        discAnimation.start();
        playingPlay.setImageResource(R.drawable.music_pause);
        mMusicController.play();//播放
        isPlaying = false;
    }

    //===================================播放歌曲服务开启、停止、结束===============================
    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onStop() {
        running = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 解除绑定
        unbindService(this);
        super.onDestroy();
    }

    //---------------------播放到当前音乐的滑动条及时间设置--------------------------
    @Override
    public void run() {
        running = true;
        try {
            while (running) {
                if (mMusicController != null) {
                    long musicDuration = mMusicController.getMusicDuration();
                    final long position = mMusicController.getPosition();
                    final Date dateTotal = new Date(musicDuration);
                    final SimpleDateFormat sb = new SimpleDateFormat("mm:ss");
                    mSeekBar.setMax((int) musicDuration);
                    mSeekBar.setProgress((int) position);
                    mSwitcher.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    Date date = new Date(position);
                                    String time = sb.format(date) + "/" + sb.format(dateTotal);
                                    mSwitcher.setCurrentText(time);
                                }
                            }
                    );
                }

                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------
    //服务绑定与解除绑定的回调

    /**
     * 当服务与当前绑定对象，绑定成功，服务onBind方法调用并且返回之后
     * 回调给这个方法
     *
     * @param name
     * @param service IBinder 就是服务 onBind 返回的对象
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mMusicController = ((MediaService.MusicController) service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mMusicController = null;
    }

    public void btnStopService(View view) {
        Intent intent = new Intent(this, MediaService.class);
        stopService(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMusicController.setPosition(seekBar.getProgress());
    }
}
