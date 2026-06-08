package swing_version;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BT_Dialog {

	private static final int BOX_WIDTH = 700;

	private BT_Dialog() {
	}

	public static void show(String message) {
		System.out.println(message);
		System.out.println();
	}

	public static String format(String message) {
		return message == null ? "" : message;
	}

	// 기존 호환용 (onComplete 없음)
	public static JPanel createMessageBox(String message) {
		return createMessageBox(message, null);
	}

	// 타이핑 없이 전체 텍스트를 즉시 표시
	public static JPanel createMessageBoxInstant(String message) {
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrapper.setOpaque(false);
		wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
		wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel box = new JPanel(new BorderLayout());
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
				new EmptyBorder(12, 14, 12, 14)
		));

		String fullText = message == null ? "" : message;

		JTextArea textArea = new JTextArea(fullText);
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Dialog", Font.PLAIN, 19));
		textArea.setFocusable(false);

		textArea.setSize(new Dimension(BOX_WIDTH - 32, Short.MAX_VALUE));
		Dimension textPref = textArea.getPreferredSize();

		box.add(textArea, BorderLayout.CENTER);

		int boxHeight = textPref.height + 55;
		Dimension fixedSize = new Dimension(BOX_WIDTH, boxHeight);
		box.setPreferredSize(fixedSize);
		box.setMinimumSize(fixedSize);
		box.setMaximumSize(fixedSize);

		wrapper.add(box);

		Dimension wrapperSize = new Dimension(BOX_WIDTH, boxHeight + 10);
		wrapper.setPreferredSize(wrapperSize);
		wrapper.setMinimumSize(wrapperSize);
		wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, boxHeight + 10));

		return wrapper;
	}

	// 타이핑 완료 시 onComplete 콜백 실행
	public static JPanel createMessageBox(String message, Runnable onComplete) {
		JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		wrapper.setOpaque(false);
		wrapper.setBorder(new EmptyBorder(0, 0, 10, 0));
		wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel box = new JPanel(new BorderLayout());
		box.setBackground(Color.WHITE);
		box.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
				new EmptyBorder(12, 14, 12, 14)
		));

		String fullText = message == null ? "" : message;

		JTextArea textArea = new JTextArea("");
		textArea.setEditable(false);
		textArea.setOpaque(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Dialog", Font.PLAIN, 19));
		textArea.setFocusable(false);

		// 크기는 전체 텍스트 기준으로 미리 계산
		textArea.setText(fullText);
		textArea.setSize(new Dimension(BOX_WIDTH - 32, Short.MAX_VALUE));
		Dimension textPref = textArea.getPreferredSize();
		textArea.setText("");

		// 타자 효과: 한 글자씩 출력, 완료 시 onComplete 실행
		int[] index = {0};
		Timer typeTimer = new Timer(25, null);
		typeTimer.addActionListener((ActionEvent e) -> {
			if (index[0] < fullText.length()) {
				textArea.append(String.valueOf(fullText.charAt(index[0])));
				index[0]++;
			} else {
				typeTimer.stop();
				if (onComplete != null) {
					SwingUtilities.invokeLater(onComplete);
				}
			}
		});
		typeTimer.start();

		box.add(textArea, BorderLayout.CENTER);

		JLabel arrow = new JLabel("▼", SwingConstants.RIGHT);
		arrow.setFont(new Font("Dialog", Font.PLAIN, 11));
		arrow.setForeground(new Color(130, 130, 130));
		arrow.setBorder(new EmptyBorder(6, 0, 0, 0));
		box.add(arrow, BorderLayout.SOUTH);

		int boxHeight = textPref.height + 55;
		Dimension fixedSize = new Dimension(BOX_WIDTH, boxHeight);

		box.setPreferredSize(fixedSize);
		box.setMinimumSize(fixedSize);
		box.setMaximumSize(fixedSize);

		wrapper.add(box);

		Dimension wrapperSize = new Dimension(BOX_WIDTH, boxHeight + 10);
		wrapper.setPreferredSize(wrapperSize);
		wrapper.setMinimumSize(wrapperSize);
		wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, boxHeight + 10));

		return wrapper;
	}
}
