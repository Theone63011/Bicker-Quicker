package purdue.edu.bicker_quicker;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class KeywordTokenizer {

    String text;

    public KeywordTokenizer(String fulltext) {
        text = fulltext;
    }

    public ArrayList<Keyword> getKeywords() {
        text = text.toLowerCase();
        text = text.replaceAll(",","");
        text = text.replaceAll("[.]","");
        text = text.replaceAll("/"," ");
        text = text.replaceAll("\""," ");
        text = text.replaceAll(";","");
        text = text.replaceAll("\\?","");
        text = text.replaceAll("!","");
        String[] sArray = text.split(" ");
        List<String> list = Arrays.asList(sArray);
        ArrayList<String> sList = new ArrayList<String>();
        sList.addAll(list);

        sList = trimFrequent(sList);
        sList = stemList(sList);
        HashMap<String, Integer> hMap = wordCounter(sList);
        ArrayList<Keyword> keys = mapToList(hMap);

        return keys;
    }

    // Removes the 100 most commonly used words in english, add to this if you notice issues with other words
    public ArrayList<String> trimFrequent(ArrayList<String> s) {
        String[] freq = {"a","about","all","also","and","as","at","be","because","but","by","can","come","could","day","do","even","find","first","for","from","get","give","go","have","he","her","here","him","his","how","I","if","in","into","it","its","just","know","like","look","make","man","many","me","more","my","new","no","not","now","of","on","one","only","or","other","our","out","people","say","see","she","so","some","take","tell","than","that","the","their","them","then","there","these","they","thing","think","this","those","time","to","two","up","use","very","want","way","we","well","what","when","which","who","will","with","would","year","you","your"};
        ArrayList<String> freqL = new ArrayList<String>();
        freqL.addAll(Arrays.asList(freq));
        s.removeAll(freqL);
        return s;
    }

    // Stems all words to simplify searching
    public ArrayList<String> stemList(ArrayList<String> s) {
        Stemmer stem = new Stemmer();
        ArrayList<String> stems = new ArrayList<String>();

        for (String word : s) {
            stems.add(stem.stem(word));
        }

        return stems;
    }

    // Simply counts frequency of words in a given list of words
    public HashMap<String, Integer> wordCounter(ArrayList<String> words) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (String word : words) {
            if (map.get(word) == null) {
                map.put(word, 1);
            } else {
                map.put(word, map.get(word) + 1);
            }
        }
        return map;
    }

    public ArrayList<Keyword> mapToList(HashMap<String, Integer> map) {
        ArrayList<Keyword> words = new ArrayList<Keyword>();
        for (String s : map.keySet()) {
            if (map.get(s) > 1 || s.length() > 6)
                words.add(new Keyword(s, map.get(s)));
        }

        Collections.sort(words);

        return words;
    }
}
