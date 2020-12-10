import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MainFrame extends JFrame {
    private static final int WIDTH = 700;
    private static final int HEIGHT = 500;
    private ArrayList<Double[]> graphicsData;
    private JFileChooser fileChooser = null;
    private JMenuItem resetGraphicsMenuItem;
    private JMenuItem saveToGraphicsFileMenuItem;
    private GraphicsDisplay display = new GraphicsDisplay();
    private boolean fileLoaded = false;

    public MainFrame() {
        super("Обработка событий от мыши");
        this.setSize(700, 500);
        Toolkit kit = Toolkit.getDefaultToolkit();
        this.setLocation((kit.getScreenSize().width - 700) / 2, (kit.getScreenSize().height - 500) / 2);
        this.setExtendedState(6);
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        Action openGraphicsAction = new AbstractAction("Открыть файл с графиком") {
            public void actionPerformed(ActionEvent event) {
                if (MainFrame.this.fileChooser == null) {
                    MainFrame.this.fileChooser = new JFileChooser();
                    MainFrame.this.fileChooser.setCurrentDirectory(new File("."));
                }

                MainFrame.this.fileChooser.showOpenDialog(MainFrame.this);
                MainFrame.this.openGraphics(MainFrame.this.fileChooser.getSelectedFile());
            }
        };
        fileMenu.add(openGraphicsAction);
        Action saveToGraphicsAction = new AbstractAction("Сохранить в файл для построения графиков") {
            public void actionPerformed(ActionEvent event) {
                if (fileChooser==null) {
// Если экземпляр диалогового окна
// "Открыть файл" ещѐ не создан,
// то создать его
                    fileChooser = new JFileChooser();
// и инициализировать текущей директорией
                    fileChooser.setCurrentDirectory(new File("."));
                }
// Показать диалоговое окно
                if (fileChooser.showSaveDialog(MainFrame.this) ==
                        JFileChooser.APPROVE_OPTION);
// Если результат его показа успешный,
// сохранить данные в двоичный файл
                saveToGraphicsFile(fileChooser.getSelectedFile());
            }
        };
        this.saveToGraphicsFileMenuItem= fileMenu.add(saveToGraphicsAction);
        this.saveToGraphicsFileMenuItem.setEnabled(false);
        Action resetGraphicsAction = new AbstractAction("Отменить все изменения") {
            public void actionPerformed(ActionEvent event) {
                MainFrame.this.display.reset();
            }
        };
        this.resetGraphicsMenuItem = fileMenu.add(resetGraphicsAction);
        this.resetGraphicsMenuItem.setEnabled(false);
        this.getContentPane().add(this.display, "Center");
    }
    protected void saveToGraphicsFile(File selectedFile)
    {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(selectedFile));
            for(int i =0;i<graphicsData.size();i++){
                out.writeDouble((Double)graphicsData.get(i)[0]);
                out.writeDouble((Double)graphicsData.get(i)[1]);
            }
            out.close();
        }
        catch (IOException e)
        {

        }
    }

    protected void openGraphics(File selectedFile) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(selectedFile));
            graphicsData = new ArrayList(50);

            while(in.available() > 0) {
                Double x = in.readDouble();
                Double y = in.readDouble();
                in.skipBytes(2*(Double.SIZE/8));
                graphicsData.add(new Double[]{x, y});
            }

            if (graphicsData.size() > 0) {
                this.fileLoaded = true;
                this.resetGraphicsMenuItem.setEnabled(true);
                this.saveToGraphicsFileMenuItem.setEnabled(true);
                this.display.displayGraphics(graphicsData);
            }

        } catch (FileNotFoundException var6) {
            JOptionPane.showMessageDialog(this, "Указанный файл не найден", "Ошибка загрузки данных", 2);
        } catch (IOException var7) {
            JOptionPane.showMessageDialog(this, "Ошибка чтения координат точек из файла", "Ошибка загрузки данных", 2);
        }
    }

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
}
