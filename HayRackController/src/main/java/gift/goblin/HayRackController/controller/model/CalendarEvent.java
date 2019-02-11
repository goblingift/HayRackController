/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gift.goblin.HayRackController.controller.model;

import java.util.Objects;

/**
 * Contains all required fields to render a calendar event with its data.
 * @author andre
 */
public class CalendarEvent {
    
    private String title;
    private String date;
    private String color;
    private String textColor;
    
    public static final String DATEFORMAT = "yyyy-MM-dd";
    public static final String COLOR_HIGHEST_TEMP = "red";
    public static final String COLOR_LOWEST_TEMP = "blue";
    public static final String DEFAULT_TEXTCOLOR = "white";

    public CalendarEvent() {
    }

    public CalendarEvent(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public CalendarEvent(String title, String date, String color, String textColor) {
        this.title = title;
        this.date = date;
        this.color = color;
        this.textColor = textColor;
    }

    //<editor-fold defaultstate="collapsed" desc="getterSetter">
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getTextColor() {
        return textColor;
    }
    
    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }
    
//</editor-fold>
    
//<editor-fold defaultstate="collapsed" desc="equalsHashcode">
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.date);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalendarEvent other = (CalendarEvent) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        if (!Objects.equals(this.textColor, other.textColor)) {
            return false;
        }
        return true;
    }
//</editor-fold>
    
    @Override
    public String toString() {
        return "CalendarEvent{" + "title=" + title + ", date=" + date + ", color=" + color + ", textColor=" + textColor + '}';
    }
    
}
