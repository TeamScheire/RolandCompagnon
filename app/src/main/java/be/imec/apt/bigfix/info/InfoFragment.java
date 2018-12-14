package be.imec.apt.bigfix.info;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BaseFragment;
import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.utils.CustomTypefaceSpan;
import be.imec.apt.bigfix.utils.DateUtils;
import be.imec.apt.bigfix.utils.HtmlCompat;
import butterknife.BindView;
import butterknife.OnClick;

public class InfoFragment extends BaseFragment implements InfoPresenter.InfoView {

    @BindView(R.id.textview_time)
    TextView textViewTime;

    @BindView(R.id.textview_date)
    TextView textViewDate;

    @Inject
    InfoPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setupPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_info, container, false);

        useButterKnife(view);

        presenter.start();

        return view;
    }

    private void setupPresenter() {
        presenter.attachView(this);
    }

    //region InfoPresenter.InfoView

    @Override
    public void updateTime(long time) {
        if (textViewTime == null) {
            return;
        }

        textViewTime.setText(DateUtils.formatTime(time));

        final String day = DateUtils.formatDay(time);
        final SpannableStringBuilder spannableStringBuilder = SpannableStringBuilder.valueOf(day + ", " + DateUtils.formatDate(time));
        final Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.raleway_bold);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan(null, typeface), 0, day.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        textViewDate.setText(spannableStringBuilder);
    }

    //endregion
}
