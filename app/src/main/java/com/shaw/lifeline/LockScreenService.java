package com.shaw.lifeline;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created on 2018/12/14.
 *
 * @author XCZ
 */
public class LockScreenService extends Service {
	private static final String TAG = "LockScreenService";

	private ScreenBroadcastReceiver receiver = null;

	@androidx.annotation.Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		receiver = new ScreenBroadcastReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

	class ScreenBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "onReceive: ");
			String action = intent.getAction();
			if (TextUtils.equals(action, Intent.ACTION_SCREEN_OFF)) {
				Intent intent1 = new Intent(context, LockScreenActivity.class);
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(intent1);
			}
		}
	}
}
