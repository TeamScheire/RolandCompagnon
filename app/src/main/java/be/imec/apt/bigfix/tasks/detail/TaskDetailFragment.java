package be.imec.apt.bigfix.tasks.detail;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BaseFragment;
import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.tasks.running.TaskRunningActivity;
import be.imec.apt.bigfix.utils.CustomTypefaceSpan;
import be.imec.apt.bigfix.utils.DateUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class TaskDetailFragment extends BaseFragment implements TaskDetailPresenter.TaskDetailView {

    private static final String EXTRA_EVENT_ID = "extraEventId";

    @Inject
    TaskDetailPresenter presenter;

    @BindView(R.id.textview_title)
    TextView textViewTitle;

    @BindView(R.id.lottieview_icon)
    LottieAnimationView lottieViewIcon;

    @BindView(R.id.button_cta)
    Button buttonCta;

    public static Bundle getBundle(Event event) {
        final Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_EVENT_ID, event.getId());
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setupPresenter();
    }

    private void setupPresenter() {
        presenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        useButterKnife(view);

        if (getArguments() != null) {
            final long eventId = getArguments().getLong(EXTRA_EVENT_ID);
            presenter.start(eventId);
        }

        return view;
    }

    @OnClick(R.id.layout_detail)
    public void onStartTaskClicked() {
        presenter.onStartTaskClicked();
    }

    //region TaskDetailPresenter.TaskDetailView

    @Override
    public void showTask(Task task) {
        // Title
        final Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.raleway_bold);
        final CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(null, typeface);

        final String time = DateUtils.formatTime(task.getEvent().getStartTime());
        final String summary = task.getEvent().getSummary();
        String title = getString(R.string.task_title).replace("{{time}}", time);
        final int indexTitle = title.indexOf("{{title}}");
        title = title.replace("{{title}}", summary);
        final SpannableStringBuilder titleSpan = SpannableStringBuilder.valueOf(title);
        titleSpan.setSpan(typefaceSpan, indexTitle, title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        textViewTitle.setText(titleSpan);

        // Animating icon
        //TODO make sure all files are available
        try {
            lottieViewIcon.setAnimation(task.getTaskInfo().getAnimationPath());
            lottieViewIcon.playAnimation();
        } catch (IllegalArgumentException ignore) {

        }
    }

    @Override
    public void showTaskStarted(Task task) {
        final Intent intent = TaskRunningActivity.getIntent(getContext(), task);
        this.getActivity().startActivityForResult(intent, TaskRunningActivity.REQUEST_CODE);
    }

    //endregion


}
