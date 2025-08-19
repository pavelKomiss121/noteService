package ru.mentee.power.notes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Тесты для NoteService")
class NoteServiceTest {

  private NoteService noteService;

  @BeforeEach
  void setUp() {

    noteService = new NoteService();
  }

  @Nested
  @DisplayName("Тесты добавления заметок")
  class AddNoteTests {

    @Test
    @DisplayName("Добавление валидной заметки")
    void shouldAddValidNote() {
      // Arrange
      String title = "Первая заметка";
      String text = "Текст первой заметки";
      Set<String> tags = Set.of("Java", "тест");

      Note addedNote = noteService.addNote(title, text, tags);

      // Assert
      assertThat(addedNote).isNotNull();

      assertThat(addedNote.getId()).isGreaterThan(0);
      assertThat(addedNote.getTitle()).isEqualTo(title);
      assertThat(addedNote.getText()).isEqualTo(text);

      assertThat(addedNote.getTags()).isNotEqualTo(tags);
      Set<String> tagsLowerCase = new HashSet<>();
      for (String tag : tags) {
        tagsLowerCase.add(tag.toLowerCase());
      }
      assertThat(addedNote.getTags()).isEqualTo(tagsLowerCase);

      assertThat(noteService.getAllNotes()).hasSize(1);
      assertThat(noteService.getAllNotes().getFirst().getTitle()).isEqualTo(title);

      assertThat(noteService.getAllNotes().getFirst().getCreationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Добавление заметки с null title/text")
    void shouldHandleNullTitleAndText() {
      noteService.addNote("hi", null, null);
      assertThatThrownBy(() -> noteService.addNote(null, null, null)).isInstanceOf(
          IllegalArgumentException.class);
      assertThat(noteService.getAllNotes()).hasSize(1);
      assertThat(noteService.getAllNotes().getFirst().getText()).isEmpty();
      assertThat(noteService.getAllNotes().getFirst().getTags()).isEqualTo(Set.of());
    }
  }

  @Nested
  @DisplayName("Тесты получения заметок")
  class GetNoteTests {


    @Test
    @DisplayName("Успешное получение заметки по id")
    void shouldGetNoteById() {
      Note created = noteService.addNote("Заголовок", "Текст", Set.of("java"));

      Optional<Note> foundOpt = noteService.getNoteById(created.getId());
      assertThat(foundOpt).isPresent();

      Note found = foundOpt.orElseThrow();
      assertThat(found.getId()).isEqualTo(created.getId());
      assertThat(found.getTitle()).isEqualTo("Заголовок");
      assertThat(found.getTags()).containsExactlyInAnyOrder("java");
    }

    @Test
    @DisplayName("Поиск несуществующей заметки по id")
    void shouldReturnEmptyOptionalForMissingId() {
      assertThat(noteService.getNoteById(999_999)).isEmpty();
    }

    @Test
    @DisplayName("getAllNotes: пустой список")
    void shouldReturnEmptyListWhenNoNotes() {
      assertThat(noteService.getAllNotes()).isEmpty();
    }

    @Test
    @DisplayName("getAllNotes: несколько заметок")
    void shouldReturnAllNotes() {
      noteService.addNote("A", "a", Set.of("x"));
      noteService.addNote("B", "b", Set.of("y"));
      noteService.addNote("C", "c", Set.of("x", "y"));

      assertThat(noteService.getAllNotes()).hasSize(3).extracting(Note::getTitle)
          .containsExactly("A", "B", "C");
    }
  }

  @Nested
  @DisplayName("Тесты обновления заметок")
  class UpdateNoteTests {

    @Test
    @DisplayName("Успешное обновление текста заметки")
    void shouldUpdateNoteText() {
      Note n = noteService.addNote("T", "old", Set.of("tag"));
      boolean updated = noteService.updateNoteText(n.getId(), n.getTitle(), "new text");

      assertThat(updated).isTrue();

      Optional<Note> foundOpt = noteService.getNoteById(n.getId());
      assertThat(foundOpt).isPresent();
      assertThat(foundOpt.orElseThrow().getText()).isEqualTo("new text");

      n.setText(null);
      assertThat(n.getText()).isEmpty();
    }

    @Test
    @DisplayName("Успешное обновление заголовки заметки")
    void shouldUpdateNoteTitle() {
      Note n = noteService.addNote("T", "old", Set.of("tag"));
      n.setTitle("new title");
      assertThat(n.getTitle()).isEqualTo("new title");

      assertThatThrownBy(() -> n.setTitle(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Обновление несуществующей заметки")
    void shouldNotUpdateMissingNote() {
      assertThat(noteService.updateNoteText(123456, "hi", "text")).isFalse();
    }

    @Test
    @DisplayName("addTagToNote: добавление нового тега")
    void shouldAddNewTagLowercased() {
      Note n = noteService.addNote("T", "t", Set.of("java"));
      boolean added = noteService.addTagToNote(n.getId(), "TEST");

      assertThat(added).isTrue();

      Optional<Note> foundOpt = noteService.getNoteById(n.getId());
      assertThat(foundOpt).isPresent();
      assertThat(foundOpt.orElseThrow().getTags()).containsExactlyInAnyOrder("java", "test");

      n.addTag("hi");
      assertThat(n.getTags()).containsExactlyInAnyOrder("java", "test", "hi");
      assertThatThrownBy(() -> n.addTag("")).isInstanceOf(IllegalArgumentException.class);
      assertThatThrownBy(() -> n.addTag(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("addTagToNote: добавление существующего тега не меняет набор")
    void shouldNotDuplicateExistingTag() {
      Note n = noteService.addNote("T", "t", Set.of("java"));
      boolean first = noteService.addTagToNote(n.getId(), "JAVA");
      boolean second = noteService.addTagToNote(n.getId(), "java");

      assertThat(first).isFalse();
      assertThat(second).isFalse();

      Optional<Note> foundOpt = noteService.getNoteById(n.getId());
      assertThat(foundOpt).isPresent();
      assertThat(foundOpt.orElseThrow().getTags()).containsExactly("java");
    }

    @Test
    @DisplayName("addTagToNote: к несуществующей заметке")
    void shouldNotAddTagToMissingNote() {
      assertThat(noteService.addTagToNote(999_999, "x")).isFalse();
    }

    @Test
    @DisplayName("removeTagFromNote: удаление существующего тега")
    void shouldRemoveExistingTag() {
      Note n = noteService.addNote("T", "t", Set.of("a", "b"));
      boolean removed = noteService.removeTagFromNote(n.getId(), "A");

      assertThat(removed).isTrue();

      Optional<Note> foundOpt = noteService.getNoteById(n.getId());
      assertThat(foundOpt).isPresent();
      assertThat(foundOpt.orElseThrow().getTags()).containsExactly("b");
    }

    @Test
    @DisplayName("removeTagFromNote: попытка удалить несуществующий тег")
    void shouldNotRemoveMissingTag() {
      Note n = noteService.addNote("T", "t", Set.of("a"));
      boolean removed = noteService.removeTagFromNote(n.getId(), "zzz");

      assertThat(removed).isFalse();

      Optional<Note> foundOpt = noteService.getNoteById(n.getId());
      assertThat(foundOpt).isPresent();
      assertThat(foundOpt.orElseThrow().getTags()).containsExactly("a");
    }

    @Test
    @DisplayName("removeTagFromNote: у несуществующей заметки")
    void shouldNotRemoveTagFromMissingNote() {
      assertThat(noteService.removeTagFromNote(999_999, "a")).isFalse();
    }
  }

  @Nested
  @DisplayName("Тесты удаления заметок")
  class DeleteNoteTests {

    @Test
    @DisplayName("Успешное удаление заметки")
    void shouldDeleteNote() {
      Note n = noteService.addNote("Del", "t", Set.of());
      boolean deleted = noteService.deleteNote(n.getId());

      assertThat(deleted).isTrue();
      assertThat(noteService.getNoteById(n.getId())).isEmpty();
      assertThat(noteService.getAllNotes()).isEmpty();
    }

    @Test
    @DisplayName("Удаление несуществующей заметки")
    void shouldNotDeleteMissingNote() {
      assertThat(noteService.deleteNote(123456)).isFalse();
    }
  }

  @Nested
  @DisplayName("Тесты поиска заметок")
  class FindNoteTests {

    @Test
    @DisplayName("findNotesByText: существующий текст")
    void shouldFindByExistingText() {
      Note n1 = noteService.addNote("A", "Hello world", Set.of("java"));
      noteService.addNote("B", "Nothing here", Set.of("misc"));

      var result = noteService.findNotesByText("Hello");
      assertThat(result).extracting(Note::getId).containsExactly(n1.getId());
    }

    @Test
    @DisplayName("findNotesByText: часть текста")
    void shouldFindBySubstring() {
      Note n1 = noteService.addNote("A", "Hello world", Set.of("java"));
      Note n2 = noteService.addNote("B", "Saying hellO again", Set.of("tdd"));

      var result = noteService.findNotesByText("ello");
      assertThat(result).extracting(Note::getId).containsExactlyInAnyOrder(n1.getId(), n2.getId());
    }

    @Test
    @DisplayName("findNotesByText: регистр не влияет")
    void shouldIgnoreCaseInTextSearch() {
      Note n1 = noteService.addNote("A", "HELLO!", Set.of());
      Note n2 = noteService.addNote("B", "heLLo there", Set.of());

      var result = noteService.findNotesByText("hello");
      assertThat(result).extracting(Note::getId).containsExactlyInAnyOrder(n1.getId(), n2.getId());
    }

    @Test
    @DisplayName("findNotesByText: несуществующий текст")
    void shouldReturnEmptyForMissingText() {
      noteService.addNote("A", "some text", Set.of());
      var result = noteService.findNotesByText("qwerty");
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findNotesByTags: один тег")
    void shouldFindBySingleTag() {
      Note n1 = noteService.addNote("A", "t", Set.of("Java"));
      noteService.addNote("B", "t", Set.of("tdd"));

      var result = noteService.findNotesByTags(Set.of("java")); // нижний регистр
      assertThat(result).extracting(Note::getId).containsExactly(n1.getId());
    }

    @Test
    @DisplayName("findNotesByTags: без тегов")
    void shouldFindWithoutTag() {
      Note n1 = noteService.addNote("A", "t", null);

      var result = noteService.findNotesByTags(Set.of()); // нижний регистр
      assertThat(result).extracting(Note::getId).containsExactly(n1.getId());
    }

    @Test
    @DisplayName("findNotesByTags: несколько существующих тегов (логическое И)")
    void shouldFindByAllProvidedTags() {
      Note n1 = noteService.addNote("A", "t", Set.of("java", "tdd"));
      noteService.addNote("B", "t", Set.of("java"));
      noteService.addNote("C", "t", Set.of("tdd"));

      var result = noteService.findNotesByTags(Set.of("JAVA", "Tdd")); // регистр не важен
      assertThat(result).extracting(Note::getId).containsExactly(n1.getId());
    }

    @Test
    @DisplayName("findNotesByTags: смесь существующих и несуществующих → пусто")
    void shouldBeEmptyForMixedExistingAndMissingTags() {
      noteService.addNote("A", "t", Set.of("java", "tdd"));
      var result = noteService.findNotesByTags(Set.of("java", "missing"));
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findNotesByTags: только несуществующие → пусто")
    void shouldBeEmptyForOnlyMissingTags() {
      noteService.addNote("A", "t", Set.of("java"));
      var result = noteService.findNotesByTags(Set.of("kotlin", "spring"));
      assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findNotesByTags: пустой набор тегов → пусто")
    void shouldBeEmptyForEmptyTagsSet() {
      noteService.addNote("A", "t", Set.of("java"));
      var result = noteService.findNotesByTags(Set.of());
      assertThat(result).isEmpty();
      result = noteService.findNotesByTags(null);
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("Тесты работы с тегами")
  class TagTests {


    @Test
    @DisplayName("getAllTags: пустой список")
    void shouldReturnEmptyTagsWhenNoNotes() {
      assertThat(noteService.getAllTags()).isEmpty();
    }

    @Test
    @DisplayName("getAllTags: уникальные теги из нескольких заметок (нижний регистр)")
    void shouldReturnUniqueLowercasedTags() {
      noteService.addNote("A", "t", Set.of("Java", "tdd"));
      noteService.addNote("B", "t", Set.of("JAVA", "Gradle"));
      noteService.addNote("C", "t", Set.of("gradle", "build"));

      var tags = noteService.getAllTags();
      assertThat(tags).containsExactlyInAnyOrder("java", "tdd", "gradle", "build");
    }

    @Nested
    @DisplayName("equals и hashCode")
    class Equality {

      @Test
      @DisplayName("Равны, если id одинаковые (даже при разных полях)")
      void equalsByIdOnly() {
        Note a = new Note(1, "A", "textA");
        Note b = new Note(1, "B", "textB");

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
      }

      @Test
      @DisplayName("Не равны, если id разные")
      void notEqualsIfIdDiffers() {
        Note a = new Note(1, "A", "t");
        Note b = new Note(2, "A", "t");
        assertThat(a).isNotEqualTo(b);
      }

      @Test
      @DisplayName("equals: рефлексивность/симметричность/сравнение с null и другим классом")
      void equalsContractBasics() {
        Note a = new Note(1, "A", "t");

        // рефлексивность
        assertThat(a).isEqualTo(a);

        // сравнение с null
        assertThat(a.equals(null)).isFalse();

        // сравнение с другим классом
        assertThat(a.equals("not a note")).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("toString")
  class ToStringTests {

    @Test
    @DisplayName("toString содержит все основные поля")
    void toStringContainsFields() {
      Note note = new Note(1, "TestTitle", "SomeText");
      String str = note.toString();

      assertThat(str).contains("Note{").contains("creationDate=" + note.getCreationDate())
          .contains("title='TestTitle'").contains("text='SomeText'");
    }

    @Test
    @DisplayName("toString корректно работает с пустым текстом")
    void toStringHandlesEmptyText() {
      Note note = new Note(2, "OnlyTitle", null); // text -> ""
      String str = note.toString();

      assertThat(str).contains("title='OnlyTitle'")
          .contains("text=''"); // текст должен быть пустой строкой
    }

    @Test
    @DisplayName("toString разные заметки дают разные строки")
    void toStringDiffersForDifferentNotes() {
      Note note1 = new Note(3, "A", "aaa");
      Note note2 = new Note(4, "B", "bbb");

      assertThat(note1.toString()).isNotEqualTo(note2.toString());
    }
  }

}