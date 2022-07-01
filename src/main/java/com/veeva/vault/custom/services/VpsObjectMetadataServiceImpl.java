/*
 * --------------------------------------------------------------------
 * UDS:         VpsObjectMetadataServiceImpl
 * Author:      Veeva Professional Services
 * Version:     1.0
 *---------------------------------------------------------------------
 * Description:  Implementation of the Object Metadata Service
 *
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.LogService;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.data.*;

@UserDefinedServiceInfo
public class VpsObjectMetadataServiceImpl implements VpsObjectMetadataService {

    /**
     * Retrieves a list of object fields for the object
     * @param objectName name of the object (study__v, etc)
     * @return an object field collection response, that contains all the metadata for the object
     */
    @Override
    public ObjectFieldCollectionResponse getObjectFields(String objectName) {
        LogService log = ServiceLocator.locate(LogService.class);
        log.info("[ENTRY] {}", "getObjectFields()");
        // Initialize service
        ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);

        // Get the builder for building the request object of ObjectFieldCollectionResponse
        ObjectFieldCollectionRequest objectFieldCollectionRequest = objectMetadataService
                .newFieldCollectionRequestBuilder()
                .withObjectName(objectName)
                .build();

        log.info("[EXIT] {}", "getObjectFields()");
        return objectMetadataService.getFields(objectFieldCollectionRequest);
    }

    /**
     * Retrieves the metadata only for the specific field
     *
     * @param objectName name of the object (study__v, etc)
     * @param fieldName name of the field of the object (name__v, id, etc)
     * @return the specific field metadata for that field name
     */
    @Override
    public ObjectField getObjectField(String objectName, String fieldName) {
        LogService log = ServiceLocator.locate(LogService.class);
        log.info("[ENTRY] {}", "getObjectField()");

        // Initialize service
        ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);

        // Get the builder for building the request object of ObjectFieldCollectionResponse
        ObjectFieldCollectionRequest objectFieldCollectionRequest = objectMetadataService
                .newFieldCollectionRequestBuilder()
                .withObjectName(objectName)
                .build();

        log.info("[EXIT] {}", "getObjectField()");
        return objectMetadataService.getFields(objectFieldCollectionRequest).getField(fieldName);
    }

    /**
     * Returns the ValueType of an object field e.g. ValueType.STRING
     *
     * @param objectName object's API name e.g. study__v
     * @param fieldName field's API name e.g. name__v
     * @return the ValueType of the field provided
     */
    public ValueType<?> getValueType(String objectName, String fieldName) {
        ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);

        ObjectFieldRequest objectFieldRequest = objectMetadataService
                .newFieldRequestBuilder()
                .withObjectName(objectName)
                .withFieldName(fieldName)
                .build();

        ObjectField objectField = objectMetadataService.getField(objectFieldRequest);

        return objectField.getValueType();
    }
}