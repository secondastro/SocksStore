package com.astero.socksstore.service;

import com.astero.socksstore.model.Operation.Operation;

public interface OperationService {

    void readOperationsFromFile();

    void addOperationToList(Operation operation);
}
