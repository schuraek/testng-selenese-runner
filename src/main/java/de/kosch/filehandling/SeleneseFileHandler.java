package de.kosch.filehandling;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

public class SeleneseFileHandler extends ResourceHandler {

	private static final Logger log = LoggerFactory.getLogger(SeleneseFileHandler.class);
	private String[] groups;

	public SeleneseFileHandler(String[] groups, String... paths) {
		super(paths);
		this.groups = groups != null ? groups : new String[0];
	}

	/**
	 * searches although in method<groups> directories
	 */
	@Override
	public File[] getAllFiles() throws URISyntaxException {
		Set<File> allFiles = new HashSet<File>(Arrays.asList(super.getAllFiles()));
		for (String path : getPathsList()) {
			for (String group : groups) {
				String groupPath = group + System.getProperty("file.separator") + path;
				File fileFromPath = FileUtils.getFile(groupPath);
				if (fileFromPath != null && fileFromPath.exists()) {
					allFiles.add(fileFromPath);
				} else {
					URL url = Resources.getResource(groupPath);
					fileFromPath = url != null ? new File(url.toURI()) : null;
					if (fileFromPath != null) {
						allFiles.add(fileFromPath);
					}
				}
			}
		}
		return allFiles.toArray(new File[allFiles.size()]);
	}

	public String[] getAllFilePaths() {
		List<String> paths = new ArrayList<String>();
		try {
			File[] allFiles = getAllFiles();
			for (File file : allFiles) {
				paths.add(file.getAbsolutePath());
			}
		} catch (URISyntaxException e) {
			log.error("URISyntaxException  by loading selenese paths", e);
		}
		return paths.toArray(new String[paths.size()]);
	}

}
