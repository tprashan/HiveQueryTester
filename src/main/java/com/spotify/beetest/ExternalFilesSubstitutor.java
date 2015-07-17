package com.spotify.beetest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.IOException;

public class ExternalFilesSubstitutor {
    public static final String replace(final String source) {
        StrSubstitutor strSubstitutor = new StrSubstitutor(
                new StrLookup<Object>() {
                    @Override
                    public String lookup(final String key) {
                        try {
                            return Utils.readFile(StringUtils.trim(key));
                        } catch (IOException e) {
                            return "";
                        }
                    }
                }, "<%", "%>", '$');
        return strSubstitutor.replace(source);
    }
}
