package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.ygames.ysoccer.framework.Assets.EXTENSIONS;
import static com.ygames.ysoccer.framework.EMath.randomPick;

public class CommonComment {

    public static final Map<CommonCommentType, Set<Sound>> commonCommentary = new HashMap<>();
    public static final Map<CommonCommentType, Set<Sound>> commonCommentarySecondary = new HashMap<>();

    static {
        for (CommonCommentType value : CommonCommentType.values()) {
            commonCommentary.put(value, new HashSet<>());
            commonCommentarySecondary.put(value, new HashSet<>());
        }
    }

    public static final Set<Sound> allComments = new HashSet<>();
    public static final Sound[] numbers = new Sound[999];

    public static Sound pull(CommonCommentType type) {
        return randomPick(commonCommentary.get(type));
    }

    public static Sound pullSecond(CommonCommentType type) {
        return randomPick(commonCommentarySecondary.get(type));
    }

    public static void load() {
        FileHandle numbersFolder = Gdx.files.local("sounds/commentary/numbers");
        for (FileHandle fileHandle : numbersFolder.list()) {
            if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                String name = fileHandle.nameWithoutExtension();
                numbers[Integer.parseInt(name)] = Gdx.audio.newSound(fileHandle);
            }
        }
        // Legacy load
        FileHandle commentaryFolder = Gdx.files.local("sounds/commentary");
        for (FileHandle fileHandle : commentaryFolder.list()) {
            if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                String name = fileHandle.nameWithoutExtension();
                for (CommonCommentType type : CommonCommentType.values()) {
                    String fileType = type.name().toLowerCase();
                    if (name.startsWith(fileType)) {
                        commonCommentary.get(type).add(Gdx.audio.newSound(fileHandle));
                    }
                }
            }
        }
        // Comments in their folders
        for (CommonCommentType commentType : CommonCommentType.values()) {
            commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/");
            for (FileHandle fileHandle : commentaryFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    commonCommentary.get(commentType).add(Gdx.audio.newSound(fileHandle));
                }
            }
            // Secondary comments
            commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/secondary/");
            for (FileHandle fileHandle : commentaryFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    commonCommentarySecondary.get(commentType).add(Gdx.audio.newSound(fileHandle));
                }
            }
        }
        allComments.addAll(Arrays.asList(numbers));
        commonCommentary.forEach((k, v) -> allComments.addAll(v));
        commonCommentarySecondary.forEach((k, v) -> allComments.addAll(v));
        allComments.remove(null);
    }

    public static void stop() {
        allComments.forEach(Sound::stop);
    }

}
