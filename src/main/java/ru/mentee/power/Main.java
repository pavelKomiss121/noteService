package ru.mentee.power;

import java.util.logging.Level;
import java.util.logging.Logger;
import ru.mentee.power.notes.Note;
import ru.mentee.power.notes.NoteService;

/**
 * Точка входа в приложение заметок.
 * В этой демо-версии создаётся сервис {@link NoteService}, добавляются две заметки и выводятся в
 * лог через {@link java.util.logging.Logger}.
 */
public class Main {

  /**
   * Логгер для вывода сообщений на уровне INFO.
   */
  private static final Logger logger = Logger.getLogger(Main.class.getName());

  /**
   * Главный метод, запускающий приложение.
   *
   * @param args аргументы командной строки (в этой версии не используются)
   */
  public static void main(String[] args) {
    NoteService noteService = new NoteService();
    noteService.addNote("Home", "I wanna go Home ", null);
    noteService.addNote("Home2", "I am at Home!!!", null);

    for (Note note : noteService.getAllNotes()) {
      // Используем ленивую форму log, чтобы избежать лишних вычислений строки
      logger.log(Level.INFO, note::toString);
    }
  }
}
