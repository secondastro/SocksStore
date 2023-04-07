package com.astero.socksstore.service;

import java.io.File;

public interface FileService {


     String readFromFile();

     String readOperationsFromFile();

    void saveToFile(String json);

    File getSocksFile();

    File getOperationFile();

    void cleanDataFile();

    void cleanOperationsFile();

    void saveOperationToFile(String json);
}
