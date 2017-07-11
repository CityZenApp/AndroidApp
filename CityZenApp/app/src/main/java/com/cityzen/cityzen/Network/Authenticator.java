package com.cityzen.cityzen.Network;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthExpectationFailedException;

/**
 * Created by Valdio Veliu on 11/05/2017.
 */

public class Authenticator {

    public static OAuthConsumer createOAuthConsumer(OAuthConsumer oauth) throws OAuthExpectationFailedException {
        if (oauth == null) {
            throw new OAuthExpectationFailedException(
                    "This class has been initialized without a OAuthConsumer. Only API calls " +
                            "that do not require authentication can be made.");
        }

        // "clone" the original consumer every time because the consumer is documented to be not
        // thread safe and maybe multiple threads are making calls to this class
        OAuthConsumer consumer = new DefaultOAuthConsumer(
                oauth.getConsumerKey(), oauth.getConsumerSecret());
        consumer.setTokenWithSecret(oauth.getToken(), oauth.getTokenSecret());
        return consumer;
    }
}
