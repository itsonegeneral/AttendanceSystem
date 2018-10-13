package com.rstudio.hp.attendancesystem;

public class NotificationClass {
    private String Notdescription;
    private String Notdate;
    private String Nottitle;
    private int priority;
    private boolean readStatus;
    public NotificationClass(){

    }

    public NotificationClass( String nottitle,String notdescription, String notdate, int priority,boolean readStatus) {
        Notdescription = notdescription;
        Notdate = notdate;
        Nottitle = nottitle;
        this.priority = priority;
        this.readStatus = readStatus;
    }

    public boolean getReadStatus(){
        return readStatus;
    }
    public void setReadStatus(boolean status){
        this.readStatus  = status;
    }
    public String getNotdescription() {

        return Notdescription;
    }

    public void setNotdescription(String notdescription) {
        Notdescription = notdescription;
    }

    public String getNotdate() {
        return Notdate;
    }

    public void setNotdate(String notdate) {
        Notdate = notdate;
    }

    public String getNottitle() {
        return Nottitle;
    }

    public void setNottitle(String nottitle) {
        Nottitle = nottitle;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
