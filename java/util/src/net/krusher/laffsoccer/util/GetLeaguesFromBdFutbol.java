package net.krusher.laffsoccer.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GetLeaguesFromBdFutbol {

    public static Map<String, String> leagues = new HashMap<>();

    static {
        leagues.put("https://www.bdfutbol.com/es/t/t2025-261rf1.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\LALIGA\\1RFEF\\GRUPO1");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-261rf2.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\1RFEF\\GRUPO2");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB1.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\2RFEF\\GRUPO1");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB2.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\2RFEF\\GRUPO2");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB3.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\2RFEF\\GRUPO3");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB4.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\2RFEF\\GRUPO4");
        leagues.put("https://www.bdfutbol.com/es/t/t2025-262aB5.html", "C:\\dev\\repos\\LaffSoccer-ligas\\2025\\\\LALIGA\\2RFEF\\GRUPO5");
    }

    public static void main(String[] args) {
        for (Map.Entry<String, String> league : leagues.entrySet()) {

            File dir = new File(league.getValue());
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try {
                GetLeagueFromBdFutbol.main(new String[]{league.getKey(), league.getValue()});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
