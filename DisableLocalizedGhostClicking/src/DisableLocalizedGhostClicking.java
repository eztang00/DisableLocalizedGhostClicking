

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * where I mentioned it:
 * http://qnundrum.com/question/1368516
 * http://answers.microsoft.com/en-us/windows/forum/windows_8-tms/ghost-touches-on-touchscreen-samsung-laptop-np/0dc17394-ed88-4f43-ba1f-fa7542ef7f1e?page=2
 * https://youtu.be/D7nBiNtaTJk
 * https://www.youtube.com/watch?v=sc4PplE4RhA
 * https://youtu.be/O2tVT9dDnzE
 * https://youtu.be/5PR7N7F19ew
 */

/**
 * public domain
 */
public class DisableLocalizedGhostClicking extends JFrame {


	static Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	static JFrame blockingBackground = new JFrame() {
		{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setUndecorated(true);
			setSize(screen);
		}
	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					blockingBackground.setVisible(true);
					step1();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	static void ex(int i) {
		switch (i) {
		case JOptionPane.CANCEL_OPTION:
		case JOptionPane.CLOSED_OPTION:
			System.exit(0);
			break;
		}
	}
	static void info(String s) {
		long l = System.currentTimeMillis()+2000;
		while (true) {	
			int i = JOptionPane.showConfirmDialog(null, s, "prompt", JOptionPane.OK_CANCEL_OPTION);
			if (System.currentTimeMillis() < l) {
				continue;
			}
			switch (i) {
			case JOptionPane.CANCEL_OPTION:
			case JOptionPane.CLOSED_OPTION:
				System.exit(0);
				break;
			default: return;
			}
		
		}
	}

	static void step1() {
		info("If your computer touchscreen is clicking on its own,\nand always within a certain region, this program tries to fix that.\nIt'll try to disable the touchscreen in those regions. Press enter to continue.");
		JOptionPane.showMessageDialog(null, "First, we check if partially clickable windows are supported. Click anywhere in the green window when it pops up.");
		final JFrame partClickable = new JFrame() {
			{
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setUndecorated(true);
				setBackground(new Color(0,0,0,0));
				setSize(screen);
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						JOptionPane.showMessageDialog(null, "Partially clickabe windows not supported.");
						System.exit(0);
					}
				});
			}
			public void paint(Graphics g) {
				g.drawRect(0, 0, 10, 10);
				g.drawRect(screen.width-10, 0, 10, 10);
				g.drawRect(0, screen.height-10, 10, 10);
			}
			
		};
		final JFrame f = new JFrame() {
			{
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setUndecorated(true);
				setSize(screen);
				addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						dispose();
						partClickable.dispose();
						step2();
					}
				});
			}
			public void paint(Graphics g) {
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, screen.width, screen.height);
			}
			
		};
		partClickable.setVisible(true);
		f.setVisible(true);
		f.toFront();
		partClickable.toFront();
	}
	static long timeout;
	static boolean[] step3Start = {false};
	static void step2() {
		JOptionPane.showMessageDialog(null, "Click or tap the regions on the screen you want to disable.");
		blockingBackground.setBackground(new Color(0,0,0,1));
		final BufferedImage im = new BufferedImage(screen.width, screen.height, BufferedImage.TYPE_INT_ARGB);
		final JFrame regionFinder = new JFrame() {
			int r = 50;
			{
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setUndecorated(true);
				setBackground(new Color(0, 0, 0, 128+63));
				setSize(screen);
				MouseAdapter m = new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						Graphics g = im.getGraphics();
						g.setColor(Color.WHITE);
						g.fillOval(e.getX()-r, e.getY()-r, 2*r+1, 2*r+1);
						g.dispose();
						repaint();
					}
					public void mouseDragged(MouseEvent e) {
						mousePressed(e);
					}
				};
				addMouseListener(m);
				addMouseMotionListener(m);
				addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent e) {
						switch (e.getKeyCode()) {
						
						case KeyEvent.VK_ENTER:
							dispose();
							synchronized (step3Start) {
								if (step3Start[0]) {
									return;
								}
								step3Start[0] = true;
							}
							step3(im);
							return;
						case KeyEvent.VK_MINUS:
							r--;
							break;
						case KeyEvent.VK_PLUS:
						case KeyEvent.VK_EQUALS:
							r++;
							break;
						case KeyEvent.VK_DELETE:
							Graphics2D g = (Graphics2D) im.getGraphics();
							g.setComposite(AlphaComposite.Clear);
							g.fillRect(0, 0, screen.width, screen.height);
							g.dispose();
							repaint();
							break;
						case KeyEvent.VK_BACK_SPACE:
							timeout = System.currentTimeMillis()+5*60*1000;
							break;
						}
					}
				});
			}
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(im, 0, 0, null);
				g.setColor(Color.YELLOW);
				g.setXORMode(Color.RED);
				g.drawString("Click where you want to disable. Press enter when done. Or wait 5 minutes.", screen.width/4, screen.height/3);
				g.drawString("If dots too big press \"-\" to decrease. Pressing delete clears screen. If short on time, press backspace to reset 5 min.", screen.width/4, screen.height/3+50);
			}
		};
		regionFinder.setVisible(true);
		timeout = System.currentTimeMillis()+5*60*1000;
		new Thread() {
			public void run() {
				while (true) {
					long time = timeout-System.currentTimeMillis();
					if (time>0) {
						try {
							Thread.sleep(time);
						} catch (InterruptedException e) {
							System.exit(0);
						}
					} else {
						synchronized (step3Start) {
							if (step3Start[0]) {
								return;
							}
							step3Start[0] = true;
						}
						regionFinder.dispose();
						step3(im);
						return;
					}
				}
			}
		}.start();
	}
	static synchronized void step3(final BufferedImage im) {
		Graphics2D g = (Graphics2D) im.getGraphics();
		g.setComposite(AlphaComposite.SrcIn);
		g.setColor(new Color(255, 255, 0, 0x55));
		g.fillRect(0, 0, screen.width, screen.height);
		g.dispose();
		Curtain c = new Curtain(im);
		c.setVisible(true);
		blockingBackground.dispose();
		new DisableLocalizedGhostClicking(c).setVisible(true);
	}
	
	
	private JPanel mainPane;
	private JSpinner minL;
	private JSpinner maxL;
	private JSpinner minR;
	private JSpinner maxR;
	/**
	 * Create the frame.
	 */
	public DisableLocalizedGhostClicking(final Curtain c) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPane = new JPanel();
		getContentPane().add(mainPane);
		
		final JButton btnClickHereTo = new JButton("Click here to compare your click durations with your touchscreen durations.");
		btnClickHereTo.addMouseListener(new MouseListener() {
			long t = 0;
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				btnClickHereTo.setText("" + (e.getWhen()-t) + (e.getButton()==MouseEvent.BUTTON1 ? "" : e.getButton()==MouseEvent.BUTTON2 ? "r" : "R"));
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				t = e.getWhen();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		mainPane.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblCloseThisWindow = new JLabel("<html>Close this window to destroy the program.<br />Or press \"Done\" to hide it.</html>");
		mainPane.add(lblCloseThisWindow);

		JLabel lbl2 = new JLabel("<html>Once hidden, it can only be closed from task manager (maybe javaw.exe),<br />or maybe by right clicking an icon on taskbar below.</html>");
		mainPane.add(lbl2);
		
		final JCheckBox chckbxConstantlyEnsureOn = new JCheckBox("constantly ensure on front of special windows e.g. taskbar");
		chckbxConstantlyEnsureOn.setSelected(c.constantToFront);
		chckbxConstantlyEnsureOn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.constantToFront = chckbxConstantlyEnsureOn.isSelected();
			}
		});
		mainPane.add(chckbxConstantlyEnsureOn);

		final JCheckBox chckbxDrawArtificial = new JCheckBox("draw artifical cursor (may accidentally block clicks)");
		
		final JCheckBox chckbxTryMoveMouse = new JCheckBox("try move mouse cursor back where it was before ghost click");
		if (c.robot==null) {
			chckbxTryMoveMouse.setEnabled(false);
		}
		chckbxTryMoveMouse.setSelected(c.moveBackCursor);
		chckbxTryMoveMouse.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.moveBackCursor = chckbxTryMoveMouse.isSelected();
				chckbxDrawArtificial.setEnabled(c.moveBackCursor);
				c.repaint();
			}
		});
		mainPane.add(chckbxTryMoveMouse);

		chckbxDrawArtificial.setEnabled(c.moveBackCursor);
		chckbxDrawArtificial.setSelected(c.drawArtificalMouseCursor);
		chckbxDrawArtificial.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.drawArtificalMouseCursor = chckbxDrawArtificial.isSelected();
				c.repaint();
			}
		});
		mainPane.add(chckbxDrawArtificial);
		
		final JCheckBox chckbxAllowMouseOr = new JCheckBox("allow mouse or touchpad (not touchscreen) to click in region");
		if (c.robot==null) {
			chckbxAllowMouseOr.setEnabled(false);
		}
		chckbxAllowMouseOr.setSelected(c.allowMouseTouchpad);
		chckbxAllowMouseOr.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				c.allowMouseTouchpad = chckbxAllowMouseOr.isSelected();
				minL.setEnabled(c.allowMouseTouchpad);
				minR.setEnabled(c.allowMouseTouchpad);
				maxL.setEnabled(c.allowMouseTouchpad);
				maxR.setEnabled(c.allowMouseTouchpad);
			}
		});
		mainPane.add(chckbxAllowMouseOr);
		
		JLabel lblDelayRangeTo = new JLabel("<html>Some touchpads have a very consistent click duration to distinguish their clicks.<br />Click durations to allow in region:</html>");
		mainPane.add(lblDelayRangeTo);
		
		JSplitPane splitPane_3 = new JSplitPane();
		splitPane_3.setResizeWeight(0.5);
		mainPane.add(splitPane_3);
		
		JLabel lblMinAllowedClick = new JLabel("min allowed left click duration");
		splitPane_3.setLeftComponent(lblMinAllowedClick);
		
		JLabel lblMinAllowedRight = new JLabel("min allowed right click duration");
		splitPane_3.setRightComponent(lblMinAllowedRight);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		mainPane.add(splitPane);
		
		minL = new JSpinner();
		minL.setEnabled(false);
		minL.setValue(c.minL);
		minL.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				c.minL = (int) minL.getValue();
			}
		});
		splitPane.setLeftComponent(minL);
		
		minR = new JSpinner();
		minR.setEnabled(false);
		minR.setValue(c.minR);
		minR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				c.minR = (int) minR.getValue();
			}
		});
		splitPane.setRightComponent(minR);
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.5);
		mainPane.add(splitPane_1);
		
		JLabel lblMaxAllowedClick = new JLabel("max allowed left click duration");
		splitPane_1.setLeftComponent(lblMaxAllowedClick);
		
		JLabel lblMaxAllowedRight = new JLabel("max allowed right click duration");
		splitPane_1.setRightComponent(lblMaxAllowedRight);
		
		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setResizeWeight(0.5);
		mainPane.add(splitPane_2);
		
		maxL = new JSpinner();
		maxL.setEnabled(false);
		maxL.setValue(c.maxL);
		maxL.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				c.maxL = (int) maxL.getValue();
			}
		});
		splitPane_2.setLeftComponent(maxL);
		
		maxR = new JSpinner();
		maxR.setEnabled(false);
		maxR.setValue(c.maxR);
		maxR.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				c.maxR = (int) maxR.getValue();
			}
		});
		splitPane_2.setRightComponent(maxR);

		mainPane.add(btnClickHereTo);
		
		JButton btnDone = new JButton(new AbstractAction("Done") {
			public void actionPerformed(ActionEvent e) {
				c.isDone = true;
				Graphics2D g = (Graphics2D) c.im.getGraphics();
				g.setComposite(AlphaComposite.SrcIn); //alpha multiply 0x03/0xFF with 0x55/0xFF = 0x01/0xFF
				g.setColor(new Color(255, 255, 255, 3));
				g.fillRect(0, 0, screen.width, screen.height);
				g.dispose();
				dispose();
				c.setVisible(false);
				c.setVisible(true);
				JOptionPane.showMessageDialog(null, "One more thing: if your area is too small, not every ghost click is caught,\nand some may \"pass through,\" especially when moving your cursor.\nSometimes pressing Ctrl+Alt+Delete fixes it for a second.\nOther issues: sometimes the program exits on its own.\nSometimes the ghost clicking does a ghost zoom, which deselects any window you're on.");
			}
		});
		mainPane.add(btnDone);
		mainPane.setSize(mainPane.getPreferredSize());
		pack();
		getContentPane().setLayout(null);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				setLocation(e.getLocationOnScreen());
			}
		});
	}

}

