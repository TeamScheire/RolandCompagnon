package be.imec.apt.bigfix.calendar;

import android.support.v7.widget.RecyclerView;

import be.imec.apt.bigfix.database.entities.Event;

class CalendarEventViewHolder extends RecyclerView.ViewHolder {

    public CalendarEventViewHolder(CalendarEventView view) {
        super(view);
    }

    public void bindData(Event event) {
        ((CalendarEventView) itemView).bindData(event);
    }
}
