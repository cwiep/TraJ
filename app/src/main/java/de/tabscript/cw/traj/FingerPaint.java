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

package de.tabscript.cw.traj;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class FingerPaint extends Activity {

    private WritingView mWritingView;
    private TextView mTouchToleranceLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finger_paint);
        mWritingView = (WritingView) findViewById(R.id.writing_surface_view);
		mTouchToleranceLabel = (TextView) findViewById(R.id.coords);
        mTouchToleranceLabel.setText("" + mWritingView.getmTouchTolerance());
	}

	public void buttonRaiseTolerance(View v) {
		mWritingView.raiseTouchTolerance();
        updateTouchToleranceLabel();
	}

	public void buttonLowerTolerance(View v) {
		mWritingView.lowerTouchTolerance();
        updateTouchToleranceLabel();
	}

    private void updateTouchToleranceLabel() {
        if (mTouchToleranceLabel != null) {
            mTouchToleranceLabel.setText("" + mWritingView.getmTouchTolerance());
        }
    }

	private static final int ERASE_MENU_ID = Menu.FIRST;
	private static final int SAVE_MENU_ID = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, ERASE_MENU_ID, 0, "Clear").setShortcut('5', 'z');
		menu.add(0, SAVE_MENU_ID, 0, "Save pointlist").setShortcut('5', 'z');

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ERASE_MENU_ID:
			mWritingView.clearCanvas();
			return true;
		case SAVE_MENU_ID:
			savePointListToFile(mWritingView.getPointList());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    private void savePointListToFile(List<Point> pointList) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/TraJData");
        if (!folder.exists()) {
            boolean success = folder.mkdir();
            if(!success) {
                Toast.makeText(this, "Creating folder '<externalStorage>/TraJData' failed.", Toast.LENGTH_SHORT).show();
            }
        }
        FileWriter fw;
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String filename = "traj_" + ts + ".txt";
        try {
            fw = new FileWriter(folder.getAbsolutePath() + "/" + filename, false);
            fw.write(pointList.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
    }
}
