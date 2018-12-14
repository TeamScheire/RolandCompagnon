package be.imec.apt.bigfix.tasks.overview;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.tasks.Task;

class TasksAdapter extends RecyclerView.Adapter<TaskViewHolder> {

    public enum ViewType {
        ACTIVE(R.layout.listitem_task_active),
        NOT_ACTIVE(R.layout.listitem_task),
        COMPLETED(R.layout.listitem_task_completed);

        private final int layoutId;

        ViewType(@LayoutRes int layoutId) {

            this.layoutId = layoutId;
        }
    }

    private final LayoutInflater inflater;
    private final TasksClickListener tasksClickListener;

    private List<Task> data;

    TasksAdapter(Context context, TasksClickListener tasksClickListener) {
        inflater = LayoutInflater.from(context);
        this.tasksClickListener = tasksClickListener;
    }

    public void setData(List<Task> data) {
        this.data = data;

        notifyDataSetChanged();
    }


    private Task getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        final Task task = getItem(position);
        final long currentTime = System.currentTimeMillis();
        if (task.getEvent().isCompleted()) {
            return ViewType.COMPLETED.ordinal();
        } else if (task.getEvent().isMissed() || task.getEvent().isStarted()) {
            return ViewType.NOT_ACTIVE.ordinal();
        } else if (task.getEvent().getStartTime() <= currentTime && currentTime <= task.getEvent().getEndTime()) {
            return ViewType.ACTIVE.ordinal();
        }

        return ViewType.NOT_ACTIVE.ordinal();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ViewType resolvedViewType = ViewType.values()[viewType];
        final View view = inflater.inflate(resolvedViewType.layoutId, parent, false);

        return new TaskViewHolder(view, tasksClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        final Task task = getItem(position);
        holder.bindData(task);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void updateTask(int position) {
        notifyItemChanged(position);
    }
}
