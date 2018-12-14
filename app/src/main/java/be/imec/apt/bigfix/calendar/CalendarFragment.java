package be.imec.apt.bigfix.calendar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import be.imec.apt.bigfix.general.BaseFragment;
import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.info.LocalizerDialogFragment;
import butterknife.BindView;

public class CalendarFragment extends BaseFragment implements CalendarPresenter.CalendarView {

    @BindView(R.id.view_overlay)
    View viewOverlay;

    @BindView(R.id.textview_header)
    TextView textViewHeader;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Inject
    CalendarPresenter presenter;

    private CalendarAdapter adapter;

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
        final View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        useButterKnife(view);

        setupRecyclerView();
        setupClickListeners();

        presenter.start();

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.addItemDecoration(new CalendarItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CalendarAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        viewOverlay.setOnClickListener(v -> presenter.onCalendarClicked());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.stop();
    }

    //region CalendarPresenter.CalendarView

    @Override
    public void showCalendarOwner(User user) {
        final String header = getString(R.string.calendar_header).replace("{{name}}", user.getName());
        textViewHeader.setText(header);
    }

    @Override
    public void showCalendar(List<Event> events) {
        adapter.setData(events);
    }

    @Override
    public void openLocalizer() {
        final DialogFragment dialogFragment = new LocalizerDialogFragment();
        dialogFragment.show(getChildFragmentManager(), LocalizerDialogFragment.class.getSimpleName());
    }

    //endregion
}
