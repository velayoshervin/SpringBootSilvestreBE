package com.silvestre.web_applicationv1.Dto;

import com.silvestre.web_applicationv1.entity.CalendarCalendars;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarCalendarsDto {
    private Long calendarId;
    private String name;
    private String color;

    public CalendarCalendarsDto(CalendarCalendars cal){
        this.calendarId = cal.getId();
        this.name = cal.getName();
        this.color = cal.getColor();
    }
}
