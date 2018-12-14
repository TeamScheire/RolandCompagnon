package be.imec.apt.bigfix.tasks.overview;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import be.imec.apt.bigfix.R;

class TasksItemDecoration extends RecyclerView.ItemDecoration {
    private final int margin;

    public TasksItemDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.padding_xlarge);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.left = margin;

        final int position = parent.getChildAdapterPosition(view);
        final TasksAdapter.ViewType viewtype = TasksAdapter.ViewType.values()[parent.getAdapter().getItemViewType(position)];

        outRect.right = viewtype == TasksAdapter.ViewType.ACTIVE ? 0 : margin;
        outRect.top = position == 0 ? 0 : margin / 2;
    }
}
