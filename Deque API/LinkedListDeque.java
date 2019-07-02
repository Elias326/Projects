
public class LinkedListDeque<T> implements Deque<T> {

    private Deque sentinel;
    private int size;

    private class Deque {
        Deque front;
        T item;
        Deque back;

        private Deque() {
            this.front = null;
            this.item = null;
            this.back = null;
        }

        private Deque(T item) {
            this.front = null;
            this.item = item;
            this.back = null;
        }

    }
    public LinkedListDeque(LinkedListDeque other) {
        sentinel = new Deque();
        sentinel.back = sentinel;
        sentinel.front = sentinel;
        size = 0;

        for (int i = 0; i < other.size(); i = i + 1) {
            addLast((T) other.get(i));
        }
    }

    public LinkedListDeque() {
        sentinel = new Deque();
        sentinel.back = sentinel;
        sentinel.front = sentinel;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        if (item == null) {
            throw new java.lang.NullPointerException("Cannot add null");
        }
        Deque deque = new Deque(item);
        deque.back = sentinel;
        deque.front = sentinel.front;
        sentinel.front.back = deque;
        sentinel.front = deque;
        size = size + 1;
    }

    @Override
    public void addLast(T item) {
        if (item == null) {
            throw new java.lang.NullPointerException("Cannot add null");
        }
        Deque deque = new Deque(item);
        deque.back = sentinel.back;
        deque.front = sentinel;
        sentinel.back.front = deque;
        sentinel.back = deque;
        size = size + 1;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Deque x = sentinel.front;

        while (x != sentinel) {
            System.out.print(x.item + " ");
            x = x.front;
        }
    }

    @Override
    public T removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        Deque d = sentinel.front;
        T delete = d.item;

        sentinel.front = d.front;
        d.front.back = sentinel;

        d = null;

        size = size - 1;
        return delete;
    }

    @Override
    public T removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        Deque d = sentinel.back;
        T delete = d.item;

        sentinel.back = d.back;
        d.back.front = sentinel;

        d = null;

        size = size - 1;
        return delete;
    }

    @Override
    public T get(int index) {
        Deque x = sentinel.front;

        while (index > 0) {
            x = x.front;
            index = index - 1;
        }

        return x.item;
    }

    public T getRecursive(int index) {
        Deque d = sentinel.front;
        return getRecursiveHelper(index, d);
    }

    private T getRecursiveHelper(int index, Deque d) {
        if (index == 0) {
            return d.item;
        } else {
            return getRecursiveHelper(index - 1, d.front);
        }
    }
}
