package net.krusher.laffsoccer.util;

import com.ygames.ysoccer.framework.FileUtils;
import com.ygames.ysoccer.match.Coach;
import com.ygames.ysoccer.match.Hair;
import com.ygames.ysoccer.match.Player;
import com.ygames.ysoccer.match.Skin;
import com.ygames.ysoccer.match.Team;
import net.krusher.laffsoccer.util.auxiliary.Auxiliary;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.krusher.laffsoccer.util.GenerateTeam.HAIR_STYLES;
import static net.krusher.laffsoccer.util.GenerateTeam.randomKit;
import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.RND;
import static net.krusher.laffsoccer.util.auxiliary.Auxiliary.USER_AGENT;

public class GetTeamFromBdFutbol {

    private final static Map<String, String> COUNTRY_CONV = new HashMap<>();
    private final static Map<String, Player.Role> POSITION_CONV = new HashMap<>();

    static {
        COUNTRY_CONV.put("españa", "ESP");
        COUNTRY_CONV.put("argentina", "ARG");
        COUNTRY_CONV.put("brasil", "BRA");
        COUNTRY_CONV.put("argelia", "DZA");
        COUNTRY_CONV.put("colombia", "COL");
        COUNTRY_CONV.put("hungria", "HUN");
        COUNTRY_CONV.put("uruguay", "URU");
        COUNTRY_CONV.put("mexico", "MEX");
        COUNTRY_CONV.put("paraguay", "PAR");
        COUNTRY_CONV.put("bolivia", "BOL");
        COUNTRY_CONV.put("peru", "PER");
        COUNTRY_CONV.put("chile", "CHI");
        COUNTRY_CONV.put("venezuela", "VEN");
        COUNTRY_CONV.put("espana", "ESP"); COUNTRY_CONV.put("espanya", "ESP");
        COUNTRY_CONV.put("italia", "ITA");
        COUNTRY_CONV.put("portugal", "POR");
        COUNTRY_CONV.put("francia", "FRA");
        COUNTRY_CONV.put("alemania", "GER");
        COUNTRY_CONV.put("australia", "AUS");
        COUNTRY_CONV.put("austria", "AUT");
        COUNTRY_CONV.put("belgica", "BEL");
        COUNTRY_CONV.put("canada", "CAN");
        COUNTRY_CONV.put("suecia", "SWE");
        COUNTRY_CONV.put("irlanda", "IRL");
        COUNTRY_CONV.put("rusia", "RUS");
        COUNTRY_CONV.put("turquia", "TUR");
        COUNTRY_CONV.put("polonia", "POL");
        COUNTRY_CONV.put("serbia", "SRB");
        COUNTRY_CONV.put("bosnia", "BIH");
        COUNTRY_CONV.put("croacia", "CRO");
        COUNTRY_CONV.put("eslovaquia", "SVK");
        COUNTRY_CONV.put("estonia", "EST");
        COUNTRY_CONV.put("finlandia", "FIN");
        COUNTRY_CONV.put("lituania", "LTU");
        COUNTRY_CONV.put("luxemburgo", "LUX");
        COUNTRY_CONV.put("moldavia", "MDA");
        COUNTRY_CONV.put("mongolia", "MNG");
        COUNTRY_CONV.put("noruega", "NOR");
        COUNTRY_CONV.put("romania", "ROU");
        COUNTRY_CONV.put("slovaquia", "SVK");
        COUNTRY_CONV.put("marruecos", "MAR");
        COUNTRY_CONV.put("republicadominicana", "DOM");
        COUNTRY_CONV.put("costademarfil", "CIV");
        COUNTRY_CONV.put("honduras", "HND");
        COUNTRY_CONV.put("guinea", "GIN");
        COUNTRY_CONV.put("guatemala", "GTM");
        COUNTRY_CONV.put("belice", "BLZ");
        COUNTRY_CONV.put("elsalvador", "SLV");
        COUNTRY_CONV.put("nicaragua", "NIC");
        COUNTRY_CONV.put("panama", "PAN");
        COUNTRY_CONV.put("georgia", "GEO");
        COUNTRY_CONV.put("azerbaijan", "AZE");
        COUNTRY_CONV.put("turkmenistan", "TKM");
        COUNTRY_CONV.put("uzbekistan", "UZB");
        COUNTRY_CONV.put("tajikistan", "TJK");
        COUNTRY_CONV.put("afghanistan", "AFG");
        COUNTRY_CONV.put("iran", "IRN");
        COUNTRY_CONV.put("pakistan", "PAK");
        COUNTRY_CONV.put("bangladesh", "BGD");
        COUNTRY_CONV.put("india", "IND");
        COUNTRY_CONV.put("srilanka", "LKA");
        COUNTRY_CONV.put("nepal", "NPL");
        COUNTRY_CONV.put("ucrania", "UKR");
        COUNTRY_CONV.put("armenia", "ARM");
        COUNTRY_CONV.put("republicacheca", "CZE");
        COUNTRY_CONV.put("rumania", "ROM");
        COUNTRY_CONV.put("suiza", "SUI");


        POSITION_CONV.put("por", Player.Role.GOALKEEPER);
        POSITION_CONV.put("mig", Player.Role.MIDFIELDER);
        POSITION_CONV.put("dav", Player.Role.ATTACKER);
        POSITION_CONV.put("lti", Player.Role.LEFT_BACK);
        POSITION_CONV.put("cen", Player.Role.DEFENDER);
        POSITION_CONV.put("ltd", Player.Role.RIGHT_BACK);
    }

