package com.jhone.demo.barcode;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.CheckBox;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.jhone.demo.R;
import com.jhone.demo.barcode.camera.CameraManager;
import com.jhone.demo.barcode.camera.PlanarYUVLuminanceSource;
import com.jhone.demo.barcode.camera.RGBLuminanceSource;
import com.jhone.demo.barcode.decoding.CaptureActivityHandler;
import com.jhone.demo.barcode.decoding.DecodeFormatManager;
import com.jhone.demo.barcode.decoding.InactivityTimer;
import com.jhone.demo.barcode.view.ViewfinderView;
import com.jhone.demo.event.CodeEvent;
import com.jhone.demo.utils.ToastUtils;
import com.yolanda.nohttp.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class CaptureActivity extends AppCompatActivity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    @Bind(R.id.cb_light)CheckBox cb_light;
    @Bind(R.id.cb_local)CheckBox cb_local;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private int REQUEST_CODE=100;
    @OnCheckedChanged({R.id.cb_local,R.id.cb_light})
    public void checkedChanged(CheckBox checkBox){
        switch (checkBox.getId()){
            case R.id.cb_light:
                if (checkBox.isChecked()){
                    CameraManager.get().startPreview(true);
                }else {
                    CameraManager.get().startPreview(false);
                }
                break;
            case R.id.cb_local:
                checkBox.setChecked(false);
                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE);
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(final Result obj, Bitmap barcode) {
        CameraManager.get().startPreview(false);
        inactivityTimer.onActivity();
        viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();


//        Intent join = new Intent(this, JoinActivity.class);
//        join.putExtra("flag", JoinFragment.FLAG_JOIN_COMPANY);
//        join.putExtra("barcodeResult", obj.getText());
//        startActivity(join);
//        finish();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        if (barcode == null) {
            dialog.setIcon(null);
        } else {

            Drawable drawable = new BitmapDrawable(barcode);
            dialog.setIcon(drawable);
        }
        dialog.setTitle("扫描结果");
        dialog.setMessage(obj.getText());
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //用默认浏览器打开扫描得到的地址
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(obj.getText());
                intent.setData(content_url);
                startActivity(intent);
                finish();
            }
        });
        dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.create().show();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private String photo_path;
    private ProgressDialog mProgress;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){
            //获取选中图片的路径
            Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
            if (cursor.moveToFirst()) {
                photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Result result = scanningImage(photo_path);
                    CodeEvent event=new CodeEvent(result,null,CodeEvent.SCAN_SUCCESS_LOCAL);
                    EventBus.getDefault().post(event);
                }
            }).start();
        }
    }
    private Bitmap scanBitmap;
    /**
     * 扫描二维码图片的方法
     * @param path
     * @return
     */
    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        //回显图片
        Rect frame=viewfinderView.getFrame();
        Bitmap tempBitmap=scanBitmap.createScaledBitmap(scanBitmap,frame.right-frame.left,frame.bottom-frame.top,false);
        CodeEvent codeEvent=new CodeEvent(null,tempBitmap,CodeEvent.FIT_VIEWFIND_IMAGE);
        EventBus.getDefault().post(codeEvent);

        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();

        Hashtable<DecodeHintType, Object> hints =  new Hashtable<DecodeHintType, Object>(3);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码
        Vector<BarcodeFormat> mDecodeFormats = new Vector<BarcodeFormat>();
        mDecodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
        mDecodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
        mDecodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, mDecodeFormats);
        try {
            return reader.decode(bitmap, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final CodeEvent event){
        switch (event.getType()){
            case CodeEvent.FIT_VIEWFIND_IMAGE:
                if (event.getBitmap()!=null)
                viewfinderView.drawResultBitmap(event.getBitmap());
                mProgress = new ProgressDialog(CaptureActivity.this);
                mProgress.setMessage("正在扫描...");
                mProgress.setCancelable(false);
                mProgress.show();
                break;
            case CodeEvent.SCAN_SUCCESS_LOCAL:
                mProgress.dismiss();
                if (event.getResult()==null){
                    ToastUtils.showShort("解码失败,图片失真");
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(CaptureActivity.this);
                if (event.getBitmap() == null) {
                    dialog.setIcon(null);
                } else {
                    Drawable drawable = new BitmapDrawable(event.getBitmap());
                    dialog.setIcon(drawable);
                }
                dialog.setTitle("扫描结果");
                dialog.setMessage(event.getResult().getText());
                dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //用默认浏览器打开扫描得到的地址
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(event.getResult().getText());
                        intent.setData(content_url);
                        startActivity(intent);
                        finish();
                    }
                });
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                dialog.create().show();
                break;
        }
    }
}