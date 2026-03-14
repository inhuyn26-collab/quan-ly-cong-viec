import java.io.*;
import java.util.*;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private final String FILE_NAME = "tasks.txt";

    public TaskManager() { loadData(); }

    public List<Task> getAllTasks() { return tasks; }

    public void addTask(String title) {
        int id = tasks.isEmpty() ? 1 : tasks.get(tasks.size() - 1).getId() + 1;
        tasks.add(new Task(id, title));
        saveData();
    }

    public void markDone(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) { t.setCompleted(true); break; }
        }
        saveData();
    }

    // HÀM NÀY GIÚP HẾT LỖI GẠCH ĐỎ
    public void deleteTask(int id) {
        tasks.removeIf(t -> t.getId() == id);
        saveData();
    }

    private void saveData() {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Task t : tasks) out.println(t.toFileString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadData() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.isEmpty()) tasks.add(Task.fromFileString(line));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}