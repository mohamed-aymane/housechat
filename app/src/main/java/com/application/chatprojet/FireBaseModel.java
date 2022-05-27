package com.application.chatprojet;


public class FireBaseModel {
    /* the key and value format  that i use to store  data
    and the uid it help me to create a room in database
        userdata.put("name",name);
        userdata.put("image",imageUri);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","Online");*/
    private String name;
    private String image;
    private String uid;//receiveruid
    private String status;

    public FireBaseModel() {
    }

    public FireBaseModel(String name, String image, String uid, String status) {
        this.name = name;
        this.image = image;
        this.uid = uid;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
