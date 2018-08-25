package com.example.administrator.ballgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.VISIBLE;


public class MainActivity extends Activity
{
    // 桌面的宽度
    public int tableWidth;
    // 桌面的高度
    public int tableHeight;
    // 球拍的垂直位置
    public int racketY;
    // 下面定义球拍的高度和宽度
    public final int RACKET_HEIGHT = 30;
    public final int RACKET_WIDTH = 90;
    // 小球的大小
    public final int BALL_SIZE = 16;
    // 小球纵向的运行速度
    public int ySpeed = 15;
    Random rand = new Random(System.currentTimeMillis());
    // 返回一个-0.5~0.5的比率，用于控制小球的运行方向
    public double xyRate = rand.nextDouble() - 0.5;
    // 小球横向的运行速度
    public int xSpeed = (int) (ySpeed * xyRate * 2);
    // ballX和ballY代表小球的坐标
    public int ballX = rand.nextInt(200) + 20;
    public int ballY = rand.nextInt(10) + 20;
    // racketX代表球拍的水平位置
    public int racketX = rand.nextInt(200);
    // 游戏是否结束的旗标
    public boolean isLose = false;

    private Button b3,b4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉窗口标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        int level=intent.getIntExtra("level", 0);
        ySpeed += (level-1)/2*ySpeed;
        xSpeed += (level-1)/2*xSpeed;
        // 创建GameView组件
        final GameView gameView = new GameView(this);
        final Direction_right right = new Direction_right(this);
        final Direction_left left = new Direction_left(this);
        final Direction_down down = new Direction_down(this);
        final Direction_up up = new Direction_up(this);

        setContentView(R.layout.activity_main);
        AbsoluteLayout absoluteLayout = (AbsoluteLayout) findViewById(R.id.activity_main);
        AbsoluteLayout absoluteLayout1 = (AbsoluteLayout) findViewById(R.id.abso1);
        AbsoluteLayout absoluteLayout2 = (AbsoluteLayout) findViewById(R.id.abso2);
        AbsoluteLayout absoluteLayout3 = (AbsoluteLayout) findViewById(R.id.abso3);
        AbsoluteLayout absoluteLayout4 = (AbsoluteLayout) findViewById(R.id.abso4);

        b3 = (Button) findViewById(R.id.bn3);
        b4 = (Button) findViewById(R.id.bn4);

        absoluteLayout1.addView(right);
        absoluteLayout2.addView(left);
        absoluteLayout3.addView(down);
        absoluteLayout4.addView(up);

        absoluteLayout.addView(gameView);

