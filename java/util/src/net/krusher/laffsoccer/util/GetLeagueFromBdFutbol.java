package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.framework.FileUtils;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.USER_AGENT;

public class GetLeagueFromBdFutbol {

    public static final String HISTORICOS_JSON = "[\n" +
        "  \"HISTÓRICOS\"\n" +
        "]\n";

    public static void main(String[] args) throws IOException {

        String url;
        File teamsDirectory;
        if (args.length < 2) {
            url = Auxiliary.askForUrl("Introduce la URL de la página www.bdfutbol.com");
            teamsDirectory = Auxiliary.chooseDirectory();
        } else {
            url = args[0];
            teamsDirectory = new File(args[1]);
        }

        if (url == null || teamsDirectory == null || !teamsDirectory.exists() || !teamsDirectory.isDirectory()) {
            return;
        }

        Document doc;
        try {
            doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(10_000)
                .get();
        } catch (Exception e) {
            System.out.println("Error loading: " + url);
            return;
        }

        doc.select("table.taula_classificacio tr").parallelStream()
            .skip(1)
            .forEach(tr -> {
                String team = tr.select("td.text-nowrap a").text();
                String teamUrl = tr.select("td.text-nowrap a").attr("href");
                File teamFile = new File(teamsDirectory.getAbsolutePath() + "/team." + FileUtils.normalizeName(team) + ".json");
                try {
                    Files.write(Paths.get(teamsDirectory.getAbsolutePath()+ "/leagues.json"), HISTORICOS_JSON.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (teamFile.exists()) {
                    return;
                }
                try {
                    GetTeamFromBdFutbol.main(new String[]{url.substring(0, url.lastIndexOf('/') + 1) + teamUrl, teamFile.getAbsolutePath()});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

    }
}
