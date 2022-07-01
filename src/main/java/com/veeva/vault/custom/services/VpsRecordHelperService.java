/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsRecordHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2020-10-02
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2020 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsRecordHelperService extends UserDefinedService {

	public void deleteRecords(List<Record> recordList, String errorMessage) throws VaultRuntimeException;
	public void deleteRecords(List<Record> recordList, String errorMessage, Boolean rollbackOnError);
	public void deleteRecordsInBatch(List<Record> recordList, String errorMessage) throws VaultRuntimeException;
	public void deleteRecordsInBatch(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException;
	public void saveRecords(List<Record> recordList, String errorMessage) throws VaultRuntimeException;
	public void saveRecords(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException;
	public void saveRecordsInBatch(Map<String, Record> recordsMap, String errorMessage) throws VaultRuntimeException;
	public void saveRecordsInBatch(List<Record> recordList, String errorMessage) throws VaultRuntimeException;
	public void saveRecordsInBatch(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException;
	public String getObjectTypeApiName(String objectTypeId);
	public String getObjectTypeId(String objectTypeApiName, String objectName);
	public Object getValueForField(String fieldName, String fieldType, Record sourceRecord);
	public List<String> saveRecordsInBatchReturnIds(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException;
}