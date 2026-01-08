package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.chooseLoadTeam;
import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.generateSkills;

public class RandomizeTeamStats {

    public static final Random RND = new Random();

    public static void main(String[] arg) throws IOException {

        System.out.println("Loading team...");

        File fileToLoad = Auxiliary.chooseLoadTeam();
        if (fileToLoad == null) {
            return;
        }

        Team team = Auxiliary.loadTeamFile(fileToLoad.getAbsolutePath());

        team.players.forEach(Auxiliary::randomizePlayerStats);

        File userSelection = chooseLoadTeam();

        if (userSelection != null) {
            System.out.println("Save as file: " + userSelection.getAbsolutePath());
            Auxiliary.writeTeamFile(team, userSelection);
        }

        System.out.print("Done");

    }

}
