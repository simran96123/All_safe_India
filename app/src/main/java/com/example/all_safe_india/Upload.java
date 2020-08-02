package com.example.all_safe_india;

public class Upload {

    private  String mName;
    private String mImageUri;

    public Upload()
    {

    }
    public  Upload(String name , String imageuri)
    {
        if(name.trim().equals(""))
        {
            name = "no name";
        }

        mName = name;
        mImageUri = imageuri;

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUri() {
        return mImageUri;
    }

    public void setmImageUri(String mImageUri) {
        this.mImageUri = mImageUri;
    }
}
