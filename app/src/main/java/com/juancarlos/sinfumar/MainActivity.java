package com.juancarlos.sinfumar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    TextView tTime, tCigs, tMoney;
    EditText eCigs, ePrecio;
    final Handler h = new Handler(Looper.getMainLooper());
    final Runnable tick = new Runnable() {
        public void run() {
            tTime.setText(Stats.tiempo());
            tCigs.setText(Stats.cigsTxt(MainActivity.this));
            tMoney.setText(Stats.dineroTxt(MainActivity.this));
            h.postDelayed(this, 1000);
        }
    };

    TextView tv(String s, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(sp); t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setPadding(0, 12, 0, 12);
        return t;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(pad, pad * 2, pad, pad);
        l.setBackgroundColor(Color.parseColor("#1B5E20"));

        l.addView(tv(Stats.NOMBRE + " sin fumar", 20, Color.parseColor("#C8E6C9"), false));
        tTime = tv("", 30, Color.WHITE, true);
        tCigs = tv("", 18, Color.WHITE, false);
        tMoney = tv("", 24, Color.parseColor("#FFEB3B"), true);
        l.addView(tTime); l.addView(tCigs); l.addView(tMoney);
        l.addView(tv("Desde el viernes 2 de octubre de 2026, 11:00", 13, Color.parseColor("#C8E6C9"), false));

        l.addView(tv("Cigarros que fumabas al día", 14, Color.WHITE, false));
        eCigs = new EditText(this);
        eCigs.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        eCigs.setTextColor(Color.WHITE);
        eCigs.setText(fmt(Stats.cigsPorDia(this)));
        l.addView(eCigs);

        l.addView(tv("Precio del paquete (€)", 14, Color.WHITE, false));
        ePrecio = new EditText(this);
        ePrecio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ePrecio.setTextColor(Color.WHITE);
        ePrecio.setText(fmt(Stats.precioPaquete(this)));
        l.addView(ePrecio);

        Button g = new Button(this);
        g.setText("Guardar");
        g.setOnClickListener(v -> {
            try {
                float c = Float.parseFloat(eCigs.getText().toString().replace(',', '.'));
                float p = Float.parseFloat(ePrecio.getText().toString().replace(',', '.'));
                Stats.guardar(this, c, p);
                CounterWidget.refreshAll(this);
                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Pon números válidos", Toast.LENGTH_SHORT).show();
            }
        });
        l.addView(g);

        ScrollView s = new ScrollView(this);
        s.setFillViewport(true);
        s.setBackgroundColor(Color.parseColor("#1B5E20"));
        s.addView(l);
        setContentView(s);
    }

    String fmt(float f) { return f == (long) f ? String.valueOf((long) f) : String.valueOf(f); }

    @Override protected void onResume() { super.onResume(); h.post(tick); }
    @Override protected void onPause() { super.onPause(); h.removeCallbacks(tick); }
}
