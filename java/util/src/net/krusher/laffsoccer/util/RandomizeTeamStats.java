package net.krusher.laffsoccer.util;

import com.badlogic.gdx.utils.Json;
import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.generateSkills;

public class RandomizeTeamStats {

    public static final Random RND = new Random();

    public static void main(String[] arg) throws IOException {

        System.out.println("Loading team...");

        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        FileFilter extensionFilter = new FileNameExtensionFilter("JSON File", "json");
        fileChooser.setDialogTitle("Specify a file to load");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(extensionFilter);
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        if (userSelection == JFileChooser.CANCEL_OPTION) {
            System.exit(0);
        }
        File fileToLoad = fileChooser.getSelectedFile();

        Team team = Auxiliary.loadTeamFile(fileToLoad.getAbsolutePath());

        team.players.forEach(player -> {
            // Goalkeeper
            if (player.role == Player.Role.GOALKEEPER) {
                player.value = RND.nextInt(30) + 10;
            } else {

                player.skills = generateSkills(player.role);

                List<Player.Skill> skills = new LinkedList<Player.Skill>(Arrays.asList(Player.Skill.values()));
                Collections.shuffle(skills);
                player.bestSkills.clear();
                player.bestSkills.addAll(skills.subList(0, RND.nextInt(skills.size())));

            }
        });

        Json json = new Json();
        String result = json.toJson(team, Team.class);

        JFileChooser fileChooserSave = new JFileChooser();
        fileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooserSave.setCurrentDirectory(fileToLoad);
        fileChooserSave.setSelectedFile(new File("team." + FileUtils.normalizeName(team.name) + ".json"));
        fileChooserSave.setFileFilter(extensionFilter);
        fileChooserSave.setDialogTitle("Specify a file to save");

        userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save as file: " + fileToSave.getAbsolutePath());
            Auxiliary.writeTeamFile(team, fileToSave);
        }

        System.out.print("Done");
        parentFrame.dispose();

    }

}
