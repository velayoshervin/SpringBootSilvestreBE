package com.silvestre.web_applicationv1.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarCreateRequest {
    String name;
    String color;
    Long userId;
}
