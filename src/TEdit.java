import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

//MD5ダイジェスト値を計算
class MD5 {
	// バッファ全体から計算
	public static byte[] digest(byte[] bytes) throws NoSuchAlgorithmException {
		return digest(bytes, 0, bytes.length);
	}

	// バッファの途中から計算
	public static byte[] digest(byte[] bytes, int off, int len)
			throws NoSuchAlgorithmException {
		if (bytes == null || bytes.length == 0) {
			throw new IllegalArgumentException("byte size is 0 or null"); //$NON-NLS-1$
		}
		MessageDigest digest = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
		digest.update(bytes, off, len);
		return digest.digest();
	}
}

// Tukusiの設定情報クラス。setter/getterがあるだけ
class TukusiConfig {
	public TukusiConfig(String rootpath, Font font, String sep,
			boolean isSearchCaseSensitive, String encode, ArrayList<String> exts) {
		super();
		this.rootpath = rootpath;
		this.font = font;
		this.sep = sep;
		this.isSearchCaseSensitive = isSearchCaseSensitive;
		this.encode = encode;
		this.exts = exts;
	}

	public TukusiConfig(String rootpath, Font font, String sep,
			boolean isSearchCaseSensitive, String encode) {
		super();
		this.rootpath = rootpath;
		this.font = font;
		this.sep = sep;
		this.isSearchCaseSensitive = isSearchCaseSensitive;
		this.encode = encode;
		// デフォルトの追加拡張子を生成
		this.exts = new ArrayList<String>();
		addTextExt("txt"); // TextFile //$NON-NLS-1$
		addTextExt("xml"); // XML //$NON-NLS-1$
		addTextExt("asm"); // Assembler //$NON-NLS-1$
		addTextExt("c"); // C //$NON-NLS-1$
		addTextExt("cpp"); // C++ //$NON-NLS-1$
		addTextExt("hpp"); // C++ //$NON-NLS-1$
		addTextExt("h"); // C/C++ //$NON-NLS-1$
		addTextExt("java"); // Java //$NON-NLS-1$
		addTextExt("js"); // JavaScript //$NON-NLS-1$
		addTextExt("hs"); // Haskell //$NON-NLS-1$
		addTextExt("fs"); // FSharp //$NON-NLS-1$
		addTextExt("pl"); // Perl //$NON-NLS-1$
		addTextExt("scm"); // Scheme //$NON-NLS-1$
		addTextExt("php"); // PHP //$NON-NLS-1$
		addTextExt("py"); // Python //$NON-NLS-1$
		addTextExt("rb"); // Ruby //$NON-NLS-1$
		addTextExt("cgi"); // perl php etc //$NON-NLS-1$
		addTextExt("d"); // D //$NON-NLS-1$
		addTextExt("l"); // Lisp //$NON-NLS-1$
		addTextExt("ml"); // OCaml //$NON-NLS-1$
		addTextExt("el"); // Emacs Lisp //$NON-NLS-1$
		addTextExt("e"); // Eiffel //$NON-NLS-1$
		addTextExt("bat"); // Windows Batch File //$NON-NLS-1$
		addTextExt("sh"); // Shellscript //$NON-NLS-1$
		addTextExt("vb"); // Visual Basic //$NON-NLS-1$
		addTextExt("cs"); // C# //$NON-NLS-1$
		addTextExt("as"); // ActionScript //$NON-NLS-1$
		addTextExt("htm"); // HTML //$NON-NLS-1$
		addTextExt("html"); // HTML //$NON-NLS-1$
		addTextExt("css"); // CSS //$NON-NLS-1$
		addTextExt(".vim"); // vim //$NON-NLS-1$
		addTextExt(".emacs"); // emacs //$NON-NLS-1$
		addTextExt(".bashrc"); // bash //$NON-NLS-1$
	}

	public static final String filer = "pcmanfm"; //$NON-NLS-1$
	public static final String editor = "emacs"; //$NON-NLS-1$
	private String rootpath; // ルートのパス
	private Font font; // フォント
	private String sep; // アプリケーションの改行コード
	private boolean isSearchCaseSensitive; // 大文字小文字を無視した検索をするか
	private String encode; // エンコード
	private ArrayList<String> exts; // テキストファイルとして処理する拡張子

	// システムの改行コード
	// private static final String syssep =
	// System.getProperty("line.separator");

	public ArrayList<String> getExts() {
		return this.exts;
	}

	public void setExts(ArrayList<String> exts) {
		this.exts = exts;
	}

