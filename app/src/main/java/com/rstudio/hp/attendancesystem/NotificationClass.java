package com.rstudio.hp.attendancesystem;

public class NotificationClass {
    private String Notdescription;
    private String Notdate;
    private String Nottitle;
    private int priority;
    public NotificationClass(){

    }

    public NotificationClass( String nottitle,String notdescription, String notdate, int priority) {
        Notdescription = notdescription;
        Notdate = notdate;
        Nottitle = nottitle;
        this.priority = priority;
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
