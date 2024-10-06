/**
 * Реализация кастомного списка с использованием массива.
 *
 * @param <A> тип элементов в списке.
 */
public class CustomArrayList<A> implements Methods<A> {
    private static final int INITIAL_CAPACITY = 10;
    private A[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomArrayList() {
        elements = (A[]) new Object[INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public void add(A element) {
        if (element == null) {
            throw new IllegalArgumentException("Null values are not allowed");
        }
        ensureCapacity();
        elements[size++] = element;
    }

    @Override
    public A get(int index) {
        checkIndex(index);
        return elements[index];
    }

    @Override
    public A remove(int index) {
        checkIndex(index);
        A removedElement = elements[index];
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null;
        return removedElement;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            int newCapacity = elements.length * 2;
            elements = java.util.Arrays.copyOf(elements, newCapacity);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
