package com.eric.autowifi;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class BluetoothReceiver extends BroadcastReceiver {
	@SuppressLint("NewApi")
	@Override
	public void onReceive(final Context context, Intent intent) {
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
				System.out.println("ACTION_ACL_CONNECTED");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				System.out.println("ACTION_ACL_DISCONNECTED");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
					.equals(action)) {
				System.out.println("ACTION_ACL_DISCONNECT_REQUESTED");
			} else if (BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED
					.equals(action) && Utils.getBluetoothA2dpToggle(context)) {
				final BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				final String deviceName = device.getName();
				ServiceListener listener = new ServiceListener() {
					public void onServiceDisconnected(int profile) {
					}

					public void onServiceConnected(int profile,
							BluetoothProfile proxy) {
						if (profile == BluetoothProfile.HEADSET) {
							// _headset = (BluetoothHeadset) proxy; //
							// _headset为BluetoothHeadset类型
						} else if (profile == BluetoothProfile.A2DP) {
							BluetoothA2dp a2dp = (BluetoothA2dp) proxy; // _A2dp为BluetoothA2dp类型
							int connectState = a2dp.getConnectionState(device);
							switch (connectState) {
							case BluetoothProfile.STATE_CONNECTING:
								break;
							case BluetoothProfile.STATE_CONNECTED:
								if (Constants.MY_IMEI.equals(Utils
										.getImei(context))
										&& Constants.MY_CAR_A2DP_NAME
												.equals(deviceName)) {
									AudioManager mAudioManager = (AudioManager) context
											.getSystemService(Context.AUDIO_SERVICE);
									int volume = (int) (mAudioManager
											.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.9);
									mAudioManager.setStreamVolume(
											AudioManager.STREAM_MUSIC, volume,
											0);
									Log.d("BluetoothReceiver",
											"MusicAudio set to 90%");
									// TODO
									Toast.makeText(context,
											"MusicAudio set to 90%",
											Toast.LENGTH_LONG).show();
								}
								break;
							case BluetoothProfile.STATE_DISCONNECTED:
								int state = adapter.getState();
								if (state == BluetoothAdapter.STATE_ON) {
									adapter.disable();
									Log.d("BluetoothReceiver",
											"Bluetooth hardware off.");
								}
								if (Constants.MY_IMEI.equals(Utils
										.getImei(context))
										&& Constants.MY_CAR_A2DP_NAME
												.equals(deviceName)) {
									AudioManager mAudioManager = (AudioManager) context
											.getSystemService(Context.AUDIO_SERVICE);
									int volume = (int) (mAudioManager
											.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.4);
									mAudioManager.setStreamVolume(
											AudioManager.STREAM_MUSIC, volume,
											0);
									Log.d("BluetoothReceiver",
											"MusicAudio set to 40%");
									// TODO
									Toast.makeText(context,
											"MusicAudio set to 40%",
											Toast.LENGTH_LONG).show();
								}
								break;
							}
						}
					}
				};
				adapter.getProfileProxy(context, listener,
						BluetoothProfile.A2DP);
				// adapter.getProfileProxy(context, listener,
				// BluetoothProfile.HEADSET);
			}
		}
	}
}
