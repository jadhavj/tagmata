package com.jasscon.tagmata;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class Tagmata {

	private JFrame frmTagmata;
	private JTextField cardTags;
	private JTable table;
	private JTextField queryString;
	private JButton searchBtn;
	private JButton btnReset;
	private Card activeCard;
	private JSplitPane windowSplit;
	private JButton delBmBtn;
	private JButton mvBmUp;
	private JButton mvBmDown;

	private JList bookmarkList;
	private List<Card> bookmarks = new ArrayList<Card>();
	private JTextPane cardText;
	private JMenuBar menuBar;
	private JMenu mnTagmata;
	private JMenuItem mntmAbout;
	private JMenuItem mntmExit;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Indexer.init();
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
		try {
			// Bind to localhost adapter with a zero connection queue
			ServerSocket socket = new ServerSocket(9999, 0,
					InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
		} catch (BindException e) {
			System.exit(0);
		} catch (IOException e) {
			System.exit(0);
		}
		frmTagmata = new JFrame();
		frmTagmata.setTitle(" Tagmata");
		frmTagmata.setBounds(100, 100, 826, 562);
		frmTagmata.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - frmTagmata.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - frmTagmata.getHeight()) / 2);
		frmTagmata.setLocation(x, y);

		windowSplit = new JSplitPane();
		windowSplit.setDividerLocation(250);
		GroupLayout groupLayout = new GroupLayout(frmTagmata.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(windowSplit, GroupLayout.DEFAULT_SIZE,
								605, Short.MAX_VALUE).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(windowSplit, GroupLayout.DEFAULT_SIZE,
								509, Short.MAX_VALUE).addContainerGap()));

		JSplitPane mainSplit = new JSplitPane();
		windowSplit.setRightComponent(mainSplit);
		mainSplit.setResizeWeight(0.5);
		mainSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);

		JPanel resultsPanel = new JPanel();
		mainSplit.setRightComponent(resultsPanel);

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

		table = new JTable();
		table.setBorder(null);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
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
					activeCard.setCardId(Long.parseLong(cardId));
					activeCard.setTags(tags);
					activeCard.setText(text);
				}
			}
		});

		scrollPane.setViewportView(table);
		GroupLayout gl_resultsPanel = new GroupLayout(resultsPanel);
		gl_resultsPanel
				.setHorizontalGroup(gl_resultsPanel
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								Alignment.LEADING,
								gl_resultsPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_resultsPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																scrollPane,
																GroupLayout.DEFAULT_SIZE,
																513,
																Short.MAX_VALUE)
														.addGroup(
																gl_resultsPanel
																		.createSequentialGroup()
																		.addComponent(
																				queryString,
																				GroupLayout.DEFAULT_SIZE,
																				403,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				searchBtn,
																				GroupLayout.PREFERRED_SIZE,
																				104,
																				GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		gl_resultsPanel
				.setVerticalGroup(gl_resultsPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_resultsPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_resultsPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(searchBtn)
														.addComponent(
																queryString,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(scrollPane,
												GroupLayout.DEFAULT_SIZE, 155,
												Short.MAX_VALUE)
										.addContainerGap()));
		resultsPanel.setLayout(gl_resultsPanel);

		JPanel panel_1 = new JPanel();
		mainSplit.setLeftComponent(panel_1);

		JLabel lblNewLabel = new JLabel("Tags");

		cardTags = new JTextField();
		cardTags.setColumns(10);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = bookmarkList.getSelectedIndex();
				if (cardTags.getText().trim().length() == 0
						|| cardText.getText().trim().length() == 0) {
					return;
				}
				if (activeCard != null) {
					Indexer.deleteCard(
							activeCard.getCardId() + "",
							activeCard.getBookmarkId() != null ? activeCard
									.getBookmarkId() + "" : null);
					Indexer.addCard(activeCard.getCardId() + "",
							cardTags.getText(), cardText.getText());
					activeCard = null;
				} else {
					Indexer.addCard(cardTags.getText(), cardText.getText());
				}
				DefaultTableModel dm = (DefaultTableModel) table.getModel();
				int rowCount = dm.getRowCount();
				for (int i = rowCount - 1; i >= 0; i--) {
					dm.removeRow(i);
				}
				cardTags.setText("");
				cardText.setText("");
				table.removeAll();
				updateBookmarks();
				refreshBookmarks();
				if (sel != -1) {
					bookmarkList.setSelectedIndex(sel);
				}
			}
		});

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

		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1
				.setHorizontalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																scrollPane_1,
																GroupLayout.DEFAULT_SIZE,
																520,
																Short.MAX_VALUE)
														.addGroup(
																Alignment.TRAILING,
																gl_panel_1
																		.createSequentialGroup()
																		.addComponent(
																				lblNewLabel,
																				GroupLayout.PREFERRED_SIZE,
																				30,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				cardTags,
																				GroupLayout.DEFAULT_SIZE,
																				486,
																				Short.MAX_VALUE))
														.addGroup(
																Alignment.TRAILING,
																gl_panel_1
																		.createSequentialGroup()
																		.addComponent(
																				btnSave,
																				GroupLayout.DEFAULT_SIZE,
																				410,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnReset,
																				GroupLayout.PREFERRED_SIZE,
																				104,
																				GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()));
		gl_panel_1
				.setVerticalGroup(gl_panel_1
						.createParallelGroup(Alignment.TRAILING)
						.addGroup(
								gl_panel_1
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(scrollPane_1,
												GroupLayout.DEFAULT_SIZE, 191,
												Short.MAX_VALUE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																cardTags,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																lblNewLabel))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																btnSave,
																GroupLayout.PREFERRED_SIZE,
																51,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																btnReset,
																GroupLayout.PREFERRED_SIZE,
																51,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));

		cardText = new JTextPane();
		scrollPane_1.setViewportView(cardText);
		panel_1.setLayout(gl_panel_1);

		JPanel panel = new JPanel();
		panel.setBorder(null);
		windowSplit.setLeftComponent(panel);

		delBmBtn = new JButton("Delete");
		delBmBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = bookmarkList.getSelectedIndex();
				if (sel != -1) {
					Indexer.deleteBookmark(bookmarks.get(sel).getBookmarkId()
							+ "");
					refreshBookmarks();
					if (bookmarks.isEmpty()) {
						return;
					}
					if (sel == bookmarks.size()) {
						sel = bookmarks.size() - 1;
					}
					bookmarkList.setSelectedIndex(sel);
					bookmarkList.doLayout();
				}
			}
		});

		mvBmUp = new JButton("Up");
		mvBmUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = bookmarkList.getSelectedIndex();
				if (sel != -1 && sel != 0) {
					Card bm = bookmarks.get(sel);
					Card bm2 = bookmarks.get(sel - 1);
					bookmarks.remove(sel);
					bookmarks.add(sel - 1, bm);
					updateBookmarks();
					refreshBookmarks();
					bookmarkList.setSelectedIndex(sel - 1);
				}
			}
		});

		mvBmDown = new JButton("Down");
		mvBmDown.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int sel = bookmarkList.getSelectedIndex();
				if (sel != -1 && sel != bookmarks.size() - 1) {
					Card bm = bookmarks.get(sel);
					Card bm2 = bookmarks.get(sel + 1);
					bookmarks.remove(sel);
					bookmarks.add(sel + 1, bm);
					updateBookmarks();
					refreshBookmarks();
					bookmarkList.setSelectedIndex(sel + 1);
				}
			}
		});

		bookmarkList = new JList(new DefaultListModel());
		bookmarkList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));

		bookmarkList.setCellRenderer(getRenderer());

		bookmarkList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int sel = bookmarkList.getSelectedIndex();
					if (sel != -1) {
						Card card = bookmarks.get(sel);
						card = Indexer.getCard(card.getCardId() + "",
								card.getBookmarkId() + "");
						cardText.setText(card.getText());
						cardTags.setText(card.getTags());
						activeCard = card;
					}
				}
			}
		});

		JLabel lblNewLabel_1 = new JLabel("Favorites");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.TRAILING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.TRAILING)
												.addComponent(
														bookmarkList,
														Alignment.LEADING,
														GroupLayout.DEFAULT_SIZE,
														229, Short.MAX_VALUE)
												.addComponent(
														lblNewLabel_1,
														Alignment.LEADING,
														GroupLayout.PREFERRED_SIZE,
														229,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														Alignment.LEADING,
														gl_panel.createSequentialGroup()
																.addComponent(
																		delBmBtn,
																		GroupLayout.PREFERRED_SIZE,
																		77,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		mvBmUp,
																		GroupLayout.PREFERRED_SIZE,
																		71,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		ComponentPlacement.RELATED)
																.addComponent(
																		mvBmDown,
																		GroupLayout.PREFERRED_SIZE,
																		69,
																		GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addContainerGap()
								.addComponent(lblNewLabel_1)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(delBmBtn)
												.addComponent(mvBmDown)
												.addComponent(mvBmUp))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(bookmarkList,
										GroupLayout.DEFAULT_SIZE, 440,
										Short.MAX_VALUE).addContainerGap()));
		panel.setLayout(gl_panel);
		frmTagmata.getContentPane().setLayout(groupLayout);

		SystemTray tray = SystemTray.getSystemTray();
		Image image = null;
		try {
			image = ImageIO.read(Tagmata.class.getResource("tagmata.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		TrayIcon trayIcon = new TrayIcon(image, "Tagmata");
		trayIcon.setImageAutoSize(true);
		trayIcon.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				frmTagmata.setVisible(true);
				frmTagmata.setState(java.awt.Frame.NORMAL);
			}

		});
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.err.println(e);
		}
		final PopupMenu popup = new PopupMenu();
		MenuItem exitItem = new MenuItem("     Exit     ");
		exitItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		popup.add(exitItem);
		trayIcon.setPopupMenu(popup);
		ImageIcon img = new ImageIcon(Tagmata.class.getResource("tagmata.png"));
		frmTagmata.setIconImage(img.getImage());
		
		menuBar = new JMenuBar();
		frmTagmata.setJMenuBar(menuBar);
		
		mnTagmata = new JMenu("Options");
		menuBar.add(mnTagmata);
		
		mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				About about = new About(frmTagmata);
				about.setVisible(true);
				
			}
		});
		mnTagmata.add(mntmAbout);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnTagmata.add(mntmExit);
		refreshBookmarks();
	}

	private ListCellRenderer getRenderer() {
		return new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel listCellRendererComponent = (JLabel) super
						.getListCellRendererComponent(list, value, index,
								isSelected, cellHasFocus);
				listCellRendererComponent.setBorder(BorderFactory
						.createMatteBorder(0, 0, 1, 0, Color.BLACK));
				return listCellRendererComponent;
			}
		};
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
		JMenuItem bookmarkItem = new JMenuItem("Bookmark");
		deleteItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				int result = JOptionPane.showConfirmDialog(frmTagmata,
						"Delete card?", "Confirm Deletion",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == 0) {
					String cardId = table.getModel()
							.getValueAt(table.getSelectedRow(), 0).toString();
					Indexer.deleteCard(cardId, null);
					DefaultTableModel dm = (DefaultTableModel) table.getModel();
					dm.removeRow(table.getSelectedRow());
					if (activeCard != null
							&& activeCard.getCardId().equals(cardId)) {
						cardText.setText("");
						cardTags.setText("");
						activeCard = null;
					}
				}
			}

		});
		bookmarkItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String cardId = table.getModel()
						.getValueAt(table.getSelectedRow(), 0).toString();
				for (Card bookmark : bookmarks) {
					if (bookmark.getCardId().equals(cardId)) {
						return;
					}
				}
				Indexer.addBookmark(cardId, bookmarks.size());
				refreshBookmarks();
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
		popupMenu.add(bookmarkItem);
		table.setComponentPopupMenu(popupMenu);
		table.doLayout();
	}

	private void searchCards(String terms) {
		if (terms.trim().length() == 0) {
			int result = JOptionPane
					.showConfirmDialog(
							frmTagmata,
							"You have entered no search terms and this will show all the cards you have indexed till now.\nThis can take a really long time if you have indexed thousands of cards. \nIf the application crashes in between, you might end up corrupting your index. \n\nContinue?",
							"No Search Terms Entered",
							JOptionPane.OK_CANCEL_OPTION);
			if (result == 2) {
				return;
			}
		}
		List<Card> cards = Indexer.getCards(terms);
		showResults(cards);
	}

	public void refreshBookmarks() {
		bookmarks = Indexer.getBookmarks();
		if (bookmarks.isEmpty()) {
			delBmBtn.setEnabled(false);
			mvBmUp.setEnabled(false);
			mvBmDown.setEnabled(false);
		} else {
			delBmBtn.setEnabled(true);
			mvBmUp.setEnabled(true);
			mvBmDown.setEnabled(true);
		}
		DefaultListModel model = (DefaultListModel) bookmarkList.getModel();
		model.removeAllElements();
		for (Card bookmark : bookmarks) {
			model.addElement(bookmark.snapshot());
		}
	}

	public void updateBookmarks() {
		int i = 0;
		for (Card bm : bookmarks) {
			bm.setPos(i++);
		}
		Indexer.updateBookmarks(bookmarks);
	}
}
