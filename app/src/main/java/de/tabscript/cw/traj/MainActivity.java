package de.tabscript.cw.traj;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
    
    public void startPaint(View view) {
    	startActivity(new Intent(this, FingerPaint.class));
    }

    public void startWithSource(View view) {
        File mPath = new File(Environment.getExternalStorageDirectory() + "/TrajData/");
        FileDialog fileDialog = new FileDialog(this, mPath);
        final Context context = this.getApplicationContext();
        fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                ArrayList<String> wordList = new ArrayList<String>();
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = br.readLine()) != null) {
                        wordList.add(line.trim());
                    }
                    br.close();
                }
                catch (IOException e) {
                    Toast.makeText(context, "Error reading file!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String listName = file.getName().replaceFirst("[.][^.]+$", "");
                intentWithSource(wordList, listName);
            }
        });
        fileDialog.showDialog();
    }

    private void intentWithSource(ArrayList<String> wordList, String listName) {
        Intent i = new Intent(this, FingerPaint.class);
        i.putStringArrayListExtra("words", wordList);
        i.putExtra("listname", listName);
        startActivity(i);
    }
}
