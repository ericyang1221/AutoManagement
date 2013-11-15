package com.eric.autowifi;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class A2dpService extends Service {
	private Context context;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		context = this.getApplicationContext();
		super.onCreate();
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("A2dpService", "onStartCommand");
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter != null) {
			final BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (device != null) {
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
											"MusicAudio set to 98%");
									// TODO
									Toast.makeText(context,
											"MusicAudio set to 98%",
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
						adapter.closeProfileProxy(BluetoothProfile.A2DP, proxy);
					}
				};
				adapter.getProfileProxy(context, listener,
						BluetoothProfile.A2DP);
				// adapter.getProfileProxy(context, listener,
				// BluetoothProfile.HEADSET);
			}
		}
		stopSelf();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Log.d("A2dpService", "onDestroy");
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}