import java.util.Comparator;
import java.util.List;



/** Общий контракт для алгоритмов сортировки уровня 3*/
public interface SortAlgorithm<T> {
    void sort(List<T> elements, Comparator<? super T> comparator);
}
