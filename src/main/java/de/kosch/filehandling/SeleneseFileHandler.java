package de.kosch.filehandling;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeleneseFileHandler extends ResourceHandler {

    private static final Logger log = LoggerFactory.getLogger(SeleneseFileHandler.class);
    private String[] groups;
    private String seleneseDir;

    public SeleneseFileHandler(String seleneseDir, String[] groups, String... paths) {
        super(paths);
        this.seleneseDir = seleneseDir;
        this.groups = groups != null ? groups : new String[0];
    }

    /**
     * searches although in method<groups> directories
     */
    @Override
    public File[] getAllFiles() {
        Set<File> allFiles = new HashSet<File>(Arrays.asList(super.getAllFiles()));
        Set<File> collectFiles = getPathsList().parallelStream().map(path -> findFilesForGroup(path))
            .filter(file -> file != null).collect(Collectors.toSet());
        allFiles.addAll(collectFiles);
        return allFiles.toArray(new File[allFiles.size()]);
    }

    protected File findFilesForGroup(String path) {
        File returnValue = null;
        try {
            Stream<File> flatMap = Arrays.stream(groups).parallel().flatMap(group -> Stream.of( //
                findFile(Paths.get(seleneseDir, group, path).toString()),//
                findFile(Paths.get(group, path).toString()),//
                findFile(Paths.get(seleneseDir, path).toString())//
                )).filter(file -> file != null);
            returnValue = flatMap.findFirst().get();
        } catch (NoSuchElementException e) {
            log.debug("file with path: '" + path + "' not found");
        }
        return returnValue;
    }
}
