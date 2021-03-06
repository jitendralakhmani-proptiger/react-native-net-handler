
package com.reactlibrary;

import android.content.BroadcastReceiver;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;


public class NetworkBroadcastReceiver extends BroadcastReceiver {
    private static final String EVENT_CHANGE = "connectionChange";
    private ReactApplicationContext mContext = null;
    DeviceEventManagerModule.RCTDeviceEventEmitter jsModuleEventEmitter = null;

    public NetworkBroadcastReceiver(ReactApplicationContext context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn =  (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo receivedInfo = conn.getActiveNetworkInfo();
        boolean connStatus = (receivedInfo != null && receivedInfo.isConnected());
        this.setConnectionStatus(connStatus);
    }

    private void setConnectionStatus(boolean status) {
        NetworkConnection netInfo = NetworkConnection.getInstance(mContext);
        boolean currentStatus = netInfo.getConnectionStatus();
        WritableMap receivedMessage = Arguments.createMap();
        receivedMessage.putBoolean("status", status);
        if (null==jsModuleEventEmitter){
            jsModuleEventEmitter =
                mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        } 

        if(status!=currentStatus){
            jsModuleEventEmitter.emit(EVENT_CHANGE, receivedMessage);
        }

        if (netInfo != null) {
            netInfo.setNetConnected(status);
        }
    }
}
