package com.example.smartalarm;

import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

public class AudioThread extends Thread {
	
	private static final int RECORDER_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	//[NOTE] AudioFormat.ENCODING_PCM_8BIT fails because it's unimplemented
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private boolean running = false;
	private LinkedBlockingQueue<Short> output;
	private int sample_rate;
	private int min_buffer;
	
	public AudioThread(LinkedBlockingQueue<Short> sharedQueue) {
		this(sharedQueue, 8000);
	}
	
	public AudioThread(LinkedBlockingQueue<Short> sharedQueue, int sampleRate) {
		output = sharedQueue;
		sample_rate = sampleRate;
		min_buffer = AudioRecord.getMinBufferSize(sample_rate, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		setDaemon(true);
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
		AudioRecord recorder = null;
		try {
			short[] buffer = new short[min_buffer];
			recorder = new AudioRecord(RECORDER_SOURCE, sample_rate, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, buffer.length);
			recorder.startRecording();
			running = true;
			while (running) {
				int samples = recorder.read(buffer, 0, buffer.length);
				for (int i = 0; i < samples; i++) {
					output.offer((Short) buffer[i]);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}
	
	public void close() {
		running = false;
	}
}
