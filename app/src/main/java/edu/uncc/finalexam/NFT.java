package edu.uncc.finalexam;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class NFT implements Serializable {

    String  image_url,image_thumbnail_url,nft_name,description,id,token_id, collection_name,collection_image;

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumbnail_url() {
        return image_thumbnail_url;
    }

    public void setImage_thumbnail_url(String image_thumbnail_url) {
        this.image_thumbnail_url = image_thumbnail_url;
    }

    public String getName() {
        return nft_name;
    }

    public void setName(String nft_name) {
        this.nft_name = nft_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public String getCollection_image() {
        return collection_image;
    }

    public void setCollection_image(String collection_image) {
        this.collection_image = collection_image;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public NFT(JSONObject jsonObject) throws JSONException {

        this.image_thumbnail_url = jsonObject.getJSONObject("nft").getString("image_thumbnail_url");
        this.image_url = jsonObject.getJSONObject("nft").getString("image_url");
        this.nft_name = jsonObject.getJSONObject("nft").getString("name");
        this.description = jsonObject.getJSONObject("nft").getString("description");
        this.id = jsonObject.getJSONObject("nft").getString("id");
        this.token_id = jsonObject.getJSONObject("nft").getString("token_id");
        this.collection_name=jsonObject.getJSONObject("collection").getString("name");
        this.collection_image=jsonObject.getJSONObject("collection").getString("banner_image_url");
    }
}