        // 获取窗口管理器
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // 获得屏幕宽和高
        tableWidth = metrics.widthPixels;
        tableHeight = metrics.heightPixels;
        racketY = tableHeight - 100;
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("touch","1");
                if (racketX < tableWidth - RACKET_WIDTH - 60 - 45) racketX += 45;
                else racketX = tableWidth - RACKET_WIDTH -60;
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (racketX > 65) racketX -= 45;
                else racketX = 20;
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (racketY < tableHeight - RACKET_HEIGHT - 60) racketY += 20;
                else racketY = tableHeight - RACKET_HEIGHT - 60;
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (racketY > 40) racketY -= 20;
                else racketY = 20;
            }
        });
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    gameView.invalidate();
                    if(isLose){
                        b3 = (Button) findViewById(R.id.bn3);
                        b4 = (Button) findViewById(R.id.bn4);
                        b3.setVisibility(VISIBLE);
                        b4.setVisibility(VISIBLE);
                    }
                }
            }
        };
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() // ①
        {
            @Override
            public void run() {
                // 如果小球碰到左边边框
                if (ballX <= 20 || ballX >= tableWidth - BALL_SIZE - 60) {
                    xSpeed = -xSpeed;
                }
                // 如果小球高度超出了屏幕位置，且横向不在球拍范围之内，游戏结束
                if (ballY >= tableHeight - BALL_SIZE - 60
                        && (ballX < racketX || ballX > racketX
                        + RACKET_WIDTH)) {
                    timer.cancel();
                    // 设置游戏是否结束的旗标为true
                    isLose = true;
                }
                // 如果小球位于球拍之内，且到达球拍位置，小球反弹
                else if (ballY <= 15
                        || (ballY >= racketY - BALL_SIZE
                        && ballX > racketX && ballX <= racketX
                        + RACKET_WIDTH)) {
                    ySpeed = -ySpeed;
                }
                // 小球坐标增加
                ballY += ySpeed;
                ballX += xSpeed;
                // 发送消息，通知系统重绘组件
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 100);
    }
    class GameView extends View
    {
        Paint paint = new Paint();
        public GameView(Context context)
        {
            super(context);
            setFocusable(true);
        }
        // 重写View的onDraw方法，实现绘画
        public void onDraw(Canvas canvas)
        {
            paint.setStyle(Paint.Style.FILL);
            // 设置去锯齿
            paint.setAntiAlias(true);
            // 如果游戏已经结束
            if (isLose)
            {
                paint.setColor(Color.RED);
                paint.setTextSize(40);
                canvas.drawText("游戏已结束", tableWidth / 2 - 100 - 50 , 200, paint);
            }
            // 如果游戏还未结束
            else
            {
                // 设置颜色，并绘制小球
                paint.setColor(Color.rgb(255, 0, 0));
                canvas.drawCircle(ballX, ballY, BALL_SIZE, paint);
                // 设置颜色，并绘制球拍
                paint.setColor(Color.rgb(80, 80, 200));
                canvas.drawRect(racketX, racketY, racketX + RACKET_WIDTH,
                        racketY + RACKET_HEIGHT, paint);
            }
        }
    }
    public class Direction_right extends View {
        public Direction_right(Context context){
            super(context);
        }
        public Direction_right(Context context, AttributeSet set){
            super(context);
        }
        @Override
        public void onDraw(Canvas canvas){
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Path pathLeft = new Path();
            pathLeft.moveTo(0, 0);
            pathLeft.lineTo(width / 2 , 0);
            pathLeft.lineTo(width , height / 2 );
            pathLeft.lineTo(width / 2 , height );
            pathLeft.lineTo(0, height);
            pathLeft.close();

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawPath(pathLeft, paint);

            Rect targetRect = new Rect(0,0,width/2,height);
            paint.setStrokeWidth(3);
            paint.setTextSize(50);
            String testString = "右";
            paint.setColor(Color.BLACK);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            int baseline2=targetRect.bottom-((targetRect.bottom-targetRect.top-fontMetrics.bottom+fontMetrics.top)/2+fontMetrics.bottom);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(testString, targetRect.centerX(), baseline, paint);

        }
    }
    public class Direction_left extends View {
        public Direction_left(Context context){
            super(context);
        }
        public Direction_left(Context context, AttributeSet set){
            super(context);
        }
        @Override
        public void onDraw(Canvas canvas){
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Path pathLeft = new Path();
            pathLeft.moveTo(width,0);
            pathLeft.lineTo(width/2,0);
            pathLeft.lineTo(0,height/2);
            pathLeft.lineTo(width/2,height);
            pathLeft.lineTo(width,height);
            pathLeft.close();
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawPath(pathLeft, paint);
            Rect targetRect = new Rect(width/2,0,width,height);

            paint.setStrokeWidth(3);
            paint.setTextSize(50);
            String testString = "左";
            paint.setColor(Color.BLACK);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            int baseline2=targetRect.bottom-((targetRect.bottom-targetRect.top-fontMetrics.bottom+fontMetrics.top)/2+fontMetrics.bottom);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(testString, targetRect.centerX(), baseline, paint);

        }
    }
    public class Direction_down extends View {
        public Direction_down(Context context){
            super(context);
        }
        public Direction_down(Context context, AttributeSet set){
            super(context);
        }
        public void onDraw(Canvas canvas){
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Path pathLeft = new Path();
            pathLeft.moveTo(0,0);
            pathLeft.lineTo(0,height/2);
            pathLeft.lineTo(width/2,height);
            pathLeft.lineTo(width,height/2);
            pathLeft.lineTo(width,0);
            pathLeft.close();

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawPath(pathLeft, paint);

            Rect targetRect = new Rect(0,0,width,height/2);
            paint.setStrokeWidth(3);
            paint.setTextSize(40);
            String testString = "下";
            paint.setColor(Color.BLACK);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            int baseline2=targetRect.bottom-((targetRect.bottom-targetRect.top-fontMetrics.bottom+fontMetrics.top)/2+fontMetrics.bottom);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(testString, targetRect.centerX(), baseline, paint);
        }
    }
    public class Direction_up extends View {
        public Direction_up(Context context){
            super(context);
        }
        public Direction_up(Context context, AttributeSet set){
            super(context);
        }
        public void onDraw(Canvas canvas){
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            Path pathLeft = new Path();
            pathLeft.moveTo(0,height);
            pathLeft.lineTo(0,height/2);
            pathLeft.lineTo(width/2,0);
            pathLeft.lineTo(width,height/2);
            pathLeft.lineTo(width,height);
            pathLeft.close();

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            canvas.drawPath(pathLeft, paint);

            Rect targetRect = new Rect(0,height,width,height/2);
            paint.setStrokeWidth(3);
            paint.setTextSize(40);
            String testString = "上";
            paint.setColor(Color.BLACK);
            Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            int baseline2=targetRect.bottom-((targetRect.bottom-targetRect.top-fontMetrics.bottom+fontMetrics.top)/2+fontMetrics.bottom);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(testString, targetRect.centerX(), baseline, paint);
        }
    }
}

