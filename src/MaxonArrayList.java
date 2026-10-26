import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;

/** Динамический массив add, get, remove, size и автоматическое расширение. */
public class MaxonArrayList<E> extends AbstractList<E> implements RandomAccess {
    private static final int INITIAL_CAPACITY = 4;
    private Object[] elements;
    private int size;

    public MaxonArrayList() {
        elements = new Object[INITIAL_CAPACITY];
    }

    @Override
    public boolean add(E element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        modCount++;
        return true;
    }


    @Override
    public void add(int index, E element) {
        checkPositionIndex(index);
        ensureCapacity(size + 1);
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
        modCount++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkElementIndex(index);
        return (E) elements[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        checkElementIndex(index);
        E previous = (E) elements[index];
        elements[index] = element;
        return previous;
    }


    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkElementIndex(index);
        E removed = (E) elements[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        modCount++;
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        Arrays.fill(elements, 0, size, null);
        size = 0;
        modCount++;
    }

    private void ensureCapacity(int required) {
        if (required > elements.length) {
            int newCapacity = Math.max(required, elements.length * 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Индекс " + index + ", размер списка " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Индекс " + index + ", размер списка " + size);
        }
    }
}
