/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsMilestoneHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2021-02-19
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
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

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsMilestoneHelperImpl implements VpsMilestoneHelperService {
	private static final String OBJFIELD_MILESTONE_TEMPLATE = "milestone_template__v";
	private static final String OBJECT_MILESTONE_TEMPLATE = "milestone_template__v";
	private static final String OBJFIELD_LEVEL = "level__v";
	private static final String OBJFIELD_ID = "id";
	private static final String OBJECT_MILESTONE = "milestone__v";
	private static final String OBJFIELD_SITE = "site__v";
	private static final String OBJFIELD_STUDY = "study__v";
	private static final String OBJFIELD_STUDY_COUNTRY = "study_country__v";
	private static final String OBJFIELD_MILESTONE_TYPE = "milestone_type__v";
	private static final String OBJFIELD_STATE = "state__v";
	private static final String OBJECT_MILESTONE_ITEM = "milestone_item__v";
	private static final String OBJFIELD_EDL_ITEM = "edl_item__v";
	private static final String OBJFIELD_MILESTONE = "milestone__v";
	private static final String OBJECT_CLINICAL_USER_TASK = "clinical_user_task__clin";
	private static final String OBJFIELD_OBJECT_TYPE = "object_type__v";
	private static final String OBJFIELD_CLINICAL_USER_TASK_STUDY = "study__clin";
	private static final String OBJFIELD_API_NAME = "api_name__v";
	private static final String STUDY_SITE_TASKS_OBJECT_TYPE_NAME = "study_site_task__clin";
	private static final String OBJFIELD_CLINICAL_USER_TASK_MILESTONE_TYPE = "milestone_type__c";
	private static final String OBJFIELD_CLINICAL_USER_TASK_SITE = "site__clin";
	private static final String OBJFIELD_CLINICAL_USER_TASK_STUDY_COUNTRY = "study_country__clin";
	private static final String OBJECT_MILESTONE_DEPENDENCY = "milestone_dependency__v";
	private static final String OBJFIELD_NEXT_MILESTONE = "next_milestone__v";
	private static final String OBJFIELD_PREVIOUS_MILESTONE = "previous_milestone__v";
	private static final String OBJFIELD_NAME = "name__v";


	public String getMilestoneLevel(Record milestoneRecord)
	{
		VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);
		List<String> level = VaultCollections.newList();
		String milestoneTemplateId = milestoneRecord.getValue(OBJFIELD_MILESTONE_TEMPLATE, ValueType.STRING);
		QueryResponse queryResponse = vqlService.query(
				SELECT, vqlService.appendFields(
						OBJFIELD_LEVEL
				),
				FROM, OBJECT_MILESTONE_TEMPLATE,
				WHERE,  OBJFIELD_ID + " = '" + milestoneTemplateId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String milestoneTemplateLevel = queryResult.getValue(OBJFIELD_LEVEL, ValueType.PICKLIST_VALUES).get(0);
			level.add(milestoneTemplateLevel);
		});

		return level.get(0);
	}

	//select id, milestone_type__v from milestone__v where study__v = '0ST000000009006' and milestone_type__v = 'site_ready_to_enroll__c'
	public List<String> getMilestonesForStudy(String studyId, String milestoneType, String milestoneState, String filter)
	{
		List<String> milestones = VaultCollections.newList();
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(OBJFIELD_STUDY);
		whereBuilder.append(" = '");
		whereBuilder.append(studyId);
		whereBuilder.append("'");
		if (!milestoneType.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_MILESTONE_TYPE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneType);
			whereBuilder.append("'");

		}
		if (!milestoneState.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_STATE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneState);
			whereBuilder.append("'");

		}

		if (!filter.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(filter);
		}

		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_MILESTONE,
				WHERE, whereBuilder.toString()
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			milestones.add(id);
		});

		return milestones;
	}

	public List<String> getMilestonesForStudyCountry(String studyCountryId, String milestoneType, String milestoneState, String filter)
	{
		List<String> milestones = VaultCollections.newList();
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(OBJFIELD_STUDY_COUNTRY);
		whereBuilder.append(" = '");
		whereBuilder.append(studyCountryId);
		whereBuilder.append("'");
		if (!milestoneType.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_MILESTONE_TYPE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneType);
			whereBuilder.append("'");

		}
		if (!milestoneState.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_STATE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneState);
			whereBuilder.append("'");

		}

		if (!filter.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(filter);
		}

		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_MILESTONE,
				WHERE, whereBuilder.toString()
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			milestones.add(id);
		});

		return milestones;
	}

	public List<String> getMilestonesForSite(String siteId, String milestoneType, String milestoneState)
	{
		List<String> milestones = VaultCollections.newList();
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		StringBuilder whereBuilder = new StringBuilder();
		whereBuilder.append(OBJFIELD_SITE);
		whereBuilder.append(" = '");
		whereBuilder.append(siteId);
		whereBuilder.append("'");
		if (!milestoneType.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_MILESTONE_TYPE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneType);
			whereBuilder.append("'");

		}
		if (!milestoneState.isEmpty())
		{
			whereBuilder.append(" and ");
			whereBuilder.append(OBJFIELD_STATE);
			whereBuilder.append(" = '");
			whereBuilder.append(milestoneState);
			whereBuilder.append("'");

		}
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_MILESTONE,
				WHERE, whereBuilder.toString()
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			milestones.add(id);
		});

		return milestones;
	}

	public List<String> getMilestoneEDLs(String milestoneId)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		List<String> edls = VaultCollections.newList();
		//select id, name__v, milestone__v, edl_item__v from milestone_item__v where milestone__v = '0MI00000000F252'

		QueryResponse queryResponse = vpsVQLService.query(
			SELECT, vpsVQLService.appendFields(OBJFIELD_EDL_ITEM),
			FROM, OBJECT_MILESTONE_ITEM,
			WHERE, OBJFIELD_MILESTONE + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String edlId = queryResult.getValue(OBJFIELD_EDL_ITEM, ValueType.STRING);
			edls.add(edlId);
		});

		return edls;
	}

	public List<String> getMilestoneMilestoneItems(String milestoneId)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		List<String> milestoneItems = VaultCollections.newList();
		//select id, name__v, milestone__v, edl_item__v from milestone_item__v where milestone__v = '0MI00000000F252'

		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_MILESTONE_ITEM,
				WHERE, OBJFIELD_MILESTONE + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String edlId = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			milestoneItems.add(edlId);
		});

		return milestoneItems;
	}

	public void createMilestoneItemsForMilestone(String milestoneId, List<String> edlIds)
	{
		List<Record> newMilestoneItems = VaultCollections.newList();
		RecordService recordService = ServiceLocator.locate(RecordService.class);
		VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
		for (String edlId : edlIds) {
			Record newMilestoneItem = recordService.newRecord(OBJECT_MILESTONE_ITEM);
			newMilestoneItem.setValue(OBJFIELD_EDL_ITEM, edlId);
			newMilestoneItem.setValue(OBJFIELD_MILESTONE, milestoneId);

			newMilestoneItems.add(newMilestoneItem);
		}

		vpsRecordHelperService.saveRecordsInBatch(newMilestoneItems, "Failed to create new milestone items");

	}

	public List<Map<String,Object>> getClinicalUserTasksForMilestoneTypeFromStudy(String studyId, String milestoneType, Map<String, String> fields)
	{
		List<Map<String,Object>> clinicalUserTasks = VaultCollections.newList();

		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, String.join(", ", fields.keySet()),
				FROM, OBJECT_CLINICAL_USER_TASK,
				WHERE, OBJFIELD_CLINICAL_USER_TASK_STUDY + " = '" + studyId + "' and " + OBJFIELD_CLINICAL_USER_TASK_MILESTONE_TYPE + " = '" + milestoneType + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			Map<String, Object> clinicalUserTask = VaultCollections.newMap();
			for (Map.Entry<String, String> field : fields.entrySet()) {
				String fieldType = field.getValue();
				Object value = null;
				if (fieldType.equalsIgnoreCase("string"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.STRING);
				}
				if (fieldType.equalsIgnoreCase("picklist"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.PICKLIST_VALUES);
				}
				if (fieldType.equalsIgnoreCase("number"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.NUMBER);
				}
				if (fieldType.equalsIgnoreCase("boolean"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.BOOLEAN);
				}
				if (fieldType.equalsIgnoreCase("date"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.DATE);
				}
				if (fieldType.equalsIgnoreCase("datetime"))
				{
					value = queryResult.getValue(field.getKey(), ValueType.DATETIME);
				}

				clinicalUserTask.put(field.getKey(), value);
			}
			clinicalUserTasks.add(clinicalUserTask);
		});

		return clinicalUserTasks;
	}

	public void createClinicalUserTasksForMilestone(String milestone, String countryId, String siteId, List<Map<String, Object>> clinicalUserTasks)
	{
		RecordService recordService = ServiceLocator.locate(RecordService.class);
		VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);

		List<Record> clinicalUserTaskRecords = VaultCollections.newList();

		for (Map<String, Object> clinicalUserTaskData : clinicalUserTasks) {
			Record clinicalUserTask = recordService.newRecord(OBJECT_CLINICAL_USER_TASK);
			for (Map.Entry<String, Object> field : clinicalUserTaskData.entrySet()) {
				clinicalUserTask.setValue(field.getKey(), field.getValue());
			}
			clinicalUserTask.setValue(OBJFIELD_MILESTONE, milestone);
			clinicalUserTask.setValue(OBJFIELD_CLINICAL_USER_TASK_STUDY_COUNTRY, countryId);
			clinicalUserTask.setValue(OBJFIELD_CLINICAL_USER_TASK_SITE, siteId);
			String objectTypeId = vpsRecordHelperService.getObjectTypeId(STUDY_SITE_TASKS_OBJECT_TYPE_NAME, OBJECT_CLINICAL_USER_TASK);
			clinicalUserTask.setValue(OBJFIELD_OBJECT_TYPE, objectTypeId);
			clinicalUserTaskRecords.add(clinicalUserTask);


		}

		vpsRecordHelperService.saveRecordsInBatch(clinicalUserTaskRecords, "Failed to create clinical user tasks for milestone: " + milestone);
	}

	public void copyMilestoneDependcies(String fromMilestoneId, String toMilestoneId)
	{
			//get the depedencies for the from milestone
		List<Map<String, String>> previousMilestones = getPreviousMilestones(fromMilestoneId);
		List<Map<String, String>> nextMilestones = getNextMilestones(fromMilestoneId);

		for (Map<String, String> previousMilestone : previousMilestones) {
			createMilestoneDependency(previousMilestone.get(OBJFIELD_ID), toMilestoneId, previousMilestone.get(OBJFIELD_NAME));
		}

		for (Map<String, String> nextMilestone : nextMilestones) {
			createMilestoneDependency(toMilestoneId, nextMilestone.get(OBJFIELD_ID), nextMilestone.get(OBJFIELD_NAME));
		}
	}

	private void createMilestoneDependency(String previousMilestoneId, String nextMilestoneId, String name)
	{
		RecordService recordService = ServiceLocator.locate(RecordService.class);
		VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);

		Record newDependency = recordService.newRecord(OBJECT_MILESTONE_DEPENDENCY);
		newDependency.setValue(OBJFIELD_NEXT_MILESTONE, nextMilestoneId);
		newDependency.setValue(OBJFIELD_PREVIOUS_MILESTONE, previousMilestoneId);
		newDependency.setValue(OBJFIELD_NAME, name);

		vpsRecordHelperService.saveRecords(VaultCollections.asList(newDependency), "Failed to save milestone dependency", true);
	}

	private List<Map<String, String>> getPreviousMilestones(String milestoneId)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		List<Map<String, String>> milestones = VaultCollections.newList();

		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_PREVIOUS_MILESTONE, OBJFIELD_NAME),
				FROM, OBJECT_MILESTONE_DEPENDENCY,
				WHERE, OBJFIELD_NEXT_MILESTONE + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_PREVIOUS_MILESTONE, ValueType.STRING);
			String name = queryResult.getValue(OBJFIELD_NAME, ValueType.STRING);
			Map<String, String> milestoneData = VaultCollections.newMap();
			milestoneData.put(OBJFIELD_ID, id);
			milestoneData.put(OBJFIELD_NAME, name);
			milestones.add(milestoneData);
		});

		return milestones;
	}

	private List<Map<String, String>> getNextMilestones(String milestoneId)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		List<Map<String, String>> milestones = VaultCollections.newList();

		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_NEXT_MILESTONE, OBJFIELD_NAME),
				FROM, OBJECT_MILESTONE_DEPENDENCY,
				WHERE, OBJFIELD_PREVIOUS_MILESTONE + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_NEXT_MILESTONE, ValueType.STRING);
			String name = queryResult.getValue(OBJFIELD_NAME, ValueType.STRING);
			Map<String, String> milestoneData = VaultCollections.newMap();
			milestoneData.put(OBJFIELD_ID, id);
			milestoneData.put(OBJFIELD_NAME, name);
			milestones.add(milestoneData);
		});
		return milestones;
	}

	public void deleteMilestoneDependencies(String milestoneId)
	{
		VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
		LogService logService = ServiceLocator.locate(LogService.class);
		logService.debug("Start deleteMilestoneDependencies");
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		RecordService recordService = ServiceLocator.locate(RecordService.class);

		List<Record> milestoneDepencies = VaultCollections.newList();

		QueryResponse queryResponse = vpsVQLService.query(
			SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
			FROM, OBJECT_MILESTONE_DEPENDENCY,
			WHERE, OBJFIELD_NEXT_MILESTONE + " ='" + milestoneId + "' OR " + OBJFIELD_PREVIOUS_MILESTONE + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String id = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			Record dependency = recordService.newRecordWithId(OBJECT_MILESTONE_DEPENDENCY, id);
			milestoneDepencies.add(dependency);
		});

		vpsRecordHelperService.deleteRecords(milestoneDepencies, "Failed to delete milestone dependencies.");
	}

	public String getMilestoneState(String milestoneId)
	{
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		LogService logService = ServiceLocator.locate(LogService.class);

		List<String> stateList = VaultCollections.newList();
		String state = "";
		logService.debug("Start getMilestoneState");
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_STATE),
				FROM, OBJECT_MILESTONE,
				WHERE, OBJFIELD_ID + " = '" + milestoneId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String curState = queryResult.getValue(OBJFIELD_STATE, ValueType.STRING);
			stateList.add(curState);
		});


		if (!stateList.isEmpty())
		{
			state = stateList.get(0);
		}

		logService.debug("state: " + state);
		return state;
	}




}