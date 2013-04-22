package com.example.smartalarm;

import java.util.concurrent.LinkedBlockingQueue;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;

/*
 * Fills a given LinkedBlockingQueue with short[]s corresponding to microphone data.
 * The arrays are NOT guaranteed to be the same length!
 */
public class AudioThread extends Thread {
	
	private static final int RECORDER_SOURCE = MediaRecorder.AudioSource.MIC;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	//[NOTE] AudioFormat.ENCODING_PCM_8BIT fails because it's unimplemented
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private boolean running = false;
	private LinkedBlockingQueue<AudioDataPoint> output;
	private int sample_rate;
	private int min_buffer;
	
	public AudioThread(LinkedBlockingQueue<AudioDataPoint> sharedQueue) {
		this(sharedQueue, 8000);
	}
	
	public AudioThread(LinkedBlockingQueue<AudioDataPoint> sharedQueue, int sampleRate) {
		setDaemon(true);
		output = sharedQueue;
		sample_rate = sampleRate;
		int smallest_buffer = AudioRecord.getMinBufferSize(sample_rate, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		min_buffer = 1;
		while (smallest_buffer != 0) { //round up to nearest power of 2
			smallest_buffer >>= 1;
			min_buffer <<= 1;
		}
	}
	
	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
		AudioRecord recorder = null;
		try {
			recorder = new AudioRecord(RECORDER_SOURCE, sample_rate, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, min_buffer);
			recorder.startRecording();
			running = true;
			while (running) {
				short[] buffer = new short[min_buffer];
				int samples = recorder.read(buffer, 0, buffer.length);
				long ts = System.currentTimeMillis();
				if (samples == buffer.length) {
					output.offer(new AudioDataPoint(ts, buffer));
				} else if (samples > 0) {
					short[] s = new short[samples];
					for (int i = 0; i < samples; i++) {
						s[i] = buffer[i];
					}
					output.offer(new AudioDataPoint(ts, s));
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
