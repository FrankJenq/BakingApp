package com.example.android.bakingapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.utils.NetworkUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class StepDetailActivity extends AppCompatActivity {

    private final String TAG_FRAGMENT = "step_detail_fragment";

    private final String STEP_ID = "step_id";
    private final String RECIPE_ID = "recipe_id";

    private int mStepId = -1;
    private int mRecipeId = -1;
    private static final String TAG = StepDetailActivity.class.getSimpleName();
    private SimpleExoPlayer mExoPlayer = null;
    private SimpleExoPlayerView mPlayerView;
    private AudioManager mAudioManager;
    private float mVolume = 1;

    private TextView mStepDescriptionView;
    private Button mPreButton;
    private Button mNextButton;

    private Uri mCurrentUri = null;

    private final String CURRENT_POSITION = "current_position";
    private final String PLAY_WHEN_READY = "play_when_ready";
    private final String LAND_SCREEN = "land_screen";
    private long mPlayPosition = 0;
    private boolean mPlayWhenReady = true;
    private boolean mIsLandScreen = false;

    // 判断是否为Activity启动时第一次加载内容
    private boolean mIsInitialContent = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_detail);
        if (savedInstanceState != null && savedInstanceState.containsKey(RECIPE_ID)) {
            mRecipeId = savedInstanceState.getInt(RECIPE_ID);
            mStepId = savedInstanceState.getInt(STEP_ID);

            if (savedInstanceState.containsKey(PLAY_WHEN_READY)) {
                mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
                mPlayPosition = savedInstanceState.getLong(CURRENT_POSITION);
            }
        }

        if (mRecipeId < 0 || mStepId < 0) {
            mRecipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
            mStepId = getIntent().getIntExtra(StepsActivity.STEP_ID, 0);
        }

        // 无论是横屏还是竖屏模式都要设置AudioManager
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Request audio focus for play back
        setAudioManager();

        // 根据有无按钮可以判断是否为横屏模式
        if (findViewById(R.id.pre_button) != null) {
            mIsLandScreen = false;

            mPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
            mStepDescriptionView = (TextView) findViewById(R.id.step_description);
            mPreButton = (Button) findViewById(R.id.pre_button);
            mNextButton = (Button) findViewById(R.id.next_button);
            mPreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    --mStepId;
                    mIsInitialContent = false;
                    mPlayPosition = 0;
                    mPlayWhenReady = true;
                    refreshContent();
                }
            });

            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ++mStepId;
                    mIsInitialContent = false;
                    mPlayPosition = 0;
                    mPlayWhenReady = true;
                    refreshContent();
                }
            });
        } else {
            getSupportActionBar().hide();
            mIsLandScreen = true;
            mPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
            mStepDescriptionView = (TextView) findViewById(R.id.step_description);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23 && mCurrentUri != null) {
            initializeVideoPlayer(mCurrentUri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsLandScreen) {
            refreshContent();
        } else {
            refreshLandScreenContent();
        }
        if ((Util.SDK_INT <= 23 || mExoPlayer == null) && mCurrentUri != null) {
            initializeVideoPlayer(mCurrentUri);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
        mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentUri != null) {
            outState.putLong(CURRENT_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(PLAY_WHEN_READY, mExoPlayer.getPlayWhenReady());
            mIsLandScreen = !mIsLandScreen;
            outState.putBoolean(LAND_SCREEN, mIsLandScreen);
            mPlayPosition = mExoPlayer.getContentPosition();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }
        outState.putInt(RECIPE_ID, mRecipeId);
        outState.putInt(STEP_ID, mStepId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mRecipeId = savedInstanceState.getInt(RECIPE_ID);
        mStepId = savedInstanceState.getInt(STEP_ID);
        mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        mPlayPosition = savedInstanceState.getLong(CURRENT_POSITION);
        super.onRestoreInstanceState(savedInstanceState);
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

        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        mExoPlayer.seekTo(mPlayPosition);

        mExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady) {
                    setAudioManager();
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer = null;
            mCurrentUri = null;
        }
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

    private void refreshContent() {
        releasePlayer();
        String title;
        if (mStepId > 0) {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Step " + mStepId;
        } else {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Introduction";
        }

        setTitle(title);

        mStepDescriptionView.setText(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getDescription());

        if (mStepId == 0) {
            mPreButton.setVisibility(View.INVISIBLE);
        } else {
            mPreButton.setVisibility(View.VISIBLE);
        }

        if (mStepId == RecipeList.recipes.get(mRecipeId).getSteps().size() - 1) {
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
        }

        Uri videoUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getVideoURL());
        Uri thumbnailUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getThumbnailURL());

        // Initialize the mExoPlayer.
        if (videoUri != null && !videoUri.toString().equals("")) {
            mPlayerView.setVisibility(View.VISIBLE);
            mCurrentUri = videoUri;
        } else if (thumbnailUri != null && !thumbnailUri.toString().equals("")) {
            mCurrentUri = thumbnailUri;
            mPlayerView.setVisibility(View.VISIBLE);
        } else {
            mPlayerView.setVisibility(View.GONE);
            return;
        }

        // 在使用pre button或next button更新fragment后，需要在此处启动ExoPlayer
        if (!mIsInitialContent) {
            initializeVideoPlayer(mCurrentUri);
        }
    }

    private void refreshLandScreenContent() {
        if (RecipeList.recipes == null) {
            if (!NetworkUtils.isHttpsConnectionOk(this)) {
                Intent intent = new Intent(StepDetailActivity.this,RecipeListActivity.class);
                startActivity(intent);
            }
            new RecipeLoader(this);
        }
        releasePlayer();
        Uri videoUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getVideoURL());
        Uri thumbnailUri = Uri.parse(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getThumbnailURL());

        // Initialize the mExoPlayer.
        if (videoUri != null && !videoUri.toString().equals("")) {
            mPlayerView.setVisibility(View.VISIBLE);
            mCurrentUri = videoUri;
        } else if (thumbnailUri != null && !thumbnailUri.toString().equals("")) {
            mCurrentUri = thumbnailUri;
            mPlayerView.setVisibility(View.VISIBLE);
        } else {
            mPlayerView.setVisibility(View.GONE);
            mStepDescriptionView.setText(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getDescription());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(StepDetailActivity.this, StepsActivity.class);
                intent.putExtra(RecipeListActivity.RecipeId, mRecipeId);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAudioManager() {
        // Request audio focus for play back
        mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
    }
}
