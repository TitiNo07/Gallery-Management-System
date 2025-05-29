package util;

import java.util.Arrays;
import java.util.Iterator;


public class ArrayList<E> implements Iterable<E> {
    private static final int INITIAL_CAPACITY = 10;

    private Object[] data;
    private int size;

    public ArrayList() {
        data = new Object[INITIAL_CAPACITY];
        size = 0;
    }

    public void add(E element) {
        ensureCapacity();
        data[size++] = element;
    }

    public void add(int index, E element) {
        checkIndexForAdd(index);
        ensureCapacity();
        System.arraycopy(data, index, data, index + 1, size - index);
        data[index] = element;
        size++;
    }

    public E get(int index) {
        checkIndex(index);
        return (E) data[index];
    }

    public E set(int index, E element) {
        checkIndex(index);
        E old = (E) data[index];
        data[index] = element;
        return old;
    }

    public E remove(int index) {
        checkIndex(index);
        E removed = (E) data[index];
        int moveCount = size - index - 1;
        if (moveCount > 0) {
            System.arraycopy(data, index + 1, data, index, moveCount);
        }
        data[--size] = null;
        return removed;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    private void ensureCapacity() {
        if (size == data.length) {
            int newCapacity = data.length * 2;
            data = Arrays.copyOf(data, newCapacity);
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    private void checkIndexForAdd(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    public int indexOf(Object o) {
        for (int i = 0; i < size; i++) {
            if (data[i] == null && o == null) return i;
            if (data[i] != null && data[i].equals(o)) return i;
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int index = 0;
    
            @Override
            public boolean hasNext() {
                return index < size;
            }
    
            @Override
            public E next() {
                if (!hasNext()) throw new IndexOutOfBoundsException("No more elements to iterate");
                return (E) data[index++];
            }
    
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported");
            }
        };
    }
    
    public java.util.stream.Stream<E> stream() {
        return java.util.stream.IntStream.range(0, size)
                .mapToObj(i -> (E) data[i]);
    }
}
