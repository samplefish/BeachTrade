package com.example.tyren.beachtrade;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by William on 10/24/2017.
 */

@DynamoDBTable(tableName = "userItems")

public class ItemsMapperClass {

    String userID;
    String itemName;
    Double price;
    String description;
    String pictureLink;
    String itemID;
    String itemType;
    String username;
    String email;

    @DynamoDBHashKey(attributeName = "itemID")
    @DynamoDBAttribute(attributeName = "itemID")
    public String getItemID() {return itemID;}
    public void setItemID(String itemID){this.itemID = itemID;}

    @DynamoDBAttribute(attributeName = "userID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    @DynamoDBAttribute(attributeName = "itemName")
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @DynamoDBAttribute(attributeName = "price")
    public Double getPrice(){return price;}
    public void setPrice(Double price) {this.price = price;};

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBAttribute(attributeName = "pictureLink")
    public String getPictureLink() {
        return pictureLink;
    }
    public void setPictureLink(String pictureLink) {
        this.pictureLink = pictureLink;
    }

    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute(attributeName = "username")
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }


    @DynamoDBAttribute(attributeName = "itemType")
    public String getItemType(){return itemType;}
    public void setItemType(String itemType){this.itemType = itemType;}


}
