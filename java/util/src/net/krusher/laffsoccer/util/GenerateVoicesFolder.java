package net.krusher.laffsoccer.util;

import net.krusher.laffsoccer.util.auxiliary.Auxiliary;

import java.io.File;
import java.io.IOException;

public class GenerateVoicesFolder {


    public static void main(String[] args) throws IOException {

        final File teamsDirectory;
        if (args.length > 0) {
            teamsDirectory = new File(args[0]);
        } else {
            teamsDirectory = Auxiliary.chooseDirectory();
        }

        if (teamsDirectory == null || !teamsDirectory.exists() || !teamsDirectory.isDirectory()) {
            System.exit(1);
        }

        for (File teamFile : teamsDirectory.listFiles()) {

            if (!teamFile.getName().endsWith(".json") || !teamFile.getName().startsWith("team.")) {
                continue;
            }

            GenerateVoices.main(new String[] {teamFile.getAbsolutePath()});
        }

    }

}
