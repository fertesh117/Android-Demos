/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.PathDemo;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class PathEffects extends GraphicsActivity implements
		ColorPickerDialog.OnColorChangedListener {
	static Paint mPaint;
	private static int[] mColors;
	private static final int COLOR_MENU_ID = Menu.FIRST;
	//private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
	//private static final int BLUR_MENU_ID = Menu.FIRST + 2;
	//private static final int ERASE_MENU_ID = Menu.FIRST + 3;
	//private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new SampleView(this));
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
		//menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
		//menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
		//menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
		//menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

		/****
		 * Is this the mechanism to extend with filter effects? Intent intent =
		 * new Intent(null, getIntent().getData());
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions( Menu.ALTERNATIVE, 0, new ComponentName(this,
		 * NotesList.class), null, intent, 0, null);
		 *****/
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mPaint.setXfermode(null);
		mPaint.setAlpha(0xFF);

		switch (item.getItemId()) {
		case COLOR_MENU_ID:
			new ColorPickerDialog(this, this, mPaint.getColor()).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void colorChanged(int color) {
		//mPaint.setColor(color);
		mColors[5]=color;
	}

	private static class SampleView extends View {

		private Path mPath;
		private PathEffect[] mEffects;

		
		private float mPhase;

		private static PathEffect makeDash(float phase) {
			return new DashPathEffect(new float[] { 15, 5, 8, 5 }, phase);			
		}

		private static void makeEffects(PathEffect[] e, float phase) {
			e[0] = null; // no effect
			e[1] = new CornerPathEffect(10);
			e[2] = new DashPathEffect(new float[] { 10, 5, 5, 5 }, phase);
			e[3] = new PathDashPathEffect(makePathDash(), 12, phase,
					PathDashPathEffect.Style.ROTATE);
			e[4] = new ComposePathEffect(e[2], e[1]);
			e[5] = new ComposePathEffect(e[3], e[1]);
		}

		public SampleView(Context context) {
			super(context);
			setFocusable(true);
			setFocusableInTouchMode(true);

			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(6);

			mPath = makeFollowPath();

			mEffects = new PathEffect[6];

			mColors = new int[] { Color.BLACK, Color.RED, Color.BLUE,
					Color.GREEN, Color.MAGENTA, Color.BLACK };
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);

			RectF bounds = new RectF();
			mPath.computeBounds(bounds, false);
			// canvas.translate(10 - bounds.left, 10 - bounds.top);

			makeEffects(mEffects, mPhase);
			mPhase -= 1;
			invalidate();

			// for (int i = 0; i < mEffects.length; i++) {
			mPaint.setPathEffect(mEffects[5]);
			mPaint.setColor(mColors[5]);
			canvas.drawPath(mPath, mPaint);

			// canvas.translate(0, 28);
			// }
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
				mPath = makeFollowPath();
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

		private static Path makeFollowPath() {
			Path p = new Path();
			p.moveTo(0, 0);
			for (int i = 1; i <= 15; i++) {
				p.lineTo(i * 20, (float) Math.random() * 35);
			}
			return p;
		}

		private static Path makePathDash() {
			Path p = new Path();
			p.moveTo(4, 0);
			p.lineTo(0, -4);
			p.lineTo(8, -4);
			p.lineTo(12, 0);
			p.lineTo(8, 4);
			p.lineTo(0, 4);
			return p;
		}

		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;

		private void touch_start(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touch_up() {
			mPath.lineTo(mX, mY);
			// commit the path to our offscreen
			// --mCanvas.drawPath(mPath, mPaint);
			// kill this so we don't double draw
			// mPath.reset();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				touch_start(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
			}
			return true;
		}

	}

}
