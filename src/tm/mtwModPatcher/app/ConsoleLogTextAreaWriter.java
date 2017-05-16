package tm.mtwModPatcher.app;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import tm.mtwModPatcher.lib.engines.ConsoleLogWriter;

import javax.swing.*;

/**
 * Created by tomek on 10.04.2017.
 */
public class ConsoleLogTextAreaWriter implements ConsoleLogWriter {
	@Override
	public void write(StringBuilder strB) {
		write(strB.toString());
	}

	@Override
	public void write(String str) {
		textArea.append(str);

		scrollToEnd();

		statusBar.setText(str);
	}

	@Override
	public void scrollToEnd() {
		val textAreaVerticalScroll = textAreaScroll.getVerticalScrollBar();
		textAreaVerticalScroll.setValue(textAreaVerticalScroll.getMaximum());
	}

	@Getter @Setter
	private JTextArea textArea;
	@Getter @Setter
	private JScrollPane textAreaScroll;
	@Getter @Setter
	private JLabel statusBar;
}
