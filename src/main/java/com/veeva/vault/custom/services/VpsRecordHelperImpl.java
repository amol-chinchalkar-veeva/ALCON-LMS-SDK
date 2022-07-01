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
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsRecordHelperImpl implements VpsRecordHelperService {
	public static final int MAX_BATCH_RECORDS = 500;
	private static final String OBJFIELD_ID = "id";
	private static final String PROCESS_ERROR = "ERROR";
	private static final String PROCESS_RACECONDITION = "RACE_CONDITION";
	private static final String PROCESS_SUCCESS = "SUCCESS";
	public static final int RETRY_ATTEMPTS_COUNT = 5;
	private static final String OBJECT_FIELD_API_NAME = "api_name__v";
	private static final String OBJECT_TYPE_OBJECT_NAME = "object_name__v";
	private static final String OBJECT_TYPE_NAME = "object_type__v";
	private static final String USER_QUERY = "select id from user__sys where id = '%s' limit 0";

	/**
	 * Deletes object records for a list of records
	 * When error occurs, a rollback exception is thrown
	 * @param recordList list of records to delete
	 * @param errorMessage error message to display after error
	 */
	public void deleteRecords(List<Record> recordList, String errorMessage) throws VaultRuntimeException {
		deleteRecords(recordList, errorMessage, true);
	}

	/**
	 * Deletes object records for a list of records
	 * @param recordList list of records to delete
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	public void deleteRecords(List<Record> recordList, String errorMessage, Boolean rollbackOnError) {
		try {
			RecordService recordService = ServiceLocator.locate(RecordService.class);

			if ((recordList != null) && (recordList.size() > 0)) {

				//this list is used to track the results of the batch process (success vs. error)
				//errors that are non-race conditions are marked as error
				//note: using a list because lambda expressions require final variables
				Set<String> results = VaultCollections.newSet();

				//this list is used to track the number of batch attempts
				//race condition errors are retried up to RETRY_ATTEMPTS_COUNT
				//note: using a list because lambda expressions require final variables
				List<String> batchAttempts = VaultCollections.newList();

				while ((batchAttempts.size() < RETRY_ATTEMPTS_COUNT)
						&& (!results.contains(PROCESS_ERROR))
						&& (!results.contains(PROCESS_SUCCESS))) {

					// process the changes in batch
					// if error occurs
					//		1. log the error
					//		2. if race condition, retry until max retries, then if rollbackOnError=true, throw rollback exception
					//		3. if non race condition and rollbackOnError=true, throw rollback exception
					// if success, mark as success
					recordService.batchDeleteRecords(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String id = recordList.get(errPosition).getValue(OBJFIELD_ID, ValueType.STRING);
									String fullMessage = errorMessage + " " + id + " because of " + errMsg;

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										sleep();

										//if we reached the max number of retries and rollebackOnError=true
										//throw rollback exception
										if ((batchAttempts.size() == RETRY_ATTEMPTS_COUNT) && (rollbackOnError)) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", errorMessage + PROCESS_RACECONDITION);
										}
									} else {
										results.add(PROCESS_ERROR);

										if (rollbackOnError) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", fullMessage);
										}
									}
								});
							})
							.onSuccesses(batchOperationSuccess -> {
								batchAttempts.add(PROCESS_SUCCESS);
								results.add(PROCESS_SUCCESS);
							})
							.execute();
				}
			}
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}

	/**
	 * Deletes object records for a list of records in batches
	 * When error occurs, a rollback exception is thrown
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to delete
	 * @param errorMessage error message to display after error
	 */
	public void deleteRecordsInBatch(List<Record> recordList, String errorMessage) throws VaultRuntimeException {
		deleteRecordsInBatch(recordList,errorMessage,true);
	}

	/**
	 * Deletes object records for a list of records in batches
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to delete
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	public void deleteRecordsInBatch(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		try {
			if ((recordList != null) && (recordList.size() > 0)) {
				//now save the part records in batches
				List<Record> batchRecords = VaultCollections.newList();
				for (Record r : recordList) {
					if (batchRecords.size() == MAX_BATCH_RECORDS) {
						deleteRecords(batchRecords, errorMessage, rollbackOnError);
						batchRecords.clear();
					}
					batchRecords.add(r);
				}
				if (batchRecords.size() > 0) {
					deleteRecords(batchRecords, errorMessage);
				}
			}
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}

	/**
	 * Upserts object records for a list of records in batches.
	 * When error occurs, a rollback exception is thrown
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 */
	public void saveRecords(List<Record> recordList, String errorMessage) throws VaultRuntimeException {
		saveRecords(recordList,errorMessage,true);
	}

	/**
	 * Upserts object records for a list of records in batches
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	public void saveRecords(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		try {
			RecordService recordService = ServiceLocator.locate(RecordService.class);

			if ((recordList != null) && (recordList.size() > 0)) {

				//this list is used to track the results of the batch process (success vs. error)
				//errors that are non-race conditions are marked as error
				//note: using a list because lambda expressions require final variables
				Set<String> results = VaultCollections.newSet();

				//this list is used to track the number of batch attempts
				//race condition errors are retried up to RETRY_ATTEMPTS_COUNT
				//note: using a list because lambda expressions require final variables
				List<String> batchAttempts = VaultCollections.newList();

				while ((batchAttempts.size() < RETRY_ATTEMPTS_COUNT)
						&& (!results.contains(PROCESS_ERROR))
						&& (!results.contains(PROCESS_SUCCESS))) {

					// process the changes in batch
					// if error occurs
					//		1. log the error
					//		2. if race condition, retry until max retries, then if rollbackOnError=true, throw rollback exception
					//		3. if non race condition and rollbackOnError=true, throw rollback exception
					// if success, mark as success
					recordService.batchSaveRecords(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String id = recordList.get(errPosition).getValue(OBJFIELD_ID, ValueType.STRING);
									String fullMessage = errorMessage + " " + id + " because of " + errMsg;

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										sleep();

										//if we reached the max number of retries and rollebackOnError=true
										//throw rollback exception
										if ((batchAttempts.size() == RETRY_ATTEMPTS_COUNT) && (rollbackOnError)) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", errorMessage + PROCESS_RACECONDITION);
										}
									} else {
										results.add(PROCESS_ERROR);

										if (rollbackOnError) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", fullMessage);
										}
									}
								});
							})
							.onSuccesses(batchOperationSuccess -> {
								batchAttempts.add(PROCESS_SUCCESS);
								results.add(PROCESS_SUCCESS);
							})
							.execute();
				}
			}
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}

	/**
	 * Upserts object records for a list of records in batches
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 * returns a list of record Ids for the newly created records
	 */
	public List<String> saveRecordsReturnIds(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		LogService logService = ServiceLocator.locate(LogService.class);
		RecordService recordService = ServiceLocator.locate(RecordService.class);

		logService.info("VpsRecordHelper.saveRecords");
		List<String> newRecordIds = VaultCollections.newList();

		try {
			if ((recordList != null) && (recordList.size() > 0)) {

				//this list is used to track the results of the batch process (success vs. error)
				//errors that are non-race conditions are marked as error
				//note: using a list because lambda expressions require final variables
				Set<String> results = VaultCollections.newSet();

				//this list is used to track the number of batch attempts
				//race condition errors are retried up to RETRY_ATTEMPTS_COUNT
				//note: using a list because lambda expressions require final variables
				List<String> batchAttempts = VaultCollections.newList();

				while ((batchAttempts.size() < RETRY_ATTEMPTS_COUNT)
						&& (!results.contains(PROCESS_ERROR))
						&& (!results.contains(PROCESS_SUCCESS))) {

					// process the changes in batch
					// if error occurs
					//		1. log the error
					//		2. if race condition, retry until max retries, then if rollbackOnError=true, throw rollback exception
					//		3. if non race condition and rollbackOnError=true, throw rollback exception
					// if success, mark as success
					recordService.batchSaveRecords(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String id = recordList.get(errPosition).getValue(OBJFIELD_ID, ValueType.STRING);
									String fullMessage = errorMessage + " " + id + " because of " + errMsg;

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										logService.info("VpsRecordHelper.logBatchMessage - recordId {}, message {}, attempt {}", id, fullMessage, batchAttempts.size());
										sleep();

										//if we reached the max number of retries and rollebackOnError=true
										//throw rollback exception
										if ((batchAttempts.size() == RETRY_ATTEMPTS_COUNT) && (rollbackOnError)) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", errorMessage + PROCESS_RACECONDITION);
										}
									} else {
										results.add(PROCESS_ERROR);

										if (rollbackOnError) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", fullMessage);
										}
									}
								});
							})
							.onSuccesses(batchOperationSuccess -> {
								batchAttempts.add(PROCESS_SUCCESS);
								results.add(PROCESS_SUCCESS);
								batchOperationSuccess.stream().forEach(success -> {
									newRecordIds.add(success.getRecordId());
								});
							})
							.execute();
				}
			}
		}
		catch (VaultRuntimeException exception) {
			logService.error("VpsRecordHelper.saveRecords - {}", exception.getMessage());
			throw exception;
		}

		return newRecordIds;

	}

	public void saveRecordsInBatch(Map<String, Record> recordsMap, String errorMessage) throws VaultRuntimeException
	{
		List<Record> records = VaultCollections.newList();
		for (Map.Entry<String, Record> recordEntry : recordsMap.entrySet()) {
			records.add(recordEntry.getValue());
		}
		saveRecordsInBatch(records, errorMessage);
	}

	/**
	 * Upserts object records for a list of records
	 * When error occurs, a rollback exception is thrown
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * returns a list of record Ids for the newly created records
	 */
	public List<String> saveRecordsInBatchReturnIds(List<Record> recordList, String errorMessage) throws VaultRuntimeException {
		List<String> recordIds = saveRecordsInBatchReturnIds(recordList,errorMessage,true);
		return recordIds;
	}

	/**
	 * Upserts object records for a list of records
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 * Returns a list of the record Ids for the newly created records
	 */
	public List<String> saveRecordsInBatchReturnIds(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		LogService logService = ServiceLocator.locate(LogService.class);
		logService.info("VpsRecordHelper.saveRecordsInBatch");
		List<String> newRecordIds = VaultCollections.newList();

		try {
			if ((recordList != null) && (recordList.size() > 0)) {
				//now save the part records in batches
				List<Record> batchRecords = VaultCollections.newList();
				for (Record r : recordList) {
					if (batchRecords.size() == MAX_BATCH_RECORDS) {
						newRecordIds = saveRecordsReturnIds(batchRecords, errorMessage, rollbackOnError);
						batchRecords.clear();
					}
					batchRecords.add(r);
				}
				if (batchRecords.size() > 0) {
					newRecordIds = saveRecordsReturnIds(batchRecords, errorMessage, true);
				}
			}
			return newRecordIds;
		}
		catch (VaultRuntimeException exception) {
			logService.error("VpsRecordHelper.saveRecordsInBatch - {}", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Upserts object records for a list of records
	 * When error occurs, a rollback exception is thrown
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 */
	public void saveRecordsInBatch(List<Record> recordList, String errorMessage) throws VaultRuntimeException {
		saveRecordsInBatch(recordList,errorMessage,true);
	}

	/**
	 * Upserts object records for a list of records
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	public void saveRecordsInBatch(List<Record> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		try {
			if ((recordList != null) && (recordList.size() > 0)) {
				//now save the part records in batches
				List<Record> batchRecords = VaultCollections.newList();
				for (Record r : recordList) {
					if (batchRecords.size() == MAX_BATCH_RECORDS) {
						saveRecords(batchRecords, errorMessage, rollbackOnError);
						batchRecords.clear();
					}
					batchRecords.add(r);
				}
				if (batchRecords.size() > 0) {
					saveRecords(batchRecords, errorMessage);
				}
			}
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}

	public String getObjectTypeApiName(String objectTypeId)
	{
		List<String> apiNames = VaultCollections.newList();
		VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);

		//select api_name__v from object_type__v where id = 'OOT000000000A32'
		QueryResponse queryResponse = vqlService.query(
				SELECT, vqlService.appendFields(
						OBJECT_FIELD_API_NAME
				),
				FROM, OBJECT_TYPE_NAME,
				WHERE, OBJFIELD_ID + " = '" + objectTypeId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			apiNames.add(queryResult.getValue(OBJECT_FIELD_API_NAME, ValueType.STRING));
		});

		return apiNames.get(0);
	}

	public String getObjectTypeId(String objectTypeApiName, String objectName)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		List<String> id = VaultCollections.newList();
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_TYPE_NAME,
				WHERE,OBJECT_TYPE_OBJECT_NAME + " = '" + objectName + "' and " + OBJECT_FIELD_API_NAME + " = '" + objectTypeApiName + "'"
		);
		queryResponse.streamResults().forEach(queryResult ->
		{
			String recordId = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			id.add(recordId);
		});

		return id.get(0);
	}

	protected void sleep() {
		try {
			RuntimeService runtimeService = ServiceLocator.locate(RuntimeService.class);
			runtimeService.sleep(1000);
		}
		catch (VaultRuntimeException exception) {
		}
	}

	public Object getValueForField(String fieldName, String fieldType, Record sourceRecord)
	{
		Object value = new Object();
		if (fieldType.equalsIgnoreCase("date"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.DATE);
		}
		if (fieldType.equalsIgnoreCase("string"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.STRING);
		}
		if (fieldType.equalsIgnoreCase("picklist"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.PICKLIST_VALUES);
		}
		if (fieldType.equalsIgnoreCase("boolean"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.BOOLEAN);
		}
		if (fieldType.equalsIgnoreCase("number"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.NUMBER);
		}
		if (fieldType.equalsIgnoreCase("references"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.REFERENCES);
		}
		if (fieldType.equalsIgnoreCase("datetime"))
		{
			value =  sourceRecord.getValue(fieldName, ValueType.DATETIME);
		}
		return value;
	}
}