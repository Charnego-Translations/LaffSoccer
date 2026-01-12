package com.ygames.ysoccer.framework.commentary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Team;

import java.util.HashMap;
import java.util.Map;

import static com.ygames.ysoccer.framework.Assets.EXTENSIONS;

public class TeamCommentary {

        public static final Map<String, TeamCommentary> teams = new HashMap<>();

        public static final TeamCommentary EMPTY = new TeamCommentary();

        public Sound teamName;
        public Sound stadiumName;
        public Sound city;
        public final Map<String, Sound> players = new HashMap<>();

        public static void load(Team team) {

            String teamFile = FileUtils.getTeamFromFile(team.path);
            String teamPath = FileUtils.getPathFromTeamPath(team.path);
            String soundPath = "data/teams" + teamPath + teamFile;

            TeamCommentary element = new TeamCommentary();

            element.teamName = loadSound(getMedia(soundPath + "/team"));
            element.stadiumName = loadSound(getMedia(soundPath + "/stadium"));
            element.city = loadSound(getMedia(soundPath + "/" + "city"));

            team.players.forEach(player ->
                element.players.put(player.shirtName, loadSound(getMedia(soundPath + "/player_" + FileUtils.normalizeName(player.shirtName))))
            );

            teams.put(teamFile, element);

        }

        private static String getMedia(String name) {
            for (String extension : EXTENSIONS) {
                if (Gdx.files.internal(name + "." + extension).exists()) {
                    return name + "." + extension;
                }
            }
            return name + ".ogg";
        }

        public static void unload() {
            teams.forEach((name, team) -> {
                stopAndDispose(team.teamName);
                stopAndDispose(team.stadiumName);
                stopAndDispose(team.city);
                team.players.forEach((playerName, sound) -> stopAndDispose(sound));
            });
            teams.clear();
        }

    /**
     * Load sound from absolute path from assets directory
     * @param filename path of the sound
     * @return the sound or null if it doesn't exist
     */
    private static Sound loadSound(String filename) {
        FileHandle file = Gdx.files.internal(filename);
        if (file.exists()) {
            return Gdx.audio.newSound(file);
        } else {
            Gdx.app.debug("Tried to load sound but not found", filename);
            return null;
        }
    }

    private static void stopAndDispose(Sound sound) {
        if (sound == null) {
            return;
        }
        sound.stop();
        sound.dispose();
    }
}
