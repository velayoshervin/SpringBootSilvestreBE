package com.silvestre.web_applicationv1.service;

import com.silvestre.web_applicationv1.Dto.CalendarCalendarsDto;
import com.silvestre.web_applicationv1.response.CalendarEventResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarDataResponse
{
    private List<CalendarCalendarsDto> calendars;
    private List<CalendarEventResponse> events;
}
