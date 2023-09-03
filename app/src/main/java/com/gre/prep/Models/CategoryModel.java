package com.gre.prep.Models;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {

    String category,categoryID;
    List<WordModel> wordList;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public List<WordModel> getWordList() {
        return wordList;
    }

    public void setWordList(List<WordModel> wordList) {
        this.wordList = wordList;
    }
}
