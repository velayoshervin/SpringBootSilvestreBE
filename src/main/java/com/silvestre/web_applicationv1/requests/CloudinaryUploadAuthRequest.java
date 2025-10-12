package com.silvestre.web_applicationv1.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CloudinaryUploadAuthRequest {

    private File file;

    private String api_key;


}
