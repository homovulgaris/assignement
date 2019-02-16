package sample.core;

import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Author: Jozef Marcin (d60548)
 * Date: 02/16/2019
 * Time: 07:11
 */
public class Eater {
    
    private String page;
    private URLConnection connection;
    private boolean numberSelected;
    private boolean characterSelected;
    
    List<String> wholeText = null;
    List<String> clearedText = null;
    Map<String, Long> ofOccurencesOfWord = null;
    Pair<String, Long> mostCommonWord = null;
    Pair<String, Long> longestWord = null;
    String mostCommonLetter = null;
    private final int CONNECTION_TIMEOUT = 60000;
    
    public Eater(String aPage, boolean aNumberSelected, boolean aCharacterSelected) {
        this.page = aPage;
        this.numberSelected = aNumberSelected;
        this.characterSelected = aCharacterSelected;
    }
    
    public List<String> getWholeText() throws Exception {
        if (wholeText == null) {
            try {
                wholeText = getAllText();
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return wholeText;
    }
    
    public List<String> getClearedText() {
        if (clearedText == null) {
            try {
                clearedText = clearTextAndLowerCase(getAllText());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clearedText;
    }
    
    public Map<String, Long> getOfOccurencesOfWord() {
        
        if (ofOccurencesOfWord == null) {
            ofOccurencesOfWord = processNumbersAndSingleCharacter(numberOfOccurencesOfWord(getClearedText()));
        }
        return ofOccurencesOfWord;
    }
    
    public Pair<String, Long> getMostCommonWord() {
        if (mostCommonWord == null) {
            mostCommonWord = getMostCommonWord(getOfOccurencesOfWord());
        }
        return mostCommonWord;
    }
    
    public Pair<String, Long> getLongestWord() {
        if (longestWord == null) {
            longestWord = getLongestWord(getOfOccurencesOfWord().keySet());
        }
        return longestWord;
    }
    
    public String getMostCommonLetter() {
        if (mostCommonLetter == null) {
            mostCommonLetter = getMostCommonLetter(getOfOccurencesOfWord().keySet());
        }
        return mostCommonLetter;
    }
    
    private List<String> getAllText() throws Exception {
        Document doc;
        try {
            doc = Jsoup.connect(page).timeout(CONNECTION_TIMEOUT).get();
        } catch (Exception e) {
            throw new Exception("Malformed URL, or connection problems!");
        }
        Elements elements = doc.body().select("*");
        List<String> allText = new ArrayList<String>();
        for (Element element : elements) {
            final String text = element.ownText().trim();
            if (!text.equals("")) allText.add(text);
        }
        return allText;
    }
    
    private List<String> clearTextAndLowerCase(List<String> texts) {
        List<String> clearedList = new ArrayList<>();
        for (String text : texts) {
            String clear = text.toLowerCase().trim();
            if (!clear.equals("")) clearedList.add(clear.trim());
        }
        return clearedList;
    }
    
    private Map<String, Long> numberOfOccurencesOfWord(List<String> texts) {
        
        
        Map<String, Long> occurenceTable = new HashMap<>();
        for (String text : texts) {
            final String[] split = text.split("\\s");
            final List<String> strings = Arrays.asList(split);
            final List<String> words = strings.stream().map(str -> str.replaceAll("\\s", "")).collect(Collectors.toList());
            final List<String> cleanedWords = cleanWords(words);
            cleanedWords.stream().forEach(str -> {
                                              final Long aLong = occurenceTable.get(str);
                                              if (aLong == null) {
                                                  occurenceTable.put(str, 1L);
                                              } else {
                                                  occurenceTable.put(str, aLong + 1);
                                              }
                                          }
            
            );
        }
        
        return occurenceTable;
    }
    
    
    private Pair<String, Long> getMostCommonWord(Map<String, Long> theWords) {
        Map<String, Long> result = sortedByLengthOfWord(theWords);
        Map.Entry<String, Long> entry = result.entrySet().iterator().next();
        return new Pair<>(entry.getKey(), entry.getValue());
        
    }
    
    public Map<String, Long> sortedByLengthOfWord(Map<String, Long> theWords) {
        return theWords.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                          (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
    
    private Pair<String, Long> getLongestWord(Set<String> theWords) {
        final String o = theWords.stream().sorted(Comparator.comparingInt(String::length).reversed()).findFirst().get();
        return new Pair<>(o, (long) o.length());
        
    }
    
    private String getMostCommonLetter(Set<String> allWords) {
        final StringBuilder stringBuilder = new StringBuilder();
        allWords.stream().forEach(stringBuilder::append);
        return String.valueOf(getMax(stringBuilder.toString()));
        
    }
    
    private char getMax(String word) {
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("input word must have non-empty value.");
        }
        char maxchar = ' ';
        int maxcnt = 0;
        int[] charcnt = new int[Character.MAX_VALUE + 1];
        for (int i = word.length() - 1; i >= 0; i--) {
            char ch = word.charAt(i);
            if (++charcnt[ch] >= maxcnt) {
                maxcnt = charcnt[ch];
                maxchar = ch;
            }
        }
        return maxchar;
    }
    
    private static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);
        
        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                                             urlMatcher.end(0)));
        }
        
        return containedUrls;
    }
    
    private List<String> cleanWords(List<String> words) {
        List<String> allWords = new ArrayList<>();
        for (String word : words) {
            final String string = word.replaceAll("(?U)[^\\p{Alnum}\\s]+", " ");
            final String[] split = string.split("\\s");
            final List<String> strings = Arrays.asList(split);
            final List<String> cleanedWords = strings.stream().map(str -> str.replaceAll("\\s", "")).collect(Collectors.toList());
            final List<String> collect = cleanedWords.stream().filter(str -> !str.equals("")).collect(Collectors.toList());
            allWords.addAll(collect);
        }
        
        
        return allWords;
    }
    
    private Map<String, Long> processNumbersAndSingleCharacter(Map<String, Long> occurenceMap) {
        Map<String, Long> traversedOccurenceMap = new HashMap<>();
        occurenceMap.forEach((key, value) -> {
            if(isWordCharacterOrNumber(key)){
                if(numberSelected && isNumberOrContainsNumber(key)){
                    traversedOccurenceMap.put(key, value);
                }
                if(characterSelected && isCharacter(key)){
                    traversedOccurenceMap.put(key, value);
                }
            }else{
                traversedOccurenceMap.put(key, value);
            }
        });
        return traversedOccurenceMap;
    }
    
    private boolean isWordCharacterOrNumber(String key) {
        return isCharacter(key) || isNumberOrContainsNumber(key);
    }
    
    private boolean isNumberOrContainsNumber(String key) {
        return isNumber(key) || containsNumbers(key);
    }
    
    private boolean isNumber(String key) {
        return StringUtil.isNumeric(key);
    }
    
    private boolean isCharacter(String key) {
        return key.length() == 1 && !isNumber(key);
    }
    
    private boolean containsNumbers(String str){
        return str.matches(".*\\d+.*");
    }
}
