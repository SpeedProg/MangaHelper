package tk.speedprog.manga.mangahelper.ui;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JList;
import javax.swing.JToolBar;
import javax.swing.JButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import tk.speedprog.manga.mangahelper.data.ArchiveFile;
import tk.speedprog.manga.mangahelper.data.ArchiveFileListCellRenderer;
import tk.speedprog.manga.mangahelper.settings.SettingsConstants;
import tk.speedprog.utils.Settings;

import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

/**
 * Main Window of Manga Helper.
 */
public class MainWindow {

	private static final String SETTINGS_FILE = "settings.xml";
	private JFrame frame;
	private MUIActionListener actionListener;
	private JButton btnCopyToArchive;
	private JButton btnCreate7Zip;
	private JButton btnSetEnded;
	private JList<ArchiveFile> listArchive;
	private JList<ArchiveFile> listRipper;
	private JButton btnRefresh;
	private Settings settings;
	//private Preferences preferences;

	private Log mLog;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JButton btnPrepareForReading;
	private JCheckBox chckbxHidezFiles;
	private ListFilter archiveViewFilter;
	private ListFilter ripperViewFilter;

	/**
	 * Launch the application.
	 * @param args Commandline argument (none)
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		/*
		 * DeviceManager dManager = DeviceManager.getInstance();
		 * dManager.scanDevices(); Map<String, UsbDevice> deviceMap =
		 * dManager.getDeviceList(); for (Entry<String, UsbDevice> entry :
		 * deviceMap.entrySet()) { System.out.println("Device: "+entry.getKey()+
		 * " "+entry.getValue().dump());
		 * 
		 * }
		 */

	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		mLog = LogFactory.getLog(getClass());
		settings = new Settings(SETTINGS_FILE);
		settings.storeString(SettingsConstants.PATH_ARCHIVE,
				"F:\\Dokumente\\Mangas");
		settings.storeString(SettingsConstants.PATH_RIPPER,
				"F:\\Programme\\MangaRipper\\mangas");
		settings.storeString(SettingsConstants.SEVENZIP_COMMAND, "F:\\Programme\\7-Zip\\7z");
/*		preferences = (Preferences.userNodeForPackage(getClass()).parent());
		preferences.put(SettingsConstants.PATH_ARCHIVE, "F:\\Dokumente\\Mangas");
		preferences.put(SettingsConstants.PATH_RIPPER, "F:\\Programme\\MangaRipper\\mangas");
		preferences.put(SettingsConstants.SEVENZIP_COMMAND, "F:\\Programme\\7-Zip\\7z");*/
		archiveViewFilter = new ShowAllFilter();
		ripperViewFilter = new ShowAllFilter();
		actionListener = new MUIActionListener(this);
		initialize();
		addUIHandling();
		refreshViews();
	}

	/**
	 * Refresh the JLists for archive and ripper
	 */
	public void refreshViews() {
		String ripperPath = settings
				.getString(SettingsConstants.PATH_RIPPER);
		String archivePath = settings.getString(SettingsConstants.PATH_ARCHIVE);
		
//		String ripperPath = preferences.get(SettingsConstants.PATH_RIPPER, "");
//		String archivePath = preferences.get(SettingsConstants.PATH_ARCHIVE, "");
		refreshFolderView(listArchive, archivePath,archiveViewFilter);
		refreshFolderView(listRipper, ripperPath, ripperViewFilter);
	}

	private void refreshFolderView(final JList<ArchiveFile> listArchive2, String path, final ListFilter f) {
		File root = new File(path);
		if (!root.exists() || !root.isDirectory()) {
			mLog.info("This is not a directory!");
			return;
		}
		File[] content = root.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (dir.isDirectory() || name.endsWith(".7z"))
					return true;
				return false;
			}
		});
		
		final ArchiveFile[] archiveFiles = new ArchiveFile[content.length];
		for (int i = 0; i < content.length; i++) {
			archiveFiles[i] = new ArchiveFile(content[i]);
			
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				DefaultListModel<ArchiveFile> model = (DefaultListModel<ArchiveFile>) listArchive2.getModel();
				model.clear();
				for (ArchiveFile aFile : archiveFiles) {
					if (f.show(aFile)) {
						model.addElement(aFile);
					}
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				/*settings.close();*/
			}
		});

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JToolBar toolBar = new JToolBar();
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);

		btnRefresh = new JButton("Refresh");
		toolBar.add(btnRefresh);

		btnCreate7Zip = new JButton("Create 7zip");
		//btnCreate7Zip.setEnabled(false);
		toolBar.add(btnCreate7Zip);

		btnSetEnded = new JButton("Set Ended");
		btnSetEnded.setEnabled(false);
		toolBar.add(btnSetEnded);

		btnCopyToArchive = new JButton("Copy to Archive");
		btnCopyToArchive.setEnabled(false);
		toolBar.add(btnCopyToArchive);
		
		btnPrepareForReading = new JButton("Prepare for Reading");
		toolBar.add(btnPrepareForReading);
		
		chckbxHidezFiles = new JCheckBox("Hide 7z files");
		toolBar.add(chckbxHidezFiles);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setContinuousLayout(true);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		listArchive = new JList<ArchiveFile>();
		listArchive.setModel(new DefaultListModel<ArchiveFile>());
		listArchive.setCellRenderer(new ArchiveFileListCellRenderer<ArchiveFile>());
		scrollPane.setViewportView(listArchive);

		scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		listRipper = new JList<ArchiveFile>();
		listRipper.setModel(new DefaultListModel<ArchiveFile>());
		listRipper.setCellRenderer(new ArchiveFileListCellRenderer<ArchiveFile>());
		scrollPane_1.setViewportView(listRipper);
	}

	/**
	 * Add Handling to the UI
	 */
	private void addUIHandling() {
		btnCopyToArchive.setActionCommand(MUIActionListener.AC_MOVE_TO_ARCHIVE);
		btnCreate7Zip.setActionCommand(MUIActionListener.AC_CREATE7ZIP);
		btnSetEnded.setActionCommand(MUIActionListener.AC_SETENDED);
		btnRefresh.setActionCommand(MUIActionListener.AC_REFRESH);
		btnPrepareForReading.setActionCommand(MUIActionListener.AC_PREPARE);
		chckbxHidezFiles.setActionCommand(MUIActionListener.AC_HIDE7ZIP);

		btnCopyToArchive.addActionListener(actionListener);
		btnSetEnded.addActionListener(actionListener);
		btnCreate7Zip.addActionListener(actionListener);
		btnRefresh.addActionListener(actionListener);
		btnPrepareForReading.addActionListener(actionListener);
		chckbxHidezFiles.addActionListener(actionListener);
	}

	/**
	 * @return the JList representing the Archive
	 */
	public JList<ArchiveFile> getArchiveJList() {
		return listArchive;
	}

	/**
	 * @return the JList representing the Ripper
	 */
	public JList<ArchiveFile> getRipperJList() {
		return listRipper;
	}

	/**
	 * @return the settings object
	 */
	public Settings getSetting() {
		return settings;
	}

	/**
	 * @return the preferences object
	 */
//	public Preferences getPreferences() {
//		return preferences;
//	}

	/**
	 * @param f the ListFilter to use on the Archive
	 */
	public void setArchiveFilter(ListFilter f) {
		archiveViewFilter = f;
	}

	/**
	 * @param f the ListFilter to use on the Ripper
	 */
	public void setRipperFilter(ListFilter f) {
		ripperViewFilter = f;
	}

	/**
	 * ListFilter interface used to filter the JList entry.
	 *
	 */
	public interface ListFilter {
		/**
		 * @param a ArchiveFile to check
		 * @return true if it should be shown, false otherwise
		 */
		boolean show(ArchiveFile a);
	};

	/**
	 * ListFilter implementation that filters out .7z files and invalid files.
	 *
	 */
	public class DoNotShow7zipedFilter implements ListFilter {
		@Override
		public boolean show(ArchiveFile a) {
			if (a.isValid() && !a.is7zip())
				return true;
			return false;
		}	
	}
	
	/**
	 * ListFilter that allows all archives that are valid.
	 *
	 */
	public class ShowAllFilter implements ListFilter {
		@Override
		public boolean show(ArchiveFile a) {
			if (a.isValid())
				return true;
			return false;
		}
		
	}
}
