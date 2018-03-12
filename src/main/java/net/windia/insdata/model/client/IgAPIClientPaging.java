package net.windia.insdata.model.client;

import lombok.Data;

import java.io.Serializable;

@Data
public class IgAPIClientPaging implements Serializable {
    private String previous;
    private String next;
    private IgAPIClientPagingCursors cursors;
}
