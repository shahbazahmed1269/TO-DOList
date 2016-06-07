package in.shapps.todoapp;

/**
 * Created by James on 1/23/2016.
 */
public class List {
    private int id;
    private String listName;

    public List(){ }
    public List(String listName) {
        this.listName = listName;
    }

    public List(int id, String listName) {
        this.id = id;
        this.listName = listName;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
