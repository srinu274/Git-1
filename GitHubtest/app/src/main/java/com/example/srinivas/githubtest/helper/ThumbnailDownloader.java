package com.example.srinivas.githubtest.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG="ThumbnailDownloader";
	private static final int MESSAGE_DOWNLOAD=0;
	
	private Handler mHandler;
	private Handler mResponseHandler;
	private Listener<Token> mListener;
	private Map<Token,String> requestMap=Collections.synchronizedMap(new HashMap<Token,String>());
	private GlobalImageCache mCache;

	@Override
	protected void onLooperPrepared(){
		mHandler=new Handler(){
			@Override
			public void handleMessage(Message msg){
				if(msg.what==MESSAGE_DOWNLOAD){
					Token token=(Token)msg.obj;
					Log.i(TAG, "Got a request for url: " + requestMap.get(token));
					handleRequest(token);
				}
			}
		};
	}
	
	public interface Listener<Token>{
		void onThumbnailDownloaded(Token token, Bitmap Thumbnail);
	}
	public ThumbnailDownloader(Handler responseHandler){
		super(TAG);
		mResponseHandler=responseHandler;
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize= maxMemory/8;
		mCache=new GlobalImageCache(cacheSize);
	}
	
	public void setListener(Listener<Token> listener){
		if(listener!=null)
			mListener=listener;
	}
	
	public void queueThumbnail(Token token,String url){
		Log.i(TAG,"Got an url :"+url);
		requestMap.put(token, url);
		mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
	}
	
	
	private void handleRequest(final Token token){
		try{
			final String url=requestMap.get(token);
			if(url==null){
				return;
			}
			Bitmap bmp=mCache.get(url);
			if(bmp!=null) {
				if(requestMap.get(token)!=url) return;
				requestMap.remove(token);
				mListener.onThumbnailDownloaded(token, bmp);
				return;
			}
			byte[] bitmapBytes = GitHubJSONParser.getImageBytes(url);
			if(bitmapBytes==null) return;
			final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
			mCache.put(url,bitmap);
			Log.i(TAG, "Bitmap created");
			mResponseHandler.post(new Runnable(){
				public void run(){
					if(requestMap.get(token)!=url) return;
					requestMap.remove(token);
					mListener.onThumbnailDownloaded(token, bitmap);
				}
			});
		}catch(Exception ioe){
			ioe.printStackTrace();
		}
	}
	
	public void clearQueue(){
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}
}
