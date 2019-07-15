package bearmaps.proj2ab;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * ArrayHeapMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    private Node<T>[] pqheap;
    private int size;
    private HashMap<T, Integer> itemIndex;

    private class Node<J> {
        private J item;
        private double priority;

        public Node(J i, double p) {
            item = i;
            priority = p;
        }
        public J nItem() {
            return item;
        }
        @Override
        public boolean equals(Object o) {
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            } else {
                return (((Node) o).item).equals(item);
            }
        }
        @Override
        public int hashCode() {
            return item.hashCode();
        }
    }

    /**
     * Constructs an empty ArrayHeapMinPQ with the default initial capacity (20)
     */
    public ArrayHeapMinPQ() {
        pqheap = (Node<T>[]) new Node[20];
        pqheap[0] = null;
        size = 1;
        itemIndex = new HashMap<>(20);
    }

    /**
     * Constructs an empty ArrayHeapMinPQ with the default initialCapacity
     * @param initialCapacity the initial capacity.
     */
    public ArrayHeapMinPQ(int initialCapacity) {
        if (initialCapacity > 0) {
            pqheap = (Node<T>[]) new Node[initialCapacity];
            pqheap[0] = null;
            size = 1;
            itemIndex = new HashMap<>(initialCapacity);
        } else {
            throw new IllegalArgumentException("Invalid size");
        }
    }

    @Override
    public void add(T item, double priority) {
        if (!itemIndex.containsKey(item)) {
            resizeExpansion();
            pqheap[size] = new Node(item, priority);
            itemIndex.put(item, size);
            pFloat(size);
            size++;
        } else {
            throw new IllegalArgumentException("Cannot have duplicates!");
        }
    }

    private void pFloat(int i) {
        if (i <= 1) {
            return;
        } else {
            int pIndex = parentIndex(i);
            Node<T> current = pqheap[i];
            Node<T> parent = pqheap[pIndex];
            if (current.priority < parent.priority) {
                swap(current, parent);
                pFloat(pIndex);
            }
        }
    }

    private void swap(Node<T> a, Node<T> b) {
        int aIndex = itemIndex.get(a.item);
        int bIndex = itemIndex.get(b.item);
        Node<T> temp = a;
        pqheap[aIndex] = b;
        pqheap[bIndex] = temp;
        itemIndex.replace((T) b.item, aIndex);
        itemIndex.replace((T) a.item, bIndex);
    }

    private int findMin(int parentIndex) {
        int minIndex = parentIndex;
        int left = leftIndex(parentIndex);
        int right = rightIndex(parentIndex);
        if (left <= size() && pqheap[left].priority < pqheap[minIndex].priority) {
            minIndex = left;
        }
        if (right <= size() && pqheap[right].priority <  pqheap[minIndex].priority) {
            minIndex = right;
        }
        return minIndex;
    }

    private void pSink(int currentIndex) {
        int sinkIndex = findMin(currentIndex);
        if (sinkIndex != currentIndex) {
            swap(pqheap[currentIndex], pqheap[sinkIndex]);
            pSink(sinkIndex);
        }
    }

    @Override
    public boolean contains(T item) {
        return itemIndex.containsKey(item);
    }

    @Override
    public T getSmallest() {
        if (size() > 0) {
            return pqheap[1].item;
        } else {
            throw new NoSuchElementException("There's no items");
        }
    }

    @Override
    public T removeSmallest() {
        if (size() > 0) {
            T toReturn = getSmallest();
            swap(pqheap[1], pqheap[size - 1]);
            pqheap[size - 1] = null;
            itemIndex.remove(toReturn);
            size--;
            pSink(1);
            resizeReduction();
            return toReturn;
        } else {
            throw new NoSuchElementException("There's no items");
        }
    }

    @Override
    public int size() {
        return size - 1;
    }

    @Override
    public void changePriority(T item, double newPriority) {
        if (itemIndex.containsKey(item)) {
            int i = itemIndex.get(item);
            Node<T> current = pqheap[i];
            if (newPriority < current.priority) {
                current.priority = newPriority;
                pFloat(i);
            } else if (newPriority > current.priority) {
                current.priority = newPriority;
                pSink(i);
            }
        } else {
            throw new NoSuchElementException("Item doesn't exist");
        }
    }

    private int parentIndex(int childIndex) {
        return childIndex / 2;
    }

    private int leftIndex(int parentIndex) {
        return parentIndex * 2;
    }

    private int rightIndex(int parentIndex) {
        return parentIndex * 2 + 1;
    }

    private void resizeExpansion() {
        if (size == pqheap.length) {
            Node<T>[] temp = (Node<T>[]) new Node[pqheap.length * 2];
            for (int i = 0; i < size; i++) {
                temp[i] = pqheap[i];
            }
            pqheap = temp;
        }
    }

    private void resizeReduction() {
        if ((float) size / (float) pqheap.length < 0.25 && pqheap.length > 20) {
            Node<T>[] temp = (Node<T>[]) new Node[pqheap.length / 2];
            for (int i = 0; i < size; i++) {
                temp[i] = pqheap[i];
            }
            pqheap = temp;
        }
    }

    private void clear() {
        pqheap = (Node<T>[]) new Node[20];
        pqheap[0] = null;
        size = 1;
        itemIndex.clear();
    }

    private void printHeap() {
        System.out.println("--- Heap ---");
        StringBuilder str = new StringBuilder();
        StringBuilder priority = new StringBuilder();
        for (int i = 1; i < size; i++) {
            Node<T> target = pqheap[i];
            str.append(target.nItem());
            str.append(" ");
            priority.append(target.priority);
            priority.append(" ");
        }
        System.out.println(str.toString());
        System.out.println(priority.toString());
    }

    private void printTree() {
        System.out.print("--- Tree ---\n");
        int count = 0;
        int layer = 1;
        StringBuilder pStr = new StringBuilder();
        StringBuilder iStr = new StringBuilder();
        for (int i = 1; i < size; i++) {
            pStr.append(pqheap[i].priority);
            pStr.append(" ");
            iStr.append(pqheap[i].nItem());
            iStr.append(" ");
            count++;
            if (count == layer) {
                pStr.append("\n");
                iStr.append("\n");
                layer *= 2;
                count = 0;
            }
        }
        System.out.println(pStr.toString());
        System.out.println(iStr.toString());
    }

    // public static void main(String[] args) {
    //     // ArrayHeapMinPQ<Point> x = new ArrayHeapMinPQ<>();
    //     // Point a = new Point(1, 1);
    //     // // Point b = new Point(1, 1);
    //     // x.add(a, 324);
    //     // // x.add(b, 3244); // duplicate
    //     // System.out.println(x.contains(new Point(1,1)));
    //     // System.out.println(x.removeSmallest());
    //     // System.out.println(x.size());


    //     ArrayHeapMinPQ<Integer> y = new ArrayHeapMinPQ<>();
    //     for (int i = 1; i <= 10; i++) {
    //         y.add(i, i);
    //     }
    //     y.printTree();
    //     y.changePriority(2, 1);
    //     y.changePriority(9, -10);
    //     y.changePriority(10, -334);
    //     y.printTree();
    //     // System.out.println(y.size());
    // }
}
