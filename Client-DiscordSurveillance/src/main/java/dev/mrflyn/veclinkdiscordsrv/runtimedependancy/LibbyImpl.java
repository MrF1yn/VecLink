package dev.mrflyn.veclinkdiscordsrv.runtimedependancy;

import net.byteflux.libby.LibraryManager;
import net.byteflux.libby.logging.adapters.LogAdapter;

import java.nio.file.Path;

public class LibbyImpl extends LibraryManager {

    protected LibbyImpl(LogAdapter logAdapter, Path dataDirectory, String directoryName) {
        super(logAdapter, dataDirectory, directoryName);
    }

    @Override
    protected void addToClasspath(Path file) {

    }
}
