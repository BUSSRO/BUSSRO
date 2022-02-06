/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.detection;

import static android.os.VibrationEffect.EFFECT_HEAVY_CLICK;
import static android.os.VibrationEffect.EFFECT_TICK;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.location.Location;
import android.media.AudioManager;
import android.media.ImageReader.OnImageAvailableListener;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tensorflow.lite.examples.detection.customview.OverlayView;
import org.tensorflow.lite.examples.detection.customview.OverlayView.DrawCallback;
import org.tensorflow.lite.examples.detection.env.BorderedText;
import org.tensorflow.lite.examples.detection.env.ImageUtils;
import org.tensorflow.lite.examples.detection.env.Logger;
import org.tensorflow.lite.examples.detection.tflite.Detector;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;
import org.tensorflow.lite.examples.detection.tracking.MultiBoxTracker;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 720;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE_BUS = "detect"; // bus_detect_0
    private static final String TF_OD_API_LABELS_FILE_BUS = "labelmap.txt";
    private static final String TF_OD_API_MODEL_FILE_BELL = "model"; // bell_detect
    private static final String TF_OD_API_LABELS_FILE_BELL = "labelmap2.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(960, 720);
    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Detector detector;
    private TextRecognizer recognizer;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;

    //
    private Paint textPaint;
    private Paint rectPaint;

    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        final int[] cropSize = {TF_OD_API_INPUT_SIZE};

        try {
            if (fromWhere.equals("BoardingDialog")) {
                // 카메라 기능
                detector =
                        TFLiteObjectDetectionAPIModel.create(
                                getApplicationContext(),
                                TF_OD_API_MODEL_FILE_BUS + ".tflite",
//                                            model.getFile().getPath(),
                                TF_OD_API_LABELS_FILE_BUS,
                                TF_OD_API_INPUT_SIZE,
                                TF_OD_API_IS_QUANTIZED);
            } else {
                // 하차벨 기능
                detector =
                        TFLiteObjectDetectionAPIModel.create(
                                getApplicationContext(),
                                TF_OD_API_MODEL_FILE_BELL + ".tflite",
//                                            model.getFile().getPath(),
                                TF_OD_API_LABELS_FILE_BELL,
                                TF_OD_API_INPUT_SIZE,
                                TF_OD_API_IS_QUANTIZED);
            }


        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing Detector!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
//                            Log.d("fileCheck", model.getFile().getpath)
            finish();
        }
        cropSize[0] = TF_OD_API_INPUT_SIZE;
        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize[0], cropSize[0], Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        cropSize[0], cropSize[0],
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);


