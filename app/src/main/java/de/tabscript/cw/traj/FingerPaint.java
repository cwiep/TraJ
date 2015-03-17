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
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FingerPaint extends Activity {

    private WritingView mWritingView;
    private TextView mTouchToleranceLabel;
    private TextView mCurrentWordView;
    private TextView mProgressView;
    private ArrayList<String> mWordList;
    private String mListName;
    private int mCurrentWordIndex;
    private boolean mUsingWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finger_paint);
        mWritingView = (WritingView) findViewById(R.id.writing_surface_view);
        mTouchToleranceLabel = (TextView) findViewById(R.id.coords);
        mTouchToleranceLabel.setText("" + mWritingView.getmTouchTolerance());
        Intent intent = getIntent();
        mWordList = intent.getStringArrayListExtra("words");
        mListName = intent.getStringExtra("listname");
        if (mWordList != null) {
            mUsingWords = true;
            mCurrentWordIndex = 0;
            mCurrentWordView = (TextView) findViewById(R.id.current_word_textview);
            mCurrentWordView.setVisibility(View.VISIBLE);
            mCurrentWordView.setText(mWordList.get(mCurrentWordIndex));
            mProgressView = (TextView) findViewById(R.id.progress_textview);
            mProgressView.setVisibility(View.VISIBLE);
            mProgressView.setText("" + (mCurrentWordIndex + 1) + "/" + mWordList.size());
        }
    }

    public void onRaiseToleranceClicked(View v) {
        mWritingView.raiseTouchTolerance();
        updateTouchToleranceLabel();
    }

    public void onLowerToleranceClicked(View v) {
        mWritingView.lowerTouchTolerance();
        updateTouchToleranceLabel();
    }

    private void updateTouchToleranceLabel() {
        if (mTouchToleranceLabel != null) {
            mTouchToleranceLabel.setText("" + mWritingView.getmTouchTolerance());
        }
    }

    public void onClearButtonClicked(View v) {
        mWritingView.clearCanvas();
    }

    public void onSaveButtonClicked(View v) {
        savePointListToFile(mWritingView.getPointList());
        if (mUsingWords) {
            ++mCurrentWordIndex;
            if(mCurrentWordIndex < mWordList.size()) {
                mCurrentWordView.setText(mWordList.get(mCurrentWordIndex));
                mProgressView.setText("" + (mCurrentWordIndex + 1) + "/" + mWordList.size());
            } else {
                Toast.makeText(this, "All done. Congratulations.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        mWritingView.clearCanvas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void savePointListToFile(List<PenPoint> pointList) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/TraJData");
        if (!folder.exists()) {
            boolean success = folder.mkdir();
            if (!success) {
                Toast.makeText(this, "Creating folder '<externalStorage>/TraJData' failed.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        File docFolder = new File(Environment.getExternalStorageDirectory() + "/TraJData/" + mListName);
        if (mUsingWords) {
            if (!docFolder.exists()) {
                boolean success = docFolder.mkdir();
                if (!success) {
                    Toast.makeText(this, "Creating folder '<externalStorage>/TraJData" + mListName + "/' failed!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // oh goood... what am I doing...
            folder = docFolder;
        }
        FileWriter fw;
        String filename = buildFilename();
        try {
            fw = new FileWriter(folder.getAbsolutePath() + "/" + filename, false);
            if (mUsingWords) {
                fw.write(mWordList.get(mCurrentWordIndex) + "\n");
            }
            for (PenPoint p : pointList) {
                fw.write("" + p.x + " " + p.y + " " + (p.isPenUp() ? "1" : "0") + "\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
    }

    private String buildFilename() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String filename = "" + ts;
        if (mUsingWords) {
            filename += "_" + mWordList.get(mCurrentWordIndex);
        }
        return filename + ".txt";
    }
}
