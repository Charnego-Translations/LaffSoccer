package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.framework.FileUtils;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.USER_AGENT;

public class GetLeagueFromBdFutbol {


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

        Document doc = Jsoup.connect(url)
            .userAgent(USER_AGENT)
            .timeout(10_000)
            .get();

        doc.select("table.taula_classificacio tr").parallelStream().
            skip(1)
            .forEach(tr -> {
                String team = tr.select("td.text-nowrap").text();
                String teamUrl = tr.select("td.text-nowrap a").attr("href");

                try {
                    GetTeamFromBdFutbol.main(new String[]{url.substring(0, url.lastIndexOf('/') + 1) + teamUrl, teamsDirectory.getAbsolutePath() + "/team." + FileUtils.normalizeName(team) + ".json"});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

    }
}
