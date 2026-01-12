package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;

import java.io.File;
import java.io.IOException;

public class GenerateVoices {


    public static void main(String[] args) throws IOException {

        final File file;
        if (args.length > 0) {
            file = new File(args[0]);
        } else {
            file = Auxiliary.chooseLoadTeam();
        }

        if (file == null) {
            return;
        }

        String teamFile = FileUtils.getTeamFromFile(file.toString());
        File directory = new File(file.getParent() + "/" + teamFile);

        if (!directory.exists()) {
            directory.mkdir();
        }

        Team team = Auxiliary.loadTeamFile(file.getAbsolutePath());

        createIfNotExist(team.city + ".", new File(directory.getAbsolutePath() +"/city.mp3"));
        createIfNotExist(team.stadium + ".", new File(directory.getAbsolutePath() +"/stadium.mp3"));
        createIfNotExist(team.name + ".", new File(directory.getAbsolutePath() +"/team.mp3"));

        for (Player player : team.players) {

            File voice = new File(directory.getAbsolutePath() + "/player_" + FileUtils.normalizeName(player.shirtName) + ".mp3");
            if (voice.exists()) {
                continue;
            }

            try {
                String text = player.shirtName.replaceAll("\\w+\\.", "").trim();
                System.out.println("Processing: " + text);
                createIfNotExist(text + ".", voice);
            } catch (Exception e) {
                System.out.println("Error downloading " + player.shirtName + " voice");
                e.printStackTrace();
            }

        }

    }

    private static void createIfNotExist(String text, File voiceFile) throws IOException {
        if (!voiceFile.exists()) {
            Auxiliary.generateVoice(text, voiceFile);
        }
    }

}
