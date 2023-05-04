package com.sonicgdx.sonicgdx.android;

import android.content.Context;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidAudio;
import com.sonicgdx.sonicgdx.Init;

import barsoosayque.libgdxoboe.OboeAudio;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    //Uses libGDX-Oboe extension
    @Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        return new OboeAudio(context.getAssets());
    }

    //Uncomment for alternate AsynchronousAndroidAudio implementation
    /*@Override
    public AndroidAudio createAudio(Context context, AndroidApplicationConfiguration config) {
        return new AsynchronousAndroidAudio(context, config);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        initialize(new Init(), configuration);
    }
}
