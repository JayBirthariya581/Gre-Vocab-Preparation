package com.gre.prep.Models;

import java.io.Serializable;
import java.util.List;

public class WordModel implements Serializable {

    String word,wordID,type,note,image;
    List<MeaningModel> meaningList;
    List<GroupModel> categoryList;

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

    public List<MeaningModel> getMeaningList() {
        return meaningList;
    }

    public void setMeaningList(List<MeaningModel> meaningList) {
        this.meaningList = meaningList;
    }

    public List<GroupModel> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<GroupModel> categoryList) {
        this.categoryList = categoryList;
    }
}
