package com.gre.prep.Models;

import java.io.Serializable;

public class MeaningModel implements Serializable {
    String meaningID, meaning;


    public String getMeaningID() {
        return meaningID;
    }

    public void setMeaningID(String meaningID) {
        this.meaningID = meaningID;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
