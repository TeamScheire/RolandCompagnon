package be.imec.apt.bigfix.info;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BaseFragment;
import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.utils.CustomTypefaceSpan;
import be.imec.apt.bigfix.utils.DateUtils;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

public class LocalizerDialogFragment extends BaseFragment implements LocalizerPresenter.LocalizerView {

    @Inject
    LocalizerPresenter localizerPresenter;

    @BindView(R.id.imageview_user_image)
    ImageView imageViewUser;

    @BindView(R.id.textview_title)
    TextView textViewTitle;

    @BindView(R.id.textview_estimated_time)
    TextView textViewEstimatedTime;

    @BindView(R.id.textview_calendar)
    TextView textViewCalendar;

    @BindView(R.id.layout_event)
    ViewGroup layoutEvent;

    @BindView(R.id.textview_start_end_time)
    TextView textViewStartEndTime;

    @BindView(R.id.textview_summary)
    TextView textViewSummary;

    @BindView(R.id.textview_location)
    TextView textViewLocation;

    @BindView(R.id.textview_description)
    TextView textViewDescription;

    @BindView(R.id.textview_no_activity)
    TextView textViewNoActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setupPresenter();
    }

    private void setupPresenter() {
        localizerPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_localizer, container, false);

        useButterKnife(view);

        localizerPresenter.start();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        return dialog;
    }

    @OnClick(R.id.button_back)
    public void onBackPressed() {
        this.dismiss();
    }

    //region LocalizerPresenter.LocalizerView

    @Override
    public void showUser(User user) {
        textViewTitle.setText(getString(R.string.localizer_calendar_title).replace("{{name}}", user.getName()));

        Glide.with(this)
                .load(user.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewUser);
    }

    @Override
    public void showEvent(Event event) {
        this.textViewCalendar.setVisibility(View.GONE);
        this.layoutEvent.setVisibility(View.VISIBLE);
        this.textViewNoActivity.setVisibility(View.GONE);
        this.textViewEstimatedTime.setVisibility(View.VISIBLE);

        final int minuteToReturn = (int) Math.ceil((event.getEndTime() - System.currentTimeMillis()) / 1000 / 60.0);
        final String unit = getResources().getQuantityString(R.plurals.estimated_time_minutes, minuteToReturn);
        final String result = getString(R.string.localizer_calendar_return).replace("{{minutes}}", String.valueOf(minuteToReturn)).replace("{{unit}}", unit);

        textViewEstimatedTime.setText(result);

        final String start = DateUtils.formatTime(event.getStartTime());
        final String end = DateUtils.formatTime(event.getEndTime());
        this.textViewStartEndTime.setText(getString(R.string.localizer_calendar_start_end).replace("{{starttime}}", start).replace("{{endtime}}", end));

        this.textViewSummary.setText(event.getSummary());

        if (TextUtils.isEmpty(event.getLocation())) {
            textViewLocation.setVisibility(View.GONE);
        } else {
            textViewLocation.setVisibility(View.VISIBLE);
            textViewLocation.setText(event.getLocation());
        }

        if (!TextUtils.isEmpty(event.getDescription())) {
            textViewDescription.setText(event.getDescription());
            textViewDescription.setVisibility(View.VISIBLE);
        } else {
            textViewDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNoEvents() {
        this.textViewCalendar.setVisibility(View.VISIBLE);
        this.layoutEvent.setVisibility(View.GONE);
        this.textViewNoActivity.setVisibility(View.VISIBLE);
        this.textViewEstimatedTime.setVisibility(View.GONE);
    }

    //endregion
}
