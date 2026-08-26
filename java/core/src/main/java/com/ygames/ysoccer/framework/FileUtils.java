package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.audio.Mp3;
import com.badlogic.gdx.backends.lwjgl3.audio.Ogg;
import com.badlogic.gdx.backends.lwjgl3.audio.Wav;
import com.badlogic.gdx.files.FileHandle;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class FileUtils {

    public static final char[] BAD_CHARS = new char[] {'.', '\\', '/', '\'', ',', ':', ';', ' '};

    // Absolute directory to resolve local() paths against, set by platform-specific launchers (e.g. the
    // desktop lwjgl3 launcher, for a macOS app bundle) when Gdx.files.local's cwd-based resolution can't be
    // trusted. Null means fall back to the normal Gdx.files.local(...) behavior.
    private static String localFilesBase = null;

    public static void setLocalFilesBase(String absolutePath) {
        localFilesBase = absolutePath;
    }

    /** Like {@code Gdx.files.local(path)}, but resolves against {@link #setLocalFilesBase} when it's set. */
    public static FileHandle local(String path) {
        if (localFilesBase != null) {
            return Gdx.files.absolute(localFilesBase + "/" + path);
        }
        return Gdx.files.local(path);
    }

    static byte[] inputStreamToBytes(ByteArrayInputStream byteArrayInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int read = byteArrayInputStream.read();
            while (read != -1) {
                byteArrayOutputStream.write(read);
                read = byteArrayInputStream.read();
            }
        } catch (Exception e) {
            Gdx.app.error("Error converting inputStream", e.toString());
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String getTeamFromFile(String path) {
        if (path == null) {
            return null;
        }
        return path.substring(path.indexOf('.') + 1, path.lastIndexOf('.'));
    }

    public static String getPathFromTeamPath(String path) {
        if (path == null) {
            return null;
        }
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    public static float soundDuration(Sound sound) {
       if (sound instanceof Ogg.Sound) {
            return ((Ogg.Sound) sound).duration();
        } else if (sound instanceof Wav.Sound) {
            return ((Wav.Sound) sound).duration();
        } else if (sound instanceof Mp3.Sound) {
            return ((Mp3.Sound) sound).duration();
        } else {
            return 0f;
        }
    }

    public static String normalizeName(String name) {
        String normalized = StringUtils.stripAccents(name).toLowerCase();
        for (char badChar : BAD_CHARS) {
            normalized = normalized.replace(String.valueOf(badChar), "");
        }
        return normalized;
    }

}
