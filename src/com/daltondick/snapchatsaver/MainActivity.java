package com.daltondick.snapchatsaver;

import java.io.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
					// Perform su to get root privileges
					Process p = runtime.exec("su");
					String snapchatData = "/data/data/com.snapchat.android/cache/received_image_snaps";
					DataOutputStream output = new DataOutputStream(p.getOutputStream());
					output.writeBytes("ls " + snapchatData + "\n");
					output.flush();
					InputStream stdout = p.getInputStream();
					byte[] buffer = new byte[1024];
					int read;
					String out = new String();
					while(true){
						read = stdout.read(buffer);
						out += new String(buffer, 0, read);
						if(read < 1024){
							break;
						}
					}
					Log.d("OUTPUT", out.toString());
					//Split up files
					String[] files = out.split("\n");
					
					//Check if folder exists, move files to new folder
					String savePath = Environment.getExternalStorageDirectory().getPath() + "/SavedSnapchats";
					File folder = new File(savePath);
					if(!folder.exists() && !folder.isDirectory()){
						folder.mkdir();
					}
					
					int count = 0;
					for(String file: files){
						File checkFile = new File(savePath + "/" + file.replaceAll(".nomedia", ""));
						if(!checkFile.exists()){
							String command = "cp " + snapchatData + "/" + file + " " + savePath + "/" + file.replace(".nomedia", "") + "\n";
							Log.v("Command", command);
							output.writeBytes(command);
							output.flush();
							count++;
						}
					}
					
					Context context = getApplicationContext();
					int duration = Toast.LENGTH_SHORT;
					if(count > 0){
						Toast.makeText(context, "Saved " + count + " snapchats", duration).show();
					}
					else{
						Toast.makeText(context, "No snapchats found", duration).show();
					}
				}
				catch(Exception e){
					Log.e("ROOT", e.getMessage());
				}
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
