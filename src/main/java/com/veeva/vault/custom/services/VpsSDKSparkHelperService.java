/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsSDKSparkHelper
 * Author:				Krunal Shah @ Veeva
 * Date:				2021-06-09
 *---------------------------------------------------------------------
 * Description: Useful Methods to help with Spark Integration
 * between Vaults
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;

@UserDefinedServiceInfo
public interface VpsSDKSparkHelperService extends UserDefinedService {
	public boolean isIntegrationActive(String integrationApiName);
	public void createUserExceptionMessage(String integrationApiName,
												  String integrationPointApiName,
												  String errorMessage,
												  String messageProcessor,
												  String messageBody);
	public String getIntegrationId(String integrationAPIName);
	public String getIntegrationPointId(String integrationPointAPIName);
	public String getConnectionId(String connectionAPIName);
}