package interfaces.repository.custom;


import java.util.List;

/**
 * Интерфейс для доступа к БД
 * @param <T> Тип элемента
 */
public interface Repository<T> {

    /**
     * Добавить данные в БД
     * @param element добавляемый элемент
     * @return добавленный элемент
     */
    T addElement(T element);

    /**
     * Обновить данные в БД
     * @param element обновлемый элемент
     * @return количество обновленных строк
     */
    long updateElement(T element);

    /**
     * Удалить данные из БД
     * @param element удаляеммый элемент
     * @return количество удаленных строк
     */
    long deleteElement(T element);

    /**
     * Получить данные из БД
     * @param element Элемент для поиска
     * @return Найденные элементы
     */
    List<T> getElements(T element);
}
