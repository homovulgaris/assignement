package sample.core;

/**
 * Author: Jozef Marcin (d60548)
 * Date: 02/16/2019
 * Time: 07:55
 */
public class CommonsPart {
    
    private String longestWord;
    private String mostCommonLetter;
    
    public CommonsPart(String longestWord, String mostCommontLetter) {
        this.longestWord = longestWord;
        this.mostCommonLetter = mostCommontLetter;
    }
    
    public String getLongestWord() {
        return longestWord;
    }
    
    public String getMostCommonLetter() {
        return mostCommonLetter;
    }
}
