import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class StartMenu {
    int numRows;
    int mineCount;

    public int getNumRows() {
        return numRows;
    }
    public int getMineCount() {
        return mineCount;
    }

    public static void main(String[] args) {
        StartMenu startMenu = new StartMenu();
        Minesweeper minesweeper = new Minesweeper(startMenu);
        int boardWidth = 8 * 50;
        int boardHeight = boardWidth;

        // buttons layout
        int buttonWidth = 100;
        int buttonHeight = 30;
        int gap = 10;
        int totalWidth = buttonWidth * 3 + gap * 2;
        int startX = (boardWidth - totalWidth) / 2;
        int startY = (boardHeight - buttonHeight) / 2;

        JFrame frame = new JFrame("Minesweeper");
        JLabel welcomeLabel = new JLabel("Welcome to Minesweeper!", SwingConstants.CENTER);
        JLabel difficultyLabel = new JLabel("Choose a difficulty level:", SwingConstants.CENTER);
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton hardButton = new JButton("Hard");

        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMenu.numRows = 8;
                startMenu.mineCount = 10;
                frame.dispose();
                minesweeper.startGame();
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMenu.numRows = 16;
                startMenu.mineCount = 40;
                frame.dispose();
                minesweeper.startGame();
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMenu.numRows = 20;
                startMenu.mineCount = 80;
                frame.dispose();
                minesweeper.startGame();
            }
        });

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        welcomeLabel.setBounds(0, 100, boardWidth, 50);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        difficultyLabel.setBounds(0, 200, boardWidth, 20);

        easyButton.setBounds(startX, startY + 50, buttonWidth, buttonHeight);
        mediumButton.setBounds(startX + buttonWidth + gap, startY + 50, buttonWidth, buttonHeight);
        hardButton.setBounds(startX + 2 * (buttonWidth + gap), startY + 50, buttonWidth, buttonHeight);

        frame.add(welcomeLabel);
        frame.add(difficultyLabel);
        frame.add(easyButton);
        frame.add(mediumButton);
        frame.add(hardButton);

        frame.setVisible(true);
    }
}