package de.kosch.filehandling;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

/**
 * handles all resources such a config or seleneses
 * 
 * @author Schuraev
 *
 */
public class ResourceHandler {

    private List<String> pathsList;

    private static final Logger log = LoggerFactory.getLogger(SeleneseFileHandler.class);

    public ResourceHandler(String... paths) {
        this.pathsList = new ArrayList<String>(Arrays.asList(paths));
        this.pathsList.removeAll(Arrays.asList(null, StringUtils.EMPTY));
    }

    /**
     * join all files to one
     * 
     * @param target
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public File getJoinedFile(File target) throws IOException, URISyntaxException {
        new IOCopier().joinFiles(target, getAllFiles());
        return target;
    }

    /**
     * return all founded files from paths
     * 
     * @return
     * @throws URISyntaxException
     */
    public File[] getAllFiles() {
        Set<File> collectFiles = pathsList.parallelStream().map(path -> findFile(path)).filter(file -> file != null)
            .collect(Collectors.toSet());
        return collectFiles.toArray(new File[collectFiles.size()]);
    }

    protected List<String> getPathsList() {
        return pathsList;
    }

    /**
     * joins files
     * 
     * @author Schuraev
     *
     */
    protected class IOCopier {
        public void joinFiles(File destination, File[] sources) throws IOException {
            OutputStream output = null;
            try {
                output = createAppendableStream(destination);
                for (File source : sources) {
                    appendFile(output, source);
                }
            } finally {
                IOUtils.closeQuietly(output);
            }
        }

        private BufferedOutputStream createAppendableStream(File destination) throws FileNotFoundException {
            return new BufferedOutputStream(new FileOutputStream(destination, true));
        }

        private void appendFile(OutputStream output, File source) throws IOException {
            InputStream input = null;
            try {
                input = new BufferedInputStream(new FileInputStream(source));
                IOUtils.copy(input, output);
            } finally {
                IOUtils.closeQuietly(input);
            }
        }
    }

    protected File findFile(String path) {
        File file = Paths.get(path).toFile();
        if (!file.exists()) {
            try {
                file = FileUtils.toFile(Resources.getResource(path));
            } catch (Exception e) {
                log.debug("file with path: '" + path + "' not found");
                return null;
            }
        }
        return file;
    }

}