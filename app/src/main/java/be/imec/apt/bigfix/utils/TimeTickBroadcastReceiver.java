package be.imec.apt.bigfix.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import be.imec.apt.bigfix.di.DateChangeBus;
import be.imec.apt.bigfix.di.TimeTickBus;
import be.imec.apt.bigfix.general.BigFixApplication;

public class TimeTickBroadcastReceiver extends BroadcastReceiver {

    @Inject
    TimeTickBus timeTickBus;

    @Inject
    DateChangeBus dateChangedBus;

    public TimeTickBroadcastReceiver() {
        BigFixApplication.appComponent.inject(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        timeTickBus.send(System.currentTimeMillis());

        final String formatTime = DateUtils.formatTime(System.currentTimeMillis());
        if ("00:00".equals(formatTime)) {
            // If it's midnight, send out a date change event
            dateChangedBus.send(System.currentTimeMillis());
        }
    }
}
