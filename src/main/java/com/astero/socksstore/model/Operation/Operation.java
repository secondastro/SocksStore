package com.astero.socksstore.model.Operation;

import com.astero.socksstore.model.Socks;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Operation {
    private OperationType operationType;
    private String localDateTime;
    private Socks socks;
}
