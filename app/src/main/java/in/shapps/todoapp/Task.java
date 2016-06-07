package in.shapps.todoapp;

import java.util.Date;

/**
 * Created by James on 1/23/2016.
 */
public class Task {
    private int id;
    private String title;
    private String desc;
    private int listID;
    private String alarmStatus;
    private Date datetime;
    private String taskStatus;

    public Task(){}
   /* public Task(int id, String title, String desc, int listID, String alarmStatus, Date datetime, String taskStatus) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.listID = listID;
        this.alarmStatus = alarmStatus;
        this.datetime = datetime;
        this.taskStatus = taskStatus;
    }*/
    public Task(String title, String desc, int listID, String alarmStatus, Date datetime, String taskStatus) {
        this.title = title;
        this.desc = desc;
        this.listID = listID;
        this.alarmStatus = alarmStatus;
        this.datetime = datetime;
        this.taskStatus = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getListID() {
        return listID;
    }

    public void setListID(int listID) {
        this.listID = listID;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}
