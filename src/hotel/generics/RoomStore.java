package hotel.generics;

import hotel.model.Room;
import java.util.ArrayList;
import java.util.List;

public class RoomStore<T extends Room> {
    private final List<T> rooms = new ArrayList<>();

    public void add(T room) {
        rooms.add(room);
    }

    public List<T> getAll() {
        return rooms;
    }

    // Week 7: Generic method — works on any number type for price check
    public static <N extends Number> boolean isPriceAffordable(N price, N budget) {
        return price.doubleValue() <= budget.doubleValue();
    }

    // Generic print method
    public static <T> void printArray(T[] array) {
        for (T item : array) {
            System.out.print(item + " | ");
        }
        System.out.println();
    }
}
