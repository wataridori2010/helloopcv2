package com.example.myandroid.helloopcv;
import android.widget.ImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;// cameraのプレビューの処理
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraView";

    private int degrees;
    private Camera camera;
    private int[] rgb;
    private Bitmap bitmap;
    private Bitmap bitmap2;
    private Mat image;
    private Mat image2;
    private CascadeClassifier detector;
    private MatOfRect objects;
    private List<RectF> faces = new ArrayList<RectF>();

    public CameraView(Context context, int displayOrientationDegrees) {
        super(context);
        setWillNotDraw(false);
        getHolder().addCallback(this);

        String filename = context.getFilesDir().getAbsolutePath() + "/haarcascades/haarcascade_frontalface_alt.xml";
        detector = new CascadeClassifier(filename);
        objects = new MatOfRect();
        degrees = displayOrientationDegrees;


    }

	/*
	 * SurfaceHolder.Callback
	 */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: holder=" + holder);

        if(camera == null) {
            camera = Camera.open(0);
        }
        camera.setDisplayOrientation(degrees);
        camera.setPreviewCallback(this);
        try {
            camera.setPreviewDisplay(holder);
        } catch(IOException e) {
            e.printStackTrace();
        }

        Camera.Parameters params = camera.getParameters();
        for(Camera.Size size : params.getSupportedPreviewSizes()) {
            Log.i(TAG, "preview size: " + size.width + "x" + size.height);
        }
        for(Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i(TAG, "picture size: " + size.width + "x" + size.height);
        }
        params.setPreviewSize(640, 480);
        camera.setParameters(params);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: holder=" + holder + ", format=" + format + ", width=" + width + ", height=" + height);

        if(image != null) {
            image.release();
            image = null;
        }
        if(bitmap != null) {
            if(!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
        if(rgb != null) {
            rgb = null;
        }
        faces.clear();
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: holder=" + holder);

        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        if(image != null) {
            image.release();
            image = null;
        }
        if(bitmap != null) {
            if(!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
        if(rgb != null) {
            rgb = null;
        }
        faces.clear();
    }

	/*
	 * SurfaceHolder.Callback
	 */

	// カメラのプレビュー画像の受け取り
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d(TAG, "onPreviewFrame: ");

        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
        Log.d(TAG, "onPreviewFrame: width=" + width + ", height=" + height);

        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample);
        Bitmap bitmap = decode(data, width, height, degrees);
        if(degrees == 90) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        if(image == null) {
            image = new Mat(height, width, CvType.CV_8U, new Scalar(4));
        }


        // gary image conversion

        Utils.bitmapToMat(bitmap, image);

        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY);

        //Utils.matToBitmap(image, bitmap);

        if(bitmap2 == null) {
            bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Utils.matToBitmap(image, bitmap2);

        //ImageView img=(ImageView)findViewById(R.id.activity_main);
        //ViewGroup activityMain = (ViewGroup)findViewById(R.id.activity_main);
        //img.setImageBitmap(bitmap);

        /*
        // face detection

        // conversion from bitmap to Mat
        Utils.bitmapToMat(bitmap, image);

        // detection
        detector.detectMultiScale(image, objects);


        // update face instance
        faces.clear();
        for(org.opencv.core.Rect rect : objects.toArray()) {
            float left = (float)(1.0 * rect.x / width);
            float top = (float)(1.0 * rect.y / height);
            float right = left + (float)(1.0 * rect.width / width);
            float bottom = top + (float)(1.0 * rect.height / height);
            faces.add(new RectF(left, top, right, bottom));
        }
        */
        invalidate();
    }

	/*
	 * View
	 */

    @Override
    protected void onDraw(Canvas canvas) {
        //もともとSurfaceView は setWillNotDraw(true) なので super.onDraw(canvas) を呼ばなくてもよい。


        Log.d(TAG, "onDraw");
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        int width = getWidth();
        int height = getHeight();



//        if(bitmap2 == null) {
//            bitmap2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//            canvas.drawBitmap(bitmap2, 10, 10, null);
//        }


        //if(bitmap != null) {
        if(bitmap2 != null) {
            //canvas.drawBitmap(bitmap, width, height, paint);
            //canvas.drawBitmap(bitmap2, width, height, paint);
            //canvas.drawBitmap(bitmap2, 0, 0, paint);

            int width_ = camera.getParameters().getPreviewSize().width;
            float scale_gain = (float)width / (float)width_;
            canvas.scale(scale_gain, scale_gain);
            canvas.drawBitmap(bitmap2, 0, 0, null);

            //canvas.drawBitmap(bitmap, new Rect(width / 2, 0, width, height),new Rect(50, 50, width / 4 + 50, height / 2 + 50), null);
        }

        RectF r = new RectF(0, 0, width , height);
        canvas.drawRect(r, paint);


        /*
        // Drwa Rectangle
        for(RectF face : faces) {
            RectF r = new RectF(width * face.left, height * face.top, width * face.right, height * face.bottom);
            canvas.drawRect(r, paint);
        }
        */

    }

	/*
	 *
	 */

    /* * Camera.PreviewCallback.onPreviewFrame で渡されたデータを Bitmap に変換します。
     *
     * @param data
     * @param width
     * @param height
     * @param degrees
     * @return
    */
    private Bitmap decode(byte[] data, int width, int height, int degrees) {
        if (rgb == null) {
            rgb = new int[width * height];
        }

        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) data[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & data[uvp++]) - 128;
                    u = (0xff & data[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }

        if(degrees == 90) {
            int[] rotatedData = new int[rgb.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    rotatedData[x * height + height - y - 1] = rgb[x + y * width];
                }
            }
            int tmp = width;
            width = height;
            height = tmp;
            rgb = rotatedData;
        }

        if(bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }

        bitmap.setPixels(rgb, 0, width, 0, 0, width, height);
        return bitmap;
    }
}