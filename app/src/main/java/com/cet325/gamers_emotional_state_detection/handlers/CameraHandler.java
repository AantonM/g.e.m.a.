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
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import com.cet325.gamers_emotional_state_detection.holders.ImageHolder;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * CameraHandler is responsible to handle all the work regarding the picture taking process.
 * It makes a use of Camera2 API.
 */
public class CameraHandler {

    private Context context;

    //Camera variables
    private String cameraId;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;

    //Object used to store images
    private ImageHolder imageHolder;

    //Threads
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    //bitmap of the taken photo
    private Bitmap bitmapImage;

    //camera state callback
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
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

    /**
     * Constructor.
     * Manager - CameraManager
     *
     * @param context - Current context
     */
    public CameraHandler(Context context) {
        this.context = context;
    }

    /**
     * This method open the camera lences and starts the thread required to use the camera.
     */
    public void openCameraPicture() {
        startBackgroundThread();
        openCamera();
        Log.d("DevDebug", "CameraHandler: Camera opened.");
    }

    /**
     * This method stops the backgroud thread used by the camera which also closes the camera
     */
    public void closeCameraPicture() {
        stopBackgroundThread();
    }

    /**
     * This method takes a picture using the front-facing camera and saves it into a holder.
     * <p>
     * TargetApi - LOLLIPOP (21)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void takePicture() {
        Log.d("DevDebug", "CameraHandler: Inside TakePicture");

        //return if there is no camera attached
        if (cameraDevice == null) {
            return;
        }

        //initialise the camera manager.
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
            if (jpegSizes != null && jpegSizes.length > 0) {
                width = jpegSizes[jpegSizes.length-1].getWidth();
                height = jpegSizes[jpegSizes.length-1].getHeight();
            }

            //definition of the image reader
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurface = new ArrayList<>(2);
            outputSurface.add(reader.getSurface());

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {

                /**
                 * {@inheritDoc}
                 *
                 * @param imageReader - the ImageReader the callback is associated with.
                 */
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Log.d("DevDebug", "onImageAvailable: Picture has been taken");
                    Image image = null;

                    //get the image
                    image = reader.acquireLatestImage();

                    //save in into a byte array
                    ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.capacity()];
                    buffer.get(bytes);

                    //convert the image to bitmap
                    bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);

                    //rotate the image
                    bitmapImage = rotateImage(bitmapImage, 270);

                    //Save image to holder object
                    imageHolder.setImage(bitmapImage);
                    Log.d("DevDebug", "CameraHandler: Image holder size:" + imageHolder.getImageNumber());
                }
            };


            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                /** {@inheritDoc} */
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            };

            cameraDevice.createCaptureSession(outputSurface, new CameraCaptureSession.StateCallback() {
                /** {@inheritDoc} */
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                /** {@inheritDoc} */
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                }

            }, mBackgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method rotates the taken picture. In android pictures are by default are rotated.
     *
     * @param source - Bitmap: the image that needs to be rotated
     * @param angle  - Float: rotation angle
     * @return Bitmap - rotated image
     */
    private static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * This method opens the camera in advance.
     * <p>
     * TargetApi - LOLLIPOP (21)
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openCamera() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            //the id of the front-facing camera 0:back-facing 1:front-facing
            cameraId = manager.getCameraIdList()[1];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;

            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Strat the background thread required for the picture taking process.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stop the background thread required for the picture taking process.
     */
    private void stopBackgroundThread() {
        try {
            if (mBackgroundThread != null) {
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
