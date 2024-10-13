package test.java.test;
import org.junit.Test;
import main.java.main.CustomBubbleSort;
import static org.junit.Assert.*;
import java.util.*;

public class BubbleSortTest {
    @Test
    public void testSort() {
        CustomBubbleSort sorter = new CustomBubbleSort(10);
        List<Integer> list = Arrays.asList(5, 2, 8, 3, 1, 6, 4);
        List<Integer> sortedList = sorter.sort(list);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 8}, sortedList.toArray());
    }

    @Test(expected = RuntimeException.class)
    public void testSort_MaxElementsExceeded() {
        CustomBubbleSort sorter = new CustomBubbleSort(5);
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        sorter.sort(list);
    }
}