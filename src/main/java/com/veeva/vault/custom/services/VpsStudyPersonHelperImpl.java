/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsTeamRoleHelper
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
public class VpsStudyPersonHelperImpl implements VpsStudyPersonHelperService {

	private static final String OBJECT_TEAM_ROLE = "team_role__v";
	private static final String OBJFIELD_ID = "id";
	private static final String OBJFIELD_SYSTEM_ID = "system_id__v";
	private static final String OBJFIELD_SITE = "site__clin";
	private static final String OBJFIELD_STUDY = "study__clin";
	private static final String OBJFIELD_STUDY_COUNTRY = "study_country__clin";

	public String getTeamRoleSystemId(String teamRoleRecordId)
	{
		List<String> teamRoleSystemId = VaultCollections.newList();

		VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
		QueryResponse queryResponse = vpsVQLService.query(
				SELECT, vpsVQLService.appendFields(
						OBJFIELD_SYSTEM_ID
				),
				FROM, OBJECT_TEAM_ROLE,
				WHERE,  OBJFIELD_ID + " = '" + teamRoleRecordId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String sysId = queryResult.getValue(OBJFIELD_SYSTEM_ID, ValueType.STRING);
			teamRoleSystemId.add(sysId);
		});

		return teamRoleSystemId.get(0);
	}

	public String getStudyPeronLevel(Record studyPersonRecord)
	{
		String level = "";

		String siteId = studyPersonRecord.getValue(OBJFIELD_SITE, ValueType.STRING);
		String countryId = studyPersonRecord.getValue(OBJFIELD_STUDY_COUNTRY, ValueType.STRING);
		String studyId = studyPersonRecord.getValue(OBJFIELD_STUDY, ValueType.STRING);

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
}