package com.jasscon.tagmata;

import java.awt.EventQueue;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import java.awt.event.KeyAdapter;

public class Tagmata {

	private JFrame frmTagmata;
	private JTextField cardTags;
	private JTable table;
	private JTextField queryString;
	private JButton searchBtn;
	private JButton btnReset;
	private Card activeCard;
	private JScrollPane scrollPane_1;
	private JTextPane cardText;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(new WindowsLookAndFeel());
					Tagmata window = new Tagmata();
					window.frmTagmata.setVisible(true);
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
		frmTagmata = new JFrame();
		frmTagmata.setTitle("Tagmata");
		frmTagmata.setBounds(100, 100, 563, 506);
		frmTagmata.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (activeCard != null) {
					Indexer.deleteCard(activeCard.getCardId());
					Indexer.addCard(activeCard.getCardId(), cardTags.getText(), cardText.getText());
					searchCards(queryString.getText());
					activeCard = null;
				} else {
					Indexer.addCard(cardTags.getText(), cardText.getText());
					DefaultTableModel dm = (DefaultTableModel) table.getModel();
					int rowCount = dm.getRowCount();
					for (int i = rowCount - 1; i >= 0; i--) {
						dm.removeRow(i);
					}
				}
				cardTags.setText("");
				cardText.setText("");
			}
		});

		cardTags = new JTextField();
		cardTags.setColumns(10);

		JLabel lblNewLabel = new JLabel("Tags");

		queryString = new JTextField();
		queryString.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == '\n') {
					searchCards(queryString.getText());
				}
			}
		});
		queryString.setColumns(10);

		searchBtn = new JButton("Search");
		searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchCards(queryString.getText());
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cardTags.setText("");
				cardText.setText("");
				queryString.setText("");
				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				int rowCount = dm.getRowCount();
				for (int i = rowCount - 1; i >= 0; i--) {
					dm.removeRow(i);
				}
				activeCard = null;
			}
		});
		
		scrollPane_1 = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frmTagmata.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(cardTags, GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnSave, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnReset, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(queryString, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(searchBtn, GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cardTags, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnReset, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(queryString, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(searchBtn))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		cardText = new JTextPane();
		scrollPane_1.setViewportView(cardText);

		table = new JTable();
		
		table.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent me) {
		        JTable table =(JTable) me.getSource();
		        Point p = me.getPoint();
		        int row = table.rowAtPoint(p);
		        if (me.getClickCount() == 2) {
					String cardId = table.getModel()
							.getValueAt(table.getSelectedRow(), 0).toString();
					String tags = table.getModel()
							.getValueAt(table.getSelectedRow(), 1).toString();
					String text = table.getModel()
							.getValueAt(table.getSelectedRow(), 2).toString();
					cardTags.setText(tags);
					cardText.setText(text);
					activeCard = new Card();
					activeCard.setCardId(cardId);
					activeCard.setTags(tags);
					activeCard.setText(text);
		        }
		    }
		});
		
		scrollPane.setViewportView(table);
		frmTagmata.getContentPane().setLayout(groupLayout);
	}

	private void showResults(List<Card> cards) {
		String[] columnNames = { "ID", "Tags", "Text" };
		DefaultTableModel tableModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableModel.setColumnIdentifiers(columnNames);
		for (Card card : cards) {
			Object[] row = new Object[] { card.getCardId(), card.getTags(),
					card.getText() };
			tableModel.addRow(row);
		}
		table.setRowHeight(30);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0)
				.setPreferredWidth(Math.round(10f * table.getWidth()));
		table.getColumnModel().getColumn(1)
				.setPreferredWidth(Math.round(20f * table.getWidth()));
		table.getColumnModel().getColumn(2)
				.setPreferredWidth(Math.round(70f * table.getWidth()));
		DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
		rend.setHorizontalAlignment(SwingConstants.LEFT);
		rend.setVerticalAlignment(SwingConstants.TOP);
		table.getColumnModel().getColumn(0).setCellRenderer(rend);
		table.getColumnModel().getColumn(1).setCellRenderer(rend);
		table.getColumnModel().getColumn(2).setCellRenderer(rend);
		final JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frmTagmata,
						"Delete card?", "Confirm Deletion",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == 0) {
					String cardId = table.getModel()
							.getValueAt(table.getSelectedRow(), 0).toString();
					System.out.println(cardId);
					Indexer.deleteCard(cardId);
					DefaultTableModel dm = (DefaultTableModel) table.getModel();
					dm.removeRow(table.getSelectedRow());
					if (activeCard != null && activeCard.getCardId().equals(cardId)) {
						cardText.setText("");
						cardTags.setText("");
						activeCard = null;
					}
				}
			}

		});
		popupMenu.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						int rowAtPoint = table.rowAtPoint(SwingUtilities
								.convertPoint(popupMenu, new Point(0, 0), table));
						if (rowAtPoint > -1) {
							table.setRowSelectionInterval(rowAtPoint,
									rowAtPoint);
						}
					}
				});
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}

			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub

			}
		});
		popupMenu.add(deleteItem);
		table.setComponentPopupMenu(popupMenu);
		table.doLayout();
	}
	
	private void searchCards(String terms) {
		if (terms.trim().length() == 0) {
			int result = JOptionPane.showConfirmDialog(frmTagmata,
					"You have entered no search terms and which will show all the cards in you have indexed till now. \nThis can take a really long time if you have indexes thousands of cards. \nIf the application crashes in between, you might end up corrupting your index. \n\nContinue?", "No Search Terms Entered",
					JOptionPane.OK_CANCEL_OPTION);
			if (result == 2) {
				return;
			}
		}
		List<Card> cards = Indexer.getCards(terms);
		showResults(cards);
	}
}
