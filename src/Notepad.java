import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.*;

public class Notepad extends JFrame implements ActionListener {

    JMenuBar menubar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu edit = new JMenu("Edit");
    JMenu help = new JMenu("Help");

    JMenuItem newFile = new JMenuItem("New");
    JMenuItem openFile = new JMenuItem("Open");
    JMenuItem saveFile = new JMenuItem("Save");
    JMenuItem print = new JMenuItem("Print");
    JMenuItem exit = new JMenuItem("Exit");

    JMenuItem cut = new JMenuItem("Cut");
    JMenuItem copy = new JMenuItem("Copy");
    JMenuItem paste = new JMenuItem("Paste");
    JMenuItem selectall = new JMenuItem("Select All");

    JMenuItem about = new JMenuItem("About");
    JMenuItem darkMode = new JMenuItem("Toggle Dark Mode");

    JTextArea textArea = new JTextArea();
    JLabel statusBar = new JLabel("Words: 0");

    Notepad() {
        setTitle("Note It Down");
        setBounds(100, 100, 800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon icon = new ImageIcon(getClass().getResource("notepad.png"));
        setIconImage(icon.getImage());

        setJMenuBar(menubar);
        menubar.add(file);
        menubar.add(edit);
        menubar.add(help);

        file.add(newFile);
        file.add(openFile);
        file.add(saveFile);
        file.add(print);
        file.add(exit);

        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(selectall);
        help.add(about);
        help.add(darkMode);

        JScrollPane scrollpane = new JScrollPane(textArea);
        add(scrollpane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        textArea.setFont((new Font(Font.SANS_SERIF, Font.PLAIN, 20)));
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setBorder(BorderFactory.createEmptyBorder());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Word Count Logic
        textArea.addCaretListener(e -> {
            String text = textArea.getText().trim();
            int words = text.isEmpty() ? 0 : text.split("\\s+").length;
            statusBar.setText("Words: " + words);
        });

        // Action Listeners
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        print.addActionListener(this);
        exit.addActionListener(this);
        cut.addActionListener(this);
        copy.addActionListener(this);
        paste.addActionListener(this);
        selectall.addActionListener(this);
        about.addActionListener(this);
        darkMode.addActionListener(this);

        // Shortcuts
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK)); // Fixed
        selectall.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, KeyEvent.CTRL_DOWN_MASK));
    }

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new Notepad().setVisible(true);
    }

    private boolean confirmUnsaved() {
        if (!textArea.getText().isEmpty()) {
            int option = JOptionPane.showConfirmDialog(this, "Do you want to save changes?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) return false;
            if (option == JOptionPane.YES_OPTION) {
                saveFile.doClick();
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "New":
                if (confirmUnsaved()) textArea.setText("");
                break;

            case "Open":
                if (!confirmUnsaved()) return;
                JFileChooser openChooser = new JFileChooser();
                FileNameExtensionFilter openFilter = new FileNameExtensionFilter("Only Text Files (.txt)", "txt");
                openChooser.setAcceptAllFileFilterUsed(false);
                openChooser.addChoosableFileFilter(openFilter);

                int openAction = openChooser.showOpenDialog(this);
                if (openAction == JFileChooser.APPROVE_OPTION) {
                    File file = openChooser.getSelectedFile();
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        textArea.read(reader, null);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case "Save":
                JFileChooser saveChooser = new JFileChooser();
                FileNameExtensionFilter saveFilter = new FileNameExtensionFilter("Only Text Files (.txt)", "txt");
                saveChooser.setAcceptAllFileFilterUsed(false);
                saveChooser.addChoosableFileFilter(saveFilter);

                int saveAction = saveChooser.showSaveDialog(this);
                if (saveAction == JFileChooser.APPROVE_OPTION) {
                    String fileName = saveChooser.getSelectedFile().getAbsolutePath();
                    if (!fileName.contains(".txt")) fileName += ".txt";

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                        textArea.write(writer);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case "Print":
                try {
                    textArea.print();
                } catch (PrinterException ex) {
                    Logger.getLogger(Notepad.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

            case "Exit":
                System.exit(0);
                break;

            case "Cut":
                textArea.cut();
                break;

            case "Copy":
                textArea.copy();
                break;

            case "Paste":
                textArea.paste();
                break;

            case "Select All":
                textArea.selectAll();
                break;

            case "About":
                JOptionPane.showMessageDialog(this, "Notepad App by Yashraj\nJava Swing-based Text Editor", "About", JOptionPane.INFORMATION_MESSAGE);
                break;

            case "Toggle Dark Mode":
                boolean isDark = textArea.getBackground().equals(Color.DARK_GRAY);
                textArea.setBackground(isDark ? Color.WHITE : Color.DARK_GRAY);
                textArea.setForeground(isDark ? Color.BLACK : Color.LIGHT_GRAY);
                break;
        }
    }
}
