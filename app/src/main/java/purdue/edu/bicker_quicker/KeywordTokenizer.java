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
        text = text.replaceAll("_","");
        text = text.replaceAll("[.]","");
        text = text.replaceAll("/"," ");
        text = text.replaceAll("\""," ");
        text = text.replaceAll(";","");
        text = text.replaceAll("\\?","");
        text = text.replaceAll("!","");
        text = text.replaceAll("\n", " ");
        text = text.replaceAll("\t", " ");
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

    public ArrayList<Keyword> getTagWords() {
        text = text.toLowerCase();
        text = text.replaceAll(",","");
        text = text.replaceAll("_","");
        text = text.replaceAll("[.]","");
        text = text.replaceAll("/"," ");
        text = text.replaceAll("\""," ");
        text = text.replaceAll(";","");
        text = text.replaceAll("\\?","");
        text = text.replaceAll("!","");
        text = text.replaceAll("\n", " ");
        text = text.replaceAll("\t", " ");
        String[] sArray = text.split(" ");
        List<String> list = Arrays.asList(sArray);
        ArrayList<String> sList = new ArrayList<String>();
        sList.addAll(list);

        sList = trimFrequent(sList);
        sList = stemList(sList);
        HashMap<String, Integer> hMap = wordCounter(sList);
        ArrayList<Keyword> keys = mapToListUnbiased(hMap);

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
            if (map.get(s) > 1 || s.length() > 2)
                words.add(new Keyword(s, map.get(s)));
        }

        Collections.sort(words);

        return words;
    }

    public ArrayList<Keyword> mapToListUnbiased(HashMap<String, Integer> map) {
        ArrayList<Keyword> words = new ArrayList<Keyword>();
        for (String s : map.keySet()) {
            if (map.get(s) > 1 || s.length() >= 2)
                words.add(new Keyword(s, map.get(s)));
        }

        Collections.sort(words);

        return words;
    }

    public static ArrayList<String> keysToStrings(ArrayList<Keyword> k) {
        ArrayList<String> s = new ArrayList<String>();
        for (Keyword word : k) {
            s.add(word.value+"_"+word.word);
        }

        return s;
    }

    public static ArrayList<Keyword> stringsToKeys(ArrayList<String> s) {
        ArrayList<Keyword> k = new ArrayList<Keyword>();
        for (String word : s) {
            String[] data = word.split("_");
            try {
                int val = Integer.parseInt(data[0]);
                k.add(new Keyword(data[1], val));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return k;
    }

    public static double similarity(String query, ArrayList<String> keywords, ArrayList<String> tags) {
        KeywordTokenizer k = new KeywordTokenizer(query);
        ArrayList<Keyword> qKeys = k.getTagWords();

        String tagString = "";
        for (String tag : tags) {
            tagString += tag+" ";
        }
        KeywordTokenizer k2 = new KeywordTokenizer(tagString);
        ArrayList<Keyword> tagKeys = k2.getTagWords();

        ArrayList<Keyword> keys = stringsToKeys(keywords);
        ArrayList<Keyword> union = unionKeys(keys, tagKeys);

        double total = 0;
        for (Keyword tag : qKeys) {
            for (Keyword word : union) {
                if (tag.word.equals(word.word) || tag.word.contains(word.word) || word.word.contains(tag.word)) {
                    total += word.value;
                }
            }
        }

        return total;
    }

    public static ArrayList<Keyword> unionKeys(ArrayList<Keyword> old, ArrayList<Keyword> resp) {

        HashMap<String, Integer> map = new HashMap<String, Integer>();

        for (Keyword k : old) {
            map.put(k.word, k.value);
        }

        for (Keyword k : resp) {
            if (map.get(k.word) == null) {
                map.put(k.word, k.value);
            } else {
                map.put(k.word, k.value + map.get(k.word));
            }
        }

        KeywordTokenizer k = new KeywordTokenizer("");
        ArrayList<Keyword> sorted = k.mapToListUnbiased(map);
        return sorted;
    }
}
