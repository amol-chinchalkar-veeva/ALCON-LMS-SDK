/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsExternalFTRTrainingHelperService
 * Author:				Amol Chinchalkar @ Veeva
 * Date:				2022-06-30
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

import com.veeva.vault.custom.model.VpsExternalTrainingInfo;
import com.veeva.vault.sdk.api.core.*;

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsMultiAssignmentHelperService extends UserDefinedService {
    Map<String, List<VpsExternalTrainingInfo>> getExternalTrainingAssignmentInfo(String id, String trainingType);

    Map<List<String>, List<String>> createFacilitatedTrainingForAssignments(Map<String, List<VpsExternalTrainingInfo>> vpsExternalTrainingQueueItemsMap);

    List<String> addTrainingAssignmentsInFacilitiatedTraining(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap);

    void completeFacilitatedTrainingRequest(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap, String actionName);
}