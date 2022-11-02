package pl.com.kantoch.authorizationmodule.services;

import logs.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.com.kantoch.files.FileOperationServiceImplementation;
import response.LogFileContentResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class LogServiceImplementation implements LogService {

    private final Logger LOGGER = LoggerFactory.getLogger(LogServiceImplementation.class);

    private final FileOperationServiceImplementation fileOperationService;

    public LogServiceImplementation() {
        this.fileOperationService = new FileOperationServiceImplementation();
    }

    @Override
    public Collection<String> listLogFiles(String moduleName) {
        try {
            Collection<Path> paths = fileOperationService.loadLogFiles();
            return paths
                    .stream()
                    .map(Path::toString)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LogFileContentResponse readLogFile(String fileName) {
        LOGGER.warn("Requesting read log file ({}) for Authorization Module",fileName);
        try {
            if(fileName.contains(".gz")) return new LogFileContentResponse(fileOperationService.readArchivedFile(fileName));
            return new LogFileContentResponse(fileOperationService.readFile(fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
