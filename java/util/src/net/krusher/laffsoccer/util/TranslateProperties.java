package net.krusher.laffsoccer.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateProperties {

    private static final String SOURCE_FILE = "assets/i18n/strings.properties";
    private static final String TARGET_FILE = "assets/i18n/strings_gn.properties";
    private static final Pattern WORD_PATTERN = Pattern.compile("\\p{L}{2,}");
    private static final String WORD_LIST_RESOURCE = "gn.txt";

    public static void main(String[] args) throws Exception {
        List<String> wordList = loadWordList();

        Map<Integer, List<String>> wordsByLength = new HashMap<>();
        for (String word : wordList) {
            int len = word.length();
            wordsByLength.computeIfAbsent(len, k -> new ArrayList<>()).add(word);
        }

        System.out.println("Loaded " + wordList.size() + " words, lengths: " + wordsByLength.size() + " unique");

        Properties props = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(SOURCE_FILE),
                java.nio.charset.StandardCharsets.UTF_8)) {
            props.load(reader);
        }

        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                Files.newOutputStream(Paths.get(TARGET_FILE)), StandardCharsets.ISO_8859_1))) {
            for (String key : sortedKeys(props)) {
                String value = props.getProperty(key);
                String translatedValue = translate(value, wordsByLength, random);
                writer.write(escapeKey(key) + "=" + escapeValue(translatedValue));
                writer.newLine();
            }
        }

        System.out.println("Generated " + TARGET_FILE);
    }

    static List<String> sortedKeys(Properties props) {
        List<String> keys = new ArrayList<>();
        for (Object k : props.keySet()) {
            keys.add((String) k);
        }
        java.util.Collections.sort(keys);
        return keys;
    }

    static String translate(String text, Map<Integer, List<String>> wordsByLength, Random random) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = WORD_PATTERN.matcher(text);
        int lastEnd = 0;

        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            String word = matcher.group();
            List<String> candidates = wordsByLength.get(word.length());
            if (candidates != null && !candidates.isEmpty()) {
                String replacement = candidates.get(random.nextInt(candidates.size()));
                replacement = matchCase(word, replacement);
                result.append(replacement);
            } else {
                result.append(word);
            }
            lastEnd = matcher.end();
        }
        result.append(text.substring(lastEnd));

        return result.toString();
    }

    static String matchCase(String original, String replacement) {
        if (original.equals(original.toUpperCase())) {
            return replacement.toUpperCase();
        }
        if (Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
        }
        return replacement;
    }

    static String escapeKey(String key) {
        return key.replace("\\", "\\\\").replace(" ", "\\ ");
    }

    static String escapeValue(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c > 0x7E || c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static List<String> loadWordList() throws IOException {
        List<String> words = new ArrayList<>();
        try (InputStream in = TranslateProperties.class.getClassLoader().getResourceAsStream(WORD_LIST_RESOURCE)) {
            if (in == null) {
                System.err.println("Error: resource not found: " + WORD_LIST_RESOURCE);
                return words;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (line.matches("\\p{L}+")) {
                        words.add(line.toLowerCase());
                    }
                }
            }
        }
        if (words.isEmpty()) {
            System.err.println("Warning: gn.txt is empty or contains no valid words");
        }
        return words;
    }
}
