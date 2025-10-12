package com.silvestre.web_applicationv1.enums;
public enum Role {
    ADMIN,
    CUSTOMER,
    PLANNER;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
