package com.cagan.library.integration.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ObjectLocator {
    private String bucketName;
    private String objectName;
}
