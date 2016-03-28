import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class Tagmata {

	private JFrame frame;
	private JTextField textField;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new WindowsLookAndFeel());
					Tagmata window = new Tagmata();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Tagmata() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 563, 506);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextArea textArea = new JTextArea();
		
		JButton btnSave = new JButton("Save");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		table = new JTable();
		
		JLabel lblNewLabel = new JLabel("Tags");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
						.addComponent(textArea, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
						.addComponent(table, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textField, GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(table, GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
