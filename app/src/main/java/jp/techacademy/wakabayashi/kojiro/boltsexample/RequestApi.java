package jp.techacademy.wakabayashi.kojiro.boltsexample;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

/**
 * Created by wkojiro on 2017/04/14.
 */

public class RequestApi {

    private Context mContext;

    public RequestApi(Context context){

        mContext = context;

    }


    public Task<String> test01() {
        final TaskCompletionSource<String> taskresult = new TaskCompletionSource<>();

        String s = "test01";

        taskresult.setResult(s); //これがないと先に進まない。
        Log.d("debug","test01");
        toast(s);



        return taskresult.getTask();

    }

    public  Task<String> test02() {
        final TaskCompletionSource<String> taskresult = new TaskCompletionSource<>();
        String s = "test02";
        taskresult.setResult(s);
        Log.d("debug","test02");
        toast(s);



        return taskresult.getTask();
    }

    public  Task<String> test03() {
        final TaskCompletionSource<String> taskresult = new TaskCompletionSource<>();
        String s = "test03";
        taskresult.setResult(s);
        Log.d("debug","test03");
        toast(s);
        return taskresult.getTask();
    }

    public void toast(String s){

        Toast.makeText(mContext,s,Toast.LENGTH_SHORT).show();
    }

    public void shortwaterfallRequests() {

        //test01をまず処理し、
        test01().continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                //test02に引き継ぎ、
                return test02();
            }
        }).continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {
                //test03に引き継ぎ、
                return test03();
            }
        }).continueWithTask(new Continuation<String, Task<String>>() {
            @Override
            public Task<String> then(Task<String> task) throws Exception {

                //終了する
                return null;
            }
        });
    }
}
