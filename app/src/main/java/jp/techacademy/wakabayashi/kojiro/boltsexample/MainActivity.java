package jp.techacademy.wakabayashi.kojiro.boltsexample;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


import bolts.Continuation;
import bolts.Task;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.HttpResponseException;

public class MainActivity extends AppCompatActivity {


    private static final String kApiBase = "https://www.google.co.jp/?gws_rd=ssl#"; // an example of Web API

    private final AsyncHttpClient client = new AsyncHttpClient();

    //memo: プログレス
    private ProgressDialog mProgress;

    private EditText mEdittext;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //memo:
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("処理中...");

        mEdittext = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);



        final TextView textView = (TextView) findViewById(R.id.body);
        textView.setText("");
        mProgress.show();
        waterfallRequests();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                String text = mEdittext.getText().toString();
                new GeocoderDataSource(MainActivity.this).findAddresses(text);
                Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();

            }
        });


        //shortwaterfallRequests();
       // new RequestApi(MainActivity.this).shortwaterfallRequests();

      //  new RequestApi().shortwaterfallRequests();

        /*
        new RequestApi(MainActivity.this).test01().continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {

                return new RequestApi(MainActivity.this).test02();
            }
        });
*/
      /*
        new RequestApi(MainActivity.this).test01().onSuccessTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                return new RequestApi(MainActivity.this).test02();
            }
        });
        */

    }


    private Task<String> getApiAsync(String word) {
        final Task<String>.TaskCompletionSource taskSource = Task.create();

        RequestParams params = new RequestParams();
        params.put("q", word);

        client.get(this, kApiBase, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                if (statusCode == 200) {
                    taskSource.setResult(s);
                } else {
                    taskSource.setError(new HttpResponseException(statusCode, s));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        return taskSource.getTask();
    }

    private void shortwaterfallRequests() {
        final TextView textView = (TextView) findViewById(R.id.body);
        textView.append("waterfall\n");

        final long t0 = System.currentTimeMillis();
        getApiAsync("apple").continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                textView.append("apple : ");

                return getApiAsync("banana");
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                textView.append("xxx : ");

                textView.append("elapsed (serial): " + (System.currentTimeMillis() - t0) + "ms\n");

                return null;
            }
        });
    }



    private void waterfallRequests() {
        final TextView textView = (TextView) findViewById(R.id.body);
        textView.append("waterfall\n");

        final long t0 = System.currentTimeMillis();
        getApiAsync("apple").continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                textView.append("apple : ");

                return getApiAsync("banana");
            }
        }).continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                textView.append("banana : ");

                return getApiAsync("beef");
            }
        }).continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                textView.append("beef : ");

                return getApiAsync("xxx");
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                textView.append("xxx : ");

                textView.append("elapsed (serial): " + (System.currentTimeMillis() - t0) + "ms\n");

                return null;
            }
        });

        mProgress.dismiss();
    }


}
