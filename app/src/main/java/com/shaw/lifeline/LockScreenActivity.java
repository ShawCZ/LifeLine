package com.shaw.lifeline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LockScreenActivity extends AppCompatActivity {
	private static final String TAG = "LockScreenActivity";
	AppCompatTextView tvTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		setContentView(R.layout.activity_main);
		initView();
	}

	Disposable disposable = null;

	private void initView() {
		tvTime = findViewById(R.id.tv);

		final Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		final int year = calendar.get(Calendar.YEAR);
		Log.d(TAG, "initView: date = " + date.getTime());
		Log.d(TAG, "initView: date = " + System.currentTimeMillis());

		//begin year
		calendar.set(calendar.get(Calendar.YEAR), 0, 1, 0, 0, 0);
		final Date startDate = calendar.getTime();

		//end year
		calendar.set(calendar.get(Calendar.YEAR) + 1, 0, 1, 0, 0, 0);
		Date endDate = calendar.getTime();
		Log.d(TAG, "initView: startDate = " + startDate.toString());
		Log.d(TAG, "initView: endDate = " + endDate.toString());

		final long total = endDate.getTime() - startDate.getTime();
		Log.d(TAG, "initView: total = " + total);

		disposable = Flowable.interval(0, 31536, TimeUnit.MILLISECONDS)
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<Long>() {
					@Override
					public void accept(Long aLong) throws Exception {
						long spend = System.currentTimeMillis() - startDate.getTime();
						double value = div(spend, total, 6);
						double percent = value * 100;
						String result = String.format("%.4f", percent);
						tvTime.setText(String.format("%d已经过去： %s%%", year, result));
					}
				});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		disposable.dispose();
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 *
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
}
