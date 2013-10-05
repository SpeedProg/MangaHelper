package tk.speedprog.manga.mangahelper.data;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author SpeedProg
 *
 * @param <E> The Type of file to handle
 */
public class ArchiveFileListCellRenderer<E> extends JLabel implements
		ListCellRenderer<E> {
	private static final long serialVersionUID = 1L;
	private static final Color NEED_TO_BE_7ZIPED = new Color(195, 88, 23);
	/**
	 * Default constructor
	 */
	public ArchiveFileListCellRenderer() {
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}
	@Override
	public Component getListCellRendererComponent(JList<? extends E> list,
			E value, int index, boolean isSelected, boolean cellHasFocus) {
		ArchiveFile archiveFile = null;
		if (value instanceof ArchiveFile) {
			archiveFile = (ArchiveFile) value;
			if (archiveFile.isValid()) {
				if (archiveFile.hasEnded() && !archiveFile.is7zip()) {
					setForeground(NEED_TO_BE_7ZIPED);
				}
				if (archiveFile.hasEnded() && archiveFile.is7zip()) {
					setForeground(Color.GRAY);
				}
				if (!archiveFile.hasEnded()) {
					setForeground(list.getForeground());
				}
			} else {
				setForeground(Color.RED);
			}
			setText(archiveFile.getName());
		} else {
			setText(value.toString());
		}
		if (isSelected) {
			setBackground(list.getSelectionBackground());
		} else {
			setBackground(list.getBackground());
		}
		return this;
	}

}
