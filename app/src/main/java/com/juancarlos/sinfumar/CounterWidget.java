package com.juancarlos.sinfumar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class CounterWidget extends android.appwidget.AppWidgetProvider {
    static final String REFRESH = "com.juancarlos.sinfumar.REFRESH";

    @Override
    public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id : ids) m.updateAppWidget(id, build(c));
        schedule(c);
    }

    @Override
    public void onReceive(Context c, Intent i) {
        super.onReceive(c, i);
        String a = i.getAction();
        if (REFRESH.equals(a) || Intent.ACTION_BOOT_COMPLETED.equals(a)) {
            AppWidgetManager m = AppWidgetManager.getInstance(c);
            int[] ids = m.getAppWidgetIds(new ComponentName(c, CounterWidget.class));
            for (int id : ids) m.updateAppWidget(id, build(c));
            if (ids.length > 0) schedule(c);
        }
    }

    static RemoteViews build(Context c) {
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.widget);
        v.setTextViewText(R.id.w_title, Stats.NOMBRE + " sin fumar");
        v.setTextViewText(R.id.w_time, Stats.tiempo());
        v.setTextViewText(R.id.w_cigs, Stats.cigsTxt(c));
        v.setTextViewText(R.id.w_money, Stats.dineroTxt(c));
        PendingIntent pi = PendingIntent.getActivity(c, 0, new Intent(c, MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        v.setOnClickPendingIntent(R.id.root, pi);
        return v;
    }

    static void schedule(Context c) {
        AlarmManager am = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent in = new Intent(c, CounterWidget.class).setAction(REFRESH);
        PendingIntent pi = PendingIntent.getBroadcast(c, 1, in,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        am.setAndAllowWhileIdle(AlarmManager.RTC, System.currentTimeMillis() + 15 * 60 * 1000L, pi);
    }

    static void refreshAll(Context c) {
        c.sendBroadcast(new Intent(c, CounterWidget.class).setAction(REFRESH));
    }
}
