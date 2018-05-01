package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp.data.RecipeList;
import com.example.android.bakingapp.data.Step;
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

import java.util.List;

/**
 * 用于展示菜谱步骤列表的Acitivity
 */
public class StepsActivity extends AppCompatActivity {


    private final String TAG = StepsActivity.class.getSimpleName();

    private final String RECIPE_ID = "recipe_id";
    public static final String STEP_ID = "step_id";
    private RecyclerView mRecyclerView;
    private SimpleExoPlayer mExoPlayer = null;
    private SimpleExoPlayerView mPlayerView;

    private AudioManager mAudioManager = null;
    private float mVolume = 1;
    private int mStepId = -1;
    private int mRecipeId = -1;

    private TextView mStepDescriptionView;

    private final String CURRENT_POSITION = "current_position";
    private final String PLAY_WHEN_READY = "play_when_ready";
    private final String LAND_SCREEN = "land_screen";
    private long mPlayPosition = 0;
    private boolean mPlayWhenReady = true;
    private boolean mIsLandScreen = false;
    private Uri mCurrentUri;

    private boolean mIsPadScreen = false;

    // 判断是否为Activity启动时第一次加载内容
    private boolean mIsInitialContent = true;

    private LinearLayout mStepLayout;
    private RecyclerView mIngredientsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeId = getIntent().getIntExtra(RecipeListActivity.RecipeId, 0);
        setContentView(R.layout.activity_steps);
        setTitle(RecipeList.recipes.get(mRecipeId).getName());

        mRecyclerView = (RecyclerView) findViewById(R.id.steps_recycler_view);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        StepsAdapter stepsAdapter = new StepsAdapter(RecipeList.recipes.get(mRecipeId).getSteps());

        mRecyclerView.setAdapter(stepsAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 平板模式下设置的fragment
        if (findViewById(R.id.ingredients_recycler_view) != null) {
            mIsPadScreen = true;
            // 设置AudioManager
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            // Request audio focus for play back
            mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            mStepLayout = (LinearLayout) findViewById(R.id.layout_step);
            mIngredientsView = (RecyclerView) findViewById(R.id.ingredients_recycler_view);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mIsPadScreen) {
            return;
        }
        if (mStepId < 0) {
            initializeIngredientsContent();
            mStepLayout.setVisibility(View.GONE);
        } else {
            refreshContent();
            mIngredientsView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!mIsPadScreen) {
            return;
        }
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

    private class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

        private List<Step> mStepList;

        class StepViewHolder extends RecyclerView.ViewHolder {
            TextView stepView;
            ImageView videoIcon;

            StepViewHolder(View view) {
                super(view);
                stepView = (TextView) view.findViewById(R.id.step_name);
                videoIcon = (ImageView) view.findViewById(R.id.video_icon);
            }
        }


        public StepsAdapter(List<Step> steps) {
            mStepList = steps;
        }

        @Override
        public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.steps_list_item, parent, false);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = mRecyclerView.getChildAdapterPosition(v);
                    if (mIsPadScreen) {
                        if (itemPosition == 0) {
                            mIngredientsView.setVisibility(View.VISIBLE);
                            mStepLayout.setVisibility(View.GONE);
                            mIsInitialContent = false;
                            mStepId = -1;
                            releasePlayer();
                            initializeIngredientsContent();
                        } else {
                            mPlayPosition = 0;
                            mPlayWhenReady = true;
                            mStepLayout.setVisibility(View.VISIBLE);
                            mIngredientsView.setVisibility(View.GONE);
                            mStepId = itemPosition - 1;
                            mIsInitialContent = false;
                            refreshContent();
                        }
                    } else {
                        if (itemPosition == 0) {
                            Intent intent = new Intent(StepsActivity.this, IngredientsActivity.class);
                            intent.putExtra(RecipeListActivity.RecipeId, mRecipeId);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(StepsActivity.this, StepDetailActivity.class);
                            intent.putExtra(RecipeListActivity.RecipeId, mRecipeId);
                            intent.putExtra(STEP_ID, itemPosition - 1);
                            startActivity(intent);
                        }
                    }
                }
            };
            itemView.setOnClickListener(listener);
            return new StepViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(StepViewHolder holder, int position) {
            Step step;
            holder.videoIcon.setImageResource(R.drawable.video_icon);
            if (position == 0) {
                holder.stepView.setText(getString(R.string.ingredients_label));
                holder.videoIcon.setVisibility(View.INVISIBLE);
            } else if (position == 1) {
                step = mStepList.get(position - 1);
                holder.stepView.setText("Introduction" + ": " + step.getShortDescription());
                if (step.getVideoURL() == null || step.getVideoURL().equals("")) {
                    holder.videoIcon.setVisibility(View.INVISIBLE);
                }
            } else if (position > 1) {
                step = mStepList.get(position - 1);
                int stepId = position - 1;
                holder.stepView.setText("Step " + stepId + ": " + step.getShortDescription());
                if (step.getVideoURL() == null || step.getVideoURL().equals("")) {
                    holder.videoIcon.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mStepList.size();
        }
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
                    mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN);
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

    private void refreshContent() {
        if (RecipeList.recipes == null) {
            if (!NetworkUtils.isHttpsConnectionOk(this)) {
                Intent intent = new Intent(StepsActivity.this,RecipeListActivity.class);
                startActivity(intent);
            }
            new RecipeLoader(this);
        }
        releasePlayer();
        String title;
        if (mStepId > 0) {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Step " + mStepId;
        } else {
            title = RecipeList.recipes.get(mRecipeId).getName() + " Introduction";
        }

        setTitle(title);

        mStepDescriptionView = (TextView) findViewById(R.id.step_description);
        mStepDescriptionView.setText(RecipeList.recipes.get(mRecipeId).getSteps().get(mStepId).getDescription());

        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);

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
        if (!mIsInitialContent) {
            initializeVideoPlayer(mCurrentUri);
        }
    }

    private void initializeIngredientsContent() {
        if (RecipeList.recipes == null) {
            if (!NetworkUtils.isHttpsConnectionOk(this)) {
                Intent intent = new Intent(StepsActivity.this,RecipeListActivity.class);
                startActivity(intent);
            }
            new RecipeLoader(this);
        }
        mPlayerView = (SimpleExoPlayerView) findViewById(R.id.playerView);
        mStepDescriptionView = (TextView) findViewById(R.id.step_description);

        String recipeName = getString(R.string.ingredients_act_label) + " " + RecipeList.recipes.get(mRecipeId).getName();
        setTitle(recipeName);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.ingredients_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(RecipeList.recipes.get(mRecipeId).getIngredients());
        recyclerView.setAdapter(ingredientsAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
