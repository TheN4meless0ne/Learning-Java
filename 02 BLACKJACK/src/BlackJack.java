import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Card {
        String value;
        String type;

        Card(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { // A, J, Q, K
                if ("A".equals(value)) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); // 2-10
        }

        public boolean isAce() {
            return "A".equals(value);
        }

        public String getImagePath() {
            return "/assets/cards/" + toString() + ".png";
        }
    }

    private ArrayList<Card> deck;
    private Random random = new Random(); // shuffle deck
    private Map<String, Image> cardImages = new HashMap<>();

    // dealer
    private Card hiddenCard;
    private ArrayList<Card> dealerHand;
    private int dealerSum;
    private int dealerAceCount;

    // player
    private ArrayList<Card> playerHand;
    private int playerSum;
    private int playerAceCount;

    // window
    private static final int boardWidth = 600;
    private static final int boardHeight = boardWidth;

    private static final int cardWidth = 110; // ratio 1:1.4
    private static final int cardHeight = (int) (cardWidth * 1.4);

    private JFrame frame = new JFrame("Black Jack");
    private JPanel gamePanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
            // draw hidden card
            Image hiddenCardImg = cardImages.get("/assets/cards/BACK.png");
            if (!stayButton.isEnabled()) {
                hiddenCardImg = cardImages.get(hiddenCard.getImagePath());
            }
            g.drawImage(hiddenCardImg, 20, 20, cardWidth, cardHeight, null);

            // draw dealer hand
            for (int i = 0; i < dealerHand.size(); i++) {
                Card card = dealerHand.get(i);
                Image cardImg = cardImages.get(card.getImagePath());
                g.drawImage(cardImg, cardWidth + 25 + (cardWidth + 5) * i, 20, cardWidth, cardHeight, null);
            }

            // draw player hand
            for (int i = 0; i < playerHand.size(); i++) {
                Card card = playerHand.get(i);
                Image cardImg = cardImages.get(card.getImagePath());
                g.drawImage(cardImg, 20 + (cardWidth + 5) * i, 320, cardWidth, cardHeight, null);
            }

            if (!stayButton.isEnabled()) {
                dealerSum = reduceDealerAce();
                playerSum = reducePlayerAce();

                boolean playerBlackjack = playerHand.size() == 2 && playerSum == 21;

                String message = "";
                if (playerSum > 21) {
                    message = "You Lose!";
                }
                else if (playerBlackjack && dealerSum == 21) {
                    message = "Tie!";
                }
                else if (playerBlackjack) {
                    message = "Blackjack!";
                }
                else if (dealerSum > 21) {
                    message = "You Win!";
                }
                else if (playerSum > dealerSum) {
                    message = "You Win!";
                }
                else if (playerSum == dealerSum) {
                    message = "Tie!";
                }
                else if (playerSum < dealerSum) {
                    message = "You Lose!";
                }

                g.setFont(new Font("Arial", Font.PLAIN, 30));
                g.setColor(Color.WHITE);
                g.drawString(message, 220, 250);
            }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private JPanel buttonPanel = new JPanel();
    private JButton hitButton = new JButton("Hit");
    private JButton stayButton = new JButton("Stay");
    private JButton playAgainButton = new JButton("Play Again");

    BlackJack() {
        loadImages();
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        playAgainButton.setFocusable(false);
        playAgainButton.addActionListener(e -> {
            hitButton.setEnabled(true);
            stayButton.setEnabled(true);
            buttonPanel.remove(playAgainButton);
            startGame();
            checkForBlackjack();
            gamePanel.repaint();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        });

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Card card = deck.remove(deck.size() - 1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                gamePanel.repaint();
                if (reducePlayerAce() > 21) { // bust — end game automatically
                    endGame();
                } else if (reducePlayerAce() == 21) { // Blackjack — end game automatically
                    stayButton.doClick();
                }
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Card card = deck.remove(deck.size() - 1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce() ? 1 : 0;
                    dealerHand.add(card);
                }
                gamePanel.repaint();
                buttonPanel.add(playAgainButton);
                buttonPanel.revalidate();
                buttonPanel.repaint();
            }
        });

        gamePanel.repaint();

        // Check for blackjack on initial deal — all listeners are now registered
        SwingUtilities.invokeLater(this::checkForBlackjack);
    }

    private void checkForBlackjack() {
        if (reducePlayerAce() == 21) {
            stayButton.doClick();
        }
    }

    private void loadImages() {
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};
        for (String type : types) {
            for (String value : values) {
                String path = "/assets/cards/" + value + "-" + type + ".png";
                cardImages.put(path, new ImageIcon(getClass().getResource(path)).getImage());
            }
        }
        cardImages.put("/assets/cards/BACK.png",
            new ImageIcon(getClass().getResource("/assets/cards/BACK.png")).getImage());
    }

    private void endGame() {
        hitButton.setEnabled(false);
        stayButton.setEnabled(false);
        gamePanel.repaint();
        buttonPanel.add(playAgainButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void startGame() {
        // deck
        buildDeck();
        shuffleDeck();

        // dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size() - 1); // remove last card
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 : 0;

        Card card = deck.remove(deck.size() - 1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        // player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for (int i = 0; i < 2; i++) {
            card = deck.remove(deck.size() - 1);
            playerSum += card.getValue();
            playerAceCount += card.isAce() ? 1 : 0;
            playerHand.add(card);
        }

    }

    private void buildDeck() {
        deck = new ArrayList<Card>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < values.length; j++) {
                Card card = new Card(values[j], types[i]);
                deck.add(card);
            }
        }

    }

    private void shuffleDeck() {
        for (int i = deck.size() -1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = deck.get(i);
            deck.set(i, deck.get(j));
            deck.set(j, temp);
        }

    }

    private int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    private int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }
}