package com.example.tyren.beachtrade;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by William on 10/24/2017.
 */

@DynamoDBTable(tableName = "userProfile")

public class ProfileMapperClass {

    String userID;
    String emailAddress;
    String phoneNumber;
    String firstName;
    String lastName;
    String userName;

    @DynamoDBHashKey(attributeName = "userID")
    @DynamoDBAttribute(attributeName = "userID")
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    @DynamoDBAttribute(attributeName = "emailAddress")
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String itemName) {
        this.emailAddress = itemName;
    }
    @DynamoDBAttribute(attributeName = "phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    @DynamoDBAttribute(attributeName = "firstName")
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    @DynamoDBAttribute(attributeName = "lastName")
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    @DynamoDBIndexHashKey(attributeName = "userName")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



}
