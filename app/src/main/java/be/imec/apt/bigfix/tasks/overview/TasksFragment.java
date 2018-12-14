package be.imec.apt.bigfix.tasks.overview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.general.BaseFragment;
import be.imec.apt.bigfix.general.BigFixApplication;
import be.imec.apt.bigfix.general.User;
import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.tasks.running.TaskRunningActivity;
import butterknife.BindView;

public class TasksFragment extends BaseFragment implements TasksPresenter.TasksView, TasksClickListener {

    @BindView(R.id.textview_header)
    TextView textViewHeader;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @Inject
    TasksPresenter presenter;

    private TasksAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BigFixApplication.appComponent.inject(this);

        setupPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        useButterKnife(view);

        setupRecyclerView();

        presenter.start();

        return view;
    }

    private void setupPresenter() {
        presenter.attachView(this);
    }

    private void setupRecyclerView() {
        recyclerView.addItemDecoration(new TasksItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TasksAdapter(getContext(), this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        presenter.stop();
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.onScreenVisible();
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.onScreenInvisible();
    }

    //region TasksClickListener

    @Override
    public void onTaskClicked(Task task) {
        presenter.onTaskClicked(task);
    }

    //endregion

    //region TasksPresenter.TasksView

    @Override
    public void updateUser(User user) {
        final String header = getString(R.string.tasks_header).replace("{{name}}", user.getName());
        textViewHeader.setText(header);
    }

    @Override
    public void showTasks(List<Task> tasks) {
        adapter.setData(tasks);
    }

    @Override
    public void updateTask(int position) {
        adapter.updateTask(position);
    }

    @Override
    public void startTask(Task task) {
        final Intent intent = TaskRunningActivity.getIntent(getContext(), task);
        this.getActivity().startActivityForResult(intent, TaskRunningActivity.REQUEST_CODE);
    }

    //endregion
}
