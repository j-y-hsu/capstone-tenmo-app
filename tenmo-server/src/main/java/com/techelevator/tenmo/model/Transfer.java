package com.techelevator.tenmo.model;

public class Transfer {

    private int transferId;
    private String status;
    private int senderId;
    private int receiverId;
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
