package hotel.generics;

public class Pair<T, U> {
    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst()  { return first; }
    public U getSecond() { return second; }

    // Week 7: Generic method
    public static <T> void display(T value) {
        System.out.println("Value: " + value);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
