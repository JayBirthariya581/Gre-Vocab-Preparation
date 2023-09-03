package com.gre.prep.Helper;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DBRefManager {

    DatabaseReference WordDatabaseReference;
    DatabaseReference NewWordDatabaseReference;
    DatabaseReference CombinedWordDatabaseReference;
    DatabaseReference WordCategoryReference;
    DatabaseReference RevisionDatabase;



    public DBRefManager() {
        WordDatabaseReference = FirebaseDatabase.getInstance().getReference("WordDatabase");
        NewWordDatabaseReference = FirebaseDatabase.getInstance().getReference("NewWordDatabase");
        CombinedWordDatabaseReference = FirebaseDatabase.getInstance().getReference("CombinedWordDatabase");
        WordCategoryReference = FirebaseDatabase.getInstance().getReference("CategoryDatabase");
        RevisionDatabase = FirebaseDatabase.getInstance().getReference("RevisionDatabase");
    }

    public DatabaseReference getNewWordDatabaseReference() {
        return NewWordDatabaseReference;
    }

    public void setNewWordDatabaseReference(DatabaseReference newWordDatabaseReference) {
        NewWordDatabaseReference = newWordDatabaseReference;
    }

    public DatabaseReference getCombinedWordDatabaseReference() {
        return CombinedWordDatabaseReference;
    }

    public void setCombinedWordDatabaseReference(DatabaseReference combinedWordDatabaseReference) {
        CombinedWordDatabaseReference = combinedWordDatabaseReference;
    }

    public DatabaseReference getWordDatabaseReference() {
        return WordDatabaseReference;
    }

    public void setWordDatabaseReference(DatabaseReference wordDatabaseReference) {
        WordDatabaseReference = wordDatabaseReference;
    }

    public DatabaseReference getWordCategoryReference() {
        return WordCategoryReference;
    }

    public void setWordCategoryReference(DatabaseReference wordCategoryReference) {
        WordCategoryReference = wordCategoryReference;
    }

    public DatabaseReference getRevisionDatabase() {
        return RevisionDatabase;
    }

    public void setRevisionDatabase(DatabaseReference revisionDatabase) {
        RevisionDatabase = revisionDatabase;
    }
}
