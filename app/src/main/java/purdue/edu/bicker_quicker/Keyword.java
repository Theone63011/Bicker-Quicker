package purdue.edu.bicker_quicker;

public class Keyword implements Comparable<Keyword> {

    String word;
    int value;

    public Keyword(String s, int value) {
        this.word = s;
        this.value = value;
    }

    @Override
    public int compareTo(Keyword o) {
        return Integer.compare(o.value, this.value);
    }
}
