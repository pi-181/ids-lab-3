package com.demkom58.ids_lab_2.compute.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public final class StringUtil {
    public static String toPathString(URL url) {
        String path;

        try {
            path = URLDecoder.decode(url.getFile(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

        return path;
    }
}
