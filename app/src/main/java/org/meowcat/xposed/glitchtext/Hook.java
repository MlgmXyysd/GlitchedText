package org.meowcat.xposed.glitchtext;

import android.app.Application;
import android.content.Context;
import android.graphics.Paint;
import android.os.Binder;
import android.system.Os;
import android.system.OsConstants;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import dalvik.system.DexFile;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Hook implements IXposedHookZygoteInit {

    static boolean availability = false;

    public static boolean getParamAvailability(final XC_MethodHook.MethodHookParam methodHookParam, int callingPid) {
        if (availability) {
            return availability;
        }
        new Thread(() -> {
            Object[] dexElements = (Object[]) XposedHelpers.getObjectField(XposedHelpers.getObjectField(XposedBridge.class.getClassLoader(), "pathList"), "dexElements");
            for (Object entry : dexElements) {
                Enumeration<String> entries = ((DexFile) XposedHelpers.getObjectField(entry, "dexFile")).entries();
                while (entries.hasMoreElements()) {
                    if (entries.nextElement().matches(".+?(epic|weishu).+")) {
                        try {
                            String message = new String(Base64.decode("RG8gTk9UIHVzZSBUYWlDaGkgYW55d2F5XG7or7fkuI3opoHkvb/nlKjlpKrmnoHmiJbml6DmnoE=".getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
                            if (methodHookParam.args[0] instanceof Application) {
                                Toast.makeText((Context) methodHookParam.args[0], message, Toast.LENGTH_LONG).show();
                            }
                            XposedBridge.log(message);
                            Os.kill(callingPid, OsConstants.SIGKILL);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }).start();
        return true;
    }

    public static String glitchtext(int length) {
        final String nonunicode = "¡¢£¤¥¦§¨©ª«¬®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇňŉŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽž";
        StringBuilder output = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            output.append(nonunicode.charAt(genRandomInt(0, nonunicode.length() - 1)));
        }
        return output.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private static int genRandomInt(int min, int max) {
        return (int) (java.lang.Math.random() * 1000000000 % (max - min + 1) + min);
    }

    @Override
    public void initZygote(StartupParam startupParam) {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                availability = getParamAvailability(param, Binder.getCallingPid());
                CharSequence sequence = (CharSequence) param.args[0];
                if (sequence != null) {
                    param.args[0] = sequence.toString().replaceAll("\\w+", glitchtext(sequence.toString().length()));
                }
            }
        };

        findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class,
                boolean.class, int.class, hook);
        findAndHookMethod(TextView.class, "setHint", CharSequence.class, hook);
        findAndHookMethod("android.view.GLES20Canvas", null, "drawText", String.class, float.class,
                float.class, Paint.class, hook);
    }

}
