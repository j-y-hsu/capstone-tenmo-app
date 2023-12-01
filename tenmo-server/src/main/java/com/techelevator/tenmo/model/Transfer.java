package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class Transfer {

    private int transferId;
    @NotBlank(message = "Status is required.")
    private String status;
    private int senderId = 0;
    private int receiverId = 0;
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    private double amount;

    public int getTransferId(){
        return transferId;
    }

    public void setTransferId(int transferId){
        this.transferId = transferId;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public int getSenderId(){
        return senderId;
    }

    public void setSenderId(int senderId){
        this.senderId = senderId;
    }

    public int getReceiverId(){
        return receiverId;
    }

    public void setReceiverId(int receiverId){
        this.receiverId = receiverId;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }



}
