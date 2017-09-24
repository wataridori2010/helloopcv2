package com.example.myandroid.helloopcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

//package com.example.opencv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.Object;
import android.content.Loader;

public class MainActivity extends AppCompatActivity {

/*
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
    }


    //  A native method that is implemented by the 'native-lib' native library,
    //  which is packaged with this application.
    //
    public native String stringFromJNI();
*/
    static {
        System.loadLibrary("opencv_java3");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        /* --- OpenCV Managerの呼び出しをコメントアウト --- */
/*      if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else */ {
        //Log.d(TAG, "OpenCV library found inside package. Using it!");
        //mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            // assetsの内容を /data/data/*/files/ にコピーします。
            copyAssets("haarcascades");
        } catch (IOException e) {
            e.printStackTrace();
        }

        CameraView cameraView = new CameraView(this, 90);

        ViewGroup activityMain = (ViewGroup)findViewById(R.id.activity_main);
        activityMain.addView(cameraView);


    }

    private void copyAssets(String dir) throws IOException {
        byte[] buf = new byte[8192];
        int size;

        File dst = new File(getFilesDir(), dir);
        if(!dst.exists()) {
            dst.mkdirs();
            dst.setReadable(true, false);
            dst.setWritable(true, false);
            dst.setExecutable(true, false);
        }

        for(String filename : getAssets().list(dir)) {
            File file = new File(dst, filename);
            OutputStream out = new FileOutputStream(file);
            InputStream in = getAssets().open(dir + "/" + filename);
            while((size = in.read(buf)) >= 0) {
                if(size > 0) {
                    out.write(buf, 0, size);
                }
            }
            in.close();
            out.close();
            file.setReadable(true, false);
            file.setWritable(true, false);
            file.setExecutable(true, false);
        }
    }

}
