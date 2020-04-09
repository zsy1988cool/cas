package net.unicon.cas.oauth;

import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile;

import com.fasterxml.jackson.databind.JsonNode;

public class MyOAuthWrapperClient extends CasOAuthWrapperClient {
	
	public MyOAuthWrapperClient(final String key, final String secret, final String casOAuthUrl) {
		super(key, secret, casOAuthUrl);
    }
	
	@Override
    protected CasOAuthWrapperProfile extractUserProfile(final String body) {
        final CasOAuthWrapperProfile userProfile = new CasOAuthWrapperProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            userProfile.setId(JsonHelper.get(json, "id"));
            json = json.get("attributes");
            if (json != null) {
//                final Iterator<JsonNode> nodes = json.iterator();
//                while (nodes.hasNext()) {
//                    json = nodes.next();
//                    final String attribute = json.fieldNames().next();
//                    userProfile.addAttribute(attribute, JsonHelper.get(json, attribute));
//                }
            }
        }
        return userProfile;
    }
}
