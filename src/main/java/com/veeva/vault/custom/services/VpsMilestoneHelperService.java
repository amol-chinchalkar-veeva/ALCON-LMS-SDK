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

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsMilestoneHelperService extends UserDefinedService {
	public String getMilestoneLevel(Record milestoneRecord);
	public List<String> getMilestonesForStudy(String studyId, String milestoneType, String milestoneState, String filter);
	public List<String> getMilestonesForStudyCountry(String studyCountryId, String milestoneType, String milestoneState, String filter);
	public List<String> getMilestonesForSite(String siteId, String milestoneType, String milestoneState);
	public List<String> getMilestoneEDLs(String milestoneId);
	public void createMilestoneItemsForMilestone(String milestoneId, List<String> edlIds);
	public List<String> getMilestoneMilestoneItems(String milestoneId);
	public List<Map<String,Object>> getClinicalUserTasksForMilestoneTypeFromStudy(String studyId, String milestoneType, Map<String, String> fields);
	public void createClinicalUserTasksForMilestone(String milestone, String countryId, String siteId, List<Map<String, Object>> clinicalUserTasks);
	public void copyMilestoneDependcies(String fromMilestoneId, String toMilestoneId);
	public void deleteMilestoneDependencies(String milestoneId);
	public String getMilestoneState(String milestoneId);
}