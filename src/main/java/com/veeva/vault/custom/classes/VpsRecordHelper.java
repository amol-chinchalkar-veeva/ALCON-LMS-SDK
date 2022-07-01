/*
 * --------------------------------------------------------------------
 * UDC:         VpsRecordHelper
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 * --------------------------------------------------------------------
 * Description: Helper for deleting and saving records
 * --------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 * --------------------------------------------------------------------
 */
package com.veeva.vault.custom.classes;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;

import java.util.List;
import java.util.Set;

@UserDefinedClassInfo
public class VpsRecordHelper extends VpsBaseHelper {

	public static final int MAX_BATCH_RECORDS = 500;
	private static final String OBJFIELD_ID = "id";
	private static final String PROCESS_ERROR = "ERROR";
	private static final String PROCESS_RACECONDITION = "RACE_CONDITION";
	private static final String PROCESS_SUCCESS = "SUCCESS";
	public static final int RETRY_ATTEMPTS_COUNT = 5;

	private RecordService recordService = null;

	/**
	 * Class to assist in creating, updating, and deleting records
	 */
	public VpsRecordHelper() {
		super();
	}

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
		getLogService().info("VpsRecordHelper.deleteRecords");

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
					getRecordService().batchDeleteRecords(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String id = recordList.get(errPosition).getValue(OBJFIELD_ID, ValueType.STRING);
									String fullMessage = errorMessage + " " + id + " because of " + errMsg;
									getErrorList().add(fullMessage);

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										logBatchMessage(id, fullMessage, batchAttempts.size());
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
			getLogService().error("VpsRecordHelper.deleteRecords - {}", exception.getMessage());
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
		getLogService().info("VpsRecordHelper.deleteRecordsInBatch");

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
			getLogService().error("VpsRecordHelper.deleteRecordsInBatch - {}", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Internal method to get a record service. If a service has already been created
	 * it returns the existing service.
	 * @return RecordService
	 */
	public RecordService getRecordService() {
		//initialize the service on the first call
		if (recordService == null) {
			recordService = ServiceLocator.locate(RecordService.class);
		}
		return recordService;
	}

	/**
	 * Stub for logging batch save/delete.
	 */
	public void logBatchMessage(String recordId, String message, Integer attempt) throws VaultRuntimeException {
		getLogService().info("VpsRecordHelper.logBatchMessage - recordId {}, message {}, attempt {}", recordId, message, attempt);
		//add additional logging / notifications here.
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
		getLogService().info("VpsRecordHelper.saveRecords");

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
					getRecordService().batchSaveRecords(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String id = recordList.get(errPosition).getValue(OBJFIELD_ID, ValueType.STRING);
									String fullMessage = errorMessage + " " + id + " because of " + errMsg;
									getErrorList().add(fullMessage);

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										logBatchMessage(id, fullMessage, batchAttempts.size());
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
			getLogService().error("VpsRecordHelper.saveRecords - {}", exception.getMessage());
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
		getLogService().info("VpsRecordHelper.saveRecordsInBatch");

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
			getLogService().error("VpsRecordHelper.saveRecordsInBatch - {}", exception.getMessage());
			throw exception;
		}
	}
}