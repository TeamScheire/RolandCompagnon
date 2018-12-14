package be.imec.apt.bigfix.wear;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;

import javax.inject.Inject;

import bigfix.apt.imec.be.shared.TaskInstructions;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskInstructionActivity extends WearableActivity implements TaskInstructionPresenter.TaskInstructionView {

    private static final String EXTRA_TASK_INSTRUCTIONS = "extraTaskInstructions";

    @BindView(R.id.layout_empty)
    View layoutEmpty;

    @BindView(R.id.imageview_next)
    ImageView imageViewNext;

    @BindView(R.id.imageview_repeat)
    ImageView imageViewRepeat;

    @BindView(R.id.layout_playing)
    View layoutPlaying;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    TaskInstructionPresenter presenter;

    private MediaPlayer mediaPlayer;

    public static Intent getIntent(Context context, TaskInstructions taskInstructions) {
        final Intent intent = new Intent(context, TaskInstructionActivity.class);
        intent.putExtra(EXTRA_TASK_INSTRUCTIONS, taskInstructions);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WearableApplication.appComponent.inject(this);

        setContentView(R.layout.activity_task_instructions);

        ButterKnife.bind(this);

        // Enables Always-on
        //setAmbientEnabled();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setupPresenter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        final TaskInstructions taskInstructions = intent.getParcelableExtra(EXTRA_TASK_INSTRUCTIONS);
        presenter.start(taskInstructions);
    }

    private void setupPresenter() {
        presenter.attachView(this);

        final TaskInstructions taskInstructions = getIntent().getParcelableExtra(EXTRA_TASK_INSTRUCTIONS);
        presenter.start(taskInstructions);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);

        layoutEmpty.setBackgroundResource(R.color.black);
        imageViewNext.setBackgroundResource(R.color.black);
        imageViewRepeat.setBackgroundResource(R.color.black);

        layoutPlaying.setBackgroundResource(R.color.black);
        progressBar.setBackgroundResource(R.color.black);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();

        layoutEmpty.setBackgroundResource(R.drawable.gradient_blue);
        imageViewNext.setBackgroundResource(R.color.green);
        imageViewRepeat.setBackgroundResource(R.color.imec_blue_dark);

        layoutPlaying.setBackgroundResource(R.color.imec_blue_dark);
        progressBar.setBackgroundResource(R.drawable.circle_shape);
    }

    @Override
    protected void onStop() {
        super.onStop();

        presenter.onScreenInvisible();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            if (keyCode == KeyEvent.KEYCODE_STEM_1) {
                // Ignore top right button
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.imageview_next)
    public void onNextClicked(View view) {
        presenter.onNextClicked();
    }

    @OnClick(R.id.imageview_repeat)
    public void onRepeatClicked(View view) {
        presenter.onRepeatClicked();
    }


    //region TaskInstructionPresenter.TaskInstructionView

    @Override
    public void loadInstructionAudioFile(String audioFilePath) {
        try {
            AssetFileDescriptor fileDescriptor = this.getAssets().openFd(audioFilePath);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            fileDescriptor.close();
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(mp -> presenter.onInstructionCompleted());
            presenter.onInstructionReady(mediaPlayer.getDuration());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void playInstruction() {
        mediaPlayer.start();
    }

    @Override
    public void cleanupPlaybackResources() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public void showEmptyView() {
        layoutPlaying.setVisibility(View.GONE);
        imageViewNext.setVisibility(View.GONE);
        imageViewRepeat.setVisibility(View.GONE);

        layoutEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNextButton() {
        layoutEmpty.setVisibility(View.GONE);
        layoutPlaying.setVisibility(View.GONE);
        imageViewRepeat.setVisibility(View.GONE);

        imageViewNext.setVisibility(View.VISIBLE);
        imageViewNext.setImageResource(R.drawable.ic_next);
    }

    @Override
    public void showFinishButton() {
        layoutEmpty.setVisibility(View.GONE);
        layoutPlaying.setVisibility(View.GONE);
        imageViewRepeat.setVisibility(View.GONE);

        imageViewNext.setVisibility(View.VISIBLE);
        imageViewNext.setImageResource(R.drawable.ic_finish);
    }

    @Override
    public void showRepeatButton() {
        layoutEmpty.setVisibility(View.GONE);
        layoutPlaying.setVisibility(View.GONE);
        imageViewNext.setVisibility(View.GONE);

        imageViewRepeat.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateProgress(int progress) {
        layoutEmpty.setVisibility(View.GONE);
        imageViewNext.setVisibility(View.GONE);
        imageViewRepeat.setVisibility(View.GONE);

        layoutPlaying.setVisibility(View.VISIBLE);
        progressBar.setProgress(progress);
    }

    @Override
    public void vibrate() {
        final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        final long[] pattern = new long[]{0, 100, 200, 100};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
        } else {
            vibrator.vibrate(pattern, -1);
        }
    }

    @Override
    public void closeScreen() {
        finish();
    }

    //endregion
}
