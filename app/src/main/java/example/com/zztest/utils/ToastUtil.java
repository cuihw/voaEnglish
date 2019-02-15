package example.com.zztest.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    private static Toast toast = null;

    public static void showTextToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 带判断的toast
     *
     * @param context
     * @param str
     * @param flag    是否显示标记
     */
    public static void showTextToast(Context context, String str, boolean flag) {
        if (flag) {
            showTextToast(context, str);
        }
    }
}
