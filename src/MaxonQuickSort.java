import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/** Собственная быстрая сортировка. */
public class MaxonQuickSort<T> implements SortAlgorithm<T> {
    @Override
    public void sort(List<T> elements, Comparator<? super T> comparator) {
        Objects.requireNonNull(elements, "Список не может быть null.");
        Objects.requireNonNull(comparator, "Компаратор не может быть null.");
        if (elements.size() > 1) {
            quickSort(elements, 0, elements.size() - 1, comparator);
        }
    }


    private void quickSort(List<T> elements, int from, int to,
                           Comparator<? super T> comparator) {
        while (from < to) {
            int left = from;
            int right = to;
            T pivot = elements.get(from + (to - from) / 2);

            while (left <= right) {
                while (comparator.compare(elements.get(left), pivot) < 0) {
                    left++;
                }
                while (comparator.compare(elements.get(right), pivot) > 0) {
                    right--;
                }
                if (left <= right) {
                    T temporary = elements.get(left);
                    elements.set(left, elements.get(right));
                    elements.set(right, temporary);
                    left++;
                    right--;
                }
            }

            // Рекурсивно сортируем меньшую часть, а большую — в этом же цикле.
            if (right - from < to - left) {
                if (from < right) {
                    quickSort(elements, from, right, comparator);
                }
                from = left;
            } else {
                if (left < to) {
                    quickSort(elements, left, to, comparator);
                }
                to = right;
            }
        }
    }
}
