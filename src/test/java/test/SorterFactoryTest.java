package test.java.test;

import org.junit.Test;
import main.java.main.*;
import static org.junit.Assert.*;

public class SorterFactoryTest {
    @Test
    public void testBubbleSort() {
        SorterType type = SorterType.BUBBLE_SORT;
        int maxElements = 10;
        Sorter sorter = SorterFactory.createSorter(type, maxElements);
        assertNotNull(sorter);
        assertTrue(sorter instanceof CustomBubbleSort);
    }

    @Test
    public void testMergeSort() {
        SorterType type = SorterType.MERGE_SORT;
        int maxElements = 10;
        Sorter sorter = SorterFactory.createSorter(type, maxElements);
        assertNotNull(sorter);
        assertTrue(sorter instanceof CustomMergeSort);
    }
}