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

import com.veeva.vault.custom.model.VpsExternalTrainingModel;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsExternalTrainingHelperService extends UserDefinedService {
   Map<String, List<VpsExternalTrainingModel>> getExternalTrainingAssignmentModels(String id);
   Map<List<String>, List<String>> createFacilitatedTrainingForAssignments(Map<String, List<VpsExternalTrainingModel>> vpsExternalTrainingQueueItemsMap);
   List<String>  createFacilitatedReqAndTrainingAssignment(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap);
   void callUserAction(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap, String actionName);
}