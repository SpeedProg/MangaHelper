package tk.speedprog.manga.mangahelper.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dialog to Show 7z Output
 *
 */
public class ProcessOutputDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	ProcessToDialogThread thread;
	private JTextArea textAreaOutPut;

	/**
	 * Launch the application.
	 * @param args Commandline arguments (none)
	 */
	public static void main(String[] args) {
		try {
			ProcessOutputDialog dialog = new ProcessOutputDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @param p the progress whos stdoutstream should be displayed
	 */
	public ProcessOutputDialog(Process p) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane);
			{
				textAreaOutPut = new JTextArea();
				scrollPane.setViewportView(textAreaOutPut);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		thread = new ProcessToDialogThread(p);
		thread.start();
	}
	private class ProcessToDialogThread extends Thread {
		private Process process;
		//private ProcessOutputDialog dialog;
		private InputStream stdin;
		private InputStreamReader isr;
		private Log mLog;
		public ProcessToDialogThread(Process p) {
			process = p;
			mLog = LogFactory.getLog(getClass());
		}
		
		@Override
		public void run() {
			stdin = process.getInputStream();
			isr = new InputStreamReader(stdin);
			char[] buffer = new char[1024];
			int len;
			try {
				while ((len = isr.read(buffer)) > -1) {
					final String string = new String(buffer, 0, len);
					mLog.info("Read "+len+" bytes.");
					SwingUtilities.invokeLater(new Runnable() {
						
						@Override
						public void run() {
							textAreaOutPut.append(string);
						}
					});
					
				}
			} catch (IOException e) {
				mLog.debug("Error reading from Process.", e);
			}
		}
	}
}
