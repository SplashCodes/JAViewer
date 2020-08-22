package io.github.javiewer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * Created by ipcjs on 2020/8/23
 */
public class IOUtils {
    @SuppressWarnings("CharsetObjectCanBeUsed")
    public static final Charset UTF_8 = Charset.forName("utf-8");

    public static String readText(InputStream is, Charset charset) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}
