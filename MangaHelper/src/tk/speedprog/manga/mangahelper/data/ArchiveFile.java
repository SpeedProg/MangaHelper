package tk.speedprog.manga.mangahelper.data;

import java.io.File;

/**
 * File Representing a Manga Archive
 */
public class ArchiveFile {

	private static final String MARKERFILE_ENDED = "ended.txt";
	private boolean isValid;
	private boolean hasEnded;
	private boolean is7zip;
	private File file;

	/**
	 * Default Constructor
	 * @param f File to the root of the Manga Archive
	 */
	public ArchiveFile(File f) {
		file = f;
		if (file.isFile()) {
			if (file.getName().endsWith(".7z")) {
				is7zip = true;
				hasEnded = true;
				isValid = true;
				
			} else {
				is7zip = false;
				isValid = false;
			}
		} else if (file.isDirectory()) {
			is7zip = false;
			File endedFile = new File(file, MARKERFILE_ENDED);
			if (endedFile.exists()) {
				hasEnded = true;
			} else {
				hasEnded = false;
			}
			isValid = true;
		} else {
			isValid = false;
		}
	}
	
	/**
	 * @return true if the Archive is valid, false otherwise
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * @return if the manga is marked as ended, false otherwise
	 */
	public boolean hasEnded() {
		return hasEnded;
	}

	/**
	 * @return true if the archive is a .7z file
	 */
	public boolean is7zip() {
		return is7zip;
	}

	/**
	 * @return the name of the root of the archive
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * @return the root file of the archive
	 */
	public File getFile() {
		return file;
	}

	@Override
	public String toString() {
		return file.getName();
	}

	@Override
	public int hashCode() {
		return file.getName().hashCode();
	}
}
