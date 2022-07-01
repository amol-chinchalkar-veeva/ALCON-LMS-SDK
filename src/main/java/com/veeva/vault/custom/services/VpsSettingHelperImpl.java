/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsSettingHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2020-10-02
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2020 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.custom.model.VpsSettingRecord;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.Map;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsSettingHelperImpl implements VpsSettingHelperService {

	public static final String OBJECT_VPS_SETTING = "vps_setting__c";
	public static final String OBJFIELD_EXTERNAL_ID = "external_id__c";
	public static final String OBJFIELD_ID = "id";
	public static final String OBJFIELD_ITEM_DELIMITER = "item_delimiter__c";
	public static final String OBJFIELD_KEY_VALUE_DELIMITER = "key_value_delimiter__c";
	public static final String OBJFIELD_SETTING_DELIMITER = "setting_delimiter__c";
	public static final String OBJFIELD_STATUS = "status__v";
	public static final String OBJFIELD_VALUE = "value__c";
	public static final String STATUS_ACTIVE = "active__v";


	/**
	 * setting records that have been loaded
	 *
	 * @return map of setting records
	 */
	public Map<String, VpsSettingRecord> items(String externalIdFilter, Boolean useWildCard) {
		Map<String, VpsSettingRecord> settingRecordMap = VaultCollections.newMap();
		settingRecordMap = loadData(externalIdFilter,useWildCard);
		return  settingRecordMap;

	}

	/**
	 * Loads settings records into a map by querying Vault
	 *
	 * @param externalIdFilter external id of the setting record(s) to find
	 * @param useWildCard when true, any records that start with externalIdFilter will be loaded
	 */
	private Map<String, VpsSettingRecord> loadData(String externalIdFilter, Boolean useWildCard) {
		try {
			Map<String, VpsSettingRecord> settingRecordMap = VaultCollections.newMap();
			settingRecordMap.clear();

			//query Vault for all settings
			VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);
			StringBuilder whereClause = new StringBuilder();

			whereClause.append(OBJFIELD_STATUS + " = '" + STATUS_ACTIVE + "' ");
			if ((externalIdFilter != null) && (externalIdFilter.length() > 0)) {
				whereClause.append(" and " + OBJFIELD_EXTERNAL_ID + " like '");
				whereClause.append(externalIdFilter);
				if (useWildCard) {
					whereClause.append("%");
				}
				whereClause.append("'");
			}

			QueryResponse queryResponse = vqlService.query(
					SELECT, vqlService.appendFields(
							OBJFIELD_ID,
							OBJFIELD_EXTERNAL_ID,
							OBJFIELD_KEY_VALUE_DELIMITER,
							OBJFIELD_ITEM_DELIMITER,
							OBJFIELD_SETTING_DELIMITER,
							OBJFIELD_VALUE
					),
					FROM, OBJECT_VPS_SETTING,
					WHERE, whereClause.toString()
			);

			queryResponse.streamResults().forEach(queryResult -> {
				String externalId = queryResult.getValue(OBJFIELD_EXTERNAL_ID, ValueType.STRING);
				String value = queryResult.getValue(OBJFIELD_VALUE, ValueType.STRING);

				VpsSettingRecord settingRecord = new VpsSettingRecord();
				settingRecord.setExternalId(externalId);
				settingRecord.setItemDelimiter(queryResult.getValue(OBJFIELD_ITEM_DELIMITER, ValueType.STRING));
				settingRecord.setKeyDelimiter(queryResult.getValue(OBJFIELD_KEY_VALUE_DELIMITER, ValueType.STRING));
				settingRecord.setSettingDelimiter(queryResult.getValue(OBJFIELD_SETTING_DELIMITER, ValueType.STRING));

				if (value != null) {
					String[] pairs = StringUtils.split(value,settingRecord.getSettingDelimiter());
					for (int i=0;i<pairs.length;i++) {
						String pair = pairs[i];
						String[] keyValue = StringUtils.split(pair,settingRecord.getKeyDelimiter());

						String settingKey = keyValue[0];
						if (keyValue.length > 1) {
							String settingValue = keyValue[1];
							settingRecord.setValue(settingKey, settingValue);
						}
					}
				}
				settingRecordMap.put(externalId, settingRecord);
			});

			return settingRecordMap;
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}
}