package com.gre.prep.Models;

import java.io.Serializable;
import java.util.HashMap;

public class WordModel2 implements Serializable {

    String word,wordID,type,note,image;
    HashMap<String,String> meaningList, categoryList;


    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWordID() {
        return wordID;
    }

    public void setWordID(String wordID) {
        this.wordID = wordID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public HashMap<String, String> getMeaningList() {
        return meaningList;
    }

    public void setMeaningList(HashMap<String, String> meaningList) {
        this.meaningList = meaningList;
    }

    public HashMap<String, String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(HashMap<String, String> categoryList) {
        this.categoryList = categoryList;
    }
}
