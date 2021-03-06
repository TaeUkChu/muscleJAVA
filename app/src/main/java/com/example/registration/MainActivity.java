package com.example.registration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static String userID;
    public static String userperiod;
    private AlertDialog dialog; // 알림창

    ImageView [] imageViews = new ImageView[5];
    Congestion congestion = new Congestion();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = getIntent().getStringExtra("userID");
        final Button EntranceButton = findViewById(R.id.EntranceButton);
        final Button ExitButton = findViewById(R.id.ExitButton);
        final Button RefactButton = findViewById(R.id.RefactButton);
        final Button LogoutButton = findViewById(R.id.LogoutButton);
        final Button PeriodButton = findViewById(R.id.PeriodButton);
        final Button CongestionButton = findViewById(R.id.CongestionButton);
        final TextView textView= findViewById(R.id.numbertext);

        //입장하기 버튼 - 누르면 userID를 서버에 보내고 서버는 boolean값을 바꿔줌.
        EntranceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EntranceButton.setEnabled(false);
                ExitButton.setEnabled(true);
                //결과 출력
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {   //해당 결과 받아옴
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {  //입장이 성공했을 때
                                Toast.makeText(MainActivity.this,"입장 완료",Toast.LENGTH_SHORT).show();
                                congestion.condition(Integer.parseInt(textView.getText().toString()));
                            }
                            else{
                                Toast.makeText(MainActivity.this,"입장 실패",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e)  //예외처리
                        {
                            e.printStackTrace();
                        }
                    }
                };
                // EntranceRequest 클래스의 queue 형태로 DB에 전달
                EntranceRequest EntranceRequest = new EntranceRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);  //큐에 담음
                queue.add(EntranceRequest);
            }
        });

        //퇴장하기 버튼 (입장과 반대)
        ExitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EntranceButton.setEnabled(true);
                ExitButton.setEnabled(false);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {   //해당 결과 받아옴
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {  //퇴장이 성공했을 때

                                Toast.makeText(MainActivity.this,"퇴장 성공",Toast.LENGTH_SHORT).show();
                                congestion.condition(Integer.parseInt(textView.getText().toString()));
                            }
                            else{
                                Toast.makeText(MainActivity.this,"퇴장 실패",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e)  //예외처리
                        {
                            e.printStackTrace();
                        }
                    }
                };
                // ExitRequest 클래스의 queue 형태로 DB에 전달
                ExitRequest ExitRequest = new ExitRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);  //큐에 담음
                queue.add(ExitRequest);
            }
        });
        //새로 고침 버튼을 이용한 데이터 요청 + 가져오기

        /*
        //테스트 코드
        RefactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                imageView3.setVisibility(View.VISIBLE);
                textView.setText("바뀜");
            }});
            */

        //에러발생
        RefactButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {   //해당 결과 받아옴
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("response");
                            JSONObject object = jsonArray.getJSONObject(0);
                            String sum = object.getString("sum");

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            dialog = builder.setMessage("새로고침 되었습니다.")
                                    .setPositiveButton("확인", null)
                                    .create();
                            dialog.show();  //다이얼로그 실행

                            congestion.condition(Integer.parseInt(sum));

                        } catch (Exception e)  //예외처리
                        {
                            e.printStackTrace();
                        }
                    }
                };
                // EntranceRequest 클래스의 queue 형태로 DB에 전달
                EntranceSumRequest EntranceSumRequest = new EntranceSumRequest(responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);  //큐에 담음
                queue.add(EntranceSumRequest);
            }
        });

        PeriodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if (success) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("response");

                                int count = 0;

                                while (count < jsonArray.length()) {
                                    JSONObject object = jsonArray.getJSONObject(count);
                                    userperiod = object.getString("userperiod");
                                    count++;
                                }
                                //Intent로 메인액티비티로 넘겨줌
                                Intent Periodintent = new Intent(MainActivity.this, MainActivity2.class);
                                //(개별추가)
                                Periodintent.putExtra("userID", userID);
                                Periodintent.putExtra("userPeriod", userperiod);
                                MainActivity.this.startActivity(Periodintent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            dialog = builder.setMessage("실패.")
                                    .setNegativeButton("확인", null)
                                    .create();
                            dialog.show();
                        }
                    }
                };
                PeriodRequest periodRequest = new PeriodRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(periodRequest);*/
            }
        });

        //로그 아웃 버튼
        LogoutButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent Logoutintent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(Logoutintent);
                finish();
            }
        });

        //백그라운드 쓰레드를 이용한 데이터 파싱
        new BackgroundTask().execute();
    }
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target;

        protected void onPreExecute(){
            target = "http://musclejava.cafe24.com/EntranceSum.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try{
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null){
                    stringBuilder.append(temp+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result){
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                int count = 0;
                String sum;

                while(count < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(count);
                    sum = object.getString("sum");
                    int IntSum = Integer.parseInt(sum);
                    congestion.condition(IntSum);
                    count++;
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    class Congestion{
        void condition(int sum){
            TextView TextView = findViewById(R.id.numbertext);
            ImageView [] imageViews = {findViewById(R.id.imageView1),
                    findViewById(R.id.imageView2),
                    findViewById(R.id.imageView3),
                    findViewById(R.id.imageView4),
                    findViewById(R.id.imageViewX)
            };
            //20이 최대치
            int temp = (20 - sum) / 5;
            for (int i = 0; i<imageViews.length; i++){
                imageViews[i].setVisibility(View.INVISIBLE);
            }

            switch (temp) {
                case 0:   //실제 인원: 16~20
                    imageViews[0].setVisibility(View.VISIBLE);
                    TextView.setTextColor(Color.parseColor("#ffcc0000")); //빨강
                    break;
                case 1:   //11~15
                    imageViews[1].setVisibility(View.VISIBLE);
                    TextView.setTextColor(Color.parseColor("#ffcc0000")); //빨강
                    break;
                case 2:   //6~10
                    imageViews[2].setVisibility(View.VISIBLE);
                    TextView.setTextColor(Color.parseColor("#ffff8800")); //주황
                    break;
                case 3:   //1~5
                    imageViews[3].setVisibility(View.VISIBLE);
                    TextView.setTextColor(Color.parseColor("#ffffbb33")); //노랑
                    break;
                case 4: // 0
                    imageViews[3].setVisibility(View.VISIBLE);
                    TextView.setTextColor(Color.parseColor("#ff669900")); //초록
                    break;
                default:
                    imageViews[4].setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this,"오류 발생",Toast.LENGTH_SHORT).show();
                    break;
                    //TextView.setTextColor(Color.parseColor("#ffffbb33")); //노랑
                    //TextView.setTextColor(Color.parseColor("#ff99cc00")); //연두
                    //Value:	#ff33b5e5 // 파랑
            }
            TextView.setText("["+ sum + "명 / 10명] \n" +"수용 가능:"+ (20-sum)*5 +"%");

        }
    }
}
