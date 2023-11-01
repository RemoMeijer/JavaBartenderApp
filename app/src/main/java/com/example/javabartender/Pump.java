package com.example.javabartender;

public class Pump {
    String name = "";
    String value = "";

    public Pump(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getPumpName() {
        return name;
    }

    public String getPumpValue() {
        return value;
    }
}
