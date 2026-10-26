import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Сортировка слиянием O(n log n) по времени и O(n) по памяти. */
public class MaxonMergeSort<T> implements SortAlgorithm<T> {
    @Override
    public void sort(List<T> elements, Comparator<? super T> comparator) {
        Objects.requireNonNull(elements, "Список не может быть null.");
        Objects.requireNonNull(comparator, "Компаратор не может быть null.");
        List<T> buffer = new MaxonArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            buffer.add(null);
        }
        mergeSort(elements, buffer, 0, elements.size(), comparator);
    }

    private void mergeSort(List<T> elements, List<T> buffer, int from, int to,
                           Comparator<? super T> comparator) {
        if (to - from <= 1) {
            return;
        }
        int middle = from + (to - from) / 2;
        mergeSort(elements, buffer, from, middle, comparator);
        mergeSort(elements, buffer, middle, to, comparator);

        int left = from;
        int right = middle;
        for (int i = from; i < to; i++) {
            if (left < middle && (right >= to
                    || comparator.compare(elements.get(left), elements.get(right)) <= 0)) {
                buffer.set(i, elements.get(left++));
            } else {
                buffer.set(i, elements.get(right++));
            }
        }
        for (int i = from; i < to; i++) {
            elements.set(i, buffer.get(i));
        }
    }
}
