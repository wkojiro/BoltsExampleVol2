package jp.techacademy.wakabayashi.kojiro.boltsexample;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by wkojiro on 2017/04/17.
 */

public class GeocoderDataSource {

    Context context;

    public GeocoderDataSource(Context context) {
        this.context = context;
    }

    public void findAddresses(String query){
        findAddressesAsync(query).continueWith(new Continuation<List<Address>, Void>() {
            @Override
            public Void then(Task<List<Address>> task) throws Exception {
                if (task.isCancelled()) {
                    // Task cancelled
                } else if (task.isFaulted()) {
                    // task failed
                    Exception error = task.getError();
                } else {
                    // the object was saved successfully.
                    List<Address> addresses = task.getResult();

                    Log.d("デバッグ2", String.valueOf(addresses));
                }
                return null;
            }
        });
    }

    public Task<List<Address>> findAddressesAsync(String query) {

        Task<List<Address>>.TaskCompletionSource taskCompletion = Task.create();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(query, 4);
        } catch (IOException e) {
            taskCompletion.setError(e);
        }
        if (addresses != null && addresses.size() > 0) {
            taskCompletion.setResult(addresses);
        } else {
            taskCompletion.setResult(null);
        }
        Log.d("デバッグ1", String.valueOf(taskCompletion.getTask().getResult()));
        return taskCompletion.getTask();
    }

}
