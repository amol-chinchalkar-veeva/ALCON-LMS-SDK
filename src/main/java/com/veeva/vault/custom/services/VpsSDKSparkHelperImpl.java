/*
 * User-Defined Class:  VpsSDKSparkHelperImpl
 * Author:              Krunal Shah @ Veeva
 * Date:                2020-02-03
 *-----------------------------------------------------------------------------
 * Description: Useful Methods to help with Spark Integration between Vaults
 *-----------------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved. *
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 *--------------------------------------------------------------------
 *
 */

package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.query.QueryService;

import java.util.List;

@UserDefinedServiceInfo
public class VpsSDKSparkHelperImpl implements VpsSDKSparkHelperService {

	private static final String INTEGRATION_SYS = "integration__sys";
	private static final String INTEGRATION_POINT_SYS = "integration_point__sys";
	private static final String EXCEPTION_MESSAGE_SYS = "exception_message__sys";
	private static final String ERROR_TYPE_SYS = "error_type__sys";
	private static final String ERROR_MESSAGE_SYS = "error_message__sys";
	private static final String NAME = "name__v";
	private static final String ID = "id";
	private static final String MESSAGE_PROCESSOR_SYS = "message_processor__sys";
	private static final String MESSAGE_BODY_JSON_SYS = "message_body_json__sys";
	private static final String MSG_PROCESSING_ERR_SYS = "message_processing_error__sys";
	private static final String CONNECTION_SYS = "connection__sys";
	private static final String ACTIVE = "active__v";

	/**
	 * Returns whether an active version of the specified 'integrationName'
	 * is configured against this (source) Vault.
	 *
	 * @param integrationApiName
	 */
	public boolean isIntegrationActive(String integrationApiName) {
		LogService logService = ServiceLocator.locate(LogService.class);
		QueryService queryService = ServiceLocator.locate(QueryService.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT id, name__v, status__v FROM ");
		query.append(INTEGRATION_SYS);
		query.append(" WHERE integration_api_name__sys = '").append(integrationApiName).append("'");
		query.append(" AND status__v = '").append(ACTIVE).append("'");
		logService.info("VpsSDKSparkHelper.isIntegrationActive query {}", query.toString());
		QueryResponse integration = queryService.query(query.toString());
		return integration.getResultCount() == 0 ? false : true;
	}

	/**
	 * Creates a User exception message
	 *
	 * @param integrationApiName
	 * @param integrationPointApiName
	 * @param errorMessage
	 * @param messageProcessor
	 * @param messageBody stores the message identifier so it can be re-run
	 */
	public void createUserExceptionMessage(String integrationApiName,
												  String integrationPointApiName,
												  String errorMessage,
												  String messageProcessor,
												  String messageBody) {

		LogService logService = ServiceLocator.locate(LogService.class);
		RecordService recordService = ServiceLocator.locate(RecordService.class);
		String integrationId = getIntegrationId(integrationApiName);
		String integrationPointId = getIntegrationPointId(integrationPointApiName);

		// Construct the user exception message
		Record userExceptionMessage = recordService.newRecord(EXCEPTION_MESSAGE_SYS);
		List<String> errorTypePicklistValues = VaultCollections.newList();
		errorTypePicklistValues.add(MSG_PROCESSING_ERR_SYS);
		userExceptionMessage.setValue(INTEGRATION_SYS, integrationId);
		userExceptionMessage.setValue(INTEGRATION_POINT_SYS, integrationPointId);
		userExceptionMessage.setValue(ERROR_TYPE_SYS, errorTypePicklistValues);
		userExceptionMessage.setValue(ERROR_MESSAGE_SYS, errorMessage);
		userExceptionMessage.setValue(NAME, integrationPointApiName);
		userExceptionMessage.setValue(MESSAGE_PROCESSOR_SYS, messageProcessor);
		userExceptionMessage.setValue(MESSAGE_BODY_JSON_SYS, messageBody);
		List<Record> recordsToSave = VaultCollections.newList();
		recordsToSave.add(userExceptionMessage);

		// Save the User Exception
		recordService.batchSaveRecords(recordsToSave)
								.rollbackOnErrors()
								.execute();

		StringBuilder logMessage = new StringBuilder();
		logMessage.append("Created User exception message: ").append(errorMessage);
		logService.info(logMessage.toString());
	}

	/**
	 * Returns the ID of the specified 'integrationName'
	 *
	 * @param integrationAPIName of the integration
	 */
	public String getIntegrationId(String integrationAPIName) {
		LogService logService = ServiceLocator.locate(LogService.class);
		QueryService queryService = ServiceLocator.locate(QueryService.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT id ");
		query.append("FROM ").append(INTEGRATION_SYS);
		query.append(" WHERE integration_api_name__sys = '").append(integrationAPIName).append("'");
		logService.info("VpsSDKSparkHelper.getIntegrationId query {}", query.toString());
		QueryResponse intQueryResponse = queryService.query(query.toString());
		List<String> ids = VaultCollections.newList();
		intQueryResponse.streamResults().forEach(qr -> {
			ids.add(qr.getValue(ID, ValueType.STRING));
		});

		return ids != null ? ids.get(0) : null;
	}

	/**
	 * Returns the ID of the specified 'integrationPointAPIName'
	 *
	 * @param integrationPointAPIName of the integration
	 */
	public String getIntegrationPointId(String integrationPointAPIName) {
		LogService logService = ServiceLocator.locate(LogService.class);
		QueryService queryService = ServiceLocator.locate(QueryService.class);

		StringBuilder query = new StringBuilder();
		query.append("SELECT id ");
		query.append("FROM ").append(INTEGRATION_POINT_SYS);
		query.append(" WHERE integration_point_api_name__sys = '").append(integrationPointAPIName).append("'");
		logService.info("VpsSDKSparkHelper.getIntegrationPointId query {}", query.toString());
		QueryResponse intQueryResponse = queryService.query(query.toString());
		List<String> ids = VaultCollections.newList();
		intQueryResponse.streamResults().forEach(qr -> {
			ids.add(qr.getValue(ID, ValueType.STRING));
		});

		return ids != null ? ids.get(0) : null;
	}

	/**
	 * Returns the ID of the specified 'connectionName'
	 *
	 * @param connectionAPIName of the integration
	 */
	public String getConnectionId(String connectionAPIName) {
		LogService logService = ServiceLocator.locate(LogService.class);
		QueryService queryService = ServiceLocator.locate(QueryService.class);
		StringBuilder query = new StringBuilder();
		query.append("SELECT id ");
		query.append("FROM ").append(CONNECTION_SYS);
		query.append(" WHERE api_name__sys = '").append(connectionAPIName).append("'");
		logService.info("VpsSDKSparkHelper.getConnectionIds query {}", query.toString());
		QueryResponse intQueryResponse = queryService.query(query.toString());
		List<String> ids = VaultCollections.newList();
		intQueryResponse.streamResults().forEach(qr -> {
			ids.add(qr.getValue(ID, ValueType.STRING));
		});

		return ids != null ? ids.get(0) : null;
	}
}
