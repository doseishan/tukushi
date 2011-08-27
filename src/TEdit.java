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
			throw new IllegalArgumentException("byte size is 0 or null");
		}
		MessageDigest digest = MessageDigest.getInstance("MD5");
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
		addTextExt("txt"); // TextFile
		addTextExt("xml"); // XML
		addTextExt("asm"); // Assembler
		addTextExt("c"); // C
		addTextExt("cpp"); // C++
		addTextExt("hpp"); // C++
		addTextExt("h"); // C/C++
		addTextExt("java"); // Java
		addTextExt("js"); // JavaScript
		addTextExt("hs"); // Haskell
		addTextExt("fs"); // FSharp
		addTextExt("pl"); // Perl
		addTextExt("scm"); // Scheme
		addTextExt("php"); // PHP
		addTextExt("py"); // Python
		addTextExt("rb"); // Ruby
		addTextExt("cgi"); // perl php etc
		addTextExt("d"); // D
		addTextExt("l"); // Lisp
		addTextExt("ml"); // OCaml
		addTextExt("el"); // Emacs Lisp
		addTextExt("e"); // Eiffel
		addTextExt("bat"); // Windows Batch File
		addTextExt("sh"); // Shellscript
		addTextExt("vb"); // Visual Basic
		addTextExt("cs"); // C#
		addTextExt("as"); // ActionScript
		addTextExt("htm"); // HTML
		addTextExt("html"); // HTML
		addTextExt("css"); // CSS
		addTextExt(".vim"); // vim
		addTextExt(".emacs"); // emacs
		addTextExt(".bashrc"); // bash
	}

	public static final String filer = "pcmanfm";
	public static final String editor = "emacs";
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
		return exts;
	}

	public void setExts(ArrayList<String> exts) {
		this.exts = exts;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public boolean isSearchCaseSensitive() {
		return isSearchCaseSensitive;
	}

	public void setSearchCaseSensitive(boolean isSearchCaseSensitive) {
		this.isSearchCaseSensitive = isSearchCaseSensitive;
	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getSep() {
		return sep;
	}

	public void setSep(String sep) {
		this.sep = sep;
	}

	public boolean addTextExt(String extname) {
		return exts.add("." + extname);
	}

	// これに追加されているテキストタイプのファイルがツリーに表示される
	public boolean isTextExt(String filename) {
		if (exts == null)
			return false;
		// 拡張子はcaseInsensitiveに判定しないとバグ
		for (String ext : exts) {
			if (filename.toLowerCase().endsWith(ext))
				return true;
		}
		return false;
	}
}

class Tukusi extends JPanel {
	private static final long serialVersionUID = -5480152582972274087L;
	// 設定ファイル
	private TukusiConfig conf;

	// コンポーネント
	private JLabel searchText;
	private JTextField search;
	private JPanel panel;
	private JTree tree;
	private JScrollPane treepane, areapane;
	private JTextArea area;
	UndoManager areaundo;

	DefaultMutableTreeNode root;
	DefaultTreeModel model;
	private static final int FTREE = 0;
	private static final int FAREA = 1;
	private int lastFocus = FTREE;
	private static final int SALL = 0;
	private static final int STEXT = 1;
	private int searchMode = SALL;

	// ディレクトリ選択ダイアログ
	private JFileChooser dirchooser;

	public void setSearchCaseSensitive(boolean b) {
		conf.setSearchCaseSensitive(b);
	}

	public boolean isSearchCaseSensitive() {
		return conf.isSearchCaseSensitive();
	}

	public Tukusi(TukusiConfig c) {
		conf = c;
		// 処理するテキスト系の拡張子を追加
		layout_containers(new File(c.getRootpath()));
		prepare_actions();

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e.getPath()
						.getLastPathComponent();
				if (n.isLeaf()) {
					String filename = treepath2path(tree.getSelectionPath());
					if (conf.isTextExt(filename)) {
						setFileContent(filename);
						area.setCaretPosition(0);
					} else if (filename.endsWith(".chi")) {
						String pass = openPasswordBox();
						if (pass != null) {
							setDecodedFileContent(filename, pass);
							area.setCaretPosition(0);
						}
					}
				} else {
					// ディレクトリの場合はテキストをクリアする
					area.setText("");
				}
			}
		});

		tree.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

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
						lastFocus = FTREE;
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
						lastFocus = FTREE;
						if (!nodeIsDir((DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent())) {
							area.requestFocus();
						}
						e.consume();
						break;
					case KeyEvent.VK_DELETE:
						deleteNode((DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent());
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		area.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (keycode == KeyEvent.VK_ESCAPE) {
					lastFocus = FAREA;
					tree.requestFocus();
					e.consume();
				} else if (keycode == KeyEvent.VK_S
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					try {
						DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
								.getLastSelectedPathComponent();
						if (conf.isTextExt(node2path(cur))) {
							saveCurrent();
							msgbox("ファイルを保存しました");
						} else {
							msgbox("このファイル形式は保存できません");
						}
						area.requestFocus();
					} catch (IOException e1) {
						msgbox("ファイルの保存に失敗しました");
						// e1.printStackTrace();
						area.requestFocus();
					}
				} else if (keycode == KeyEvent.VK_N
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					createNewNode();
					e.consume();
				} else if (keycode == KeyEvent.VK_F
						&& e.getModifiers() == ActionEvent.CTRL_MASK) {
					lastFocus = FAREA;
					setSearchMode(STEXT);
					focusSearch();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});

		model.addTreeModelListener(new TreeModelListener() {
			@Override
			public void treeNodesChanged(TreeModelEvent e) {
				DefaultMutableTreeNode node;
				node = (DefaultMutableTreeNode) (e.getTreePath()
						.getLastPathComponent());
				try {
					int index = e.getChildIndices()[0];
					node = (DefaultMutableTreeNode) (node.getChildAt(index));
				} catch (NullPointerException exc) {
				}
			}

			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void treeStructureChanged(TreeModelEvent e) {
				// TODO Auto-generated method stub

			}
		});

		search.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (keycode == KeyEvent.VK_ENTER
						&& e.getModifiers() == ActionEvent.SHIFT_MASK) {
					if (searchMode == SALL) {
						if (!searchAll(search.getText(), false)) {
							msgbox(search.getText() + conf.getSep()
									+ "は見つかりませんでした");
						} else {
							lastFocus = FAREA; // 見つかったところに戻れるようにする
						}
					} else {
						if (!searchLeafNode(search.getText(),
								(DefaultMutableTreeNode) tree
										.getLastSelectedPathComponent(), area
										.getSelectionStart(), false, true)) {
							msgbox(search.getText() + conf.getSep()
									+ "は見つかりませんでした");
						}
					}
					e.consume();
				} else if (keycode == KeyEvent.VK_ENTER) {
					if (searchMode == SALL) {
						if (!searchAll(search.getText(), true)) {
							msgbox(search.getText() + conf.getSep()
									+ "は見つかりませんでした");
						} else {
							lastFocus = FAREA; // 見つかったところに戻れるようにする
						}
					} else {
						if (!searchLeafNode(search.getText(),
								(DefaultMutableTreeNode) tree
										.getLastSelectedPathComponent(), area
										.getSelectionEnd(), true, true)) {
							msgbox(search.getText() + conf.getSep()
									+ "は見つかりませんでした");
						}
					}
					e.consume();
				} else if (keycode == KeyEvent.VK_ESCAPE) {
					switch (lastFocus) {
					case FTREE:
						tree.requestFocus();
						break;
					case FAREA:
						area.requestFocus();
						break;
					default:
						tree.requestFocus();
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
				// TODO Auto-generated method stub

			}
		});
	}

	private void setSearchMode(int mode) {
		searchMode = mode;
		switch (mode) {
		case SALL:
			searchText.setText("全文検索");
			break;
		case STEXT:
			searchText.setText("テキストエリア検索");
			break;
		}
	}

	private void layout_containers(File dir) {

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		makeRoot(dir);
		model = new DefaultTreeModel(root);

		searchText = new JLabel("テキストエリア検索　", JLabel.RIGHT);
		search = new JTextField();

		tree = new JTree(model);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		// ツリーの選択は1つの要素のみ
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);

		treepane = new JScrollPane();
		treepane.getViewport().add(tree);

		area = new JTextArea("");
		areapane = new JScrollPane();
		areapane.getViewport().add(area);
		area.setLineWrap(true);
		// アプリケーション内の改行を固定にする
		area.getDocument().putProperty(
				DefaultEditorKit.EndOfLineStringProperty, conf.getSep());
		areaundo = new UndoManager();
		area.getDocument().addUndoableEditListener(areaundo);

		// フォーカスがあたるのはツリーかテキスト部のみ
		panel.setFocusable(false);
		treepane.setFocusable(false);
		areapane.setFocusable(false);
		area.setFocusable(true);
		tree.setFocusable(true);
		searchText.setFocusable(false);
		search.setFocusable(true);
		this.setFocusable(false);

		// フォント設定
		area.setFont(conf.getFont());
		tree.setFont(conf.getFont());
		searchText.setFont(conf.getFont());
		search.setFont(conf.getFont());
		// TODO:システムのフォントも全部変更してる。もし公開するならここを直すのは必須
		updateFont(conf.getFont());

		// ディレクトリ選択ダイアログの設定
		dirchooser = new JFileChooser();
		dirchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		panel.setLayout(gbl);
		gbl.setConstraints(treepane, gbc);
		panel.add(treepane);

		gbc.weightx = 1;
		gbc.gridx = 1;
		gbl.setConstraints(areapane, gbc);
		panel.add(areapane);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbl.setConstraints(searchText, gbc);
		panel.add(searchText);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbl.setConstraints(search, gbc);
		panel.add(search);

		setLayout(new GridLayout(1, 1));
		this.add(panel);
	}

	// ルートノード変更
	public boolean resetRoot(File file) {
		if (file.exists() && file.isDirectory()) {
			// これがないとぬるぽする。なぜ？
			tree.setSelectionPath(node2treepath(root));

			makeRoot(file);
			model = new DefaultTreeModel(root);
			tree.setModel(model);
			if (root != null) {
				tree.setSelectionPath(node2treepath(root));
			}
			tree.requestFocus();
			return true;
		}
		return false;
	}

	@SuppressWarnings("serial")
	private void prepare_actions() {
		Action changeRoot = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dirchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int selected = dirchooser.showSaveDialog(Tukusi.this);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = dirchooser.getSelectedFile();
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
					DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
							.getLastSelectedPathComponent();
					String path = node2path(cur);
					System.out.println(path);
					File file = new File(path);
					if (!file.exists()) {
						msgbox(path + "は存在しません");
						return;
					}
					if (file.isDirectory()) {
						rt.exec(TukusiConfig.filer + " " + path);
					} else {
						rt.exec(TukusiConfig.editor + " " + path);
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
					areaundo.undo();
				} catch (CannotUndoException cannot) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		Action areaRedo = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					areaundo.redo();
				} catch (CannotRedoException cannot) {
					Toolkit.getDefaultToolkit().beep();
				}
			}
		};
		// アクションの追加
		ActionMap ta = tree.getActionMap();
		ta.put("changeRoot", changeRoot);
		ta.put("goNextSibling", goNextSibling);
		ta.put("goPreviousSibling", goPreviousSibling);
		ta.put("relativeProgram", relativeProgram);

		ActionMap aa = area.getActionMap();
		aa.put("undo", areaUndo);
		aa.put("redo", areaRedo);

		// キーバインドのカスタマイズ。keyListenerはこちらに置き換えていきたい
		InputMap ti = tree.getInputMap();
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
				"changeRoot");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK),
				"selectChild");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK),
				"selectParent");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
				"selectNext");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK),
				"selectPrevious");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK),
				"selectParent");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_J, ActionEvent.CTRL_MASK),
				"goNextSibling");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.CTRL_MASK),
				"goPreviousSibling");
		// Ctrl+[
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_OPEN_BRACKET,
				ActionEvent.CTRL_MASK), "selectFirst");
		// Ctrl+]
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_CLOSE_BRACKET,
				ActionEvent.CTRL_MASK), "selectLast");
		ti.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK),
				"relativeProgram");

		InputMap ai = area.getInputMap();
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK),
				"undo");
		ai.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK),
				"redo");

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

	private String node2path(DefaultMutableTreeNode node) {
		return treepath2path(node2treepath(node));
	}

	private String treepath2path(TreePath path) {
		StringBuilder b;
		b = new StringBuilder();
		b.append(conf.getRootpath());
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
			InputStreamReader in = new InputStreamReader(fis, conf.getEncode());
			// InputStreamReader in = new InputStreamReader(fis,"MS932");
			BufferedReader inFile = new BufferedReader(in);
			StringBuffer b;
			b = new StringBuffer();
			String s;
			while ((s = inFile.readLine()) != null) {
				// 内部で使う改行は統一
				b.append(s + conf.getSep());
			}
			return b.toString();
		} catch (Exception err) {
			System.out.println("ファイル読み込み失敗");
			return null;
		}
	}

	private void setFileContent(String filename) {
		String c = getFileContent(filename);
		if (c != null) {
			area.setText(c);
			// undo情報をクリア。これをしないと、前に開いていたテキストまで復活してしまう
			areaundo.discardAllEdits();
		} else {
			area.setText("");
		}
	}

	private void setDecodedFileContent(String filename, String password) {
		try {
			area.setText(Chi.readCipheredFile(filename, "Shift_JIS", password));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean makeRoot(File path) {
		if (!path.exists())
			return false;
		root = makeRootNode(path);
		// 今のところ、このソフトでは、ディレクトリ名は末尾に/がついてる必要がある
		String rootpath = path.getAbsolutePath() + File.separator;
		rootpath = rootpath.replace(File.separator + File.separator,
				File.separator);
		conf.setRootpath(rootpath);
		return true;
	}

	void createNewNode() {
		boolean dirty = false;
		// TOOD:ダーティフラグが立っていなければ
		if (!dirty) {
			area.setText("");
			DefaultMutableTreeNode curSelect, parent, newnode;
			curSelect = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			// 選択してるノードがディレクトリなら、その子として作成
			if (((new File(treepath2path(tree.getSelectionPath()))))
					.isDirectory()) {
				parent = curSelect;
			} else {
				// 選択してるノードもファイルの場合、兄弟ノードとして作成
				parent = (DefaultMutableTreeNode) (curSelect.getParent());
			}
			newnode = new DefaultMutableTreeNode("new.txt");
			// 末尾に作成
			model.insertNodeInto(newnode, parent, parent.getChildCount());

			TreePath newpath = new TreePath(newnode.getPath());
			tree.setSelectionPath(newpath);
			tree.scrollPathToVisible(newpath);
			tree.startEditingAtPath(newpath);
			area.setText("");
		}
	}

	// 現在のテキストエリアの内容をファイルに保存
	private boolean saveCurrent() throws IOException {
		// todo:ダーティフラグのクリア
		String filename = treepath2path(tree.getSelectionPath());
		PrintWriter pr;
		BufferedWriter bw;
		OutputStreamWriter os;
		FileOutputStream fos;
		fos = new FileOutputStream(filename);
		os = new OutputStreamWriter(fos, conf.getEncode());
		bw = new BufferedWriter(os);
		pr = new PrintWriter(bw);
		pr.print(area.getText());
		pr.close();
		bw.close();
		os.close();
		fos.close();
		return false;
	}

	// ディレクトリを与えてツリーのノードを作成する
	DefaultMutableTreeNode makeRootNode(File dir) {
		DefaultMutableTreeNode curNode;
		curNode = new DefaultMutableTreeNode(dir.getName());
		String[] childpaths = dir.list();
		Arrays.sort(childpaths);
		for (String i : childpaths) {
			if (i.equals(".") || i.equals(("..")))
				continue;
			File f = new File(dir.getPath() + File.separator + i);
			if (f.isDirectory()) {
				curNode.add(makeRootNode(f));
			} else {
				if (conf.isTextExt(i) || i.toLowerCase().endsWith(".chi")) {
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
		DefaultMutableTreeNode current = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();

		if (keyword == null || keyword.trim().equals(""))
			return false;
		// 葉ノードなら、まず現在選択しているファイルから検索
		if (current.isLeaf()) {
			// getSelection*は選択位置がない場合はgetCaretPosition()と同じ値
			int searchstart = isForward ? area.getSelectionEnd() : area
					.getSelectionStart();
			if (searchLeafNodeNowrap(keyword, current, searchstart, isForward)) {
				return true;
			}
			// 現在の葉ノードには見つからなかったので次のノードを見つけてから検索
			DefaultMutableTreeNode nextleaf;

			nextleaf = isForward ? current.getNextLeaf() : current
					.getPreviousLeaf();
			if (nextleaf != null) {
				return searchRecursive(keyword, nextleaf, isForward);
			} else {
				return false;
			}
		} else {
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
		if (conf.isSearchCaseSensitive()) {
			return s.indexOf(keyword);
		} else {
			return s.toLowerCase().indexOf(keyword.toLowerCase());
		}
	}

	private int myLastIndexOf(String s, String keyword) {
		if (conf.isSearchCaseSensitive()) {
			return s.lastIndexOf(keyword);
		} else {
			return s.toLowerCase().lastIndexOf(keyword.toLowerCase());
		}
	}

	// ツリー上の一つのノード内を検索。折り返し検索をしない
	private boolean searchLeafNodeNowrap(String keyword,
			DefaultMutableTreeNode node, int pos, boolean isForward) {
		return searchLeafNode(keyword, node, pos, isForward, false);
	}

	// ツリー上の一つのノード内を検索。見つかったら開く
	private boolean searchLeafNode(String keyword, DefaultMutableTreeNode node,
			int pos, boolean isForward, boolean wrap) {
		if (!node.isLeaf())
			return false;
		TreePath path = node2treepath(node);
		String filepath = treepath2path(path);
		// TODO:暗号メモ検索はここで追加処理がいる
		if (!conf.isTextExt(filepath))
			return false;
		String text = getFileContent(filepath);
		// 現在のカーソルの左右の部分文字列
		String leftstr, rightstr;
		if (pos == -1 && (!isForward)) {
			// 特別処理
			pos = text.length() - 1;
			leftstr = text;
			rightstr = "";
		} else {
			leftstr = text.substring(0, pos);
			rightstr = text.substring(pos);
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
					tree.setSelectionPath(path);
					area.setSelectionStart(left);
					area.setSelectionEnd(area.getSelectionStart()
							+ keyword.length());
				} else {
					return false;
				}
			} else {
				// 検索ワードが見つかった
				setFileContent(filepath);
				tree.setSelectionPath(path);
				area.setSelectionStart(pos + right);
				area.setSelectionEnd(area.getSelectionStart()
						+ keyword.length());
			}
			area.requestFocus(true);
			search.requestFocus();
			// Backward
		} else {
			if (pos > 0) {
				left = myLastIndexOf(text.substring(0, pos), keyword);
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
					tree.setSelectionPath(path);
					area.setSelectionStart(pos + right);
					area.setSelectionEnd(area.getSelectionStart()
							+ keyword.length());
				} else {
					return false;
				}
			} else {
				setFileContent(filepath);
				tree.setSelectionPath(path);
				area.setSelectionStart(left);
				area.setSelectionEnd(area.getSelectionStart()
						+ keyword.length());
			}
			area.requestFocus(true);
			search.requestFocus();
		}
		return true;
	}

	public String openPasswordBox() {
		final JPasswordField jpf = new JPasswordField();
		JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = jop.createDialog("Password:");
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
		int result = (Integer) jop.getValue();
		dialog.dispose();
		char[] password = null;
		if (result == JOptionPane.OK_OPTION) {
			password = jpf.getPassword();
			return new String(password);
		} else {
			return null;
		}
	}

	// ノードを削除する
	public boolean deleteNode(DefaultMutableTreeNode node) {
		String path = node2path(node);
		if (node.isRoot()) {
			msgbox("rootノードは削除できません");
			return false;
		} else {
			File deleted = new File(path);
			// ファイル/ディレクトリが存在しなければノードだけが作られている状態なので消す(新規作成時にこの状態ができる)
			if (!deleted.exists()) {
				tree.setSelectionPath(new TreePath(
						((DefaultMutableTreeNode) node.getParent()).getPath()));
				DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
				m.removeNodeFromParent(node);
				return true;
			} else {
				if ((msgyesno(deleted.getName() + conf.getSep() + "を削除しますか？"
						+ conf.getSep() + conf.getSep() + "詳細な場所"
						+ conf.getSep() + path))) {
					deleted.delete();
					tree.setSelectionPath(new TreePath(
							((DefaultMutableTreeNode) node.getParent())
									.getPath()));
					DefaultTreeModel m = (DefaultTreeModel) tree.getModel();
					m.removeNodeFromParent(node);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	// 対象のノードが表すパスがディレクトリならtrueを返す
	public boolean nodeIsDir(DefaultMutableTreeNode node) {
		return (new File(treepath2path(node2treepath(node)))).isDirectory();
	}

	// 検索ボックスにフォーカスがいったときに、すぐに前の入力が消せるように入力全体を選択する
	private void focusSearch() {
		search.setSelectionStart(0);
		search.setSelectionEnd(search.getText().length());
		search.requestFocus();
	}

	public void nextSibling() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getNextSibling();
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	// 通常の矢印下キーの動作をエミュレート
	public void goNext() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		int n = tree.getRowForPath(node2treepath(cur));
		int max = tree.getRowCount();
		if (n + 1 < max) {
			tree.setSelectionRow(n + 1);
		}
	}

	// 通常の矢印上キーの動作をエミュレート
	public void goPrevious(DefaultMutableTreeNode cur) {
		int n = tree.getRowForPath(node2treepath(cur));
		if (n > 0) {
			tree.setSelectionRow(n - 1);
		}
	}

	public void nextNode() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getNextNode();
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	public void previousSibling() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getPreviousSibling();
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	public void previousNode() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getPreviousNode();
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	public void firstChild() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (cur.getChildCount() <= 0)
			return;
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur
				.getChildAt(0);
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	public void goupParent() {
		DefaultMutableTreeNode cur = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (cur.isRoot())
			return;
		DefaultMutableTreeNode next = (DefaultMutableTreeNode) cur.getParent();
		if (next != null) {
			tree.setSelectionPath(node2treepath(next));
		}
	}

	private void msgbox(String s) {
		JOptionPane.showMessageDialog(this, s);
	}

	private boolean msgyesno(String s) {
		return JOptionPane.showConfirmDialog(this, s, "確認",
				JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION;
	}

	public boolean setEnc(String s) {
		conf.setEncode(s);
		return true;
	}

	private void updateFont(final Font font) {
		FontUIResource fontUIResource = new FontUIResource(font);
		for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults()
				.entrySet()) {
			if (entry.getKey().toString().toLowerCase().endsWith("font")) {
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
		tree.setSelectionPath(node2treepath(root));
		tree.requestFocus();
	}
}

// Tukusiをタブでまとめたフレーム
public class TEdit extends JFrame {
	private static final long serialVersionUID = -5480152582972274087L;

	private ArrayList<Tukusi> main;
	private JTabbedPane tabs;
	private static final Font font = new Font("MigMix 1M", Font.PLAIN, 18);
	private static final String sep = "\n";
	private static final int winx = 1920;
	private static final int winy = 1080;
	private static final int winminx = 200;
	private static final int winminy = 200;

	public TEdit(TukusiConfig config) {
		main = new ArrayList<Tukusi>();
		main.add(new Tukusi(config));
		tabs = new JTabbedPane();
		tabs.addTab(config.getRootpath(), main.get(main.size() - 1));

		tabs.setFocusable(false);
		this.setFocusable(false);
		this.setLayout(new GridLayout(1, 1));
		this.getContentPane().add(tabs);
		this.setForeground(Color.black);
		this.setBackground(Color.LIGHT_GRAY);

		this.pack();
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		installClose();
		main.get(0).requestTreeFocus();
	}

	// 呼び出すと、終了キーをインストール
	@SuppressWarnings("serial")
	private void installClose() {
		AbstractAction act = new AbstractAction("OK") {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};
		InputMap imap = getRootPane().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		// Ctrl+Qの場合
		imap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK),
				"close-it");
		getRootPane().getActionMap().put("close-it", act);
	}

	public boolean addTab(String path) {
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			TukusiConfig conf = new TukusiConfig(path, font, sep, false,
					"UTF-8");
			Tukusi t = new Tukusi(conf);
			main.add(t);
			tabs.addTab(path, t);
			return true;
		}
		return false;
	}

	public Dimension getMinimumSize() {
		return new Dimension(winminx, winminy);
	}

	public Dimension getPreferredSize() {
		return new Dimension(winx, winy);
	}

	public static void main(String[] av) {
		TukusiConfig conf;
		TEdit t;
		String enc = "UTF-8";
		if (av.length == 0) {
			conf = new TukusiConfig("/home/lfs/test/", font, sep, false, enc);
			t = new TEdit(conf);
		} else {
			File f = new File(av[0]);
			System.out.println(av[0]);
			if(f.exists() && f.isDirectory()){
				conf = new TukusiConfig(av[0], font, sep, false, enc);
				t = new TEdit(conf);
			}else{
				System.out.println("input should be directory");
			}
		}
		// t.addTab("/d/t/");
		// t.addTab("/d/h/java/android-src-froyo/packages/apps/");
		// t.addTab("/d/h/java/android-src-froyo/frameworks/");
	}
}
