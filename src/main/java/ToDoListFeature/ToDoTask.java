package ToDoListFeature;

public class ToDoTask extends ToDo{

    /**
     * Constructor for ToDoTask
     * @param name the name of the task
     */
    public ToDoTask(String name) {
        super(name);
    }

    /**
     * Get the full String representation of the task
     * @return the full String of the task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
