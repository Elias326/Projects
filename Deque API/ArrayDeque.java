
public class ArrayDeque<T> implements Deque<T> {

    private T[] items;
    private int front;
    private int back;
    private int size;
    public ArrayDeque() {
        items = (T[]) new Object[8];
        front = items.length / 2;
        back = items.length / 2;
        size = 0;
    }

    public ArrayDeque(ArrayDeque other) {
        items = (T[]) new Object[8];
        front = items.length / 2;
        back = items.length / 2;
        size = 0;

        for (int i = 0; i < other.size(); i = i + 1) {
            addLast((T) other.get(i));
        }
    }

    private void resize(int capacity) {
        T[] resized = (T[]) new Object[capacity];
        int a = 0;

        for (int i = front; i != back; i = (i + 1) % items.length) {
            resized[a] = items[i];
            a = (a + 1) % resized.length;
        }
        resized[a] = items[back];
        front = 0;
        back = a;
        items = resized;
    }

    public void addFirst(T item) {
        if (isEmpty()) {
            items[front] = item;
            size = size + 1;
        } else {
            if (front == 0) {
                front = items.length - 1;
            } else {
                front = front - 1;
            }
            items[front] = item;
            size = size + 1;

            if (size == items.length) {
                resize(items.length * 2);
            }
        }
    }

    public void addLast(T item) {
        if (isEmpty()) {
            items[front] = item;
            size = size + 1;
        } else {
            back = (back + 1) % items.length;
            items[back] = item;
            size = size + 1;
            if (size == items.length) {
                resize(items.length * 2);
            }
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        if (isEmpty()) {
            System.out.println();
        } else {
            for (int i = front; i != back; i = (i + 1) % items.length) {
                System.out.print(items[i] + " ");
            }
            System.out.print(items[back]);
        }
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            T delete = items[front];
            items[front] = null;
            front = (front + 1) % items.length;
            size = size - 1;
            return delete;
        }
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            T delete = items[back];
            items[back] = null;

            if (back == 0) {
                back = items.length - 1;
            } else {
                back = back - 1;
            }

            size = size - 1;

            return delete;
        }
    }

    public T get(int index) {
        int i = (index + front) % items.length;
        if (index >= size) {
            return null;
        }
        return items[i];
    }
}

