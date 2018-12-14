package be.imec.apt.bigfix.tasks.overview;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.tasks.Task;
import be.imec.apt.bigfix.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.imageview_icon)
    ImageView imageViewIcon;

    @BindView(R.id.textview_title)
    TextView textViewTitle;

    @Nullable
    @BindView(R.id.textview_time)
    TextView textViewTime;
    private final TasksClickListener tasksClickListener;
    private Task task;

    TaskViewHolder(View itemView, TasksClickListener tasksClickListener) {
        super(itemView);
        this.tasksClickListener = tasksClickListener;

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    public void bindData(Task task) {
        this.task = task;

        final Uri uri = Uri.parse("file:///android_asset/").buildUpon()
                .appendEncodedPath(task.getTaskInfo().getIconPath())
                .build();

        Glide.with(imageViewIcon)
                .load(uri)
                .into(imageViewIcon);

        textViewTitle.setText(task.getEvent().getSummary());

        if (textViewTime != null) {
            textViewTime.setText(DateUtils.formatTime(task.getEvent().getStartTime()));
        }
    }

    @Override
    public void onClick(View v) {
        tasksClickListener.onTaskClicked(task);
    }
}
