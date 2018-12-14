package be.imec.apt.bigfix.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import be.imec.apt.bigfix.R;
import be.imec.apt.bigfix.database.entities.Event;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.MeasureSpec.EXACTLY;

public class CalendarEventView extends FrameLayout {

    @BindView(R.id.textview_title)
    TextView textViewTitle;

    private Event event;

    public CalendarEventView(Context context) {
        super(context);

        init();
    }

    public CalendarEventView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public CalendarEventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.view_calendar_event, this);

        ButterKnife.bind(this);
    }

    public void bindData(Event event) {
        this.event = event;

        setBackgroundResource(R.drawable.shape_event_background);
        textViewTitle.setText(event.getSummary());

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int durationInMinutes = CalendarUtils.getDurationInMinutes(event.getStartTime(), event.getEndTime());

        final int blockHeight = CalendarUtils.calculateHourBlock(((RecyclerView) getParent()).getHeight());
        final double minuteHeight = CalendarUtils.getMinuteHeight(blockHeight);

        final int height = (int) (durationInMinutes * minuteHeight);

        final int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, EXACTLY);

        setMeasuredDimension(widthMeasureSpec, newHeightMeasureSpec);
    }
}
