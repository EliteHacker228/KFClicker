package com.example.max.kfclicker14;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;


    int rangcounter = 0;
    int money_score = 0;
    int money_per_time = 1;
    int money_delay=1000;
    int money_per_click=1;
    int lowest_border = 10;

    // ListView lv;

    final String SAVED_MONEY_SCORE = "SAVED_SCORE";
    final String SAVED_MONEY_PER_TIME = "SAVED_MONEY_PER_TIME";
    final String SAVED_MONEY_DELAY = "SAVED_MONEY_DELAY";
    final String SAVED_MONEY_PER_CLICK = "SAVED_MONEY_PER_CLICK";
    final String SAVED_RANGCOUNTER = "SAVED_RANGCOUNTER";
    final String SAVED_LOWEST_BORDER = "SAVED_LOWEST_BORDER";

    TextView kfc_screen_score;
    TextView chicken_cpc;       //куриц за нажатие
    TextView chicken_ranc;      //ранг
    TextView chicken_cps;       //куриц в секунду
    TextView chicken_counter;   //сколько куриц нужно
    // для повышения


    ImageView kfc_screen_wing;

    Handler chickenhandler;
    static Handler scorehandler;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kfc_screen_score = (TextView) findViewById(R.id.kfc_screen_score);
        kfc_screen_wing = (ImageView) findViewById(R.id.kfc_screen_wing);

        kfc_screen_wing.setImageResource(R.drawable.kfc_wing_v3);
        kfc_screen_wing.setOnTouchListener(clicker);

        getSupportActionBar().hide();

        chicken_cpc=findViewById(R.id.chicken_cpc);
        chicken_ranc=findViewById(R.id.chicken_ranc);
        chicken_cps=findViewById(R.id.chicken_cps);
        chicken_counter=findViewById(R.id.chicken_counter);
       // if(SAVED_LOWEST_BORDER.equals("SAVED_LOWEST_BORDER")) {
          loadData();
       // }
        chicken_cpc.setText("Крц за клик: "+money_per_click);
        chicken_cps.setText("Крц в секунду: "+money_per_time);

        chicken_counter.setText("Наберите "+lowest_border+" чтобы повысить ранг");
        chicken_ranc.setText(getranc(rangcounter));

        scorehandler = new Handler() {   // создание хэндлера
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                kfc_screen_score.setText("Счёт: " + msg.what);
                kfc_screen_score.invalidate();
            }
        };



        AutoClickerThread act = new AutoClickerThread();
        act.start();
        Log.d("Activity: ", "Thread running");





    }



    View.OnTouchListener clicker = new View.OnTouchListener() {
        @SuppressLint("HandlerLeak")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //saveData();


            if(rangcounter<20&&money_score>=lowest_border) {
                rangcounter++;
               // money_per_time=money_per_time+;
                moneyUp();
                money_per_click=money_per_click+1;
                money_score=money_score-lowest_border;
                Log.d("Деньги", "Инкремент");

                lowest_border=lowest_border+(rangcounter*30);
                chicken_cpc.setText("Крц за клик: "+money_per_click);
                chicken_cps.setText("Крц в секунду: "+money_per_time);
                chicken_counter.setText("Наберите "+lowest_border+" чтобы повысить ранг");
                chicken_ranc.setText(getranc(rangcounter));

                if(rangcounter>=20){
                    chicken_counter.setText("");
                }



            }

            money_score = money_score +money_per_click;
            Log.d("Деньги", "Инкремент");
            kfc_screen_score.setText("Счёт: "+Integer.toString(money_score));

            chickenhandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    kfc_screen_wing.setImageResource(msg.what);
                    kfc_screen_wing.invalidate();
                }
            };

            WingClickerThread wct = new WingClickerThread();
            wct.start();


            return false;
        }
    };

    class WingClickerThread extends Thread{
        @Override
        public void run(){
            saveData();
            chickenhandler.sendEmptyMessage(R.drawable.kfc_wing_v1);
            try {

                WingClickerThread.sleep(50);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chickenhandler.sendEmptyMessage(R.drawable.kfc_wing_v3);

        }
    }

    class AutoClickerThread extends Thread{
        @Override
        public void run() {
            while (true) {
                saveData();
                try {
                    AutoClickerThread.sleep(1000);
                    money_score = money_score + money_per_time;
                    Log.d("Деньги", "Инкремент");
                    scorehandler.sendEmptyMessage(money_score);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    void saveData() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAVED_MONEY_SCORE, money_score);
        Log.d("Деньги", "Инкремент");
        editor.putInt(SAVED_MONEY_PER_TIME, money_per_time);
        editor.putInt(SAVED_MONEY_DELAY, money_delay);
        editor.putInt(SAVED_MONEY_PER_CLICK, money_per_click);
        editor.putInt(SAVED_RANGCOUNTER, rangcounter);
        editor.putInt(SAVED_LOWEST_BORDER, lowest_border);
        editor.commit();
    }

    void moneyUp(){
        if(money_per_click%2==0){
            money_per_time++;
        }
    }

    void loadData() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        this.money_score = sharedPreferences.getInt(SAVED_MONEY_SCORE, 0);
        Log.d("Деньги", "Инкремент");
        this.money_per_time = sharedPreferences.getInt(SAVED_MONEY_PER_TIME, 1);
        this.money_delay = sharedPreferences.getInt(SAVED_MONEY_DELAY, 0);
        this.money_per_click = sharedPreferences.getInt(SAVED_MONEY_PER_CLICK, 1);
        this.rangcounter = sharedPreferences.getInt(SAVED_RANGCOUNTER, 0);
        this.lowest_border = sharedPreferences.getInt(SAVED_LOWEST_BORDER, 10);

    }

    public String getranc(int n){

        String[] rancs = new String[21];
//
        rancs[0]="Желторотик";
        rancs[1]="Продвинутый";
        rancs[2]="Босс этой качалки";
        rancs[3]="Полковник";
        rancs[4]="Нига";
        rancs[5]="Профессиональный баскетболист";
        rancs[6]="Андроид";
        rancs[7]="Star Platinum";
        rancs[8]="mr.robot";
        rancs[9]="Джонатан Джостар";
        rancs[10]="Человек, которому нечем заняться";
        rancs[11]="sample text";
        rancs[12]="illuminati";
        rancs[13]="xXx_NaGiBaToR_228";
        rancs[14]="MLG";
        rancs[15]="360 noscope";
        rancs[16]="Миллиардер";
        rancs[17]="Сколько ты там уже накликал?";
        rancs[18]="uTorrent";
        rancs[19]="Steam";
        rancs[20]="Его величество Gabe N";



        return rancs[n];
    }

    public int getMoney_score() {
        return money_score;
    }

    public void setMoney_score(int money_score) {
        this.money_score = money_score;
    }

    public int getMoney_per_time() {
        return money_per_time;
    }

    public void setMoney_per_time(int money_per_time) {
        this.money_per_time = money_per_time;
    }

    public int getMoney_delay() {
        return money_delay;
    }

    public void setMoney_delay(int money_delay) {
        this.money_delay = money_delay;
    }

    public int getMoney_per_click() {
        return money_per_click;
    }

    public void setMoney_per_click(int money_per_click) {
        this.money_per_click = money_per_click;
    }
}
