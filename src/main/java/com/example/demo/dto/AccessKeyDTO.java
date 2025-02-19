package com.example.demo.dto;

import com.example.demo.model.AccessKey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessKeyDTO {
    private int accessKeyId;
    private String accessKeyValue;
    private AccessKey.Status status;
    private String dateOfProcurement;
    private String expiryDate;

    public AccessKeyDTO(AccessKey accessKey) {
        this.accessKeyId = accessKey.getAccessKeyId();
        this.accessKeyValue = accessKey.getAccessKeyValue();
        this.status = accessKey.getStatus();
        this.dateOfProcurement = accessKey.getDateOfProcurement().toString();
        this.expiryDate = accessKey.getExpiryDate().toString();
    }
}
