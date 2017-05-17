package teflappybird;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class View implements ViewUpdater {

    private final String TITLE_OF_THE_GAME = "TeFlappyBird";
    private final ConstantModel model;
    private final Controller controller;
    private final JFrame window;
    private final Canvas canvas;
    private final BufferedImage imgBird;
    private final JPanel btnPanel;
    private final Dimension btnDim = new Dimension(140, 30);
    private final String BTN_NEW_LABEL = "New game";
    private final String BTN_PAUSE_LABEL = "Pause game";
    private final String BTN_QUIT_LABEL = "Quit game";
    private final JButton btnNew;
    private final JButton btnPause;
    private final JButton btnQuit;
    private int spriteSourceX = 0, ticker = 0;
    private final int SPRITE_FRAMES_NUMBER = 4;
    private final int FONT_SIZE_SMALL = 30;
    private final int FONT_SIZE_LARGE = 70;
    private final Font fontSmall = new Font("Arial", Font.BOLD, FONT_SIZE_SMALL);
    private final Font fontLarge = new Font("Arial", Font.BOLD, FONT_SIZE_LARGE);
    private final JLabel lblInfo = new JLabel();

    public View(ConstantModel model, Controller controller) throws IOException {
        this.model = model;
        this.controller = controller;
        window = new JFrame(TITLE_OF_THE_GAME);
        window.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusable(true);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_N:
                        controller.newGame();
                        break;
                    case KeyEvent.VK_ENTER:
                        controller.initiate();
                        break;
                    case KeyEvent.VK_UP:
                        controller.jump(false);
                        break;
                    case KeyEvent.VK_F:
                        controller.drop(true);
                        break;
                    case KeyEvent.VK_J:
                        controller.jump(true);
                        break;
                    case KeyEvent.VK_DOWN:
                        controller.drop(false);
                        break;
                    case KeyEvent.VK_SPACE:
                        controller.pause();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.exit(0);
                        break;
                }
            }
        });
        canvas = new Canvas();
        canvas.setBackground(Color.CYAN);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                controller.initiate();
            }
        });

        canvas.add(lblInfo);
        URL url = TeFlappyBird.class.getResource("sprite/fb_sprite4.png");
        imgBird = ImageIO.read(url);
        btnNew = new JButton(BTN_NEW_LABEL);
        btnNew.setPreferredSize(btnDim);
        btnNew.setFocusable(false);
        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.newGame();
            }
        });

        btnPause = new JButton(BTN_PAUSE_LABEL);
        btnPause.setPreferredSize(btnDim);
        btnPause.setFocusable(false);
        btnPause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.pause();
            }
        });

        btnQuit = new JButton(BTN_QUIT_LABEL);
        btnQuit.setPreferredSize(btnDim);
        btnQuit.setFocusable(false);
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        btnPanel = new JPanel();
        btnPanel.add(btnNew);
        btnPanel.add(btnPause);
        btnPanel.add(btnQuit);
        btnPanel.setFocusable(false);
        window.getContentPane().add(BorderLayout.CENTER, canvas);
        window.getContentPane().add(BorderLayout.SOUTH, btnPanel);
        window.requestFocus();
        window.setVisible(true);
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Drawing of the ground
        
        g.setColor(Color.YELLOW);
        g.fillRect(0, Constants.WINDOW_HEIGHT - Constants.GROUND_THICKNESS, Constants.WINDOW_WIDTH, Constants.GROUND_THICKNESS);

        //Drawing og the columns
        
        for (Point p : model.getColumns()) {
            g.setColor(Color.GREEN.darker().darker().darker());
            g.fillRect(p.x, 0, Constants.COLUMNS_WIDTH, Constants.WINDOW_HEIGHT - Constants.GROUND_THICKNESS);
            
            //Drawing of the gap
            
            g.setColor(Color.CYAN);
            g.fillRect(p.x, p.y, Constants.COLUMNS_WIDTH, Constants.SPACE);
        }

        //Drawing of the bird
        
        if (model.isAnimation()) {
            ++ticker;
            if (ticker % 5 == 0) {
                ++spriteSourceX;
                if (spriteSourceX >= SPRITE_FRAMES_NUMBER) {
                    spriteSourceX = 0;
                }
                ticker = 0;
            }
        } else {
            spriteSourceX = 2;
        }
        g.drawImage(imgBird.getSubimage(spriteSourceX * Constants.BIRD_WIDTH, 0, Constants.BIRD_WIDTH,
                Constants.BIRD_HEIGHT), Constants.BIRD_X, model.getBirdY(), Constants.BIRD_WIDTH, Constants.BIRD_HEIGHT, null);

        //Drawing of the score
        
        g.setColor(Color.BLUE.darker().darker());
        g.setFont(fontSmall);
        g.drawString("Total score: " + model.getScore(), Constants.WINDOW_WIDTH / 2 - 100, Constants.WINDOW_HEIGHT - 80);

        //Drawing of the game status
        
        lblInfo.setFont(fontLarge);
        lblInfo.setForeground(Color.red);
        lblInfo.setText(model.getGameStatus());
        lblInfo.setLocation((Constants.WINDOW_WIDTH - lblInfo.getBounds().width) / 2, 200);
    }

    private class Canvas extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw((Graphics2D) g);
        }
    }

    @Override
    public void updateView() {
        canvas.repaint();
    }
}
