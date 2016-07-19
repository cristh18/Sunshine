package com.example.android.sunshine.app.sync;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.android.sunshine.app.ForecastFragment;
import com.example.android.sunshine.app.Utility;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by cristhian on 4/07/16.
 */
public class JobSchedulerService extends JobService {
    private static final String KEY_RANDOM_VALUE = "random_value";
    private static final String KEY_WEATHER_ICON = "weather_icon";
    private static final String KEY_MAX_TEMP = "max_temp";
    private static final String KEY_MIN_TEMP = "min_temp";
    private static final String KEY_DATE = "date";
    private static final String ITEM_MAX_TEMP = "/temp";
    private static final String LOG_TAG = JobSchedulerService.class.getName();
    private int todayTemp;

    private Handler mJobHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "JobService task running - Today Temp = " + todayTemp, Toast.LENGTH_SHORT).show();
            jobFinished((JobParameters) msg.obj, false);
            return true;
        }
    });

    @Override
    public boolean onStartJob(JobParameters params) {
        todayTemp = getTemp();
        sendToWear(todayTemp);
        mJobHandler.sendMessage(Message.obtain(mJobHandler, 1, params));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mJobHandler.removeMessages(1);
        return false;
    }

    private int getTemp() {
        int range = (10 - 30) + 1;
        return (int) (Math.random() * range) + 10;
    }

    private void sendToWear(int temp) {
        String randomValue = String.valueOf(temp);
        int weatherIconResource = Utility.getIconResourceForWeatherCondition(ForecastFragment.todayWeather.getWeatherId());
        String todayDate = ForecastFragment.todayWeather != null ? ForecastFragment.todayWeather.getTodayDate() : "null";
        String todayMaxTemp = ForecastFragment.todayWeather != null ? ForecastFragment.todayWeather.getTodayMaxTemp() : "null";
        String todayMinTemp = ForecastFragment.todayWeather != null ? ForecastFragment.todayWeather.getTodayMinTemp() : "null";

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                weatherIconResource);
        Asset asset = Utility.toAsset(icon);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(ITEM_MAX_TEMP);

        putDataMapReq.getDataMap().putString(KEY_RANDOM_VALUE, randomValue);
        putDataMapReq.getDataMap().putAsset(KEY_WEATHER_ICON, asset);
        putDataMapReq.getDataMap().putString(KEY_DATE, todayDate);
        putDataMapReq.getDataMap().putString(KEY_MAX_TEMP, todayMaxTemp);
        putDataMapReq.getDataMap().putString(KEY_MIN_TEMP, todayMinTemp);


        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();

        if (ForecastFragment.apiClient != null) {
            Wearable.DataApi.putDataItem(ForecastFragment.apiClient, putDataReq)
                    .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                        @Override
                        public void onResult(DataApi.DataItemResult dataItemResult) {
                            Log.e(LOG_TAG, "Sending image was successful: " + dataItemResult.getStatus()
                                    .isSuccess());
                        }
                    });
        }
    }
}
