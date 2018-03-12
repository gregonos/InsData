package net.windia.insdata.model.client;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IgAPIClientDataWrapper<T> implements Serializable {

    private List<T> data;
    private IgAPIClientPaging paging;
}
