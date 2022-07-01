package com.veeva.vault.custom.services;

import com.veeva.vault.custom.model.VpsVQLRequest;
import com.veeva.vault.custom.model.VpsVQLResponse;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.json.JsonObject;
import com.veeva.vault.sdk.api.json.JsonValueType;

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public class VpsGroupHelperServiceImpl implements VpsGroupHelperService {

	private static final String OBJNAME_GROUP_MEMBERSHIP = "group_membership__sys";
	private static final String GROUP_RELATIONSHIP_NAME = "group__sysr";
	private static final String OBJFIELD_ID = "id";

	/*
	 * Gets the users that are members of a group returning a ????
	 */
	public List<Map<String, String>> getGroupMembers(String groupId, List<String> userAttributes)
	{

		List<Map<String, String>> users = VaultCollections.newList();

		VpsAPIClientService apiClient = ServiceLocator.locate(VpsAPIClientService.class);
		VpsVQLRequest vqlRequest = new VpsVQLRequest();

		vqlRequest.appendVQL("select ");
		vqlRequest.appendList(userAttributes, ",", false);
		vqlRequest.appendVQL(" from " );
		vqlRequest.appendVQL(OBJNAME_GROUP_MEMBERSHIP);
		vqlRequest.appendVQL(" where ");
		vqlRequest.appendVQL(GROUP_RELATIONSHIP_NAME);
		vqlRequest.appendVQL(".");
		vqlRequest.appendVQL(OBJFIELD_ID);
		vqlRequest.appendVQL(" = '");
		vqlRequest.appendVQL(groupId);
		vqlRequest.appendVQL("'");

		VpsVQLResponse queryResponse = apiClient.runVQL(vqlRequest, "v20.3", "CONNECTIONHERE");

		for (int i=0; i < queryResponse.getData().getSize(); i++)
		{
			JsonObject jsonObject = queryResponse.getData().getValue(i, JsonValueType.OBJECT);
			Map<String, String> userData = VaultCollections.newMap();
			for (String attr : userAttributes) {
				String value = jsonObject.getValue(attr, JsonValueType.STRING);
				userData.put(attr, value);
			}
			users.add(userData);
		}


		return users;


	}
}