	public String getEncode() {
		return this.encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public boolean isSearchCaseSensitive() {
		return this.isSearchCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean isSearchCaseSensitive) {
		this.isSearchCaseSensitive = isSearchCaseSensitive;
	}

	public String getRootpath() {
		return this.rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public Font getFont() {
		return this.font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getSep() {
		return this.sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}

	public boolean addTextExt(String extname) {
		return this.exts.add("." + extname); //$NON-NLS-1$
	}

	// これに追加されているテキストタイプのファイルがツリーに表示される
	public boolean isTextExt(String filename) {
		if (this.exts == null)
			return false;
		// 拡張子はcaseInsensitiveに判定しないとバグ
		for (String ext : this.exts) {
			if (filename.toLowerCase().endsWith(ext))
				return true;
		}
		return false;
	}
}

class Tukusi extends JPanel {
	private static final long serialVersionUID = -5480152582972274087L;
	// 設定ファイル
	TukusiConfig conf;

	// コンポーネント
	private JLabel searchText;
	JTextField search;
	private JPanel panel;
	JTree tree;
	private JScrollPane treepane, areapane;
	JTextArea area;
	UndoManager areaundo;

	DefaultMutableTreeNode root;
	DefaultTreeModel model;
	private static final int FTREE = 0;
	private static final int FAREA = 1;
	int lastFocus = FTREE;
	private static final int SALL = 0;
	private static final int STEXT = 1;
	int searchMode = SALL;

	// ディレクトリ選択ダイアログ
	JFileChooser dirchooser;

	public void setSearchCaseSensitive(boolean b) {
		this.conf.setSearchCaseSensitive(b);
	}

	public boolean isSearchCaseSensitive() {
		return this.conf.isSearchCaseSensitive();
	}

	public Tukusi(TukusiConfig c) {
		this.conf = c;
		// 処理するテキスト系の拡張子を追加
		layout_containers(new File(c.getRootpath()));
		prepare_actions();

		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.getPath()
						.getLastPathComponent();
				if (n.isLeaf()) {
					String filename = treepath2path(Tukusi.this.tree
							.getSelectionPath());
					if (Tukusi.this.conf.isTextExt(filename)) {
						setFileContent(filename);
						Tukusi.this.area.setCaretPosition(0);
					} else if (filename.endsWith(".chi")) { //$NON-NLS-1$
						String pass = openPasswordBox();
						if (pass != null) {
							setDecodedFileContent(filename, pass);
							Tukusi.this.area.setCaretPosition(0);
						}
					}
				} else {
					// ディレクトリの場合はテキストをクリアする
					Tukusi.this.area.setText(""); //$NON-NLS-1$
				}
			}
		});

		this.tree.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				//
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (((e.getModifiers() & ActionEvent.CTRL_MASK) != 0)
						&& ((e.getModifiers()) & ActionEvent.SHIFT_MASK) != 0) {
					// Ctrl+Shift+any
					switch (keycode) {
					case KeyEvent.VK_N:
						createNewNode();
						e.consume();
						break;
					default:
					}
				} else if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
					// Ctrl+any
					switch (keycode) {
					case KeyEvent.VK_F:
						Tukusi.this.lastFocus = FTREE;
						setSearchMode(SALL);
						focusSearch();
						e.consume();
						break;
					default:
					}
				} else if (((e.getModifiers()) & ActionEvent.SHIFT_MASK) != 0) {
					// Shift+any

				} else {
					// No modifier
					switch (keycode) {
					case KeyEvent.VK_ENTER:
						Tukusi.this.lastFocus = FTREE;
						if (!nodeIsDir((DefaultMutableTreeNode) Tukusi.this.tree
								.getLastSelectedPathComponent())) {
							Tukusi.this.area.requestFocus();
						}
						e.consume();
						break;
					case KeyEvent.VK_DELETE:
						deleteNode((DefaultMutableTreeNode) Tukusi.this.tree
								.getLastSelectedPathComponent());
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//

			}
		});
		this.area.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (keycode == KeyEvent.VK_ESCAPE) {
					Tukusi.this.lastFocus = FAREA;
					Tukusi.this.tree.requestFocus();
					e.consume();
				} else if (keycode == KeyEvent.VK_S
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					try {
						DefaultMutableTreeNode cur = (DefaultMutableTreeNode) Tukusi.this.tree
								.getLastSelectedPathComponent();
						if (Tukusi.this.conf.isTextExt(node2path(cur))) {
							saveCurrent();
							msgbox("ファイルを保存しました"); //$NON-NLS-1$
						} else {
							msgbox("このファイル形式は保存できません"); //$NON-NLS-1$
						}
						Tukusi.this.area.requestFocus();
					} catch (IOException e1) {
						msgbox("ファイルの保存に失敗しました"); //$NON-NLS-1$
						// e1.printStackTrace();
						Tukusi.this.area.requestFocus();
					}
				} else if (keycode == KeyEvent.VK_N
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					createNewNode();
					e.consume();
				} else if (keycode == KeyEvent.VK_F
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					Tukusi.this.lastFocus = FAREA;
					setSearchMode(STEXT);
					focusSearch();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//

			}

		});

		this.model.addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				DefaultMutableTreeNode node;
				node = (DefaultMutableTreeNode) (e.getTreePath()
						.getLastPathComponent());
				try {
					int index = e.getChildIndices()[0];
					node = (DefaultMutableTreeNode) (node.getChildAt(index));
				} catch (NullPointerException exc) {
					//
				}
			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				//

			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
				//

			}

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				//

			}
		});

		this.search.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				//

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (keycode == KeyEvent.VK_ENTER
						&& e.getModifiers() == ActionEvent.SHIFT_MASK) {
					if (Tukusi.this.searchMode == SALL) {
						if (!searchAll(Tukusi.this.search.getText(), false)) {
							msgbox(Tukusi.this.search.getText()
									+ Tukusi.this.conf.getSep() + "は見つかりませんでした"); //$NON-NLS-1$
						} else {
							Tukusi.this.lastFocus = FAREA; // 見つかったところに戻れるようにする
						}
					} else {
						if (!searchLeafNode(Tukusi.this.search.getText(),
								(DefaultMutableTreeNode) Tukusi.this.tree
										.getLastSelectedPathComponent(),
								Tukusi.this.area.getSelectionStart(), false,
								true)) {
							msgbox(Tukusi.this.search.getText()
									+ Tukusi.this.conf.getSep() + "は見つかりませんでした"); //$NON-NLS-1$
						}
					}
					e.consume();
				} else if (keycode == KeyEvent.VK_ENTER) {
					if (Tukusi.this.searchMode == SALL) {
						if (!searchAll(Tukusi.this.search.getText(), true)) {
							msgbox(Tukusi.this.search.getText()
									+ Tukusi.this.conf.getSep() + "は見つかりませんでした"); //$NON-NLS-1$
						} else {
							Tukusi.this.lastFocus = FAREA; // 見つかったところに戻れるようにする
						}
					} else {
						if (!searchLeafNode(Tukusi.this.search.getText(),
								(DefaultMutableTreeNode) Tukusi.this.tree
										.getLastSelectedPathComponent(),
								Tukusi.this.area.getSelectionEnd(), true, true)) {
							msgbox(Tukusi.this.search.getText()
									+ Tukusi.this.conf.getSep() + "は見つかりませんでした"); //$NON-NLS-1$
						}
					}
					e.consume();
				} else if (keycode == KeyEvent.VK_ESCAPE) {
					switch (Tukusi.this.lastFocus) {
					case FTREE:
						Tukusi.this.tree.requestFocus();
						break;
					case FAREA:
						Tukusi.this.area.requestFocus();
						break;
					default:
						Tukusi.this.tree.requestFocus();
					}
					e.consume();
				} else if (keycode == KeyEvent.VK_F
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					focusSearch();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				//

			}
		});
	}

	void setSearchMode(int mode) {
		this.searchMode = mode;
		switch (mode) {
		case SALL:
			this.searchText.setText("全文検索"); //$NON-NLS-1$
			break;
		case STEXT:
			this.searchText.setText("テキストエリア検索"); //$NON-NLS-1$
			break;
		}
	}

	private void layout_containers(File dir) {

		this.panel = new JPanel();
		this.panel.setLayout(new GridBagLayout());

		makeRoot(dir);
		this.model = new DefaultTreeModel(this.root);

		this.searchText = new JLabel("テキストエリア検索　", SwingConstants.RIGHT); //$NON-NLS-1$
		this.search = new JTextField();

		this.tree = new JTree(this.model);
		this.tree.setEditable(true);
		this.tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.setShowsRootHandles(true);
		// ツリーの選択は1つの要素のみ
		this.tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		this.treepane = new JScrollPane();
		this.treepane.getViewport().add(this.tree);

		this.area = new JTextArea(""); //$NON-NLS-1$
		this.areapane = new JScrollPane();
		this.areapane.getViewport().add(this.area);
		this.area.setLineWrap(true);
		// アプリケーション内の改行を固定にする
		this.area.getDocument().putProperty(
				DefaultEditorKit.EndOfLineStringProperty, this.conf.getSep());
		this.areaundo = new UndoManager();
		this.area.getDocument().addUndoableEditListener(this.areaundo);

		// フォーカスがあたるのはツリーかテキスト部のみ
		this.panel.setFocusable(false);
		this.treepane.setFocusable(false);
		this.areapane.setFocusable(false);
		this.area.setFocusable(true);
		this.tree.setFocusable(true);
		this.searchText.setFocusable(false);
		this.search.setFocusable(true);
		this.setFocusable(false);

		// フォント設定
		this.area.setFont(this.conf.getFont());
		this.tree.setFont(this.conf.getFont());
		this.searchText.setFont(this.conf.getFont());
		this.search.setFont(this.conf.getFont());
		// TODO:システムのフォントも全部変更してる。もし公開するならここを直すのは必須
		updateFont(this.conf.getFont());

		// ディレクトリ選択ダイアログの設定
		this.dirchooser = new JFileChooser();
		this.dirchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		this.panel.setLayout(gbl);
		gbl.setConstraints(this.treepane, gbc);
		this.panel.add(this.treepane);

		gbc.weightx = 1;
		gbc.gridx = 1;
		gbl.setConstraints(this.areapane, gbc);
		this.panel.add(this.areapane);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbl.setConstraints(this.searchText, gbc);
		this.panel.add(this.searchText);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(this.search, gbc);
		this.panel.add(this.search);

		setLayout(new GridLayout(1, 1));
		this.add(this.panel);
	}

	// ルートノード変更
	public boolean resetRoot(File file) {
		if (file.exists() && file.isDirectory()) {
			// これがないとぬるぽする。なぜ？
			this.tree.setSelectionPath(node2treepath(this.root));

			makeRoot(file);
			this.model = new DefaultTreeModel(this.root);
			this.tree.setModel(this.model);
			if (this.root != null) {
				this.tree.setSelectionPath(node2treepath(this.root));
			}
			this.tree.requestFocus();
			return true;
		}
		return false;
	}

	@SuppressWarnings("serial")
	private void prepare_actions() {
		Action changeRoot = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tukusi.this.dirchooser
						.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int selected = Tukusi.this.dirchooser
						.showSaveDialog(Tukusi.this);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = Tukusi.this.dirchooser.getSelectedFile();
					resetRoot(file);
				}
			}
		};
		Action goNextSibling = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tukusi.this.nextSibling();
			}
		};
		Action goPreviousSibling = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Tukusi.this.previousSibling();
			}
		};

		Action relativeProgram = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime rt = Runtime.getRuntime();
					DefaultMutableTreeNode cur = (DefaultMutableTreeNode) Tukusi.this.tree
							.getLastSelectedPathComponent();
					String path = node2path(cur);
					System.out.println(path);
					File file = new File(path);
					if (!file.exists()) {
						msgbox(path + "は存在しません"); //$NON-NLS-1$
						return;
					}
					if (file.isDirectory()) {
						rt.exec(TukusiConfig.filer + " " + path); //$NON-NLS-1$
					} else {
						rt.exec(TukusiConfig.editor + " " + path); //$NON-NLS-1$
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		};

		Action areaUndo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tukusi.this.areaundo.undo();
				} catch (CannotUndoException cannot) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		Action areaRedo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Tukusi.this.areaundo.redo();
				} catch (CannotRedoException cannot) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		// アクションの追加
		ActionMap ta = this.tree.getActionMap();
		ta.put("changeRoot", changeRoot); //$NON-NLS-1$
		ta.put("goNextSibling", goNextSibling); //$NON-NLS-1$
		ta.put("goPreviousSibling", goPreviousSibling); //$NON-NLS-1$
		ta.put("relativeProgram", relativeProgram); //$NON-NLS-1$

		ActionMap aa = this.area.getActionMap();
		aa.put("undo", areaUndo); //$NON-NLS-1$
		aa.put("redo", areaRedo); //$NON-NLS-1$

		// キーバインドのカスタマイズ。keyListenerはこちらに置き換えていきたい
		InputMap ti = this.tree.getInputMap();
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
				"changeRoot"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK),
				"selectChild"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK),
				"selectParent"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
				"selectNext"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK),
				"selectPrevious"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK),
				"selectParent"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK),
				"goNextSibling"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK),
				"goPreviousSibling"); //$NON-NLS-1$
		// Ctrl+[
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET,
				ActionEvent.CTRL_MASK), "selectFirst"); //$NON-NLS-1$
		// Ctrl+]
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET,
				ActionEvent.CTRL_MASK), "selectLast"); //$NON-NLS-1$
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK),
				"relativeProgram"); //$NON-NLS-1$

		InputMap ai = this.area.getInputMap();
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK),
				"undo"); //$NON-NLS-1$
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK),
				"redo"); //$NON-NLS-1$

		// //確認用
		// for (KeyStroke k : area.getInputMap().allKeys()) {
		// System.out.print(k + "\t");
		// System.out.println(ti.get(k));
		// }
		// TODO:こっちはnullpoで動かない。また調べる
		// area.getInputMap().put(
		// KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
		// "changeRoot");
		// area.getActionMap().put("changeRoot",changeRoot);
	}

	public TreePath node2treepath(DefaultMutableTreeNode t) {
		return new TreePath(t.getPath());
	}

	String node2path(DefaultMutableTreeNode node) {
		return treepath2path(node2treepath(node));
	}

	String treepath2path(TreePath path) {
		StringBuilder b;
		b = new StringBuilder();
		b.append(this.conf.getRootpath());
		for (int i = 1; i < path.getPathCount(); ++i) {
			if (i != path.getPathCount() - 1) {
				b.append(path.getPathComponent(i).toString() + File.separator);
			} else {
				b.append(path.getPathComponent(i).toString());
			}
		}

		return b.toString();
	}

	private String getFileContent(String filename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			InputStreamReader in = new InputStreamReader(fis,
					this.conf.getEncode());
			// InputStreamReader in = new InputStreamReader(fis,"MS932");
			BufferedReader inFile = new BufferedReader(in);
			StringBuffer b;
			b = new StringBuffer();
			String s;
			while ((s = inFile.readLine()) != null) {
				// 内部で使う改行は統一
				b.append(s + this.conf.getSep());
			}
			return b.toString();
		} catch (Exception err) {
			System.out.println("ファイル読み込み失敗"); //$NON-NLS-1$
			System.out.print(err.toString());
			return null;
		}
	}

	void setFileContent(String filename) {
		String c = getFileContent(filename);
		if (c != null) {
			this.area.setText(c);
			// undo情報をクリア。これをしないと、前に開いていたテキストまで復活してしまう
			this.areaundo.discardAllEdits();
		} else {
			this.area.setText(""); //$NON-NLS-1$
		}
	}

	void setDecodedFileContent(String filename, String password) {
		try {
			this.area.setText(Chi.readCipheredFile(filename,
					"Shift_JIS", password)); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean makeRoot(File path) {
		if (!path.exists())
			return false;
		this.root = makeRootNode(path);
		// 今のところ、このソフトでは、ディレクトリ名は末尾に/がついてる必要がある
		String rootpath = path.getAbsolutePath() + File.separator;
		rootpath = rootpath.replace(File.separator + File.separator,
				File.separator);
		this.conf.setRootpath(rootpath);
		return true;
	}

	void createNewNode() {
		boolean dirty = false;
		// TOOD:ダーティフラグが立っていなければ
		if (!dirty) {
			this.area.setText(""); //$NON-NLS-1$
			DefaultMutableTreeNode curSelect, parent, newnode;
			curSelect = (DefaultMutableTreeNode) this.tree
					.getLastSelectedPathComponent();
			// 選択してるノードがディレクトリなら、その子として作成
			if (((new File(treepath2path(this.tree.getSelectionPath()))))
					.isDirectory()) {
				parent = curSelect;
			} else {
				// 選択してるノードもファイルの場合、兄弟ノードとして作成
				parent = (DefaultMutableTreeNode) (curSelect.getParent());
			}
			newnode = new DefaultMutableTreeNode("new.txt"); //$NON-NLS-1$
			// 末尾に作成
			this.model.insertNodeInto(newnode, parent, parent.getChildCount());

			TreePath newpath = new TreePath(newnode.getPath());
			this.tree.setSelectionPath(newpath);
			this.tree.scrollPathToVisible(newpath);
			this.tree.startEditingAtPath(newpath);
			this.area.setText(""); //$NON-NLS-1$
		}
	}

	// 現在のテキストエリアの内容をファイルに保存
	boolean saveCurrent() throws IOException {
		// todo:ダーティフラグのクリア
		String filename = treepath2path(this.tree.getSelectionPath());
		PrintWriter pr;
		BufferedWriter bw;
		OutputStreamWriter os;
		FileOutputStream fos;
		fos = new FileOutputStream(filename);
		os = new OutputStreamWriter(fos, this.conf.getEncode());
		bw = new BufferedWriter(os);
		pr = new PrintWriter(bw);
		pr.print(this.area.getText());
		pr.close();
		bw.close();
		os.close();
		fos.close();
		return false;
	}

	// ディレクトリを与えてツリーのノードを作成する
	@SuppressWarnings("nls")
	DefaultMutableTreeNode makeRootNode(File dir) {
		DefaultMutableTreeNode curNode;
		curNode = new DefaultMutableTreeNode(dir.getName());
		String[] childpaths = dir.list();
		Arrays.sort(childpaths);
		for (String i : childpaths) {
			if (i.equals(".") || i.equals((".."))) //$NON-NLS-1$
				continue;
			File f = new File(dir.getPath() + File.separator + i);
			if (f.isDirectory()) {
				curNode.add(makeRootNode(f));
			} else {
				if (this.conf.isTextExt(i) || i.toLowerCase().endsWith(".chi")) { //$NON-NLS-1$
					DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(i);
					curNode.add(leaf);
				}
			}
		}
		return curNode;
	}

	// 全文検索。isForwardで方向を指定。trueなら下方向
	// TODO:Androidソースコードを全文検索してたらスタックオーバーフローした。再帰を使わず書くのはめんどいがどうするか
	public boolean searchAll(String keyword, boolean isForward) {
		DefaultMutableTreeNode current = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();

		if (keyword == null || keyword.trim().equals("")) //$NON-NLS-1$
			return false;
		// 葉ノードなら、まず現在選択しているファイルから検索
		if (current.isLeaf()) {
			// getSelection*は選択位置がない場合はgetCaretPosition()と同じ値
			int searchstart = isForward ? this.area.getSelectionEnd()
					: this.area.getSelectionStart();
			if (searchLeafNodeNowrap(keyword, current, searchstart, isForward)) {
				return true;
			}
			// 現在の葉ノードには見つからなかったので次のノードを見つけてから検索
			DefaultMutableTreeNode nextleaf;

			nextleaf = isForward ? current.getNextLeaf() : current
					.getPreviousLeaf();
			if (nextleaf != null) {
				return searchRecursive(keyword, nextleaf, isForward);
			}
			return false;
		}
		// ツリーノードなので、次の葉ノードを見つけてから検索
		DefaultMutableTreeNode nextleaf;
		if (isForward) {
			nextleaf = current.getFirstLeaf();
			if (searchLeafNodeNowrap(keyword, nextleaf, 0, isForward)) {
				return true;
			}
		} else {
			nextleaf = current.getLastLeaf();
			if (searchLeafNodeNowrap(keyword, nextleaf, 0, isForward)) {
				return true;
			}
		}
		return searchRecursive(keyword, nextleaf, isForward);
	}

	// searchAllの補助関数
	private boolean searchRecursive(String keyword,
			DefaultMutableTreeNode cursor, boolean isForward) {
		DefaultMutableTreeNode next;
		if (isForward) {
			next = cursor.getNextLeaf();
			if (next == null) {
				// 末尾まで探索した
				return false;
			}
		} else {
			next = cursor.getPreviousLeaf();
			if (next == null) {
				// 先頭まで探索した
				return false;
			}
		}
		if (isForward) {
			if (searchLeafNodeNowrap(keyword, next, 0, isForward)) {
				return true;
			}
		} else {
			// 逆方向検索。-1は末尾を表す
			if (searchLeafNodeNowrap(keyword, next, -1, isForward)) {
				return true;
			}
		}
		return searchRecursive(keyword, next, isForward);
	}

	private int myIndexOf(String s, String keyword) {
		if (this.conf.isSearchCaseSensitive()) {
			return s.indexOf(keyword);
		}
		return s.toLowerCase().indexOf(keyword.toLowerCase());
	}

	private int myLastIndexOf(String s, String keyword) {
		if (this.conf.isSearchCaseSensitive()) {
			return s.lastIndexOf(keyword);
		}
		return s.toLowerCase().lastIndexOf(keyword.toLowerCase());
	}

	// ツリー上の一つのノード内を検索。折り返し検索をしない
	private boolean searchLeafNodeNowrap(String keyword,
			DefaultMutableTreeNode node, int pos, boolean isForward) {
		return searchLeafNode(keyword, node, pos, isForward, false);
	}

	// ツリー上の一つのノード内を検索。見つかったら開く
	boolean searchLeafNode(String keyword, DefaultMutableTreeNode node,
			final int pos, boolean isForward, boolean wrap) {
		if (!node.isLeaf())
			return false;
		TreePath path = node2treepath(node);
		String filepath = treepath2path(path);
		// TODO:暗号メモ検索はここで追加処理がいる
		if (!this.conf.isTextExt(filepath))
			return false;
		String text = getFileContent(filepath);
		// 現在のカーソルの左右の部分文字列
		String leftstr, rightstr;
		int po = pos;
		if (po == -1 && (!isForward)) {
			// 特別処理
			po = text.length() - 1;
			leftstr = text;
			rightstr = ""; //$NON-NLS-1$
		} else {
			leftstr = text.substring(0, po);
			rightstr = text.substring(po);
		}

		int left, right;
		// 順方向検索
		if (isForward) {
			right = myIndexOf(rightstr, keyword);
			if (right < 0) {
				// 見つからない場合で、ラップ検索の場合
				if (wrap) {
					left = myIndexOf(leftstr, keyword);
					if (left < 0) {
						return false;
					}
					// ラップ検索で見つかった
					setFileContent(filepath);
					this.tree.setSelectionPath(path);
					this.area.setSelectionStart(left);
					this.area.setSelectionEnd(this.area.getSelectionStart()
							+ keyword.length());
				} else {
					return false;
				}
			} else {
				// 検索ワードが見つかった
				setFileContent(filepath);
				this.tree.setSelectionPath(path);
				this.area.setSelectionStart(po + right);
				this.area.setSelectionEnd(this.area.getSelectionStart()
						+ keyword.length());
			}
			this.area.requestFocus(true);
			this.search.requestFocus();
			// Backward
		} else {
			if (po > 0) {
				left = myLastIndexOf(text.substring(0, po), keyword);
			} else {
				left = -1;
			}
			if (left < 0) {
				if (wrap) {
					right = myLastIndexOf(rightstr, keyword);
					if (right < 0) {
						return false;
					}
					setFileContent(filepath);
					this.tree.setSelectionPath(path);
					this.area.setSelectionStart(po + right);
					this.area.setSelectionEnd(this.area.getSelectionStart()
							+ keyword.length());
				} else {
					return false;
				}
			} else {
				setFileContent(filepath);
				this.tree.setSelectionPath(path);
				this.area.setSelectionStart(left);
				this.area.setSelectionEnd(this.area.getSelectionStart()
						+ keyword.length());
			}
			this.area.requestFocus(true);
			this.search.requestFocus();
		}
		return true;
	}

	public String openPasswordBox() {
		final JPasswordField jpf = new JPasswordField();
		JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = jop.createDialog("Password:"); //$NON-NLS-1$
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						jpf.requestFocusInWindow();
					}
				});
			}
		});
		dialog.setVisible(true);
		int result = ((Integer) (jop.getValue())).intValue();
		dialog.dispose();
		char[] password = null;
		if (result == JOptionPane.OK_OPTION) {
			password = jpf.getPassword();
			return new String(password);
		}
		return null;
	}

	// ノードを削除する
	public boolean deleteNode(DefaultMutableTreeNode node) {
		String path = node2path(node);
		if (node.isRoot()) {
			msgbox("rootノードは削除できません"); //$NON-NLS-1$
			return false;
		}
		File deleted = new File(path);
		// ファイル/ディレクトリが存在しなければノードだけが作られている状態なので消す(新規作成時にこの状態ができる)
		if (!deleted.exists()) {
			this.tree.setSelectionPath(new TreePath(
					((DefaultMutableTreeNode) node.getParent()).getPath()));
			DefaultTreeModel m = (DefaultTreeModel) this.tree.getModel();
			m.removeNodeFromParent(node);
			return true;
		}
		if ((msgyesno(deleted.getName() + this.conf.getSep() + "を削除しますか？" //$NON-NLS-1$
				+ this.conf.getSep() + this.conf.getSep() + "詳細な場所" //$NON-NLS-1$
				+ this.conf.getSep() + path))) {
			deleted.delete();
			this.tree.setSelectionPath(new TreePath(
					((DefaultMutableTreeNode) node.getParent()).getPath()));
			DefaultTreeModel m = (DefaultTreeModel) this.tree.getModel();
			m.removeNodeFromParent(node);
			return true;
		}
		return false;
	}

	// 対象のノードが表すパスがディレクトリならtrueを返す
	public boolean nodeIsDir(DefaultMutableTreeNode node) {
		return (new File(treepath2path(node2treepath(node)))).isDirectory();
	}

	// 検索ボックスにフォーカスがいったときに、すぐに前の入力が消せるように入力全体を選択する
	void focusSearch() {
		this.search.setSelectionStart(0);
		this.search.setSelectionEnd(this.search.getText().length());
		this.search.requestFocus();
	}

	public void nextSibling() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = cur.getNextSibling();
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	// 通常の矢印下キーの動作をエミュレート
	public void goNext() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		int n = this.tree.getRowForPath(node2treepath(cur));
		int max = this.tree.getRowCount();
		if (n + 1 < max) {
			this.tree.setSelectionRow(n + 1);
		}
	}

	// 通常の矢印上キーの動作をエミュレート
	public void goPrevious(DefaultMutableTreeNode cur) {
		int n = this.tree.getRowForPath(node2treepath(cur));
		if (n > 0) {
			this.tree.setSelectionRow(n - 1);
		}
	}

	public void nextNode() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = cur.getNextNode();
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	public void previousSibling() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = cur.getPreviousSibling();
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	public void previousNode() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = cur.getPreviousNode();
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	public void firstChild() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		if (cur.getChildCount() <= 0)
			return;
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getChildAt(0);
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	public void goupParent() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) this.tree
				.getLastSelectedPathComponent();
		if (cur.isRoot())
			return;
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur.getParent();
		if (next != null) {
			this.tree.setSelectionPath(node2treepath(next));
		}
	}

	void msgbox(String s) {
		JOptionPane.showMessageDialog(this, s);
	}

	private boolean msgyesno(String s) {
		return JOptionPane.showConfirmDialog(this, s, "確認", //$NON-NLS-1$
				JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION;
	}

	public boolean setEnc(String s) {
		this.conf.setEncode(s);
		return true;
	}

	private void updateFont(final Font font) {
		FontUIResource fontUIResource = new FontUIResource(font);
		for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults()
				.entrySet()) {
			if (entry.getKey().toString().toLowerCase().endsWith("font")) { //$NON-NLS-1$
				UIManager.put(entry.getKey(), fontUIResource);
			}
		}
		// SwingUtilities.updateComponentTreeUI(this);
		recursiveUpdateUI(this);
	}

	private void recursiveUpdateUI(JComponent p) {
		for (Component c : p.getComponents()) {
			if (c instanceof JToolBar) {
				continue;
			} else if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				jc.updateUI();
				if (jc.getComponentCount() > 0)
					recursiveUpdateUI(jc);
			}
		}
	}

	public void requestTreeFocus() {
		this.tree.setSelectionPath(node2treepath(this.root));
		this.tree.requestFocus();
	}
}

