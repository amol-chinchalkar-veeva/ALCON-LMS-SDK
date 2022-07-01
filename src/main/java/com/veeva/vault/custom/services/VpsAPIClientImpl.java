/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsAPIClient
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

import com.veeva.vault.custom.model.VpsAPIResponse;
import com.veeva.vault.custom.model.VpsVQLRequest;
import com.veeva.vault.custom.model.VpsVQLResponse;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.http.HttpMethod;
import com.veeva.vault.sdk.api.http.HttpRequest;
import com.veeva.vault.sdk.api.http.HttpResponseBodyValueType;
import com.veeva.vault.sdk.api.http.HttpService;
import com.veeva.vault.sdk.api.integration.FieldRuleResult;
import com.veeva.vault.sdk.api.integration.IntegrationRule;
import com.veeva.vault.sdk.api.integration.IntegrationRuleService;
import com.veeva.vault.sdk.api.json.JsonArray;
import com.veeva.vault.sdk.api.json.JsonData;
import com.veeva.vault.sdk.api.json.JsonObject;
import com.veeva.vault.sdk.api.json.JsonValueType;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.query.QueryService;

import java.util.*;

@UserDefinedServiceInfo
public class VpsAPIClientImpl implements VpsAPIClientService {


	private static final String APIFIELD_ACTIONS = "lifecycle_actions__v";
	private static final String APIFIELD_ASSIGNED_GROUPS = "assignedGroups";
	private static final String APIFIELD_ASSIGNED_USERS = "assignedUsers";
	private static final String APIFIELD_DOCUMENT_ROLES = "documentRoles";
	private static final String APIFIELD_FROM_TEMPLATE = "fromTemplate";
	private static final String APIFIELD_LABEL = "label__v";
	private static final String APIFIELD_NAME = "name__v";
	private static final String APIFIELD_ERROR_MESSAGE = "message";
	private static final String APIFIELD_ERROR_TYPE = "type";
	private static final String APIFIELD_QUERY = "q";
	private static final String PROCESS_ERROR = "ERROR";
	private static final String PROCESS_RACECONDITION = "RACE_CONDITION";
	private static final String PROCESS_SUCCESS = "SUCCESS";
	private static final String RESPONSESTATUS_SUCCESS = "SUCCESS";
	private static final int RETRY_ATTEMPTS_COUNT = 5;
	private static final String SDK_EXTERNAL_ID = "VpsAPIClient";
	private static final String SETTING_APIVERSION = "api_version";
	private static final String URL_BINDER_CREATETEMPLATE = "/api/%s/objects/binders";
	private static final String URL_DOCUMENT_CREATETEMPLATE = "/api/%s/objects/documents";
	private static final String URL_DOCUMENT_LIFEYCLEACTIONS = "/api/%s/objects/documents/%s/versions/%s/%s/lifecycle_actions/";
	private static final String URL_INITIATE_OBJECT_ACTION = "/api/%s/vobjects/%s/%s/actions/%s";
	private static final String URL_UPDATE_OBJECT = "/api/%s/vobjects/%s";
	private static final String URL_QUERY = "/api/%s/query";
	private static final String URL_ROLES = "/api/%s/objects/documents/%s/roles/%s";
	private static final String USER_QUERY = "select id from user__sys where id = '%s' limit 0";

	public Boolean createBinderFromTemplate(String templateName, Map<String, String> documentMetadata, String apiVersion, String apiConnection) {
		return createDocumentFromTemplate(templateName,documentMetadata,true, apiVersion, apiConnection);
	}

	public Boolean createDocumentFromTemplate(String templateName, Map<String, String> documentMetadata, String apiVersion, String apiConnection) {
		return createDocumentFromTemplate(templateName,documentMetadata,false, apiVersion, apiConnection);
	}

