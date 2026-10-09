package com.juancarlos.sinfumar;

import android.content.Context;
import android.content.SharedPreferences;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class Stats {
    // Fecha fija de inicio: viernes 2 de octubre de 2026, 11:00 (Madrid). No hay forma de reiniciarla.
    public static final String START = "2026-10-02T11:00:00";
    public static final String NOMBRE = "Juan Carlos";

    public static long startMillis() {
        return LocalDateTime.parse(START).atZone(ZoneId.of("Europe/Madrid")).toInstant().toEpochMilli();
    }

    static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences("ajustes", Context.MODE_PRIVATE);
    }
    public static float cigsPorDia(Context c) { return prefs(c).getFloat("cigs", 20f); }
    public static float precioPaquete(Context c) { return prefs(c).getFloat("precio", 6.0f); }
    public static void guardar(Context c, float cigs, float precio) {
        prefs(c).edit().putFloat("cigs", cigs).putFloat("precio", precio).apply();
    }

    public static long elapsedMs() { return Math.max(0, System.currentTimeMillis() - startMillis()); }

    public static String tiempo() {
        long m = elapsedMs() / 60000;
        return (m / 1440) + " d  " + ((m % 1440) / 60) + " h  " + (m % 60) + " min";
    }
    public static double cigsEvitados(Context c) {
        return elapsedMs() / 86400000.0 * cigsPorDia(c);
    }
    public static double dineroAhorrado(Context c) {
        return cigsEvitados(c) * (precioPaquete(c) / 20.0);
    }
    public static String cigsTxt(Context c) { return String.format(Locale.getDefault(), "%d cigarros sin fumar", (long) cigsEvitados(c)); }
    public static String dineroTxt(Context c) { return String.format(Locale.getDefault(), "%.2f € ahorrados", dineroAhorrado(c)); }
}
