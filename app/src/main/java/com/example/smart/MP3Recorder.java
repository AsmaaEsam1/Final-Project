package com.example.smart;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Message;

import java.io.File;
import java.io.IOException;

public class MP3Recorder {
    // =======================AudioRecord Default
    // Settings=======================
    private static final int DEFAULT_AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;

    private static final int DEFAULT_SAMPLING_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     *  private static final int DEFAULT_AUDIO_FORMAT =
     * AudioFormat.ENCODING_PCM_16BIT;
     */

    // ======================Lame Default Settings=====================
    private static final int DEFAULT_LAME_MP3_QUALITY = 0;

    private static final int DEFAULT_LAME_IN_CHANNEL = 1;
    /**
     * Encoded bit rate. MP3 file will be encoded with bit rate 32kbps
     */
    private static final int DEFAULT_LAME_MP3_BIT_RATE = 32;

    private static final int FRAME_COUNT = 160;
    private AudioRecord mAudioRecord = null;
    private int mBufferSize;
    private short[] mPCMBuffer;
    private DataEncodeThread mEncodeThread;
    private boolean mIsRecording = false;
    private String mRecordFile;

    /**
     * Default constructor. Setup recorder with default sampling rate 1 channel,
     * 16 bits pcm
     */
    public MP3Recorder(String recordFile) {
        mRecordFile = recordFile;
    }



    /**
     * Start recording. Create an encoding thread. Start record from this
     * thread.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        if (mIsRecording)
            return;
        initAudioRecorder();
        mAudioRecord.startRecording();
        new Thread() {

            @Override
            public void run() {
                android.os.Process
                        .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                mIsRecording = true;
                while (mIsRecording) {
                    int readSize = mAudioRecord
                            .read(mPCMBuffer, 0, mBufferSize);
                    if (readSize > 0) {
                        mEncodeThread.addTask(mPCMBuffer, readSize);
                        calculateRealVolume(mPCMBuffer, readSize);
                    }
                }

                // release and finalize audioRecord
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                // stop the encoding thread and try to wait
                // until the thread finishes its job
                Message msg = Message.obtain(mEncodeThread.getHandler(),
                        DataEncodeThread.PROCESS_STOP);
                msg.sendToTarget();
            }

            /**
             *
             * @param buffer
             *            buffer
             * @param readSize
             *            readSize
             */
            private void calculateRealVolume(short[] buffer, int readSize) {
                int sum = 0;
                for (int i = 0; i < readSize; i++) {
                    sum += buffer[i] * buffer[i];
                }
                if (readSize > 0) {
                    double amplitude = sum / readSize;
                    mVolume = (int) Math.sqrt(amplitude);
                }
            }

            ;
        }.start();
    }

    private int mVolume;

    public int getVolume() {
        return mVolume;
    }

    private static final int MAX_VOLUME = 2000;

    public int getMaxVolume() {
        return MAX_VOLUME;
    }

    public void stop() {
        mIsRecording = false;
    }

    public boolean isRecording() {
        return mIsRecording;
    }


    /**
     * Initialize audio recorder
     */
    private void initAudioRecorder() throws IOException {
        mBufferSize = AudioRecord.getMinBufferSize(DEFAULT_SAMPLING_RATE,
                DEFAULT_CHANNEL_CONFIG, AudioFormat.ENCODING_PCM_16BIT);



        /* Setup audio recorder */
        mAudioRecord = new AudioRecord(DEFAULT_AUDIO_SOURCE,
                DEFAULT_SAMPLING_RATE, DEFAULT_CHANNEL_CONFIG,
                AudioFormat.ENCODING_PCM_16BIT, mBufferSize);

        // Check if AGC is supported, if so retrieve from shared prefs
        if (AudioEffectUtil.INSTANCE.isSupported(AudioEffect.EFFECT_TYPE_AGC)) {
            AutomaticGainControl gainControl = AutomaticGainControl.create(mAudioRecord.getAudioSessionId());
            if (gainControl != null)
                gainControl.setEnabled(true);
        }

        //  Check if Noise Suppression is supported, if so retrieve from shared prefs
        if (AudioEffectUtil.INSTANCE.isSupported(AudioEffect.EFFECT_TYPE_NS)) {
            NoiseSuppressor noiseSupp = NoiseSuppressor.create(mAudioRecord.getAudioSessionId());
            if (noiseSupp != null)
                noiseSupp.setEnabled(true);
        }


        int bytesPerFrame = 2016;//mAudioRecord.getBufferSizeInFrames();

        /*
         * Get number of samples. Calculate the buffer size (round up to the
         * factor of given frame size)
         */
        int frameSize = mBufferSize / bytesPerFrame;
        if (frameSize % FRAME_COUNT != 0) {
            frameSize += (FRAME_COUNT - frameSize % FRAME_COUNT);
            mBufferSize = frameSize * bytesPerFrame;
        }

        mPCMBuffer = new short[mBufferSize];
        /*
         * Initialize lame buffer mp3 sampling rate is the same as the recorded
         * pcm sampling rate The bit rate is 32kbps
         */
        MP3Encoder.init(DEFAULT_SAMPLING_RATE, DEFAULT_LAME_IN_CHANNEL,
                DEFAULT_SAMPLING_RATE, DEFAULT_LAME_MP3_BIT_RATE,
                DEFAULT_LAME_MP3_QUALITY);
        // Create and run thread used to encode data
        // The thread will
        mEncodeThread = new DataEncodeThread(new File(mRecordFile), mBufferSize);
        mEncodeThread.start();
        mAudioRecord.setRecordPositionUpdateListener(mEncodeThread,
                mEncodeThread.getHandler());
        mAudioRecord.setPositionNotificationPeriod(FRAME_COUNT);
    }

}
