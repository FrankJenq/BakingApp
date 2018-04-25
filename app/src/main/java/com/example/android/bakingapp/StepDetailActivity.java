package com.example.android.bakingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.data.RecipeList;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class StepDetailActivity extends AppCompatActivity {

    private static int mStepId;
    private static int mRecipeId;
    private static final String TAG = StepDetailActivity.class.getSimpleName();
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private AudioManager mAudioManager;
    private float mVolume = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
        mStepId = getIntent().getIntExtra(StepsActivity.STEP_ID, 0);
        setContentView(R.layout.activity_step_detail);
        String title;
        if (mStepId > 0) {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Step " + mStepId;
        }else {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Introduction";
        }
        setTitle(title);

        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);

        final TextView stepDescriptionView = (TextView) findViewById(R.id.step_description);
        stepDescriptionView.setText(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getDescription());

        final Button preButton = (Button) findViewById(R.id.pre_button);
        if (mStepId == 0) {
            preButton.setVisibility(View.INVISIBLE);
        }

        final Button nextButton = (Button) findViewById(R.id.next_button);
        if (mStepId == RecipeList.recipes.get(mRecipeId).getSteps().size() - 1) {
            nextButton.setVisibility(View.INVISIBLE);
        }

        preButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mExoPlayer != null) {
                            mExoPlayer.stop();
                        }
                        Intent intent = new Intent(StepDetailActivity.this, StepDetailActivity.class);
                        intent.putExtra(StepsActivity.STEP_ID, --mStepId);
                        intent.putExtra(RecipeListActivity.RecipeId, mRecipeId);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mExoPlayer != null) {
                            mExoPlayer.stop();
                        }
                        Intent intent = new Intent(StepDetailActivity.this, StepDetailActivity.class);
                        intent.putExtra(StepsActivity.STEP_ID, ++mStepId);
                        intent.putExtra(RecipeListActivity.RecipeId, mRecipeId);
                        finish();
                        startActivity(intent);
                    }
                });
            }
        });

        Uri videoUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getVideoURL());
        Uri thumbnailUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getThumbnailURL());

        // Initialize the mExoPlayer.
        if (videoUri != null && !videoUri.toString().equals("")) {
            initializeVideoPlayer(videoUri);
        } else if (thumbnailUri != null && !thumbnailUri.toString().equals("")) {
            initializeVideoPlayer(thumbnailUri);
        } else {
            mPlayerView.setVisibility(View.GONE);
            return;
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for play back
        mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        mPlayerView.setPlayer(mExoPlayer);
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mExoPlayer.setPlayWhenReady(false);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mExoPlayer.setPlayWhenReady(false);
            } else if (focusChange ==
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mVolume = mExoPlayer.getVolume();
                mExoPlayer.setVolume(0.1f);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mExoPlayer.setVolume(mVolume);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    };

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializeVideoPlayer(Uri mediaUri) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        mPlayerView.setPlayer(mExoPlayer);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "BakingApp"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource videoSource = new ExtractorMediaSource(mediaUri,
                dataSourceFactory, extractorsFactory, null, null);
        // Prepare the player with the source.
        mExoPlayer.prepare(videoSource);
        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();


    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                // Pause the playback
                mExoPlayer.setPlayWhenReady(false);
            }
        }
    }
}
