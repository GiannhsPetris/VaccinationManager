package com.example.ergasia3;

import java.time.LocalDate;

public class DateModel implements Comparable<DateModel> {

    String citizenUid, centerId, date1, date2, secondDose, firstDose;

    public DateModel(String citizenUid, String centerId, String date1, String date2, String secondDose, String firstDose) {
        this.citizenUid = citizenUid;
        this.centerId = centerId;
        this.date1 = date1;
        this.date2 = date2;
        this.secondDose = secondDose;
        this.firstDose = firstDose;
    }

    public DateModel() {

    }

    public String getSecondDose() {
        return secondDose;
    }

    public void setSecondDose(String secondDose) {
        this.secondDose = secondDose;
    }

    public String getFirstDose() {
        return firstDose;
    }

    public void setFirstDose(String firstDose) {
        this.firstDose = firstDose;
    }

    public String getCitizenUid() {
        return citizenUid;
    }

    public void setCitizenUid(String citizenUid) {
        this.citizenUid = citizenUid;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }

    @Override
    public int compareTo(DateModel o) {

        String dob1 = getDate1();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(dob1);

        String dob2 = o.getDate1();
        LocalDate date2 = LocalDate.parse(dob2);

         return date.compareTo(date2);
    }
}
