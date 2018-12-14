package be.imec.apt.bigfix.tasks.running;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.general.BaseActivity;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.tasks.Task;
import butterknife.BindView;
import butterknife.OnClick;

public class TaskRunningActivity extends BaseActivity implements TaskRunningPresenter.TaskRunningView {
    public static final int REQUEST_CODE = 123;

    private static final String EXTRA_TASK = "extraTask";

    public static Intent getIntent(Context context, Task task) {
        final Intent intent = new Intent(context, TaskRunningActivity.class);
        intent.putExtra(EXTRA_TASK, task);
        return intent;
    }

    @BindView(R.id.imageview_icon)
    ImageView imageViewIcon;

    @BindView(R.id.textview_title)
    TextView textViewTitle;

    @Inject
    TaskRunningPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setContentView(R.layout.activity_task_running);

        useButterKnife();

        setupPresenter();
    }

    private void setupPresenter() {
        presenter.attachView(this);

        if (getIntent().hasExtra(EXTRA_TASK)) {
            final Task task = getIntent().getParcelableExtra(EXTRA_TASK);
            presenter.start(task);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.stop();
    }

    @Override
    public void onBackPressed() {
        presenter.onBackPressed();
    }

    @OnClick(R.id.button_cancel)
    public void onTaskCanceled(View view) {
        presenter.onTaskCancelClicked();
    }

    //region TaskRunningPresenter.TaskRunningView

    @Override
    public void closeScreen() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void showTask(Task task) {
        final Uri uri = Uri.parse("file:///android_asset/").buildUpon()
                .appendEncodedPath(task.getTaskInfo().getIconPath())
                .build();

        Glide.with(imageViewIcon)
                .load(uri)
                .into(imageViewIcon);

        final String title = getString(R.string.task_detail_title_task_started).replace("{{title}}", task.getEvent().getSummary());

        textViewTitle.setText(title);
    }

    @Override
    public void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog)
                .setTitle(R.string.task_detail_stop)
                .setMessage(R.string.task_detail_stop_description)
                .setPositiveButton(R.string.task_detail_cancel_task, (dialog12, which) -> presenter.onTaskCancelConfirmationClicked())
                .setNegativeButton(R.string.task_detail_continue_task, (dialog1, which) -> presenter.onTaskCancelConfirmationCanceled())
                .show();
    }

    @Override
    public void showWearableNotConnectedError() {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog)
                .setTitle(R.string.task_detail_start_error_title)
                .setMessage(R.string.task_detail_start_error_description)
                .setPositiveButton(R.string.task_detail_start_error_try_again, (dialog12, which) -> presenter.onTaskStartErrorTryAgain())
                .setNegativeButton(R.string.task_detail_start_error_cancel, (dialog1, which) -> presenter.onTaskStartErrorCanceled())
                .show();
    }

    //endregion
}
