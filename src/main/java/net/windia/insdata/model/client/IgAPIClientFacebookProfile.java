package net.windia.insdata.model.client;

import lombok.Data;

import java.io.Serializable;

@Data
public class IgAPIClientFacebookProfile implements Serializable {

    private String id;

    private IgAPIClientDataWrapper<IgAPIClientLinkedAccount> accounts;
}
