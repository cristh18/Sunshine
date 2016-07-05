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
    private static final String ITEM_MAX_TEMP = "/temp";
    private String maxTemp;
    private TextView textView;
    private final static String LOG_TAG = MainActivity.class.getName();


    /**
     * CLIENTE
     */
    GoogleApiClient apiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        textView = (TextView) findViewById(R.id.text);


        initGoogleApiClient();


        /**
         * ((SINCRONIZACION)) cuando iniciamos la activity, vamos a obtener el contador del MOBILE y vamos a inicializar el del WEAR con el mismo numero
         */

        synchronize();


    }


    private void synchronize() {
        PendingResult<DataItemBuffer> resultado = Wearable.DataApi.getDataItems(apiClient);
        resultado.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {

                for (DataItem dataItem : dataItems) {

                    if (dataItem.getUri().getPath().equals(ITEM_MAX_TEMP)) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);

                        maxTemp = dataMapItem.getDataMap().getString(KEY_WEATHER);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText((maxTemp));

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
                .addConnectionCallbacks(this)//nos notifica cuando estamos conectados
                .addOnConnectionFailedListener(this)// ofrece el resultado del error
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
                        public void run() {//(ACTUALIZACION)CADA CLICK EN TEXTVIEW DEL MOBILE VAMOS A ACTUALIZAR EL TEXTVIEW DEL WEAR
                            textView.setText(maxTemp);
                        }
                    });
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {//algun item a sido borrado

            }


        }
    }

    //<editor-fold desc="CICLO DE VIDA">
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

    //</editor-fold>


}