/**
 * public domain
 */
class Curtain extends JFrame { //curtain 


	/**
	 * Launch the application.
	 */

	volatile Point lastSure = new Point();
	volatile Point lastUnsure = new Point();
	volatile long mousePressedTime;
	volatile boolean mousePressed;
	volatile Point wherePressed;
	Robot robot;
	BufferedImage im;
	static final int RADIUS = 50;
	static final long FPS = 50;
	volatile int minL=100;
	volatile int maxL=125;
	volatile int minR=100;
	volatile int maxR=125;
	volatile boolean constantToFront= true;
	volatile boolean moveBackCursor= true;
	volatile boolean drawArtificalMouseCursor= false;
	volatile boolean allowMouseTouchpad= false;
	volatile boolean isDone;
	/**
	 * Create the frame.
	 */
	public Curtain(final BufferedImage im) {
		try {
			robot = new Robot();
		} catch (Exception e1) {
			//no robot
			moveBackCursor = false;
			allowMouseTouchpad= false;
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0,0,im.getWidth(),im.getHeight());
		this.im = im;
		setUndecorated(true);
		setBackground(new Color(0, 0, 0, 0));
		setFocusableWindowState(false);
		setAlwaysOnTop(true);
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (mousePressed = false) {
					return;
				}
				mousePressed = false;
				if (!allowMouseTouchpad) {
					if (moveBackCursor) {
						robot.mouseMove(lastSure.x, lastSure.y);
					}
					return;
				}
				long l = e.getWhen()-mousePressedTime;
				Point p = e.getLocationOnScreen();
				int min, max, buttonMask;
				if (e.getButton()==MouseEvent.BUTTON1) {
					buttonMask = InputEvent.BUTTON1_DOWN_MASK;
					min = minL;
					max = maxL;
				} else {
					buttonMask = e.getButton()==MouseEvent.BUTTON2 ? InputEvent.BUTTON2_DOWN_MASK : InputEvent.BUTTON3_DOWN_MASK;
					min = minR;
					max = maxR;
				}
				if (l >= min && l <=max && p.equals(wherePressed)) {
					if (lastUnsure.distanceSq(p) < RADIUS*RADIUS) {
						lastSure = p;
						im.setRGB(p.x, p.y, 0x00000000);
//						pan.paintImmediately(p.x, p.y, 1, 1);
						Curtain.this.setBackground(new Color(0, 0, 0, 0));
						robot.mousePress(buttonMask);
						robot.mouseRelease(buttonMask);
						im.setRGB(p.x, p.y, 0x01FFFFFF);
//						pan.paintImmediately(p.x, p.y, 1, 1);
						Curtain.this.setBackground(new Color(0, 0, 0, 0));
						setVisible(true);
					} else {
						if (moveBackCursor) {
							robot.mouseMove(lastSure.x, lastSure.y);
						}
					}
					lastUnsure = p;
				} else {
					if (moveBackCursor) {
						robot.mouseMove(lastSure.x, lastSure.y);
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePressedTime = e.getWhen();
				mousePressed = true;
				wherePressed = e.getLocationOnScreen();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		new Thread() {
			public void run() {
				try {
					while (true) {
						Thread.sleep(FPS);
						if (getExtendedState() != Frame.NORMAL) {
							setExtendedState(Frame.NORMAL);
						}
						if (constantToFront) {
							toFront();
						}
						if (moveBackCursor) {
							if (mousePressed && System.currentTimeMillis()-mousePressedTime >= 150) { //for ghost dragging
								robot.mouseMove(lastSure.x, lastSure.y);
								mousePressed = false;
							} else {
								lastUnsure = MouseInfo.getPointerInfo().getLocation();
								if (Curtain.this.im.getRGB(lastUnsure.x, lastUnsure.y)==0x00000000 || lastUnsure.distanceSq(lastSure) < RADIUS*RADIUS) {
									Point prevLastSure = lastSure;
									lastSure = lastUnsure;
									if (drawArtificalMouseCursor&&moveBackCursor) {
										repaint(prevLastSure.x, prevLastSure.y, mousePointer.getWidth(), mousePointer.getHeight());
										repaint(lastSure.x, lastSure.y, mousePointer.getWidth(), mousePointer.getHeight());
									}
								}
							}
						}
						if (isDone && !constantToFront && !moveBackCursor) {
							break;
						}
					}
				} catch (Exception e) {
					System.exit(1);
				}
			}
		}.start();
	}
	public void paint(Graphics g) {
		if (drawArtificalMouseCursor&&moveBackCursor) {
			super.paint(g);
			g.drawImage(mousePointer, lastSure.x, lastSure.y, null);
		} else if (!isDone) {
			super.paint(g);
		}
		g.drawImage(im, 0, 0, null);
	}
	BufferedImage mousePointer = new BufferedImage(12, 21, BufferedImage.TYPE_INT_ARGB) {{
		setRGB(0, 0, 12, 21, replaceAll(replaceAll(replaceAll(new int[] {
				1,1,1,1,1,1,1,1,1,1,1,1,
				8,8,1,1,1,1,1,1,1,1,1,1,
				8,7,8,1,1,1,1,1,1,1,1,1,
				8,7,7,8,1,1,1,1,1,1,1,1,
				8,7,7,7,8,1,1,1,1,1,1,1,
				8,7,7,7,7,8,1,1,1,1,1,1,
				8,7,7,7,7,7,8,1,1,1,1,1,
				8,7,7,7,7,7,7,8,1,1,1,1,
				8,7,7,7,7,7,7,7,8,1,1,1,
				8,7,7,7,7,7,7,7,7,8,1,1,
				8,7,7,7,7,7,7,7,7,7,8,1,
				8,7,7,7,7,7,7,8,8,8,8,8,
				8,7,7,7,8,7,7,8,1,1,1,1,
				8,7,7,8,8,7,7,8,1,1,1,1,
				8,7,8,1,1,8,7,7,8,1,1,1,
				8,8,1,1,1,8,7,7,8,1,1,1,
				8,1,1,1,1,1,8,7,7,8,1,1,
				1,1,1,1,1,1,8,7,7,8,1,1,
				1,1,1,1,1,1,1,8,7,7,8,1,
				1,1,1,1,1,1,1,8,7,7,8,1,
				1,1,1,1,1,1,1,1,8,8,1,1,
		}, 8, 0xFF000000), 7, 0xFFF0F0F0), 1, 0x00000000), 0, 12);
	}};
	static int[] replaceAll(int[] array, int from, int to) {
		array = array.clone();
		for (int i=0; i<array.length; i++) {
			if (array[i] == from) {
				array[i] = to;
			}
		}
		return array;
	}
}
