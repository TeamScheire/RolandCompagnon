package be.imec.apt.bigfix.calendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import be.imec.apt.bigfix.database.entities.Event;

class CalendarAdapter extends RecyclerView.Adapter<CalendarEventViewHolder> {
    private List<Event> data;

    CalendarAdapter() {
    }

    public void setData(List<Event> data) {
        this.data = data;

        notifyDataSetChanged();
    }

    public Event getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public CalendarEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final CalendarEventView view = new CalendarEventView(parent.getContext());
        return new CalendarEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarEventViewHolder holder, int position) {
        final Event event = getItem(position);
        holder.bindData(event);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
}
