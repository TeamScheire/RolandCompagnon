package be.imec.apt.bigfix.utils;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

@SuppressLint("ParcelCreator")
public class CustomTypefaceSpan extends TypefaceSpan
{
    private final Typeface newType;

    public CustomTypefaceSpan(final String family, final Typeface type) {
        super(family);
        this.newType = type;
    }

    @Override
    public void updateDrawState(final TextPaint ds) {
        applyCustomTypeFace(ds, this.newType);
    }

    @Override
    public void updateMeasureState(final TextPaint paint) {
        applyCustomTypeFace(paint, this.newType);
    }

    private static void applyCustomTypeFace(final Paint paint, final Typeface tf) {
        final int oldStyle;
        final Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        final int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }
}
