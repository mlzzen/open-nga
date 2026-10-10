package sp.phone.util;

import android.content.Context;
import android.content.SharedPreferences;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.base.util.ContextUtils;
import gov.anzong.androidnga.common.PreferenceKey;

/**
 * Created by Justwen on 2018/7/2.
 */
public class ForumUtils {

    private static final String HTTP_SCHEME = "http://";

    private static final String HTTPS_SCHEME = "https://";

    public static String getAvailableDomain() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain)[index];
    }

    public static String getAvailableDomainNoHttp() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
        int index = Integer.parseInt(sp.getString(PreferenceKey.KEY_NGA_DOMAIN, "1"));
        return context.getResources().getStringArray(R.array.nga_domain_no_http)[index];
    }

    public static String getAvailableImageDomain() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = getPreferences(context);
        String custom = sp.getString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN_CUSTOM, "");
        if (isCustomImageDomain(sp) && !trim(custom).isEmpty()) {
            return withScheme(custom);
        }
        return context.getResources().getStringArray(R.array.nga_image_domain)[getImageDomainIndex(context, sp)];
    }

    public static String getAvailableImageDomainNoHttp() {
        Context context = ContextUtils.getContext();
        SharedPreferences sp = getPreferences(context);
        String custom = sp.getString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN_CUSTOM, "");
        if (isCustomImageDomain(sp) && !trim(custom).isEmpty()) {
            return withoutScheme(custom);
        }
        return context.getResources().getStringArray(R.array.nga_image_domain_no_http)[getImageDomainIndex(context, sp)];
    }

    /**
     * 「NGA图片域名」当前是否选中了「自定义」选项
     */
    public static boolean isCustomImageDomain() {
        return isCustomImageDomain(getPreferences(ContextUtils.getContext()));
    }

    /**
     * @return 「自定义」在 R.array.nga_image_domain 中的下标（即最后一项）
     */
    public static int getCustomImageDomainIndex() {
        return ContextUtils.getContext().getResources().getStringArray(R.array.nga_image_domain).length - 1;
    }

    private static boolean isCustomImageDomain(SharedPreferences sp) {
        return String.valueOf(getCustomImageDomainIndex())
                .equals(sp.getString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN, PreferenceKey.DEFAULT_NGA_IMAGE_DOMAIN));
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PreferenceKey.PERFERENCE, Context.MODE_PRIVATE);
    }

    private static int getImageDomainIndex(Context context, SharedPreferences sp) {
        String value = sp.getString(PreferenceKey.KEY_NGA_IMAGE_DOMAIN, PreferenceKey.DEFAULT_NGA_IMAGE_DOMAIN);
        int length = context.getResources().getStringArray(R.array.nga_image_domain).length;
        try {
            int index = Integer.parseInt(value);
            if (index >= 0 && index < length) {
                return index;
            }
        } catch (NumberFormatException ignored) {
        }
        return Integer.parseInt(PreferenceKey.DEFAULT_NGA_IMAGE_DOMAIN);
    }

    /**
     * 去掉首尾空白与结尾的斜杠，避免和后续路径拼接出双斜杠
     */
    private static String trim(String domain) {
        return domain == null ? "" : domain.trim().replaceAll("/+$", "");
    }

    private static String withScheme(String domain) {
        String value = trim(domain);
        if (value.startsWith(HTTP_SCHEME) || value.startsWith(HTTPS_SCHEME)) {
            return value;
        }
        return HTTPS_SCHEME + value;
    }

    private static String withoutScheme(String domain) {
        String value = trim(domain);
        if (value.startsWith(HTTPS_SCHEME)) {
            return value.substring(HTTPS_SCHEME.length());
        }
        if (value.startsWith(HTTP_SCHEME)) {
            return value.substring(HTTP_SCHEME.length());
        }
        return value;
    }

    /**
     * @param statusCode
     * @return 返回子板块是否被订阅
     */
    public static boolean isBoardSubscribed(int statusCode) {
        // 3,810 返回false
        return statusCode == 7 || statusCode == 558 || statusCode == 542 || statusCode == 2606 || statusCode == 2590
                || statusCode == 4654;
    }

}
