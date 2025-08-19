package ru.mentee.power.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Сервис для управления заметками.
 * Реализует основные CRUD-операции:
 * <ul>
 *   <li>создание заметки с тегами или без них;</li>
 *   <li>получение заметки по идентификатору;</li>
 *   <li>обновление текста и заголовка;</li>
 *   <li>добавление и удаление тегов;</li>
 *   <li>поиск по тексту и тегам;</li>
 *   <li>удаление заметок;</li>
 *   <li>получение всех заметок и всех тегов.</li>
 * </ul>
 * Внутренне заметки хранятся в {@link HashMap}, а уникальные идентификаторы
 * выдаются с помощью {@link AtomicInteger}.
 */
public class NoteService {

  private final Map<Integer, Note> notes = new HashMap<>();

  private final AtomicInteger nextId = new AtomicInteger(1);

  /**
   * Добавляет новую заметку.
   *
   * @param title Заголовок.
   * @param text  Текст.
   * @param tags  Набор тегов (может быть null).
   * @return Созданная заметка с присвоенным ID.
   */
  public Note addNote(String title, String text, Set<String> tags) {
    int id = nextId.getAndIncrement();
    Note note = new Note(id, title, text);
    if (tags != null) {
      for (String tag : tags) {
        note.addTag(tag);
      }

    }
    notes.put(id, note);
    return note;
  }

  /**
   * Получает заметку по ID.
   *
   * @param id ID заметки.
   * @return Optional с заметкой, если найдена, иначе Optional.empty().
   */
  public Optional<Note> getNoteById(int id) {
    return Optional.ofNullable(notes.get(id));
  }

  /**
   * Получает все заметки.
   *
   * @return Неизменяемый список всех заметок.
   */
  public List<Note> getAllNotes() {

    List<Note> notesList = new ArrayList<>(notes.values());
    return Collections.unmodifiableList(notesList);
  }

  /**
   * Обновляет заголовок и текст существующей заметки.
   *
   * @param id       ID заметки.
   * @param newTitle Новый заголовок.
   * @param newText  Новый текст.
   * @return true, если заметка найдена и обновлена, иначе false.
   */
  public boolean updateNoteText(int id, String newTitle, String newText) {

    Note note = notes.get(id);
    if (note == null) {
      return false;
    }
    note.setTitle(newTitle);
    note.setText(newText);
    return true; // Placeholder
  }

  /**
   * Добавляет тег к существующей заметке.
   *
   * @param id  ID заметки.
   * @param tag Тег для добавления.
   * @return true, если заметка найдена и тег добавлен, иначе false.
   */
  public boolean addTagToNote(int id, String tag) {
    Note note = notes.get(id);
    if (note == null) {
      return false;
    }
    if (note.getTags().contains(tag.toLowerCase())) {
      return false;
    }
    note.addTag(tag.toLowerCase());
    return true;
  }

  /**
   * Удаляет тег у существующей заметки.
   *
   * @param id  ID заметки.
   * @param tag Тег для удаления.
   * @return true, если заметка найдена и тег удален, иначе false.
   */
  public boolean removeTagFromNote(int id, String tag) {
    Note note = notes.get(id);
    if (note == null) {
      return false;
    }
    if (!note.getTags().contains(tag.toLowerCase())) {
      return false;
    }
    note.removeTag(tag);
    return true;
  }

  /**
   * Удаляет заметку по ID.
   *
   * @param id ID заметки.
   * @return true, если заметка найдена и удалена, иначе false.
   */
  public boolean deleteNote(int id) {
    Note note = notes.get(id);
    if (note == null) {
      return false;
    }
    notes.remove(id);
    return true;
  }

  /**
   * Ищет заметки, содержащие текст (без учета регистра).
   *
   * @param query Текст для поиска.
   * @return Список найденных заметок.
   */
  public List<Note> findNotesByText(String query) {
    List<Note> notesList = new ArrayList<>();
    query = query.toLowerCase();
    for (Note note : notes.values()) {
      if (note.getText().toLowerCase().contains(query)) {
        notesList.add(note);
      }
    }
    return notesList; // Placeholder
  }

  /**
   * Ищет заметки, содержащие ВСЕ указанные теги (без учета регистра).
   *
   * @param searchTags Набор тегов для поиска.
   * @return Список найденных заметок.
   */
  public List<Note> findNotesByTags(Set<String> searchTags) {

    if (searchTags == null) {
      return new ArrayList<>();
    }
    List<Note> notesList = new ArrayList<>();
    searchTags = searchTags.stream()
        .map(String::toLowerCase)
        .collect(Collectors.toSet());
    for (Note note : notes.values()) {
      if (
          (searchTags.isEmpty() && note.getTags().isEmpty())
              || (note.getTags().containsAll(searchTags) && !searchTags.isEmpty())
      ) {
        notesList.add(note);
      }
    }
    return notesList;
  }

  /**
   * Получает список всех уникальных тегов из всех заметок.
   *
   * @return Список уникальных тегов (в нижнем регистре).
   */
  public Set<String> getAllTags() {
    Set<String> tags = new HashSet<>();
    for (Note note : notes.values()) {
      tags.addAll(note.getTags());
    }
    // Пройти по всем заметкам, собрать все теги в один Set.
    return tags;
  }

}