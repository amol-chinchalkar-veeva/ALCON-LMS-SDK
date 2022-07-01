/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsStudyOrganizationHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2021-02-20
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
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.List;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsStudyOrganizationHelperImpl implements VpsStudyOrganizationHelperService {
	private static final String OBJFIELD_SITE = "site__v";
	private static final String OBJFIELD_STUDY = "study__v";
	private static final String OBJFIELD_STUDY_COUNTRY = "study_country__v";
	private static final String OBJFIELD_ID = "id";
	private static final String OBJFIELD_ORGANIZATION = "organization__clin";
	private static final String OBJECT_ORGANIZATION = "organization__v";


	public String getStudyOrgLevel(Record studyOrgRecord)
	{
		String level = "";

		String siteId = studyOrgRecord.getValue(OBJFIELD_SITE, ValueType.STRING);
		String countryId = studyOrgRecord.getValue(OBJFIELD_STUDY_COUNTRY, ValueType.STRING);
		String studyId = studyOrgRecord.getValue(OBJFIELD_STUDY, ValueType.STRING);

		if (siteId == null)
			siteId = "";
		if (countryId == null)
			countryId = "";
		if (studyId == null)
			studyId = "";

		if (!studyId.isEmpty())
			level = "study_level__v";

		if (!countryId.isEmpty())
			level = "country_level__v";

		if (!siteId.isEmpty())
			level = "site_level__v";

		return level;
	}

	public List<String> getOrganizationChildren(String orgId)
	{
		List<String> orgChildren = VaultCollections.newList();
		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

		//select id from organization__v where organization__clin = '0OR00000000N004'
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(OBJFIELD_ID),
				FROM, OBJECT_ORGANIZATION,
				WHERE, OBJFIELD_ORGANIZATION + " = '" + orgId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String childId = queryResult.getValue(OBJFIELD_ID, ValueType.STRING);
			orgChildren.add(childId);
		});

		return orgChildren;
	}

}