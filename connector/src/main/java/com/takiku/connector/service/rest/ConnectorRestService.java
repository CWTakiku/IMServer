package com.takiku.connector.service.rest;


import org.springframework.stereotype.Service;
import po.*;
import rest.AbstractRestService;

import java.util.List;

/**
 * Date: 2019-05-28
 * Time: 00:18
 *
 * @author yrw
 */


public class ConnectorRestService extends AbstractRestService<ConnectorRestClient> {


    public ConnectorRestService(String url) {
        super(ConnectorRestClient.class, url);
    }

    public List<Offline> offlines(String token) {
        return doRequest(() -> restClient.pollOfflineMsg(token).execute());
    }

    public UserCertification certification(ShakeHands shakeHands) {
        return doRequest(() -> restClient.auth(shakeHands).execute());
    }
}
