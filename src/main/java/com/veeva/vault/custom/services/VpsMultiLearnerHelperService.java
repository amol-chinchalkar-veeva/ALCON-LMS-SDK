/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsMultiLearnerHelperService
 * Author:				Amol Chinchalkar @ Veeva
 * Date:				2022-07-19
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2022 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsMultiLearnerHelperService extends UserDefinedService {
    Map<String, String> getValidLearnersForAssignment(List<String> externalPersonsList, String trainingRequirement);

    List<String> addTrainingAssignmentsInFacilitiatedTraining(Map<String, String> trainingAssignments, String newFacilitatedRequestID);

    void completeFacilitatedTrainingRequest(String facilitatedTrainingID, String actionName);

    List<String> getLearnerInfo(String adHocID);
}