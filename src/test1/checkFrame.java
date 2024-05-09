package test1;
import DB.DB;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.JButton;

public class checkFrame {
	private final Image BG_IMAGE = new ImageIcon("./image/bg_checkFrame.jpg").getImage();
	private JFrame frame;
	private List<Map<String, Object>> data = new ArrayList<>();
	private user my;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
	}

	/**
	 * Create the application.
	 */
	public checkFrame(user my) {
		this.my = my;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();

		ImagePanel bgPanel = new ImagePanel(this.BG_IMAGE);
		frame.setSize(bgPanel.getWidth(), bgPanel.getHeight());
		frame.getContentPane().add(bgPanel);
		bgPanel.setLayout(null);

		this.drawInfoButton(bgPanel);
		this.drawCheckButton(bgPanel);
		this.drawBackButton(bgPanel);
		this.drawTable(bgPanel);

		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void drawTable(ImagePanel bgPanel) {
		Object[][] bookList = this.getData();
		String[] header = { "영화 제목", "시작시간", "날짜" };
		JTable table = new JTable(bookList, header);
		short gap = 50;

		table.setDefaultEditor(Object.class, null);
		table.setPreferredScrollableViewportSize(new Dimension(450, 63));
		table.setFillsViewportHeight(true);
		table.setFont(new Font("Sans-serif", Font.BOLD, 20));
		table.setRowHeight(table.getRowHeight() + gap);
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = table.rowAtPoint(evt.getPoint());
				int col = table.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					dispose();
					reservationCancelFrame detailFrame = new reservationCancelFrame(my, (String) data.get(row).get("groupId"));
					detailFrame.setVisible(true);
				}
			}
		});

		this.setHeaderConfig(table);
		JScrollPane sp = new JScrollPane(table);
		sp.setBounds(500, 125, 800, 400);
		bgPanel.add(sp);
	}

	public Object[][] getData() {
		DB db = new DB();

		ArrayList<String> screenList = new ArrayList<>();
		List<Map<String, Object>> info = db.query(String.format(
				"SELECT " +
						"mov.title," +
						"scr.time," +
						"scr.screenDate," +
						"res.groupId," +
						"res.ReservId," +
						"res.AccountId," +
						"res.canceled" +
				" FROM " +
						"theater.reservation AS res" +
								" JOIN " +
						"theater.movie AS mov ON mov.MovieId = res.MovieId" +
								" JOIN " +
						"theater.screening AS scr ON scr.ScreeningId = res.ScreeningId" +
				" WHERE " +
						"AccountId = %s AND  res.canceled = 0" +
				" group by res.ReservId;"
				, user.accountId));

		Object[][] movieList = new Object[info.size()][3];
		int i = 0;


		for (Map<String, Object> s : info) {
			String groupId = (String) s.get("groupId");
			if (screenList.contains(groupId) == false) {

				Object[] movie = { s.get("title"), s.get("time"), s.get("screenDate"), groupId };
				movieList[i] = movie;
				i++;
				screenList.add(groupId);
				data.add(s);
			}
		}
		return movieList;
	}

	public void setHeaderConfig(JTable table) {
		JTableHeader _h = table.getTableHeader();

		Font headerStyle = new Font("Sans-serif", Font.BOLD, 20);
		_h.setFont(headerStyle);
		_h.setForeground(Color.WHITE);
		_h.setBackground(Color.BLACK);

		// 중앙정렬
		_h.setAlignmentX(Component.CENTER_ALIGNMENT);

		// 여백조절
		_h.setPreferredSize(new Dimension(_h.getWidth(), 50));
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) _h.getDefaultRenderer();
		renderer.setHorizontalAlignment((int) JLabel.CENTER_ALIGNMENT);
	}

	public void drawInfoButton(ImagePanel bgPanel) {
		JButton button = this.makeImageButton("info1.jpg", "info2.jpg");
		button.setBounds(100, 360, 225, 54);
		button.setBorderPainted(false);
		button.setFocusPainted(false);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				mypageFrame mypageframe = new mypageFrame(my);
				mypageframe.setVisible(true);
			}
		});

		bgPanel.add(button);
	}

	public void drawCheckButton(ImagePanel bgPanel) {
		JButton button = this.makeImageButton("check2.jpg", "check2.jpg");
		button.setBounds(100, 450, 225, 54);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		bgPanel.add(button);
	}

	public void drawBackButton(ImagePanel bgPanel) {
		JButton button = new JButton("");
		button.setIcon(new ImageIcon("./image/back_btn.png"));
		button.setBounds(1245, 10, 85, 85);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setBackground(Color.WHITE);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame m = new mainFrame(my);
				m.setVisible(true);
				frame.dispose();
			}
		});

		bgPanel.add(button);
	}

	/**
	 * 이미지 버튼 생성
	 *
	 * @param img      기본 이미지명
	 * @param hoverImg hover 이미지명
	 * @return
	 */
	public JButton makeImageButton(String img, String hoverImg) {
		Icon IMG = new ImageIcon("./image/" + img);
		Icon IMG_HOVER = new ImageIcon("./image/" + hoverImg);
		JButton btn = new JButton();

		btn.setIcon(IMG);
		btn.setRolloverIcon(IMG_HOVER);

		return btn;
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	public void dispose() {
		frame.dispose();
	}
}