//        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
//                .requireWifi()
//                .build();
//        FirebaseModelDownloader.getInstance()
//                .getModel(TF_OD_API_MODEL_FILE_BUS, DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                        Log.d("modelDownload", "실패했움..");
//                    }
//                })
//                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
//                    @Override
//                    public void onSuccess(CustomModel model) {
//                        try {
//                            detector =
//                                    TFLiteObjectDetectionAPIModel.create(
//                                            getApplicationContext(),
//                                            TF_OD_API_MODEL_FILE_BUS+".tflite",
////                                            model.getFile().getPath(),
//                                            TF_OD_API_LABELS_FILE_BUS,
//                                            TF_OD_API_INPUT_SIZE,
//                                            TF_OD_API_IS_QUANTIZED);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            LOGGER.e(e, "Exception initializing Detector!");
//                            Toast toast =
//                                    Toast.makeText(
//                                            getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
//                            toast.show();
////                            Log.d("fileCheck", model.getFile().getpath)
//                            finish();
//                        }
//                        cropSize[0] = TF_OD_API_INPUT_SIZE;
//                        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
//                        previewWidth = size.getWidth();
//                        previewHeight = size.getHeight();
//
//                        sensorOrientation = rotation - getScreenOrientation();
//                        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);
//
//                        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
//                        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
//                        croppedBitmap = Bitmap.createBitmap(cropSize[0], cropSize[0], Config.ARGB_8888);
//
//                        frameToCropTransform =
//                                ImageUtils.getTransformationMatrix(
//                                        previewWidth, previewHeight,
//                                        cropSize[0], cropSize[0],
//                                        sensorOrientation, MAINTAIN_ASPECT);
//
//                        cropToFrameTransform = new Matrix();
//                        frameToCropTransform.invert(cropToFrameTransform);
//
//                        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
//                        trackingOverlay.addCallback(
//                                new DrawCallback() {
//                                    @Override
//                                    public void drawCallback(final Canvas canvas) {
//                                        tracker.draw(canvas);
//                                        if (isDebug()) {
//                                            tracker.drawDebug(canvas);
//                                        }
//                                    }
//                                });
//
//                        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
//                    }
//        });
    }

    @Override
    protected void processImage(TextView view, String rtNm) {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

//        InputImage image = InputImage.fromBitmap(croppedBitmap, 0);


        runInBackground(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
//                    InputImage image = InputImage.fromBitmap(cropCopyBitmap, 0);

                        final Canvas canvas = new Canvas(cropCopyBitmap);
                        final Paint paint = new Paint();
                        paint.setColor(Color.RED);
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Detector.Recognition> mappedRecognitions =
                                new ArrayList<Detector.Recognition>();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (results.size() == 0) {
                                    if (fromWhere.equals("BoardingDialog")) {
                                        view.setText(rtNm + "번 버스가 없습니다.");
                                    } else {
                                        view.setText("하차벨이 없습니다.");
                                    }
                                } else {
                                    if (fromWhere.equals("MainActivity")) {
                                        view.setText("하차벨이 있습니다.");
                                    }
                                }
                            }
                        });

                        for (final Detector.Recognition result : results) {
                            final RectF location = result.getLocation();
                            // && result.getTitle().equals("bus")

                            if (location != null && result.getConfidence() >= minimumConfidence) {
                                if (fromWhere.equals("BoardingDialog")) {
                                    if (!result.getTitle().equals("bus")) {
                                        continue;
                                    }

                                    Rect tmp_location = new Rect();
                                    location.round(tmp_location); // 내림
                                    tmp_location.set(
                                            tmp_location.left < 0 ? 0 : tmp_location.left,
                                            tmp_location.top < 0 ? 0 : tmp_location.top,
                                            tmp_location.right > cropCopyBitmap.getWidth() ? cropCopyBitmap.getWidth() : tmp_location.right,
                                            tmp_location.bottom > cropCopyBitmap.getHeight() ? cropCopyBitmap.getHeight() : tmp_location.bottom
                                    );
                                    Log.d("cropObject", tmp_location.flattenToString());

                                    Bitmap cropObject = Bitmap.createBitmap(cropCopyBitmap, tmp_location.left, tmp_location.top, tmp_location.width(), tmp_location.height());
                                    InputImage image = InputImage.fromBitmap(cropObject, 0);
                                    recognizer.process(image)
                                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                                @Override
                                                public void onSuccess(Text visionText) {
                                                    runOnUiThread(
                                                            new Runnable() {
                                                                @SuppressLint("SetTextI18n")
                                                                @Override
                                                                public void run() {
                                                                    // 카메라로 인식한 텍스트
                                                                    String resultText = visionText.getText();
                                                                    Log.d("VisionText", visionText.getText());

                                                                    /* 텍스트에 사용자가 탑승하려는 번호가 있는지 확인 */
                                                                    if (resultText.contains(rtNm)) {
                                                                        view.setText(rtNm + "번 버스를 찾았습니다.");
                                                                        soundPool.play(beep, 1, 1, 0, 0, 1);
                                                                        vibrator.vibrate(100);
                                                                    } else {
                                                                        view.setText(rtNm + "번 버스가 없습니다.");
                                                                    }

                                                                }

                                                            });
                                                }
                                            })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Task failed with an exception
                                                            // ...
                                                            LOGGER.w("이미지 인식 실패");

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    view.setText("[에러]\n버스를 찾을 수 없습니다.");
                                                                }
                                                            });

                                                        }
                                                    });
                                } else {
                                    // 현동님 진동
                                    if (result.getTitle().equals("bell") == false) {
                                        continue;
                                    }

                                    Log.d(
                                            "tt", "run: " + result.getTitle().equals("bell"));

                                    float cx = location.centerX();
                                    float cy = location.centerY();

                                    if (previewWidth * 0.25 <= cx && cx <= previewWidth * 0.75 && previewHeight * 0.25 <= cy && cy <= previewHeight * 0.75) {
//                                        vibrator.vibrate(VibrationEffect.createPredefined(EFFECT_HEAVY_CLICK));
                                        vibrator.vibrate(100);
                                        soundPool.play(beep, 1, 1, 0, 0, 1);

                                    } else {
                                        vibrator.vibrate(VibrationEffect.createPredefined(EFFECT_HEAVY_CLICK));
//                                        vibrator.vibrate(VibrationEffect.createPredefined(EFFECT_TICK));
                                        soundPool.play(beep, 0.3F, 0.3F, 0, 0, 1);
                                    }

                                    Location loc1 = new Location("");
                                    loc1.setLatitude(previewWidth / 2);
                                    loc1.setLongitude(previewHeight / 2);

                                    Location loc2 = new Location("");
                                    loc2.setLatitude(cx);
                                    loc2.setLongitude(cy);
                                    float distanceInMeters = loc1.distanceTo(loc2);
                                    loc2.setLatitude(previewWidth);
                                    loc2.setLongitude(previewHeight);
                                    float distanceMax = loc1.distanceTo(loc2);
                                }

                                canvas.drawRect(location, paint);

                                cropToFrameTransform.mapRect(location);

                                result.setLocation(location);
                                mappedRecognitions.add(result);
                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        //            runOnUiThread(
                        //                new Runnable() {
                        //                  @Override
                        //                  public void run() {
                        //                    showFrameInfo(previewWidth + "x" + previewHeight);
                        //                    showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                        //                    showInference(lastProcessingTimeMs + "ms");
                        //                  }
                        //                });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(
                () -> {
                    try {
                        detector.setUseNNAPI(isChecked);
                    } catch (UnsupportedOperationException e) {
                        LOGGER.e(e, "Failed to set \"Use NNAPI\".");
                        runOnUiThread(
                                () -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                });
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }
}
