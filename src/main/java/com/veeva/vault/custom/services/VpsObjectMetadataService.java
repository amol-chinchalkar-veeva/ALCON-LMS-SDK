/*
 * --------------------------------------------------------------------
 * UDS:         VpsObjectMetadataService
 * Author:      Veeva Professional Services
 * Version:     1.0
 *---------------------------------------------------------------------
 * Description:
 * Interface of the ObjectMetadataService Service
 *
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.UserDefinedService;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.data.ObjectField;
import com.veeva.vault.sdk.api.data.ObjectFieldCollectionResponse;

@UserDefinedServiceInfo
public interface VpsObjectMetadataService extends UserDefinedService {

    ObjectFieldCollectionResponse getObjectFields(String objectName);

    ObjectField getObjectField(String objectName, String fieldName);

    ValueType<?> getValueType(String objectName, String fieldName);
}