    public static void main(String[] args) throws IOException {

        final String url;
        final Path fileToSave;
        if (args.length > 0) {
            url = args[0];
            fileToSave = Paths.get(args[1]);
        } else {
            url = Auxiliary.askForUrl("Introduce la URL de la página www.bdfutbol.com");
            // url = "https://www.bdfutbol.com/es/t/t1987-88175.html";
            fileToSave = Auxiliary.selectTeamFileSave();
        }

        if (url == null || fileToSave == null) {
            return;
        }

        String teamFile = FileUtils.getTeamFromFile(fileToSave.toString());

        // Conecta y descarga
        Document doc;
        try {
            doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(10_000)
                .get();
        } catch (HttpStatusException e) {
            System.out.println("Error loading: " + url);
            return;
        }

        File directory = new File(fileToSave.getParent() + "/" + teamFile);
        if (!directory.exists()) {
            directory.mkdir();
        }

        Team team = new Team();
        team.coach = new Coach();
        String paisEntrenador = doc.select("table#taulaentrenadors").select("td").get(1).select("div.pais").get(0).classNames().toArray()[1].toString();
        team.coach.name = doc.select("table#taulaentrenadors").select("td").get(2).select("span").get(1).text().toUpperCase();
        team.coach.nationality = Optional.ofNullable(COUNTRY_CONV.get(paisEntrenador)).orElse("ESP");

        String logoSrc = doc.select("img").stream().filter(img -> img.attr("height").equals("150")).findAny().get().attr("src");
        String logoUrl = url.substring(0, url.lastIndexOf('/') + 1) + logoSrc;

        Auxiliary.downloadImageAndResize(logoUrl, Paths.get(fileToSave.getParent() + "/logo." + teamFile + ".png"), 70, 70);

        String urlEquipo =  doc.select("img").stream().filter(img -> img.attr("height").equals("150")).findAny().get().parent().attr("href");
        Document teamDoc = Jsoup.connect(url.substring(0, url.lastIndexOf('/') + 1) + urlEquipo)
            .userAgent(USER_AGENT)
            .timeout(10_000)
            .get();

        team.type = Team.Type.CLUB;
        String clubCountry = teamDoc.select("div.font-weight-bold div.pais").get(0).classNames().toArray()[1].toString();
        team.country = COUNTRY_CONV.getOrDefault(clubCountry, "ESP");
        try {
            team.city = teamDoc.select("div").stream().filter(n -> n.text().equals("Localidad")).findAny().get().parent().select("div.font-weight-bold").text().replaceAll(" \\(.*", "").toUpperCase(); // TODO
            team.stadium = teamDoc.select("div").stream().filter(n -> n.text().equals("Estadio")).findAny().get().parent().select("div.font-weight-bold").text().toUpperCase();
        } catch (Exception e) {
            System.out.println("No se pudo obtener datos del equipo: " + e.getMessage());
            team.city = "";
            team.stadium = "";
        }
        String fullTeamName = teamDoc.select("div.font-weight-bold").get(0).text().toUpperCase();
        team.name = doc.select("span.heroh1 a").text().toUpperCase(); // TODO abbreviate name instead of short name
        team.league = "HISTÓRICOS";
        team.kits = new ArrayList<>();
        team.kits.add(randomKit()); // TODO and no idea how
        team.kits.add(randomKit());
        team.kits.add(randomKit());

        Auxiliary.generateVoice(team.city + ".", new File(directory.getAbsolutePath() +"/city.mp3"));
        Auxiliary.generateVoice(team.stadium + ".", new File(directory.getAbsolutePath() +"/stadium.mp3"));
        Auxiliary.generateVoice(fullTeamName + ".", new File(directory.getAbsolutePath() +"/team.mp3"));

        try {
            team.year = Integer.valueOf(doc.select("span.heroh1 span").text().replaceAll("-.*", ""));
        } catch (Exception e) {
            team.year = null;
        }

        String comment = doc.select("span.heroh1").text();

        List<Integer> numbers = IntStream.rangeClosed(1, 50).boxed().collect(Collectors.toList());

        doc.select("#taulaplantilla tr:not(.parteix, .fons-transparent)").stream()
            .skip(1)
            .forEach(tr -> {

                Player player = new Player();

                player.skinColor = Skin.Color.values()[RND.nextInt(Skin.Color.values().length - 1)];
                player.hairColor = Hair.Color.values()[RND.nextInt(Hair.Color.values().length - 1)];
                player.hairStyle = Auxiliary.getRandomItem(HAIR_STYLES);

                player.shirtName = tr.select("td span").get(0).text().toUpperCase();
                player.name = tr.select("td span").get(1).text().toUpperCase();

                String pais = tr.select("div.pais").stream().findFirst().map(element -> element.classNames().toArray()[1].toString()).orElse(null);;
                String posicion = tr.select("td div.fit").stream().findFirst().map(element -> element.classNames().toArray()[1].toString()).orElse(null);

                player.nationality = COUNTRY_CONV.getOrDefault(pais, "ESP");
                player.role = POSITION_CONV.getOrDefault(posicion, Player.Role.DEFENDER);

                Integer playerNumber;
                try {
                    String numberString = tr.select("td").get(0).text();
                    if (StringUtils.isNotBlank(numberString)) {
                        playerNumber = Integer.valueOf(tr.select("td").get(0).text());
                        numbers.remove(playerNumber);
                    } else {
                        playerNumber = null;
                    }
                } catch (Exception e) {
                    playerNumber = null;
                }
                if (playerNumber == null) {
                    player.number = numbers.remove(RND.nextInt(numbers.size()));
                } else {
                    player.number = playerNumber;
                }

                Auxiliary.randomizePlayerStats(player);

                Document playerDoc = null;
                String playerUrl = tr.select("td a").attr("href");
                try {
                    playerDoc = Jsoup.connect(url.substring(0, url.lastIndexOf('/') + 1) + tr.select("td a").attr("href"))
                        .userAgent(USER_AGENT)
                        .timeout(10_000)
                        .get();
                } catch (IOException e) {
                    System.out.println("Error loading " + playerUrl);
                }

                if (playerDoc == null) {
                    return;
                }

                String playerImageUrl = url.substring(0, url.lastIndexOf('/') + 1) + playerDoc.select("div.active img").attr("src");
                try {
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    Auxiliary.downloadImageAndResize(playerImageUrl, Paths.get(directory.getAbsolutePath() +"/" + FileUtils.normalizeName(player.shirtName) + ".png"), 70, 94);
                    //System.out.println("Downloaded " + player.shirtName + " from " + playerImageUrl);
                } catch (IOException e) {
                    System.out.println("Error downloading " + player.shirtName + " from " + playerImageUrl);
                    e.printStackTrace();
                }

                try {
                    Auxiliary.generateVoice(player.shirtName + ".", new File(directory.getAbsolutePath() +"/player_" + FileUtils.normalizeName(player.shirtName) + ".mp3"));
                } catch (Exception e) {
                    System.out.println("Error downloading " + player.shirtName + " voice");
                    e.printStackTrace();
                }

                team.players.add(player);

        });

        Auxiliary.writeTeamFile(team, fileToSave.toFile(), comment);

    }

}
