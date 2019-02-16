package sample.core;

/**
 * Author: Jozef Marcin (d60548)
 * Date: 02/16/2019
 * Time: 07:40
 */
public class OccurencePart {
    
    private String word;
    private Long count;
    
    public OccurencePart(String word, Long count) {
        this.word = word;
        this.count = count;
    }
    
    public String getWord() {
        return word;
    }
    
    public Long getCount() {
        return count;
    }
}
