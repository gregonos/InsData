package net.windia.insdata.model.client;

import lombok.Data;

import java.io.Serializable;

@Data
public class IgAPIClientPagingCursors implements Serializable {

    private String before;
    private String after;
}
