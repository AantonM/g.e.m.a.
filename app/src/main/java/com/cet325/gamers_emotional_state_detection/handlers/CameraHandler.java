package com.cet325.gamers_emotional_state_detection.handlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CameraHandler {

    Context context;

    //Input image state
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //Camera variables
    private String cameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    //Object used to store images
    private ImageHolder imageHolder;

    //Output file variables
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    //the orientation of the device
    int rotation;
    //bitmap of the taken photo
    private Bitmap bitmapImage;

    //camera state callback
    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    public CameraHandler(Context context) {
        this.context = context;
    }

    public void openCameraPicture() {
        startBackgroundThread();
        openCamera();
        Log.d("DevDebug", "CameraHandler: Camera opened.");
    }

    public void closeCameraPicture() {
        stopBackgroundThread();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void takePicture() {
        Log.d("DevDebug", "CameraHandler: Inside TakePicture");
        if (cameraDevice == null) {
            return;
        }

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        imageHolder = ImageHolder.getInstance();

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null)
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);

            //Capture image with custom size
            int width = 640;
            int height = 480;
            //The picture capture is with lowest possible resolution, but this will afect the recognition results
//            if (jpegSizes != null && jpegSizes.length > 0) {
//                width = jpegSizes[jpegSizes.length-1].getWidth();
//                height = jpegSizes[jpegSizes.length-1].getHeight();
//            }


            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            //Check orientation base on device TODO
            //rotation = getWindowManager().getDefaultDisplay().getRotation();

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Log.d("DevDebug", "onImageAvailable: Picture has been taken");
                    Image image = null;
                    //***TODO: delete file storage - currently used while developing
                    try {
                        //***


                        image = reader.acquireLatestImage();

                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);

                        //***TODO: delete file storage - currently used while developing
                        save(bytes);
                        //***

                        bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                        bitmapImage = rotateImage(bitmapImage, 270);

                        //Save image to holder object
                        imageHolder.setImage(bitmapImage);
                        Log.d("DevDebug", "CameraHandler: Image holder size:" + imageHolder.getImageNumber());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                //***TODO: delete file storage - currently used while developing
                private void save(byte[] bytes) throws IOException {
                    File folder = new File(Environment.getExternalStorageDirectory() + "/face_pictures");

                    boolean success = true;

                    if (!folder.exists()) {
                        success = folder.mkdir();
                    }

                    if (success) {
                        file = new File(folder + "/" + UUID.randomUUID().toString() + ".jpg");
                    } else {
                        file = new File(Environment.getExternalStorageDirectory() + "/" + UUID.randomUUID().toString() + ".jpg");
                    }

                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                        Log.d("DevDebug", "CameraHandler: Image is saved to external memory.");
                    } finally {
                        if (outputStream != null)
                            outputStream.close();
                    }
                }
                //***
            };


            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession){}

            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            //the id of the front-facing camera 0:back-facing 1:front-facing
            cameraId = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        try {
            if(mBackgroundThread != null){
                mBackgroundThread.quitSafely();
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
