package be.imec.apt.bigfix.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Locale;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.database.entities.Event;
import be.imec.apt.bigfix.utils.DateUtils;

class CalendarItemDecoration extends RecyclerView.ItemDecoration {

    private final int dividerSize;

    private final Drawable divider;
    private final Paint textPaint;
    private final Drawable dividerCurrentTime;

    private final int margin;

    private final int textOffset;
    private final int textWidth;
    private final int textHeight;

    private final int dividerOffset;

    public CalendarItemDecoration(final Context context) {
        dividerSize = context.getResources().getDimensionPixelSize(R.dimen.divider);
        divider = ContextCompat.getDrawable(context, R.drawable.divider_light_gray);
        margin = context.getResources().getDimensionPixelSize(R.dimen.padding_large);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(ContextCompat.getColor(context, R.color.medium_gray));
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.textsize_small));
        final Typeface typeface = ResourcesCompat.getFont(context, R.font.raleway_regular);
        textPaint.setTypeface(typeface);

        final Rect textBounds = new Rect();
        final String dummyTime = "88:88";
        textPaint.getTextBounds(dummyTime, 0, dummyTime.length(), textBounds);

        textOffset = margin * 2;
        textWidth = textBounds.width();
        textHeight = textBounds.height();
        dividerOffset = textOffset + textWidth + margin;

        dividerCurrentTime = ContextCompat.getDrawable(context, R.drawable.divider_current_time);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        //TODO if we want to make the recyclerview scrollable, take scrollY into account for time indications
        final long currentTime = System.currentTimeMillis();

        final int blockHeight = CalendarUtils.calculateHourBlock(parent.getMeasuredHeight());
        final int minutes = DateUtils.getMinutesOfHour(currentTime);
        final int hour = DateUtils.getHour(currentTime);
        final int offset = CalendarUtils.getFirstHourOffset(blockHeight, minutes);

        // Make sure we also draw previous hour which is only partly visible
        int startHour = CalendarUtils.getFirstHourToShow(hour, minutes) - 1;
        startHour = startHour < 0 ? (startHour + 24) % 24 : startHour;
        for (int i = -1; i < CalendarUtils.getHoursToShow() + 1; i++) {

            // Calculate line
            int top = i * blockHeight + offset;
            int bottom = top + dividerSize;

            // Draw hour
            final String time = String.format(Locale.getDefault(), "%02d", startHour % 24) + ":" + "00";

            int textTop = top + textHeight / 2;

            c.drawText(time, textOffset, textTop, textPaint);

            // Draw line
            int right = parent.getMeasuredWidth() - margin;

            divider.setBounds(dividerOffset, top, right, bottom);
            divider.draw(c);

            startHour++;
        }

        // Current time is always 1 hour after start
        final int currentTimeCenterOffset = blockHeight - dividerCurrentTime.getIntrinsicHeight() / 2;
        dividerCurrentTime.setBounds(0, currentTimeCenterOffset, textOffset + textWidth, currentTimeCenterOffset + dividerCurrentTime.getIntrinsicHeight());
        dividerCurrentTime.draw(c);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getHeight() == 0) {
            return;
        }

        final CalendarAdapter adapter = (CalendarAdapter) parent.getAdapter();

        final int blockHeight = CalendarUtils.calculateHourBlock(parent.getHeight());
        final double minuteHeight = CalendarUtils.getMinuteHeight(blockHeight);

        final int position = parent.getChildViewHolder(view).getAdapterPosition();
        final Event event = adapter.getItem(position);

        int offsetInPixels;
        int offsetDuration;
        int childIndex = parent.getChildLayoutPosition(view);
        if (childIndex > 0) {
            final Event previousEvent = adapter.getItem(position - 1);
            offsetDuration = CalendarUtils.getDurationInMinutes(previousEvent.getEndTime(), event.getStartTime());
            offsetInPixels = (int) (offsetDuration * minuteHeight);
        } else {
            final long currentTime = System.currentTimeMillis();
            final long calendarStart = currentTime - 1000 * 60 * 60;
            offsetDuration = CalendarUtils.getDurationInMinutes(calendarStart, event.getStartTime());
            offsetInPixels = (int) (minuteHeight * offsetDuration);
        }

        outRect.left = dividerOffset;
        outRect.right = margin;

        outRect.top = offsetInPixels + divider.getIntrinsicHeight();
    }
}
