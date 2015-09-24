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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	public File[] getAllFiles() throws URISyntaxException {
		Set<File> allFiles = new HashSet<File>();
		for (String path : pathsList) {
			File fileFromPath = FileUtils.getFile(path);
			if (fileFromPath != null && fileFromPath.exists()) {
				allFiles.add(fileFromPath);
			} else {
				try {
					URL url = Resources.getResource(path);
					fileFromPath = url != null ? new File(url.toURI()) : null;
					if (fileFromPath != null) {
						allFiles.add(fileFromPath);
					}
				} catch (Exception e) {
					log.debug("ressource not found" + path);
				}

			}
		}
		return allFiles.toArray(new File[allFiles.size()]);
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
}