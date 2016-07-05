package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String KEY_WEATHER = "weather";
    private static final String KEY_MIN_TEMP = "min_temp";
    private static final String KEY_DATE = "date";
    private static final String ITEM_MAX_TEMP = "/temp";
    private String todayDate;
    private String maxTemp;
    private String minTemp;
    private TextView mTextViewTodayDate;
    private TextView mTextViewMaxTemp;
    private TextView mTextViewMinTemp;
    private final static String LOG_TAG = MainActivity.class.getName();

    GoogleApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        initViews();
        initGoogleApiClient();
        synchronize();


    }

    private void initViews() {
        mTextViewTodayDate = (TextView) findViewById(R.id.textView_todayDate);
        mTextViewMaxTemp = (TextView) findViewById(R.id.textView_maxTemp);
        mTextViewMinTemp = (TextView) findViewById(R.id.textView_minTemp);
    }


    private void synchronize() {
        PendingResult<DataItemBuffer> result = Wearable.DataApi.getDataItems(apiClient);
        result.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {

                for (DataItem dataItem : dataItems) {

                    if (dataItem.getUri().getPath().equals(ITEM_MAX_TEMP)) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);

                        todayDate = dataMapItem.getDataMap().getString(KEY_DATE);
                        maxTemp = dataMapItem.getDataMap().getString(KEY_WEATHER);
                        minTemp = dataMapItem.getDataMap().getString(KEY_MIN_TEMP);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextViewTodayDate.setText((todayDate));
                                mTextViewMaxTemp.setText((maxTemp));
                                mTextViewMinTemp.setText((minTemp));

                            }
                        });
                    }
                }
                dataItems.release();
            }
        });
    }

    private void initGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(LOG_TAG, "onConnected");
        Wearable.DataApi.addListener(apiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "onConnectionSuspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "onConnectionFailed");
    }

    @Override
    public void onDataChanged(DataEventBuffer eventos) {
        for (DataEvent event : eventos) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals(ITEM_MAX_TEMP)) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    maxTemp = dataMap.getString(KEY_WEATHER);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewTodayDate.setText((todayDate));
                            mTextViewMaxTemp.setText((maxTemp));
                            mTextViewMinTemp.setText((minTemp));
                        }
                    });
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }


    @Override
    protected void onStop() {
        Wearable.DataApi.removeListener(apiClient, this);
        if (apiClient != null && apiClient.isConnected()) {
            apiClient.disconnect();
        }
        super.onStop();
    }
}