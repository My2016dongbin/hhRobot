package com.ehaohai.robot.ui.activity;

import static me.drakeet.multitype.MultiTypeAsserts.assertHasTheSameAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ehaohai.robot.R;
import com.ehaohai.robot.base.BaseLiveActivity;
import com.ehaohai.robot.base.ViewModelFactory;
import com.ehaohai.robot.databinding.ActivityAudioListBinding;
import com.ehaohai.robot.ui.multitype.Audio;
import com.ehaohai.robot.ui.multitype.AudioViewBinder;
import com.ehaohai.robot.ui.multitype.Empty;
import com.ehaohai.robot.ui.multitype.EmptyViewBinder;
import com.ehaohai.robot.ui.multitype.Warn;
import com.ehaohai.robot.ui.viewmodel.AudioListViewModel;
import com.ehaohai.robot.utils.Action;
import com.ehaohai.robot.utils.CommonData;
import com.ehaohai.robot.utils.CommonUtil;
import com.ehaohai.robot.utils.HhLog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.util.TextInfo;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.Random;

import me.drakeet.multitype.MultiTypeAdapter;

public class AudioListActivity extends BaseLiveActivity<ActivityAudioListBinding, AudioListViewModel> implements AudioViewBinder.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen(this);
        init_();
        bind_();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void init_() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycle.setLayoutManager(linearLayoutManager);
        obtainViewModel().adapter = new MultiTypeAdapter(obtainViewModel().items);
        binding.recycle.setHasFixedSize(true);
        binding.recycle.setNestedScrollingEnabled(false);//设置样式后面的背景颜色
        binding.refresh.setRefreshHeader(new ClassicsHeader(this));
        //设置监听器，包括顶部下拉刷新、底部上滑刷新
        binding.refresh.setOnMultiPurposeListener(new SimpleMultiPurposeListener(){
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                obtainViewModel().getAudioList();
                refreshLayout.finishRefresh(1000);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(1000);
            }
        });
        AudioViewBinder audioListViewBinder = new AudioViewBinder(this);
        audioListViewBinder.setListener(this);
        obtainViewModel().adapter.register(Audio.class, audioListViewBinder);
        obtainViewModel().adapter.register(Empty.class, new EmptyViewBinder(this));
        binding.recycle.setAdapter(obtainViewModel().adapter);
        assertHasTheSameAdapter(binding.recycle, obtainViewModel().adapter);

        obtainViewModel().getAudioList();
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    private void bind_() {
        binding.back.setOnClickListener(view -> finish());
        CommonUtil.click(binding.upload, () -> {
            if(obtainViewModel().outputFilePath == null || obtainViewModel().outputFilePath.isEmpty()){
                Toast.makeText(this, "您还没有录音", Toast.LENGTH_SHORT).show();
                return;
            }
            obtainViewModel().uploadAudio();
        });
        CommonUtil.click(binding.record, () -> {
            obtainViewModel().recording = !obtainViewModel().recording;
            if(obtainViewModel().recording){
                startRecordVoice();
                obtainViewModel().startRecordTimes();
            }else{
                stopRecordVoice();
                obtainViewModel().stopRecordTimes();
            }
        });
    }

    @Override
    protected ActivityAudioListBinding dataBinding() {
        return DataBindingUtil.setContentView(this, R.layout.activity_audio_list);
    }

    @Override
    protected void setupViewModel() {
        binding.setViewModel(obtainViewModel());
        binding.setLifecycleOwner(this);
        obtainViewModel().start(this);
    }

    @Override
    public AudioListViewModel obtainViewModel() {
        return ViewModelProviders.of(this, ViewModelFactory.getInstance()).get(AudioListViewModel.class);
    }


    @Override
    protected void subscribeObserver() {
        super.subscribeObserver();

        obtainViewModel().name.observe(this, this::nameChanged);
        obtainViewModel().recordTimes.observe(this, this::recordTimesChanged);
    }

    private void nameChanged(String name) {

    }

    private void recordTimesChanged(String recordTimes) {
        binding.recordText.setText(recordTimes);
    }

    @Override
    public void onItemClick(Audio audio) {

    }

    @Override
    public void notifyClick(String oldName,String newName) {
        obtainViewModel().renameAudio(oldName,newName);
    }

    @Override
    public void onItemDeleteClick(Audio audio) {
        CommonUtil.showConfirm(this,"确认删除该音频吗？", "删除", "取消", new Action() {
            @Override
            public void click() {
                /*
                //本地文件操作
                boolean delete = new File(audio.getFilepath()).delete();
                if(delete){
                    obtainViewModel().getAudioList();
                    Toast.makeText(AudioListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                }*/
                obtainViewModel().deleteAudio(audio.getFilename());
            }
        }, new Action() {
            @Override
            public void click() {

            }
        },true);
    }


    private boolean isRecording = false;
    private Thread recordThread;
    private AudioRecord audioRecord;

    private void startRecordVoice() {
        File dir = new File(getCacheDir() + "/device/" + CommonData.sn, "speaking");
        if (!dir.exists()) dir.mkdirs();

        obtainViewModel().fileName = "speak_" + System.currentTimeMillis() + ".wav";
        obtainViewModel().outputFilePath = new File(dir, obtainViewModel().fileName).getPath();

        int sampleRate = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, bufferSize);

        isRecording = true;
        audioRecord.startRecording();

        recordThread = new Thread(() -> {
            FileOutputStream wavOut = null;
            try {
                wavOut = new FileOutputStream(obtainViewModel().outputFilePath);
                writeWavHeader(wavOut, sampleRate, 1, 16); // 预写WAV头

                byte[] buffer = new byte[bufferSize];
                int totalAudioLen = 0;

                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        wavOut.write(buffer, 0, read);
                        totalAudioLen += read;
                    }
                }

                updateWavHeader(obtainViewModel().outputFilePath, totalAudioLen, sampleRate, 1, 16);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (wavOut != null) {
                    try {
                        wavOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        recordThread.start();
    }


    private void writeWavHeader(FileOutputStream out, int sampleRate, int channels, int bitsPerSample) throws IOException {
        byte[] header = new byte[44];

        long byteRate = sampleRate * channels * bitsPerSample / 8;

        // RIFF/WAVE header
        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
        // 4-7: file size (will update later)
        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
        header[16] = 16; // Subchunk1Size for PCM
        header[17] = 0; header[18] = 0; header[19] = 0;
        header[20] = 1; header[21] = 0; // AudioFormat (1 = PCM)
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (sampleRate & 0xff);
        header[25] = (byte) ((sampleRate >> 8) & 0xff);
        header[26] = (byte) ((sampleRate >> 16) & 0xff);
        header[27] = (byte) ((sampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (channels * bitsPerSample / 8);
        header[33] = 0;
        header[34] = (byte) bitsPerSample;
        header[35] = 0;
        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
        // 40-43: data chunk size (will update later)

        out.write(header, 0, 44);
    }

    private void updateWavHeader(String wavPath, int audioLen, int sampleRate, int channels, int bitsPerSample) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(wavPath, "rw");
        long byteRate = sampleRate * channels * bitsPerSample / 8;
        long totalDataLen = audioLen + 36;

        raf.seek(4);
        raf.write((byte) (totalDataLen & 0xff));
        raf.write((byte) ((totalDataLen >> 8) & 0xff));
        raf.write((byte) ((totalDataLen >> 16) & 0xff));
        raf.write((byte) ((totalDataLen >> 24) & 0xff));

        raf.seek(40);
        raf.write((byte) (audioLen & 0xff));
        raf.write((byte) ((audioLen >> 8) & 0xff));
        raf.write((byte) ((audioLen >> 16) & 0xff));
        raf.write((byte) ((audioLen >> 24) & 0xff));

        raf.close();
    }

    private void stopRecordVoice() {
        isRecording = false;

        if (recordThread != null) {
            try {
                recordThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            recordThread = null;
        }

        if (audioRecord != null) {
            if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.stop();
            }
            audioRecord.release();
            audioRecord = null;
        }

        obtainViewModel().uploadAudio();
    }


    private void startRecordVoice2() {
        File dir = new File(getCacheDir()+"/device"+"/"+ CommonData.sn, "recordings");
        if (!dir.exists()) dir.mkdirs();
        obtainViewModel().fileName = "record_"+System.currentTimeMillis() + ".wav";
        obtainViewModel().outputFilePath = new File(dir, Objects.requireNonNull(obtainViewModel().fileName)).getPath();

        obtainViewModel().mediaRecorder = new MediaRecorder();
        obtainViewModel().mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        obtainViewModel().mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        obtainViewModel().mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        obtainViewModel().mediaRecorder.setOutputFile(obtainViewModel().outputFilePath);

        try {
            obtainViewModel().mediaRecorder.prepare();
            obtainViewModel().mediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
            HhLog.e("Failed to start recording " + e);
        }
    }

    private void stopRecordVoice2() {
        if (obtainViewModel().mediaRecorder != null) {
            obtainViewModel().mediaRecorder.stop();
            obtainViewModel().mediaRecorder.release();
            obtainViewModel().getAudioList();
        }
    }
}