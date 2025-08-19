package ru.mentee.power.notes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Модель заметки с заголовком, текстом, датой создания и тегами.
 *
 * <p>Теги хранятся в виде множества в нижнем регистре. Дата создания фиксируется при
 * инициализации объекта.
 */
public class Note {

  /** Уникальный идентификатор заметки. */
  private final int id;

  /** Дата создания заметки. */
  private final LocalDate creationDate;

  /** Заголовок заметки. */
  private String title;

  /** Текст заметки. */
  private String text;

  /** Набор тегов (уникальные строки, всегда в нижнем регистре). */
  private Set<String> tags;

  /**
   * Создаёт новую заметку.
   *
   * <p>Если параметр {@code text} равен {@code null}, он будет заменён на пустую строку.
   *
   * @param id уникальный идентификатор заметки
   * @param title заголовок заметки (не {@code null})
   * @param text текст заметки (может быть {@code null})
   * @throws IllegalArgumentException если {@code title} равен {@code null}
   */
  public Note(int id, String title, String text) {
    if (title == null) {
      throw new IllegalArgumentException("Title cannot be null");
    }
    if (text == null) {
      text = "";
    }
    this.id = id;
    this.title = title;
    this.text = text;
    this.creationDate = LocalDate.now();
    this.tags = new HashSet<>();
  }

  /**
   * Возвращает уникальный идентификатор заметки.
   *
   * @return уникальный идентификатор заметки
   */
  public int getId() {
    return id;
  }

  /**
   * Возвращает заголовок заметки.
   *
   * @return заголовок заметки
   */
  public String getTitle() {
    return title;
  }

  /**
   * Устанавливает новый заголовок заметки.
   *
   * @param title заголовок (не {@code null})
   * @throws IllegalArgumentException если {@code title} равен {@code null}
   */
  public void setTitle(String title) {
    if (title == null) {
      throw new IllegalArgumentException("Title cannot be null");
    }
    this.title = title;
  }

  /**
   * Возвращает текст заметки.
   *
   * @return текст заметки
   */
  public String getText() {
    return text;
  }

  /**
   * Устанавливает новый текст заметки.
   *
   * <p>Если передан {@code null}, будет сохранена пустая строка.
   *
   * @param text текст (может быть {@code null})
   */
  public void setText(String text) {
    if (text == null) {
      text = "";
    }
    this.text = text;
  }

  /**
   * Возвращает дату создания заметки.
   *
   * @return дата создания заметки
   */
  public LocalDate getCreationDate() {
    return creationDate;
  }

  /**
   * Возвращает неизменяемый набор тегов.
   *
   * @return неизменяемый набор тегов
   */
  public Set<String> getTags() {
    return Collections.unmodifiableSet(tags);
  }

  /**
   * Добавляет новый тег.
   *
   * <p>Тег приводится к нижнему регистру перед добавлением.
   *
   * @param tag тег (не {@code null} и не пустой)
   * @throws IllegalArgumentException если тег равен {@code null} или пустой
   */
  public void addTag(String tag) {
    if (tag == null || tag.isEmpty()) {
      throw new IllegalArgumentException("Tag cannot be null or empty");
    }
    tags.add(tag.toLowerCase());
  }

  /**
   * Удаляет тег из множества без учёта регистра.
   *
   * @param tag тег для удаления
   * @return {@code true}, если тег существовал и удалён; {@code false} — если такого тега не было
   */
  public boolean removeTag(String tag) {
    return tags.remove(tag.toLowerCase());
  }

  /**
   * Сравнивает заметки по идентификатору.
   *
   * @param o другой объект
   * @return {@code true}, если {@code o} — это {@link Note} с тем же {@code id}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Note note = (Note) o;
    return id == note.id;
  }

  /**
   * Возвращает хеш-код, вычисленный только по {@link #id}.
   *
   * @return хеш-код заметки
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  /**
   * Возвращает строковое представление заметки.
   *
   * @return строковое представление заметки
   */
  @Override
  public String toString() {
    return "\nNote{"
        + "\n   creationDate=" + creationDate
        + "\n   title='" + title + '\''
        + "\n   text='" + text + '\''
        + "\n}";
  }
}
