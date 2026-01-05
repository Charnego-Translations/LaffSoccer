package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Team;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.TEAMS_DIR;

public class ReportMissingAssets {

    public static void main(String[] args) throws IOException {
        Path rootDir = Paths.get(TEAMS_DIR);

        try (Stream<Path> paths = Files.walk(rootDir)) {
            paths
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().endsWith(".json") && p.getFileName().toString().startsWith("team."))
                .forEach(ReportMissingAssets::processJsonFile);
        }
    }

    private static void processJsonFile(Path jsonFile) {
        // System.out.println("Procesando: " + jsonFile);
        Team team;
        try {
            team = Auxiliary.loadTeam(jsonFile.toString());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        String teamNameNormalised = FileUtils.getTeamFromFile(jsonFile.toString());
        String teamPathName = jsonFile.toString().substring(TEAMS_DIR.length(), jsonFile.toString().length() - teamNameNormalised.length() - 10);

        String assetsPath = TEAMS_DIR + teamPathName + teamNameNormalised;
        String teamPath = TEAMS_DIR + teamPathName;

        auditAssets(team, teamPath, assetsPath, teamNameNormalised);

    }

    private static void auditAssets(Team team, String teamPath, String assetsPath, String teamFileName) {

        Path teamLogoPath;
        if (team.type == Team.Type.NATIONAL) {
            teamLogoPath = Paths.get("assets/images/flags/medium/" + teamFileName + ".png");
        } else {
            teamLogoPath = Paths.get(teamPath + "/logo." + teamFileName + ".png");
        }
        if (!Files.exists(teamLogoPath)) {
            System.out.println("Falta gráfico de escudo: " + teamLogoPath);
        }

        if (!Files.exists(Paths.get(assetsPath))) {
            System.out.println("Falta carpeta de recursos: " + assetsPath);
            return;
        }

        Path soundTeamPath = Paths.get(assetsPath + "/team.ogg");
        if (!Files.exists(soundTeamPath)) {
            System.out.println("Falta sonido de equipo: " + soundTeamPath);
        }

        Path soundStadiumPath = Paths.get(assetsPath + "/stadium.ogg");
        if (!Files.exists(soundStadiumPath)) {
            System.out.println("Falta sonido de estadio: " + soundStadiumPath);
        }

        Path soundCityPath = Paths.get(assetsPath + "/city.ogg");
        if (!Files.exists(soundCityPath)) {
            System.out.println("Falta sonido de ciudad: " + soundCityPath);
        }

        for (Player player : team.players) {
            Path playerSoundPath = Paths.get(assetsPath + "/player_" + FileUtils.normalizeName(player.shirtName) + ".ogg");
            if (!Files.exists(playerSoundPath)) {
                System.out.println("Falta sonido de jugador (" + player.shirtName + "): " + playerSoundPath);
            }
            Path playerPicturePath = Paths.get(assetsPath + "/" + FileUtils.normalizeName(player.shirtName) + ".png");
            if (!Files.exists(playerPicturePath)) {
                System.out.println("Falta retrato de jugador (" + player.shirtName + "): " + playerPicturePath);
            }
        }
    }

}