// Tukusiをタブでまとめたフレーム
public class TEdit extends JFrame {
	private static final long serialVersionUID = -5480152582972274087L;

	private ArrayList<Tukusi> main;
	private JTabbedPane tabs;
	private static final Font font = new Font("MigMix 1M", Font.PLAIN, 18); //$NON-NLS-1$
	private static final String sep = "\n"; //$NON-NLS-1$
	private static final int winx = 1920;
	private static final int winy = 1080;
	private static final int winminx = 200;
	private static final int winminy = 200;

	public TEdit(TukusiConfig config) {
		this.main = new ArrayList<Tukusi>();
		this.main.add(new Tukusi(config));
		this.tabs = new JTabbedPane();
		this.tabs.addTab(config.getRootpath(),
				this.main.get(this.main.size() - 1));

		this.tabs.setFocusable(false);
		this.setFocusable(false);
		this.setLayout(new GridLayout(1, 1));
		this.getContentPane().add(this.tabs);
		this.setForeground(Color.black);
		this.setBackground(Color.LIGHT_GRAY);

		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		installClose();
		this.main.get(0).requestTreeFocus();
	}

	// 呼び出すと、終了キーをインストール
	@SuppressWarnings("serial")
	private void installClose() {
		AbstractAction act = new AbstractAction("OK") { //$NON-NLS-1$
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		InputMap imap = getRootPane().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		// Ctrl+Qの場合
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK),
				"close-it"); //$NON-NLS-1$
		getRootPane().getActionMap().put("close-it", act); //$NON-NLS-1$
	}

	public boolean addTab(String path) {
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			TukusiConfig conf = new TukusiConfig(path, font, sep, false,
					"UTF-8"); //$NON-NLS-1$
			Tukusi t = new Tukusi(conf);
			this.main.add(t);
			this.tabs.addTab(path, t);
			return true;
		}
		return false;
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(winminx, winminy);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(winx, winy);
	}

	public static void main(String[] av) {
		TukusiConfig conf;
		@SuppressWarnings("unused")
		TEdit tedit;
		String enc = "UTF-8"; //$NON-NLS-1$
		if (av.length == 0) {
			conf = new TukusiConfig("/home/lfs/test/", font, sep, false, enc); //$NON-NLS-1$
			tedit = new TEdit(conf);
		} else {
			File f = new File(av[0]);
			System.out.println(av[0]);
			if (f.exists() && f.isDirectory()) {
				conf = new TukusiConfig(av[0], font, sep, false, enc);
				tedit = new TEdit(conf);
			} else {
				System.out.println("input should be directory"); //$NON-NLS-1$
			}
		}
		// t.addTab("/d/t/");
		// t.addTab("/d/h/java/android-src-froyo/packages/apps/");
		// t.addTab("/d/h/java/android-src-froyo/frameworks/");
	}
}
