import java.awt.*;
import java.awt.Rectangle;

public class Player {
    // 外部からアクセスしやすいようpublicにしていますが、
    // 本格的にはprivateにしてgetter/setterを使うのが定石です
    public int x, y;
    public int size = 20;
    public int tileSize = 20;

    public int direction = 1; // 初期は右向き
    public boolean isDown;

    public Player(int startX, int startY) {
        // スタート位置もマスの角にきれいに合わせる補正
        this.x = (startX / tileSize) * tileSize;
        this.y = (startY / tileSize) * tileSize;
    }

    public void moveStep(int dx, int dy, int screenWidth, int screenHeight, boolean lockDirection) {
        int nextX = x + (dx * tileSize);
        int nextY = y + (dy * tileSize);

        if (!lockDirection){
            if (dy < 0) direction = 0; // 上
            if (dx > 0) direction = 1; // 右
            if (dy > 0) direction = 2; // 下
            if (dx < 0) direction = 3; // 左
        }
        // 画面外に出ないように制限
        if (nextX >= 0 && nextX <= screenWidth - size) {
            x = nextX;
        }
        if (nextY >= 0 && nextY <= screenHeight - size) {
            y = nextY;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.GREEN);
        g2.fillRect(x, y, tileSize, tileSize);

        g2.setColor(Color.BLACK);
            switch (direction) {
                case 0: // 上
                    g2.fillRect(x + 5, y, 10, 5);
                    break;
                case 1: // 右
                    g2.fillRect(x + tileSize - 5, y + 5, 5, 10);
                    break;
                case 2: // 下
                    g2.fillRect(x + 5, y + tileSize - 5, 10, 5);
                    break;
                case 3: // 左
                    g2.fillRect(x, y + 5, 5, 10);
                    break;
                default:
                    break;
            }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, tileSize, tileSize);
    }
    
    // 中心座標を取得（射撃計算用）
    public double getCenterX() { return x + tileSize / 2.0; }
    public double getCenterY() { return y + tileSize / 2.0; }
}