	private Boolean createDocumentFromTemplate(String templateName,
											   Map<String, String> documentMetadata,
											   Boolean isBinder, String apiVersion, String apiConnection) {

		List<Boolean> successList = VaultCollections.newList();
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		String createTemplateUrl;
		if (isBinder) {
			createTemplateUrl = String.format(URL_BINDER_CREATETEMPLATE, apiVersion);
		}
		else {
			createTemplateUrl = String.format(URL_DOCUMENT_CREATETEMPLATE, apiVersion);
		}
		logService.info("createDocumentFromTemplate {}", createTemplateUrl);

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.setMethod(HttpMethod.POST)
				.setBodyParam(APIFIELD_FROM_TEMPLATE, templateName)
				.appendPath(createTemplateUrl);

		for (String fieldName : documentMetadata.keySet()) {
			String fieldValue = documentMetadata.get(fieldName);
			request.setBodyParam(fieldName, fieldValue);
		}

		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("createDocumentFromTemplate {}", errorMessage);
				})
				.onSuccess(response -> {
					VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
					if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
						successList.add(true);
					}
					else {
						JsonArray errors = apiResponse.getErrors();
						if (errors != null) {
							for (int i = 0; i < errors.getSize(); i++) {
								JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
								String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
								String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
								logService.error("createDocumentFromTemplate {}", errorType + " - " + errorMessage);
							}
						}
					}

				})
				.execute();

		return successList.size() > 0;
	}

	public Map<String,String> getDocumentLifecycleActions(String docId,
														  String majorVersion,
														  String minorVersion, String apiVersion, String apiConnection) {

		Map<String,String> lifecycleActionMap = VaultCollections.newMap();
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		String lifeycleActionUrl = String.format(URL_DOCUMENT_LIFEYCLEACTIONS,apiVersion,docId,majorVersion,minorVersion);
		logService.info("getDocumentLifecycleActions {}", lifeycleActionUrl);

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.appendPath(lifeycleActionUrl);
		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("getDocumentLifecycleActions {}", errorMessage);
				})
				.onSuccess(response -> {
					VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
					if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
						JsonArray actionArray = apiResponse.getArray(APIFIELD_ACTIONS);
						for (int i = 0; i< actionArray.getSize(); i++) {
							JsonObject lifecycleAction = actionArray.getValue(i, JsonValueType.OBJECT);

							lifecycleActionMap.put(
									lifecycleAction.getValue(APIFIELD_LABEL, JsonValueType.STRING),
									lifecycleAction.getValue(APIFIELD_NAME, JsonValueType.STRING));
						}
					}
					else {
						JsonArray errors = apiResponse.getErrors();
						if (errors != null) {
							for (int i = 0; i < errors.getSize(); i++) {
								JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
								String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
								String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
								logService.error("getDocumentLifecycleActions {}", errorType + " - " + errorMessage);
							}
						}
					}
				})
				.execute();

		return lifecycleActionMap;
	}

	public List<String> getDocumentUsersAndGroupsFromRole(String docId, String roleApiName, String apiVersion, String apiConnection) {
		List<String> usersAndGroups = VaultCollections.newList();
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		String roleUrl = String.format(URL_ROLES, apiVersion, docId, roleApiName);
		logService.info("getDocumentUsersAndGroupsFromRole {}", roleUrl);

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.appendPath(roleUrl);

		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("getDocumentLifecycleActions {}", errorMessage);
				})
				.onSuccess(response -> {

					VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
					if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
						JsonArray roleArray = apiResponse.getArray(APIFIELD_DOCUMENT_ROLES);

						for (int i = 0; i < roleArray.getSize(); i++) {
							JsonObject role = roleArray.getValue(i, JsonValueType.OBJECT);

							JsonArray groupArray = role.getValue(APIFIELD_ASSIGNED_GROUPS, JsonValueType.ARRAY);
							for (int g = 0; g < groupArray.getSize(); g++) {
								usersAndGroups.add("group:" + groupArray.getValue(g, JsonValueType.NUMBER));
							}
							JsonArray userArray = role.getValue(APIFIELD_ASSIGNED_USERS, JsonValueType.ARRAY);
							for (int u = 0; u< userArray.getSize(); u++) {
								usersAndGroups.add("user:" + userArray.getValue(u, JsonValueType.NUMBER));
							}
						}
					}
					else {
						JsonArray errors = apiResponse.getErrors();
						if (errors != null) {
							for (int i = 0; i < errors.getSize(); i++) {
								JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
								String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
								String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
								logService.error("getDocumentLifecycleActions {}", errorType + " - " + errorMessage);
							}
						}
					}
				})
				.execute();

		return usersAndGroups;
	}

	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										List<String> idList, String apiVersion, String apiConnection) {
		return initiateObjectAction(objectName,userActionName,idList,true, apiVersion, apiConnection);
	}

	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										List<String> idList,
										Boolean rollbackOnError, String apiVersion, String apiConnection) {
		for (String objectId : idList) {
			if (!initiateObjectAction(objectName, userActionName, objectId, rollbackOnError, apiVersion, apiConnection)) {
				return false;
			}
		}
		return true;
	}

	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										String objectId, String apiVersion, String apiConnection) {
		return initiateObjectAction(objectName, userActionName, objectId,true, apiVersion, apiConnection);
	}

	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										String objectId,
										Boolean rollbackOnError, String apiVersion, String apiConnection) {
		LogService logService = ServiceLocator.locate(LogService.class);

		String initiateObjectActionUrl = String.format(
				URL_INITIATE_OBJECT_ACTION,
				apiVersion,
				objectName,
				objectId,
				userActionName);
		logService.info("initiateObjectAction {}", initiateObjectActionUrl);
		HttpService httpService = ServiceLocator.locate(HttpService.class);

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.setMethod(HttpMethod.POST)
				.setBody("")
				.appendPath(initiateObjectActionUrl);

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
			httpService.send(request, HttpResponseBodyValueType.STRING)
					.onError(response -> {
						batchAttempts.add(PROCESS_ERROR);
						results.add(PROCESS_ERROR);

						String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
						logService.error("initiateObjectAction {}", errorMessage);
					})
					.onSuccess(response -> {
						VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
						if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
							batchAttempts.add(PROCESS_SUCCESS);
							results.add(PROCESS_SUCCESS);
						}
						//This is HTTP 200, but an application level error
						else {
							JsonArray errors = apiResponse.getErrors();
							if (errors != null) {
								for (int i = 0; i < errors.getSize(); i++) {
									JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
									String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
									String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
									logService.error("initiateObjectAction {}", errorType + " - " + errorMessage);

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
											throw new RollbackException("OPERATION_NOT_ALLOWED", errorMessage);
										}
									}
								}
							}
						}

					})
					.execute();
		}

		return results.contains(PROCESS_SUCCESS);
	}

	/**
	 * Runs the current query. Query is logged to LogService
	 *
	 * @return QueryResponse with results from the VQL query
	 */
	public VpsVQLResponse runVQL(VpsVQLRequest vpsVQLRequest, String apiVersion, String apiConnection) {
		vpsVQLRequest.logVQL();
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		List<VpsVQLResponse> resultList = VaultCollections.newList();

		String queryUrl = String.format(URL_QUERY, apiVersion);
		logService.info("runVQL {}", queryUrl);

		//now call GET on the documents available user actions and build a map
		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.setMethod(HttpMethod.POST)
				.setBodyParam(APIFIELD_QUERY, vpsVQLRequest.getVQL())
				.appendPath(queryUrl);

		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("runVQL {}", errorMessage);
				})
				.onSuccess(response -> {
					VpsVQLResponse vpsVQLResponse = new VpsVQLResponse(response.getResponseBody());
					resultList.add(vpsVQLResponse);

					JsonArray errors = vpsVQLResponse.getErrors();
					if (errors != null) {
						for (int i = 0; i < errors.getSize(); i++) {
							JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
							String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
							String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
							logService.error("VpsVQLResponse {}", errorType + " - " + errorMessage);
						}
					}

				})
				.execute();

		if (resultList.size() > 0) {
			return resultList.get(0);
		}
		else {
			return null;
		}
	}

	public Boolean startDocumentWorkflow(String docId,
										 String majorVersion,
										 String minorVersion,
										 String lifecycleActionName,
										 String roleName,
										 Set<String> users,
										 Set<String> groups, String apiVersion, String apiConnection) {

		List<Boolean> successList = VaultCollections.newList();
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		String startWorkflowUrl = String.format(
				URL_DOCUMENT_LIFEYCLEACTIONS,
				apiVersion,
				docId,
				majorVersion,
				minorVersion) + lifecycleActionName;
		logService.info("startDocumentWorkflow {}", startWorkflowUrl);

		Set<String> usersAndGroups = VaultCollections.newSet();
		if (users != null) {
			for (String userId : users) {
				usersAndGroups.add("user:" + userId);
			}
		}
		if (groups != null) {
			for (String groupId : groups) {
				usersAndGroups.add("group:" + groupId);
			}
		}

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.setMethod(HttpMethod.PUT)
				.setBodyParam(roleName, VpsUtilHelperService.setToString(usersAndGroups,",",false))
				.appendPath(startWorkflowUrl);

		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("startDocumentWorkflow {}", errorMessage);
				})
				.onSuccess(response -> {
					VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
					if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
						successList.add(true);
					}
					else {
						JsonArray errors = apiResponse.getErrors();
						if (errors != null) {
							for (int i = 0; i < errors.getSize(); i++) {
								JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
								String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
								String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
								logService.error("startDocumentWorkflow {}", errorType + " - " + errorMessage);
							}
						}
					}

				})
				.execute();

		return successList.size() > 0;
	}

	/**
	 * Instantiate/Get an integration rule from the IntegrationRuleService
	 *
	 * @param sessionId
	 * @param integrationRuleName
	 * @return
	 */
	public IntegrationRule getIntegrationRule(String sessionId, String integrationRuleName) {
		IntegrationRuleService integrationRuleService = ServiceLocator.locate(IntegrationRuleService.class);

		IntegrationRule integrationRule = null;

		Collection<IntegrationRule> integrationRules = (integrationRuleService.getIntegrationRules(sessionId));

		for (Iterator<IntegrationRule> iterator = integrationRules.iterator(); iterator.hasNext();) {
			IntegrationRule integrationRuleResult = iterator.next();

			String integrationRuleResultName = integrationRuleResult.getName();
			Boolean integrationRuleIsActive = integrationRuleResult.isActive();

			if ((integrationRuleResultName.equalsIgnoreCase(integrationRuleName)) && (integrationRuleIsActive)) {
				return integrationRuleResult;
			}
		}
		return integrationRule;
	}

	/**
	 * Vault-to-Vault integration callback query using Integrationrule mappings.
	 * Query a source Vault for the provided foreign keys via dynamically setting
	 * fields in the query with Integrationrule. Return a list of the queried records
	 *
	 * @param sourceVault the Vault to query
	 * @param targetObjectName the target object to insert/update/delete
	 * @param sourceIdField API name of the foreign key field (ie external_id__v)
	 * @param targetIdField API name of the foreign key field (ie external_id__v)
	 * @param ids value of the foreign key
	 * @param integrationRule IntegrationRule for the mapping
	 * @return list of the queried records
	 */
	public List<Record> v2vHttpQueryIntRules(String sourceVault, String targetObjectName, String sourceIdField, String targetIdField, Set<String> ids, IntegrationRule integrationRule, String apiVersion, String apiConnection) {
		List<Record> records = new VaultCollections().newList();
		RecordService recordService = ServiceLocator.locate(RecordService.class);
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		HttpRequest request = httpService.newHttpRequest(sourceVault);

		// Create a query from the query object/fields for the configured
		String queryObject = integrationRule.getFieldRules().iterator().next().getQueryObject();
		Collection<String> queryFields = integrationRule.getQueryFields(queryObject);

		// Ensure the foreign key id field is always in the query for later mapping (if it's not in Integrationrule)
		queryFields.add(sourceIdField);
		String whereClause = " contains ('" + String.join("','", ids + "')");
		if (whereClause.contains("contains (\'[")) whereClause = whereClause.replace("contains ('[","contains ('");
		if (whereClause.contains("]')")) whereClause = whereClause.replace("]')","')");

		// Map of existing records in the target, used to determine if insert/update
		Map<String,String> existingTargetRecords = getIdMap(targetObjectName, targetIdField, whereClause);

		String query =  "select "
				+ String.join(",", queryFields)
				+ " from " + queryObject
				+ " where " + sourceIdField + whereClause;

		logService.info(query);

		//The configured connection provides the full DNS name.
		//For the path, you only need to append the API endpoint after the DNS.
		//The query endpoint takes a POST where the BODY is the query itself.
		request.setMethod(HttpMethod.POST);
		request.appendPath("/api/" + apiVersion + "/query");
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setHeader("X-VaultAPI-DescribeQuery", "true"); // *** New
		request.setBodyParam("q", query);

		//Send the request the source vault.
		httpService.send(request, HttpResponseBodyValueType.JSONDATA)
				.onSuccess(httpResponse -> {

					JsonData response = httpResponse.getResponseBody();

					if (response.isValidJson()) {
						String responseStatus = response.getJsonObject().getValue("responseStatus", JsonValueType.STRING);

						if (responseStatus.equals("SUCCESS")) {
							JsonArray data = response.getJsonObject().getValue("data", JsonValueType.ARRAY);
							JsonObject queryDescribe = response.getJsonObject().getValue("queryDescribe", JsonValueType.OBJECT);

							logService.info("HTTP Query Request: SUCCESS");

							//Retrieve each record returned from the VQL query.
							for (int i = 0; i < data.getSize();i++) {

								Record rec = null;

								JsonObject queryRecord = data.getValue(i, JsonValueType.OBJECT);

								String sourceId = queryRecord.getValue(sourceIdField, JsonValueType.STRING);
								logService.info("Queried source " + sourceIdField + "=" + sourceId);

								// Determine if target Vault has the record by mapping from external id to "id"
								if (existingTargetRecords.containsKey(sourceId))
									rec = recordService.newRecordWithId(targetObjectName, existingTargetRecords.get(sourceId));
								else
									rec = recordService.newRecord(targetObjectName);

								// Get the resulting values for a row based on the evaluation of the rule against a json query response record
								IntegrationRuleService integrationRuleService = ServiceLocator.locate(IntegrationRuleService.class);

								logService.info("queryDescribe " + queryDescribe.asString());
								logService.info("queryRecord " + queryRecord.asString());

								Collection<FieldRuleResult> fieldRuleResults = integrationRuleService.evaluateFieldRules(integrationRule, targetObjectName, queryDescribe, queryRecord);

								for (FieldRuleResult frr : fieldRuleResults) {
									logService.info("frr " + frr.getTargetField() + "=" + frr.getValue());
									rec.setValue(frr.getTargetField(),frr.getValue());
								}

								records.add(rec);

							}
							data = null;
						}
						response = null;
					}
					else {
						logService.info("v2vHttpUpdate error: Received a non-JSON response.");
					}
				})
				.onError(httpOperationError -> {
					logService.info("v2vHttpUpdate error: httpOperationError.");

				}).execute();

		request = null;

		return records;
	}

	/**
	 * Helper for determining whether the record exists or is new. Returns a map
	 * keyed by foreign key (external id) with value Vault "id" field
	 *
	 * @param object
	 * @param externalIdField
	 * @param whereClause
	 * @return
	 */
	private Map<String,String> getIdMap(String object, String externalIdField, String whereClause) {
		QueryService queryService = ServiceLocator.locate(QueryService.class);
		Map<String,String> map = VaultCollections.newMap();
		LogService logService = ServiceLocator.locate(LogService.class);

		String query = "select id ";
		if (!externalIdField.equalsIgnoreCase("id"))
			query += "," + externalIdField;
		query += " from " + object + " where " + externalIdField + whereClause;

		logService.info("getIdMap query " + query);

		QueryResponse queryResponse = queryService.query(query);
		logService.info("Total Records Returned: " + queryResponse.getResultCount());

		queryResponse.streamResults().forEach(qr -> {
			String externalIdValue = qr.getValue(externalIdField, ValueType.STRING);
			if (externalIdField.equalsIgnoreCase("id"))
				map.put(qr.getValue("id", ValueType.STRING), qr.getValue("id", ValueType.STRING));
			else
				map.put(externalIdValue, qr.getValue("id", ValueType.STRING));
		});

		return map;
	}

	public Boolean updateObject(String objectName, String jsonBody, String apiVersion, String apiConnection)
	{
		LogService logService = ServiceLocator.locate(LogService.class);
		HttpService httpService = ServiceLocator.locate(HttpService.class);
		List<Boolean> success = VaultCollections.newList();

		String objectUpdateURL = String.format(
				URL_UPDATE_OBJECT,
				apiVersion,
				objectName);

		HttpRequest request = httpService.newHttpRequest(apiConnection)
				.setMethod(HttpMethod.PUT)
				.setHeader("Content-Type", "application/json")
				.setBody(jsonBody)
				.appendPath(objectUpdateURL);

		httpService.send(request, HttpResponseBodyValueType.STRING)
				.onError(response -> {
					String errorMessage = "HTTP Status Code: " + response.getHttpResponse().getHttpStatusCode();
					logService.error("updateObjects {}", errorMessage);
				})
				.onSuccess(response -> {
					VpsAPIResponse apiResponse = new VpsAPIResponse(response.getResponseBody());
					if (apiResponse.getResponseStatus().equals(RESPONSESTATUS_SUCCESS)) {
						success.add(true);
					}
					else {
						JsonArray errors = apiResponse.getErrors();
						if (errors != null) {
							for (int i = 0; i < errors.getSize(); i++) {
								JsonObject error = errors.getValue(i, JsonValueType.OBJECT);
								String errorType = error.getValue(APIFIELD_ERROR_TYPE, JsonValueType.STRING);
								String errorMessage = error.getValue(APIFIELD_ERROR_MESSAGE, JsonValueType.STRING);
								logService.error("updateObjects {}", errorType + " - " + errorMessage);
							}
						}
					}

				})
				.execute();


		return success.get(0);
	}

	/**
	 * Method to simulate a delay. Queries the current user.
	 */
	protected void sleep() {
		LogService logService = ServiceLocator.locate(LogService.class);
		try {
			RuntimeService runtimeService = ServiceLocator.locate(RuntimeService.class);
			runtimeService.sleep(1000);
		}
		catch (VaultRuntimeException exception) {
			logService.error("VpsRecordHelper.sleep - {}", exception.getMessage());
		}
	}
}