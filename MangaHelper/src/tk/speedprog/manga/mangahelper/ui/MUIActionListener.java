package tk.speedprog.manga.mangahelper.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tk.speedprog.manga.mangahelper.data.ArchiveFile;
import tk.speedprog.manga.mangahelper.settings.SettingsConstants;
import tk.speedprog.utils.Settings;

/**
 * @author SpeedProg
 *
 */
public class MUIActionListener implements ActionListener {
	/**
	 * ActionCommand used to trigger the action
	 * to create a 7zip file from archive
	 */
	public static final String AC_CREATE7ZIP = "create7zip";
	/**
	 * ActionCommand used to trigger the action
	 * to mark the folder in the archive as ended
	 */
	public static final String AC_SETENDED = "setended";
	/**
	 * ActionCommand used to trigger the action to move the folder from the
	 * ripper to the archive
	 */
	public static final String AC_MOVE_TO_ARCHIVE = "movetoarchive";
	/**
	 * Action Command that triggers a refresh of the folder/file lists
	 */
	public static final String AC_REFRESH = "refresh";
	/**
	 * Action Command used to trigger moving the stats.xml file from the
	 * archive to the ripper.
	 */
	public static final String AC_PREPARE = "prepare";
	/**
	 * Action Command used to trigger hiding/showing the .7z files in the
	 * archive view
	 */
	public static final String AC_HIDE7ZIP = "hide7zip";
	/**
	 * This will hold a reference to the MainWindow
	 * that this Listener will be used in.
	 */
	private MainWindow mMainWindow;
	private Log mLog;
	
	/**
	 * @param window MainWindow object to use
	 */
	public MUIActionListener(MainWindow window) {
		mMainWindow = window;
		mLog = LogFactory.getLog(this.getClass());
	}
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String command = actionEvent.getActionCommand();
		Settings settings = mMainWindow.getSetting();
		//Preferences preferences = mMainWindow.getPreferences();
		List<ArchiveFile> archiveList;
		switch (command) {
		case AC_CREATE7ZIP:
			String archivePath = settings.getString(SettingsConstants.PATH_ARCHIVE);
			String sevenZ = settings.getString(SettingsConstants.SEVENZIP_COMMAND);
//			String archivePath = preferences.get(SettingsConstants.PATH_ARCHIVE, "");
//			String sevenZ = preferences.get(SettingsConstants.SEVENZIP_COMMAND, "");
			JList<ArchiveFile> list = mMainWindow.getArchiveJList();
			archiveList = list.getSelectedValuesList();
			for (ArchiveFile aFile : archiveList) {
				String mangaNameString = aFile.getName();
				// 7z a -r -scsUTF-8 -mx=9 -bd "Absolute Duo.7z" "Absolute Duo"
				ProcessBuilder pBuilder = new ProcessBuilder(sevenZ, "a", "-r", "-scsUTF-8", "-mx=9", "-bd", mangaNameString+".7z", mangaNameString);
				pBuilder.directory(new File(archivePath));
				try {
					mLog.info("Starting Progress of Builder.");
					Process process = pBuilder.start();
					try {
						ProcessOutputDialog dialog = new ProcessOutputDialog(process);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					mLog.debug("IOException on progress.", e);
					e.printStackTrace();
				}
			}
			
			break;
		case AC_MOVE_TO_ARCHIVE:
			break;
		case AC_SETENDED:
			break;
		case AC_REFRESH:
			mMainWindow.refreshViews();
			break;
		case AC_PREPARE:
			String archivePathString = settings.getString(SettingsConstants.PATH_ARCHIVE);
			String ripperPathString = settings.getString(SettingsConstants.PATH_RIPPER);
//			String archivePathString = preferences.get(SettingsConstants.PATH_ARCHIVE, "");
//			String ripperPathString = preferences.get(SettingsConstants.PATH_RIPPER, "");
			File archiveFile = new File(archivePathString);
			File ripperFile = new File(ripperPathString);
			JList<ArchiveFile> ripperList = mMainWindow.getRipperJList();
			archiveList = ripperList.getSelectedValuesList();
			for (ArchiveFile archive : archiveList) {
				String mangaNameString = archive.getName();
				File inArchive = new File(archiveFile, mangaNameString);
				File inRipperFile = new File(ripperFile, mangaNameString);
				if (inArchive.exists() && inRipperFile.exists()) {
					File statFile = new File(inArchive, "stats.xml");
					if (statFile.exists()) {
						try {
							Files.copy(Paths.get(statFile.getAbsolutePath()),
									Paths.get(
											(new File(inRipperFile, "stats.xml"))
											.getAbsolutePath()
											)
							);
						} catch (IOException e) {
							mLog.debug("Couldn't copy File.", e);
						}
					}
				}
				
			}
			break;
		case AC_HIDE7ZIP:
			JCheckBox chkbx = (JCheckBox) actionEvent.getSource();
			boolean isSelected = chkbx.isSelected();
			mLog.info("CheckBox selected:"+isSelected);
			if (isSelected) {
				mMainWindow.setArchiveFilter(mMainWindow.new DoNotShow7zipedFilter());
			} else {
				mMainWindow.setArchiveFilter(mMainWindow.new ShowAllFilter());
			}
			mMainWindow.refreshViews();
			break;
		default:
			mLog.fatal("Received Action Command that does not exist: "+command);
			break;
		}
	}

}
