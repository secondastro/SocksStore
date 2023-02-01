package com.astero.socksstore.service;

import java.io.File;

public interface FileService {


    String readFromFile();

    boolean saveToFile(String json);

    File getDataFile();
}
