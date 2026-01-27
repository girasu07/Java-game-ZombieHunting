import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

    // 画面設定
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int TILE_SIZE = 40; // ★追加：マスの定義

    // ゲームループ用
    private Thread gameThread;
    private boolean isRunning = true;

    // ゲームオブジェクト
    private Player player;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    
    // 入力状態
    private boolean keyUp, keyDown, keyLeft, keyRight;
    private boolean keyShift;
    //private boolean keySpace;
    private int mouseX, mouseY;

    private boolean isShooting = false; // マウスを押しているか
    private int shootTimer = 0;
    private static final int SHOOT_DELAY = 15; // 連射速度（小さいほど速い）

    private int PlayermoveTimer = 0;
    private double EnemymoveTimer = 0;
    private static final int MOVE_DELAY = 10; // 何フレームごとに動くか（小さいほど速い）
    // 60FPSで動作する場合、10 = 約0.16秒に1回移動

    // スポーン管理
    private Random random = new Random();
    private int spawnTimer = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        
        // リスナー登録
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);

        // プレイヤー初期化
        player = new Player(WIDTH / 2, HEIGHT / 2);
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        // ゲームループ (簡易版: 60FPS目標)
        while (isRunning) {
            updateGame();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    

    private void updateGame() {
        // 1. プレイヤー更新
        //player.move(keyUp, keyDown, keyLeft, keyRight, WIDTH, HEIGHT);

        // 2. 弾の更新
        Iterator<Bullet> bit = bullets.iterator();
        while (bit.hasNext()) {
            Bullet b = bit.next();
            b.update();
            if (b.isOffScreen(WIDTH, HEIGHT)) {
                bit.remove();
            }
        }

        // 3. 敵のスポーン (約1秒ごと)
        spawnTimer++;
        if (spawnTimer > 120) {
            spawnEnemy();
            spawnTimer = 0;
        }

        shootTimer++; // 常にカウントアップ

        if (isShooting && shootTimer >= SHOOT_DELAY) {
            // 弾の発射処理
            double angle = 0;
            switch (player.direction) {
                case 0: angle = -Math.PI / 2; break; // 上
                case 1: angle = 0; break;            // 右
                case 2: angle = Math.PI / 2; break;  // 下
                case 3: angle = Math.PI; break;      // 左
            }
            bullets.add(new Bullet(player.getCenterX(), player.getCenterY(), angle));

            // タイマーリセット
            shootTimer = 0;
        }

        // 4. 敵の移動と当たり判定
        Iterator<Enemy> eit = enemies.iterator();
        while (eit.hasNext()) {
            Enemy e = eit.next();

            // プレイヤーとの衝突
            if (e.getBounds().intersects(player.getBounds())) {
                isRunning = false;
                JOptionPane.showMessageDialog(this, "GAME OVER");
                System.exit(0);
            }

            // 弾との衝突
            boolean hit = false;
            Iterator<Bullet> bulletIt = bullets.iterator();
            while (bulletIt.hasNext()) {
                Bullet b = bulletIt.next();
                if (e.getBounds().contains(b.x, b.y)) {
                    bulletIt.remove(); // 弾消去
                    hit = true;
                    break;
                }
            }
            if (hit) {
                eit.remove(); // 敵消去
            }
        }

        // --- ★変更点3：タイマーを使った移動処理 ---
        
        // 1. タイマーを進める
        PlayermoveTimer++;
        EnemymoveTimer += 0.5;
        // 2. タイマーが溜まっている かつ キーが押されていたら移動
        if (PlayermoveTimer >= MOVE_DELAY) {
            boolean moved = false; // 動いたかどうかのチェック

            if (keyUp) {
                player.moveStep(0, -1, WIDTH, HEIGHT, keyShift);
                moved = true;
            } else if (keyDown) {
                player.moveStep(0, 1, WIDTH, HEIGHT, keyShift);
                moved = true;
            } else if (keyLeft) {
                player.moveStep(-1, 0, WIDTH, HEIGHT, keyShift);
                moved = true;
            } else if (keyRight) {
                player.moveStep(1, 0, WIDTH, HEIGHT, keyShift);
                moved = true;
            }

            // 動いた場合のみ、タイマーをリセット（＝次は10フレーム待て）
            
           if (moved) {
                PlayermoveTimer = 0;    
            } else {
                // 動いていないなら、次はすぐに動けるようにタイマーを満タンにしておく
                PlayermoveTimer = MOVE_DELAY;
            }    
        }
        if (EnemymoveTimer >= MOVE_DELAY) {
            // 敵全員移動
            for (Enemy e : enemies) {
                e.update(player.x, player.y);
            }
            EnemymoveTimer = 0;
        }
    }
    
    private void spawnEnemy() {
        // 0:上, 1:左, 2:右
        int dir = random.nextInt(3);
        int ex = 0, ey = 0;
        int size = TILE_SIZE; // 敵のサイズ

        switch (dir) {
            case 0: ex = random.nextInt(WIDTH/size); ey = -size; break;
            case 1: ex = -size; ey = random.nextInt(HEIGHT/size); break;
            case 2: ex = WIDTH; ey = random.nextInt(HEIGHT/size); break;
        }
        enemies.add(new Enemy(ex, ey));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 各オブジェクトに「自分を描画しろ」と命令する
        player.draw(g2);
        
        // 銃口のガイド線
        //g2.setColor(Color.GREEN);
        //g2.drawLine((int)player.getCenterX(), (int)player.getCenterY(), mouseX, mouseY);

        for (Bullet b : bullets) b.draw(g2);
        for (Enemy e : enemies) e.draw(g2);

        // paintComponentの中の super.paintComponent(g); の直下に書く
        g2.setColor(Color.GRAY);
        for (int i = 0; i <= WIDTH; i += TILE_SIZE) g2.drawLine(i, 0, i, HEIGHT);
        for (int i = 0; i <= HEIGHT; i += TILE_SIZE) g2.drawLine(0, i, WIDTH, i);
    }

    // --- 入力処理 ---
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) keyUp = true;
        if (code == KeyEvent.VK_S) keyDown = true;
        if (code == KeyEvent.VK_A) keyLeft = true;
        if (code == KeyEvent.VK_D) keyRight = true;

        if (code == KeyEvent.VK_SHIFT) keyShift = true;

        if (code == KeyEvent.VK_SPACE) {
            isShooting = true;
            shootTimer = SHOOT_DELAY;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) keyUp = false;
        if (code == KeyEvent.VK_S) keyDown = false;
        if (code == KeyEvent.VK_A) keyLeft = false;
        if (code == KeyEvent.VK_D) keyRight = false;

        if (code == KeyEvent.VK_SHIFT) keyShift = false;
        
        // キーを離した瞬間、次の入力がすぐ効くようにタイマーを少し進めておくテクニック
        // （必須ではありませんが、操作感が良くなります）
        if (!keyUp && !keyDown && !keyLeft && !keyRight) {
             PlayermoveTimer = MOVE_DELAY;
        }

        if (code == KeyEvent.VK_SPACE) {
            isShooting = false;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //isShooting = true;
        //shootTimer = SHOOT_DELAY; // スペースキーで発射に変更
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        //isShooting = false;
    }

    @Override public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void mouseDragged(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}

