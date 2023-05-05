package com.example.myfinal.Model;

import com.google.type.Date;

public class Msg {
    private String id_receiver;

    public void setId_receiver(String id_receiver) {
        this.id_receiver = id_receiver;
    }

    private String id_sender;
    private String msg;
    private Date date;

    public Msg() {

    }

    public Msg(String id_sender, String id_receiver, String msg, Date date) {
        this.id_receiver = id_receiver;
        this.id_sender = id_sender;
        this.msg = msg;
        this.date = date;
    }

    public Msg(String id_sender, String id_receiver, String msg) {
        this.id_receiver = id_receiver;
        this.id_sender = id_sender;
        this.msg = msg;
    }

    public String getId_receiver() {
        return id_receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId_sender() {
        return id_sender;
    }

    public void setId_sender(String id_sender) {
        this.id_sender = id_sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
