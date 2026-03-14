public class Task {
    private int id;
    private String title;
    private boolean isCompleted;

    public Task(int id, String title) {
        this.id = id;
        this.title = title;
        this.isCompleted = false;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { this.isCompleted = completed; }

    public String toFileString() { return id + ";" + title + ";" + isCompleted; }

    public static Task fromFileString(String line) {
        String[] parts = line.split(";");
        Task t = new Task(Integer.parseInt(parts[0]), parts[1]);
        t.setCompleted(Boolean.parseBoolean(parts[2]));
        return t;
    }

    @Override
    public String toString() {
        String statusIcon = isCompleted ? " ✅ " : " ⏳ ";
        return String.format(" %d. %s %s", id, statusIcon, title);
    }
}