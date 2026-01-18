package net.krusher.laffsoccer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class GetLeaguesFromBdFutbol {

    public static final String HISTORICOS_JSON = "[\n" +
        "  \"HISTÓRICOS\"\n" +
        "]\n";

    public static Map<String, String> leagues = new HashMap<>();

    public static final String TARGET_DIR = "C:\\dev\\repos\\LaffSoccer-ligas\\";

    static {
        leagues.put("https://www.bdfutbol.com/es/t/t2025-261rf1.html", TARGET_DIR + "2025\\LALIGA\\1RFEF\\GRUPO1");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-261rf2.html", TARGET_DIR + "2025\\LALIGA\\1RFEF\\GRUPO2");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB1.html", TARGET_DIR + "2025\\LALIGA\\2RFEF\\GRUPO1");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB2.html", TARGET_DIR + "2025\\LALIGA\\2RFEF\\GRUPO2");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB3.html", TARGET_DIR + "2025\\LALIGA\\2RFEF\\GRUPO3");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB4.html", TARGET_DIR + "2025\\LALIGA\\2RFEF\\GRUPO4");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB5.html", TARGET_DIR + "2025\\LALIGA\\2RFEF\\GRUPO5");
        leagues.put("https://www.bdfutbol.com/es/t/teng2025-26.html", TARGET_DIR + "2025\\PREMIER");
        leagues.put("https://www.bdfutbol.com/es/t/tger2025-26.html", TARGET_DIR + "2025\\BUNDESLIGA");
        leagues.put("https://www.bdfutbol.com/es/t/tita2025-26.html", TARGET_DIR + "2025\\SERIEA");
        leagues.put("https://www.bdfutbol.com/es/t/tfra2025-26.html", TARGET_DIR + "2025\\LIGUE1");
        leagues.put("https://www.bdfutbol.com/es/t/tpor2025-26.html", TARGET_DIR + "2025\\PRIMEIRALIGA");
        leagues.put("https://www.bdfutbol.com/es/t/thol2025-26.html", TARGET_DIR + "2025\\EREDIVISIE");
        leagues.put("https://www.bdfutbol.com/es/t/tbra2025-26.html", TARGET_DIR + "2025\\BRASILEIRAO");
        leagues.put("https://www.bdfutbol.com/es/t/targ2025-26.html", TARGET_DIR + "2025\\1ARGENTINA");
        leagues.put("https://www.bdfutbol.com/es/t/tbel2025-26.html", TARGET_DIR + "2025\\PROLEAGUE");
        leagues.put("https://www.bdfutbol.com/es/t/tesc2025-26.html", TARGET_DIR + "2025\\PREMIERSHIP");
    }

    public static void main(String[] args) throws IOException {

        Map<String, String> allLeagues = new HashMap<>();
        String initialYear = "2025-26";
        String yearFolder = "2025";
        Set<Integer> years = IntStream.range(1980, 2025).boxed().collect(java.util.stream.Collectors.toSet());
        for (Map.Entry<String, String> league : leagues.entrySet()) {
            allLeagues.put(league.getKey(), league.getValue());
            for (Integer year : years) {
                String url = league.getKey().replace(initialYear, year + "-" + followingYearTwoFigures(year));
                String dir = league.getValue().replace(yearFolder, String.valueOf(year));
                allLeagues.put(url, dir);
            }
        }

        for (Map.Entry<String, String> league : allLeagues.entrySet()) {

            File dir = new File(league.getValue());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                GetLeagueFromBdFutbol.main(new String[]{league.getKey(), league.getValue()});
                Files.write(Paths.get(league.getValue() + "/leagues.json"), HISTORICOS_JSON.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int followingYearTwoFigures(int year) {
        return (year + 1) % 100;
    }
}
