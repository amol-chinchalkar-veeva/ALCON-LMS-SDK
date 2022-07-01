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

import com.veeva.vault.custom.model.VpsExternalTrainingQueueItem;
import com.veeva.vault.sdk.api.core.*;

import java.util.List;

@UserDefinedServiceInfo
public interface VpsExternalFTRTrainingHelperService extends UserDefinedService {
   List<VpsExternalTrainingQueueItem> getExternalTrainingQueueItems();
   void createFTRFromQueueItems(VpsExternalTrainingQueueItem vpsExternalTrainingQueueItem);
}