package com.daltondick.snapchatsaver;

import java.io.*;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.util.Pair;
import de.holetzeck.util.*;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button snapchatButton = (Button)findViewById(R.id.ButtonSaveSnapchats);
        
        snapchatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Runtime runtime = Runtime.getRuntime();
				try {
					String snapchatData = "/data/data/com.snapchat.android/cache/received_image_snaps";
					Pair<Integer, String> ret = ProcessHelper.runCmd(true, "su", "-c", "ls " + snapchatData);
					//Split up files
					String[] files = ret.second.split("\n");
					
					//Check if folder exists, move files to new folder
					String savePath = Environment.getExternalStorageDirectory().getPath() + "/SavedSnapchats";
					File folder = new File(savePath);
					if(!folder.exists() && !folder.isDirectory()){
						folder.mkdir();
					}
					
					//Copy over files
					int count = 0;
					boolean first = true;
					for(String file: files){
						if(!first){
							String newPath = savePath + "/" + file.replaceAll(".nomedia", "");
							File checkFile = new File(newPath);
							if(!checkFile.exists()){
								String command = "cp " + snapchatData + "/" + file + " " + newPath;
								ProcessHelper.runCmd(true, "su", "-c", command);
								count++;
							}
						}
						else{
							first = !first;
						}
					}
					
					makeToast(count);
				}
				catch(Exception e){
				}
			}
		});
    }
    
    public void makeToast(int count){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		if(count > 0){
			Toast.makeText(context, "Saved " + count + " snapchats", duration).show();
		}
		else{
			Toast.makeText(context, "No snapchats found", duration).show();
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
