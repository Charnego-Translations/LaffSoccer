package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ygames.ysoccer.framework.Assets.EXTENSIONS;
import static com.ygames.ysoccer.framework.EMath.randomPick;

public class CommonComment {

    public static final Map<CommonCommentType, Set<Sentence>> commonCommentary = new HashMap<>();
    public static final Map<CommonCommentType, Set<Sentence>> commonCommentarySecondary = new HashMap<>();

    static {
        for (CommonCommentType value : CommonCommentType.values()) {
            commonCommentary.put(value, new HashSet<>());
            commonCommentarySecondary.put(value, new HashSet<>());
        }
    }

    public static final Set<Sentence> allComments = new HashSet<>();
    public static final Sound[] numbers = new Sound[999];

    public static Sound[] pull(CommonCommentType type, Team homeTeam, Team team, Player player) {
        TeamCommentary teamCommentary = team != null && player != null ?
            TeamCommentary.teams.get(FileUtils.getTeamFromFile(team.path))
            : TeamCommentary.EMPTY;
        TeamCommentary teamCommentaryHome = homeTeam != null ?
            TeamCommentary.teams.get(FileUtils.getTeamFromFile(homeTeam.path))
            : TeamCommentary.EMPTY;

        Sentence sentence = randomPick(commonCommentary.get(type)
            .stream()
            .filter(s -> s.requiresStart == SentenceRequirement.NONE
                || (s.requiresStart == SentenceRequirement.TEAM && teamCommentary.teamName != null)
                || (s.requiresStart == SentenceRequirement.PLAYER && player != null && teamCommentary.players.containsKey(player.shirtName))
                || (s.requiresStart == SentenceRequirement.CITY && teamCommentary.city != null)
                || (s.requiresStart == SentenceRequirement.STADIUM && teamCommentaryHome.stadiumName != null)
            )
            .collect(Collectors.toSet()));

        Sound prefix = null;
        Sound suffix = null;
        Sound main = sentence.sound;

        if (sentence.requiresStart != SentenceRequirement.NONE) {
            prefix = resolveRequirement(sentence.requiresStart, teamCommentaryHome, teamCommentary, player);
        }

        if (sentence.requiresEnd != SentenceRequirement.NONE) {
            suffix = resolveRequirement(sentence.requiresEnd, teamCommentaryHome, teamCommentary, player);
        }

        return Stream.of(prefix, main, suffix)
            .filter(Objects::nonNull)
            .toArray(Sound[]::new);
    }

    private static Sound resolveRequirement(
        SentenceRequirement requirement,
        TeamCommentary home,
        TeamCommentary teamCommentary,
        Player player
    ) {
        switch (requirement) {
            case TEAM:
                return teamCommentary.teamName;
            case PLAYER:
                return teamCommentary.players.get(player.shirtName);
            case CITY:
                return home.city;
            case STADIUM:
                return home.stadiumName;
            default:
                return null;
        }
    }

    public static Sound pull(CommonCommentType type) {
        return randomPick(commonCommentary.get(type).stream()
            .filter(sentence -> sentence.requiresStart == SentenceRequirement.NONE && sentence.requiresEnd == SentenceRequirement.NONE)
            .collect(Collectors.toSet())).sound;
    }

    public static Sentence pullSecond(CommonCommentType type) {
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
                        commonCommentary.get(type).add(new Sentence(Gdx.audio.newSound(fileHandle), SentenceRequirement.NONE, SentenceRequirement.NONE));
                    }
                }
            }
        }
        // Comments in their folders
        for (CommonCommentType commentType : CommonCommentType.values()) {
            commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/");
            for (FileHandle fileHandle : commentaryFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    Sound sound = Gdx.audio.newSound(fileHandle);
                    if (fileHandle.name().startsWith("number")) {
                        numbers[Integer.parseInt(fileHandle.name().substring(6))] = sound;
                    }
                    commonCommentary.get(commentType).add(Sentence.of(Gdx.audio.newSound(fileHandle), fileHandle.nameWithoutExtension()));
                }
            }
            // Secondary comments
            commentaryFolder = Gdx.files.local("sounds/commentary/" + commentType.name().toLowerCase() + "/secondary/");
            for (FileHandle fileHandle : commentaryFolder.list()) {
                if (EXTENSIONS.contains(fileHandle.extension().toLowerCase())) {
                    commonCommentarySecondary.get(commentType).add(Sentence.of(Gdx.audio.newSound(fileHandle)));
                }
            }
        }
        allComments.addAll(Arrays.stream(numbers).map(Sentence::of).collect(Collectors.toSet()));
        commonCommentary.forEach((k, v) -> allComments.addAll(v));
        commonCommentarySecondary.forEach((k, v) -> allComments.addAll(v));
        allComments.removeIf(comment -> comment == null || comment.sound == null);
    }

    public static void stopAll() {
        allComments.forEach(comment -> comment.sound.stop());
    }

}
