package pl.com.kantoch.authorizationmodule.rest;

import logs.LogService;
import org.springframework.web.bind.annotation.*;
import response.LogFileContentResponse;

import java.io.IOException;
import java.util.Collection;

@RestController
@RequestMapping("/api/log")
@CrossOrigin("*")
public class LogsResource {

    private final LogService logService;

    public LogsResource(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/get-all")
    public Collection<String> listLogFiles(@RequestParam String moduleName) throws IOException, InterruptedException {
        return logService.listLogFiles(moduleName);
    }

    @GetMapping("/read-file")
    public LogFileContentResponse readLogFile(@RequestParam String moduleName,@RequestParam String fileName) throws IOException, InterruptedException {
        return logService.readLogFile(moduleName,fileName);
    }
}
