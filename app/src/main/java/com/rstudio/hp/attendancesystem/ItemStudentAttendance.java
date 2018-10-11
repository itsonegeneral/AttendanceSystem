package com.rstudio.hp.attendancesystem;

public class ItemStudentAttendance {
     public boolean present;
     public String studentName;

      ItemStudentAttendance(String student,boolean check) {
         this.studentName = student;
         this.present = check;
     }

    public String getStudentName() {
        return studentName;
    }
    public boolean isSelected(){
          return present;
    }
    public void setSelected(boolean p){
        this.present=p;
    }
}
