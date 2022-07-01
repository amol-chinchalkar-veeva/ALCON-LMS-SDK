/*
 * --------------------------------------------------------------------
 * UDC:         VpsMatchedDocumentRecord
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 *---------------------------------------------------------------------
 * Description: Matched Document
 *---------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.model;

import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;

import java.math.BigDecimal;

@UserDefinedClassInfo()
public class VpsWorkflowRecord {
	BigDecimal taskAssignee;
	String taskAssigneeName;
	String taskAssignmentDate;
	String taskCancelationDate;
	String taskCapacity;
	String taskComment;
	String taskCompletionDate;
	String taskCreationDate;
	BigDecimal taskDelegate;
	String taskDocumentCapacity;
	BigDecimal taskDocumentMajorVersionNumber;
	BigDecimal taskDocumentMinorVersionNumber;
	String taskDueDate;
	BigDecimal taskDuration;
	BigDecimal taskId;
	String taskLastModifiedDate;
	String taskName;
	String taskQueueGroup;
	String taskStatus;
	String taskVerdict;
	String workflowCancelationDate;
	String workflowCompletionDate;
	String workflowDocumentId;
	BigDecimal workflowDocumentMajorVersionNumber;
	BigDecimal workflowDocumentMinorVersionNumber;
	String workflowDueDate;
	BigDecimal workflowDuration;
	BigDecimal workflowId;
	String workflowInitiator;
	String workflowInitiatorName;
	String workflowName;
	String workflowProcessInstanceGroup;
	String workflowProcessVersion;
	String workflowStartDate;
	String workflowStatus;
	String workflowType;

	public VpsWorkflowRecord() {
		super();
	}

	public BigDecimal getTaskAssignee() {
		return taskAssignee;
	}

	public void setTaskAssignee(BigDecimal taskAssignee) {
		this.taskAssignee = taskAssignee;
	}

	public String getTaskAssigneeName() {
		return taskAssigneeName;
	}

	public void setTaskAssigneeName(String taskAssigneeName) {
		this.taskAssigneeName = taskAssigneeName;
	}

	public String getTaskAssignmentDate() {
		return taskAssignmentDate;
	}

	public void setTaskAssignmentDate(String taskAssignmentDate) {
		this.taskAssignmentDate = taskAssignmentDate;
	}

	public String getTaskCancelationDate() {
		return taskCancelationDate;
	}

	public void setTaskCancelationDate(String taskCancelationDate) {
		this.taskCancelationDate = taskCancelationDate;
	}

	public String getTaskCapacity() {
		return taskCapacity;
	}

	public void setTaskCapacity(String taskCapacity) {
		this.taskCapacity = taskCapacity;
	}

	public String getTaskComment() {
		return taskComment;
	}

	public void setTaskComment(String taskComment) {
		this.taskComment = taskComment;
	}

	public String getTaskCompletionDate() {
		return taskCompletionDate;
	}

	public void setTaskCompletionDate(String taskCompletionDate) {
		this.taskCompletionDate = taskCompletionDate;
	}

	public String getTaskCreationDate() {
		return taskCreationDate;
	}

	public void setTaskCreationDate(String taskCreationDate) {
		this.taskCreationDate = taskCreationDate;
	}

	public BigDecimal getTaskDelegate() {
		return taskDelegate;
	}

	public void setTaskDelegate(BigDecimal taskDelegate) {
		this.taskDelegate = taskDelegate;
	}

	public String getTaskDocumentCapacity() {
		return taskDocumentCapacity;
	}

	public void setTaskDocumentCapacity(String taskDocumentCapacity) {
		this.taskDocumentCapacity = taskDocumentCapacity;
	}

	public BigDecimal getTaskDocumentMajorVersionNumber() {
		return taskDocumentMajorVersionNumber;
	}

	public void setTaskDocumentMajorVersionNumber(BigDecimal taskDocumentMajorVersionNumber) {
		this.taskDocumentMajorVersionNumber = taskDocumentMajorVersionNumber;
	}

	public BigDecimal getTaskDocumentMinorVersionNumber() {
		return taskDocumentMinorVersionNumber;
	}

	public void setTaskDocumentMinorVersionNumber(BigDecimal taskDocumentMinorVersionNumber) {
		this.taskDocumentMinorVersionNumber = taskDocumentMinorVersionNumber;
	}

	public String getTaskDueDate() {
		return taskDueDate;
	}

	public void setTaskDueDate(String taskDueDate) {
		this.taskDueDate = taskDueDate;
	}

	public BigDecimal getTaskDuration() {
		return taskDuration;
	}

	public void setTaskDuration(BigDecimal taskDuration) {
		this.taskDuration = taskDuration;
	}

	public BigDecimal getTaskId() {
		return taskId;
	}

	public void setTaskId(BigDecimal taskId) {
		this.taskId = taskId;
	}

	public String getTaskLastModifiedDate() {
		return taskLastModifiedDate;
	}

	public void setTaskLastModifiedDate(String taskLastModifiedDate) {
		this.taskLastModifiedDate = taskLastModifiedDate;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskQueueGroup() {
		return taskQueueGroup;
	}

	public void setTaskQueueGroup(String taskQueueGroup) {
		this.taskQueueGroup = taskQueueGroup;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public String getTaskVerdict() {
		return taskVerdict;
	}

	public void setTaskVerdict(String taskVerdict) {
		this.taskVerdict = taskVerdict;
	}

	public String getWorkflowCancelationDate() {
		return workflowCancelationDate;
	}

	public void setWorkflowCancelationDate(String workflowCancelationDate) {
		this.workflowCancelationDate = workflowCancelationDate;
	}

	public String getWorkflowCompletionDate() {
		return workflowCompletionDate;
	}

	public void setWorkflowCompletionDate(String workflowCompletionDate) {
		this.workflowCompletionDate = workflowCompletionDate;
	}

	public String getWorkflowDocumentId() {
		return workflowDocumentId;
	}

	public void setWorkflowDocumentId(String workflowDocumentId) {
		this.workflowDocumentId = workflowDocumentId;
	}

	public BigDecimal getWorkflowDocumentMajorVersionNumber() {
		return workflowDocumentMajorVersionNumber;
	}

	public void setWorkflowDocumentMajorVersionNumber(BigDecimal workflowDocumentMajorVersionNumber) {
		this.workflowDocumentMajorVersionNumber = workflowDocumentMajorVersionNumber;
	}

	public BigDecimal getWorkflowDocumentMinorVersionNumber() {
		return workflowDocumentMinorVersionNumber;
	}

	public void setWorkflowDocumentMinorVersionNumber(BigDecimal workflowDocumentMinorVersionNumber) {
		this.workflowDocumentMinorVersionNumber = workflowDocumentMinorVersionNumber;
	}

	public String getWorkflowDueDate() {
		return workflowDueDate;
	}

	public void setWorkflowDueDate(String workflowDueDate) {
		this.workflowDueDate = workflowDueDate;
	}

	public BigDecimal getWorkflowDuration() {
		return workflowDuration;
	}

	public void setWorkflowDuration(BigDecimal workflowDuration) {
		this.workflowDuration = workflowDuration;
	}

	public BigDecimal getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(BigDecimal workflowId) {
		this.workflowId = workflowId;
	}

	public String getWorkflowInitiator() {
		return workflowInitiator;
	}

	public void setWorkflowInitiator(String workflowInitiator) {
		this.workflowInitiator = workflowInitiator;
	}

	public String getWorkflowInitiatorName() {
		return workflowInitiatorName;
	}

	public void setWorkflowInitiatorName(String workflowInitiatorName) {
		this.workflowInitiatorName = workflowInitiatorName;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getWorkflowProcessInstanceGroup() {
		return workflowProcessInstanceGroup;
	}

	public void setWorkflowProcessInstanceGroup(String workflowProcessInstanceGroup) {
		this.workflowProcessInstanceGroup = workflowProcessInstanceGroup;
	}

	public String getWorkflowProcessVersion() {
		return workflowProcessVersion;
	}

	public void setWorkflowProcessVersion(String workflowProcessVersion) {
		this.workflowProcessVersion = workflowProcessVersion;
	}

	public String getWorkflowStartDate() {
		return workflowStartDate;
	}

	public void setWorkflowStartDate(String workflowStartDate) {
		this.workflowStartDate = workflowStartDate;
	}

	public String getWorkflowStatus() {
		return workflowStatus;
	}

	public void setWorkflowStatus(String workflowStatus) {
		this.workflowStatus = workflowStatus;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}
}
