import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        
        // ダイアログで役割を選択
        String[] options = {"サーバーとして起動 (Host)", "クライアントとして参加 (Guest)"};
        int choice = JOptionPane.showOptionDialog(null, 
            "モードを選択してください", 
            "ネットワーク設定", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Zombie Survival - " + (choice == 0 ? "Host" : "Guest"));

        GamePanel gamePanel = new GamePanel();
        
        // ★ここでモード情報をGamePanelに渡す必要があります
        if (choice == 0) {
            gamePanel.setupServer(); // サーバーとして待機
        } else {
            // IPアドレスを入力させる（テスト時は "localhost" でOK）
            String ip = JOptionPane.showInputDialog("サーバーのIPアドレスを入力", "localhost");
            gamePanel.setupClient(ip); // クライアントとして接続
        }

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.startGame();
    }
}