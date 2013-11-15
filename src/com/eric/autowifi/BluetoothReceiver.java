package com.eric.autowifi;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
			System.out.println("ACTION_ACL_CONNECTED");
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
			System.out.println("ACTION_ACL_DISCONNECTED");
		} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
				.equals(action)) {
			System.out.println("ACTION_ACL_DISCONNECT_REQUESTED");
		} else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED.equals(action)
				&& Utils.getBluetoothA2dpToggle(context)) {
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			Intent i = new Intent(context, A2dpService.class);
			i.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
			context.startService(i);
		}
	}
}
