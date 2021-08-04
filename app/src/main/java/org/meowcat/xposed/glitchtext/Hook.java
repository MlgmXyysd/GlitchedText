package org.meowcat.xposed.glitchtext;

import android.graphics.Paint;
import android.widget.TextView;

import java.security.SecureRandom;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Hook implements IXposedHookZygoteInit {

    public static String glitchText(int length) {
        final String nonNnicode = "¡¢£¤¥¦§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽž";
        StringBuilder output = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            output.append(nonNnicode.charAt(genRandomInt(0, nonNnicode.length() - 1)));
        }
        return output.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private static int genRandomInt(int min, int max) {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt() * 1000000000 % (max - min + 1) + min;
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                CharSequence sequence = (CharSequence) param.args[0];
                if (sequence != null) {
                    param.args[0] = sequence.toString().replaceAll("\\w+", glitchText(sequence.toString().length()));
                }
            }
        };

        findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class, hook);
        findAndHookMethod(TextView.class, "setHint", CharSequence.class, hook);
        findAndHookMethod("android.view.GLES20Canvas", null, "drawText", String.class, float.class, float.class, Paint.class, hook);
    }

}
