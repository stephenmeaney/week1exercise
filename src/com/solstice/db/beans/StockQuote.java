package com.solstice.db.beans;

import java.sql.Timestamp;

public class StockQuote {

    private int id;
    private String symbol;
    private double price;
    private int volume;
    private Timestamp date;


    public void displayData() {
        System.out.println(id + " " + symbol + " " + price + " " + volume + " " + date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
