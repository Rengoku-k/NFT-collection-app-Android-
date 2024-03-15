package edu.uncc.finalexam;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Favourites {
    String uid,docId;
    ArrayList<String> nft_ids;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
