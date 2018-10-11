package com.rstudio.hp.attendancesystem;

public class Student {
    public String name;
    public String batch;
    public long rollno;
    public long attendeddays;
    public long percentage;

    Student(){
        this.rollno=0;
        this.name= "Friend";
        this.percentage=10;
        this.batch="Unspecified";
    }
    Student(String name,String batch,long rollno){
        this.name=name;
        this.rollno=rollno;
        this.batch=batch;
        this.attendeddays=0;
        this.percentage =0;
    }
}
