/*
 * --------------------------------------------------------------------
 * UDC:         VpsWorkflowList
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 *---------------------------------------------------------------------
 * Description: Queries workflow endpoint via HTTPCallout
 *---------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.classes;

import com.veeva.vault.custom.classes.api.VpsAPIClient;
import com.veeva.vault.custom.classes.api.VpsVQLRequest;
import com.veeva.vault.custom.classes.api.VpsVQLResponse;
import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.json.JsonArray;
import com.veeva.vault.sdk.api.json.JsonObject;
import com.veeva.vault.sdk.api.json.JsonValueType;

import java.util.List;

@UserDefinedClassInfo()
public class VpsWorkflowList extends VpsBaseHelper {

	private static final String CONNECTION = "connection_api_name_here";
	private static final String OBJFIELD_TASK_ASSIGNEE = "task_assignee__v";
	private static final String OBJFIELD_TASK_ASSIGNEE_NAME = "task_assignee_name__v";
	private static final String OBJFIELD_TASK_ASSIGNMENTDATE = "task_assignmentDate__v";
	private static final String OBJFIELD_TASK_CANCELATIONDATE = "task_cancelationDate__v";
	private static final String OBJFIELD_TASK_CAPACITY = "task_capacity__v";
	private static final String OBJFIELD_TASK_COMMENT = "task_comment__v";
	private static final String OBJFIELD_TASK_COMPLETIONDATE = "task_completionDate__v";
	private static final String OBJFIELD_TASK_CREATIONDATE = "task_creationDate__v";
	private static final String OBJFIELD_TASK_DELEGATE = "task_delegate__v";
	private static final String OBJFIELD_TASK_DOCUMENT_CAPACITY = "task_document_capacity__v";
	private static final String OBJFIELD_TASK_DOCUMENT_MAJOR_VERSION_NUMBER = "task_document_major_version_number__v";
	private static final String OBJFIELD_TASK_DOCUMENT_MINOR_VERSION_NUMBER = "task_document_minor_version_number__v";
	private static final String OBJFIELD_TASK_DUEDATE = "task_dueDate__v";
	private static final String OBJFIELD_TASK_DURATION = "task_duration__v";
	private static final String OBJFIELD_TASK_ID = "task_id__v";
	private static final String OBJFIELD_TASK_LAST_MODIFIED_DATE = "task_last_modified_date__v";
	private static final String OBJFIELD_TASK_NAME = "task_name__v";
	private static final String OBJFIELD_TASK_QUEUEGROUP = "task_queueGroup__v";
	private static final String OBJFIELD_TASK_STATUS = "task_status__v";
	private static final String OBJFIELD_TASK_VERDICT = "task_verdict__v";
	private static final String OBJFIELD_WORKFLOW_CANCELATIONDATE = "workflow_cancelationDate__v";
	private static final String OBJFIELD_WORKFLOW_COMPLETIONDATE = "workflow_completionDate__v";
	private static final String OBJFIELD_WORKFLOW_DOCUMENT_ID = "workflow_document_id__v";
	private static final String OBJFIELD_WORKFLOW_DOCUMENT_MAJOR_VERSION_NUMBER = "workflow_document_major_version_number__v";
	private static final String OBJFIELD_WORKFLOW_DOCUMENT_MINOR_VERSION_NUMBER = "workflow_document_minor_version_number__v";
	private static final String OBJFIELD_WORKFLOW_DUEDATE = "workflow_dueDate__v";
	private static final String OBJFIELD_WORKFLOW_DURATION = "workflow_duration__v";
	private static final String OBJFIELD_WORKFLOW_ID = "workflow_id__v";
	private static final String OBJFIELD_WORKFLOW_INITIATOR = "workflow_initiator__v";
	private static final String OBJFIELD_WORKFLOW_INITIATOR_NAME = "workflow_initiator_name__v";
	private static final String OBJFIELD_WORKFLOW_NAME = "workflow_name__v";
	private static final String OBJFIELD_WORKFLOW_PROCESS_INSTANCE_GROUP = "workflow_process_instance_group__v";
	private static final String OBJFIELD_WORKFLOW_PROCESS_VERSION = "workflow_process_version__v";
	private static final String OBJFIELD_WORKFLOW_STARTDATE = "workflow_startDate__v";
	private static final String OBJFIELD_WORKFLOW_STATUS = "workflow_status__v";
	private static final String OBJFIELD_WORKFLOW_TYPE = "workflow_type__v";
	private static final String OBJECT_WORKFLOWS = "workflows";

	private List<VpsWorkflowRecord> items = VaultCollections.newList();

	public VpsWorkflowList() {
		super();
	}

	public List<VpsWorkflowRecord> getItems() {
		return  items;
	}

	public void load(String whereClause) {
		items.clear();
		VpsVQLRequest vqlRequest = new VpsVQLRequest();
		vqlRequest.appendVQL("select " + OBJFIELD_TASK_ASSIGNEE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_ASSIGNEE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_ASSIGNEE_NAME);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_ASSIGNMENTDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_CANCELATIONDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_CAPACITY);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_COMMENT);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_COMPLETIONDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_CREATIONDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DELEGATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DOCUMENT_CAPACITY);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DOCUMENT_MAJOR_VERSION_NUMBER);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DOCUMENT_MINOR_VERSION_NUMBER);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DUEDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_DURATION);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_ID);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_LAST_MODIFIED_DATE);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_NAME);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_QUEUEGROUP);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_STATUS);
		vqlRequest.appendVQL(", " + OBJFIELD_TASK_VERDICT);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_CANCELATIONDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_COMPLETIONDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_DOCUMENT_ID);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_DOCUMENT_MAJOR_VERSION_NUMBER);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_DOCUMENT_MINOR_VERSION_NUMBER);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_DUEDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_DURATION);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_ID);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_INITIATOR);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_INITIATOR_NAME);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_NAME);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_PROCESS_INSTANCE_GROUP);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_PROCESS_VERSION);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_STARTDATE);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_STATUS);
		vqlRequest.appendVQL(", " + OBJFIELD_WORKFLOW_TYPE);

		vqlRequest.appendVQL(" from " + OBJECT_WORKFLOWS);
		vqlRequest.appendVQL(" " + whereClause);
		VpsAPIClient apiClient = new VpsAPIClient(CONNECTION);
		VpsVQLResponse vqlResponse = apiClient.runVQL(vqlRequest);
		for (String error : apiClient.getErrorList()) {
			getErrorList().add(error);
		}

		JsonArray data = vqlResponse.getData();
		for (int i = 0; i< data.getSize(); i++) {
			JsonObject workflowJson = data.getValue(i, JsonValueType.OBJECT);
			VpsWorkflowRecord workflowRecord = new VpsWorkflowRecord();
			workflowRecord.setTaskAssignee(workflowJson.getValue(OBJFIELD_TASK_ASSIGNEE, JsonValueType.NUMBER));
			workflowRecord.setTaskAssigneeName(workflowJson.getValue(OBJFIELD_TASK_ASSIGNEE_NAME, JsonValueType.STRING));
			workflowRecord.setTaskAssignmentDate(workflowJson.getValue(OBJFIELD_TASK_ASSIGNMENTDATE, JsonValueType.STRING));
			workflowRecord.setTaskCancelationDate(workflowJson.getValue(OBJFIELD_TASK_CANCELATIONDATE, JsonValueType.STRING));
			workflowRecord.setTaskCapacity(workflowJson.getValue(OBJFIELD_TASK_CAPACITY, JsonValueType.STRING));
			workflowRecord.setTaskComment(workflowJson.getValue(OBJFIELD_TASK_COMMENT, JsonValueType.STRING));
			workflowRecord.setTaskCompletionDate(workflowJson.getValue(OBJFIELD_TASK_COMPLETIONDATE, JsonValueType.STRING));
			workflowRecord.setTaskCreationDate(workflowJson.getValue(OBJFIELD_TASK_CREATIONDATE, JsonValueType.STRING));
			workflowRecord.setTaskDelegate(workflowJson.getValue(OBJFIELD_TASK_DELEGATE, JsonValueType.NUMBER));
			workflowRecord.setTaskDocumentCapacity(workflowJson.getValue(OBJFIELD_TASK_DOCUMENT_CAPACITY, JsonValueType.STRING));
			workflowRecord.setTaskDocumentMajorVersionNumber(workflowJson.getValue(OBJFIELD_TASK_DOCUMENT_MAJOR_VERSION_NUMBER, JsonValueType.NUMBER));
			workflowRecord.setTaskDocumentMinorVersionNumber(workflowJson.getValue(OBJFIELD_TASK_DOCUMENT_MINOR_VERSION_NUMBER, JsonValueType.NUMBER));
			workflowRecord.setTaskDueDate(workflowJson.getValue(OBJFIELD_TASK_DUEDATE, JsonValueType.STRING));
			workflowRecord.setTaskDuration(workflowJson.getValue(OBJFIELD_TASK_DURATION, JsonValueType.NUMBER));
			workflowRecord.setTaskId(workflowJson.getValue(OBJFIELD_TASK_ID, JsonValueType.NUMBER));
			workflowRecord.setTaskLastModifiedDate(workflowJson.getValue(OBJFIELD_TASK_LAST_MODIFIED_DATE, JsonValueType.STRING));
			workflowRecord.setTaskName(workflowJson.getValue(OBJFIELD_TASK_NAME, JsonValueType.STRING));
			workflowRecord.setTaskQueueGroup(workflowJson.getValue(OBJFIELD_TASK_QUEUEGROUP, JsonValueType.STRING));
			workflowRecord.setTaskStatus(workflowJson.getValue(OBJFIELD_TASK_STATUS, JsonValueType.STRING));
			workflowRecord.setTaskVerdict(workflowJson.getValue(OBJFIELD_TASK_VERDICT, JsonValueType.STRING));
			workflowRecord.setWorkflowCancelationDate(workflowJson.getValue(OBJFIELD_WORKFLOW_CANCELATIONDATE, JsonValueType.STRING));
			workflowRecord.setTaskCompletionDate(workflowJson.getValue(OBJFIELD_WORKFLOW_COMPLETIONDATE, JsonValueType.STRING));
			workflowRecord.setWorkflowDocumentId(workflowJson.getValue(OBJFIELD_WORKFLOW_DOCUMENT_ID, JsonValueType.STRING));
			workflowRecord.setWorkflowDocumentMajorVersionNumber(workflowJson.getValue(OBJFIELD_WORKFLOW_DOCUMENT_MAJOR_VERSION_NUMBER, JsonValueType.NUMBER));
			workflowRecord.setWorkflowDocumentMinorVersionNumber(workflowJson.getValue(OBJFIELD_WORKFLOW_DOCUMENT_MINOR_VERSION_NUMBER, JsonValueType.NUMBER));
			workflowRecord.setWorkflowDueDate(workflowJson.getValue(OBJFIELD_WORKFLOW_DUEDATE, JsonValueType.STRING));
			workflowRecord.setWorkflowDuration(workflowJson.getValue(OBJFIELD_WORKFLOW_DURATION, JsonValueType.NUMBER));
			workflowRecord.setWorkflowId(workflowJson.getValue(OBJFIELD_WORKFLOW_ID, JsonValueType.NUMBER));
			workflowRecord.setWorkflowInitiator(workflowJson.getValue(OBJFIELD_WORKFLOW_INITIATOR, JsonValueType.STRING));
			workflowRecord.setWorkflowInitiatorName(workflowJson.getValue(OBJFIELD_WORKFLOW_INITIATOR_NAME, JsonValueType.STRING));
			workflowRecord.setWorkflowName(workflowJson.getValue(OBJFIELD_WORKFLOW_NAME, JsonValueType.STRING));
			workflowRecord.setWorkflowProcessInstanceGroup(workflowJson.getValue(OBJFIELD_WORKFLOW_PROCESS_INSTANCE_GROUP, JsonValueType.STRING));
			workflowRecord.setWorkflowProcessVersion(workflowJson.getValue(OBJFIELD_WORKFLOW_PROCESS_VERSION, JsonValueType.STRING));
			workflowRecord.setWorkflowStartDate(workflowJson.getValue(OBJFIELD_WORKFLOW_STARTDATE, JsonValueType.STRING));
			workflowRecord.setWorkflowStatus(workflowJson.getValue(OBJFIELD_WORKFLOW_STATUS, JsonValueType.STRING));
			workflowRecord.setWorkflowType(workflowJson.getValue(OBJFIELD_WORKFLOW_TYPE, JsonValueType.STRING));

			items.add(workflowRecord);
		}
	}
}
