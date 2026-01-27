import java.awt.*;
import java.awt.Rectangle;

public class Enemy {
    public int x, y;
    public int size = 20;
    public int tileSize = 20;
    public int direction = 2; // 初期は下向き

    public Enemy(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void update(int playerX, int playerY) {
        // プレイヤーに向かって移動する計算
        // 難しい計算はやめて、単純に比較します
        // 現在地とプレイヤーの距離を測る（絶対値）
        int diffX = Math.abs(this.x - playerX);
        int diffY = Math.abs(this.y - playerY);

        int moveStep = size; // 1マスの幅 (40)

        // ★ここがポイント！
        // 「XとY、どっちのズレが大きいか？」を比べて、片方だけ動かす
        
        if (diffX > diffY) {
            // 横の方が離れているなら、横だけ動く
            if (this.x < playerX) {
                this.x += moveStep;
                direction = 1; // 右
            } else {
                this.x -= moveStep;
                direction = 3; // 左
            }
        } else {
            // 縦の方が離れている（または同じ）なら、縦だけ動く
            if (this.y < playerY) {
                this.y += moveStep;
                direction = 2; // 下
            } else {
                this.y -= moveStep;
                direction = 0; // 上
            }
        }
        
        // ※これで「XもYも同時に変わる」ことがなくなり、斜め移動しなくなります
    }


    public void draw(Graphics2D g2) {
        g2.setColor(Color.RED);
        g2.fillRect(x, y, tileSize, tileSize);

        g2.setColor(Color.YELLOW);
            int eyeSize = 8;
            switch (direction) {
                case 0: // 上
                   g2.fillRect(x + 4, y + 2, eyeSize, eyeSize);
                   g2.fillRect(x + tileSize - eyeSize - 4, y + 2, eyeSize, eyeSize);
                   break;
                case 1: // 右
                   g2.fillRect(x + tileSize - eyeSize - 4, y + 4, eyeSize, eyeSize);
                   g2.fillRect(x + tileSize - eyeSize - 4, y + tileSize - eyeSize - 4, eyeSize, eyeSize);
                   break;
                case 2: // 下
                   g2.fillRect(x + 4, y + tileSize - eyeSize - 4, eyeSize, eyeSize);
                   g2.fillRect(x + tileSize - eyeSize - 4, y + tileSize - eyeSize - 4, eyeSize, eyeSize);
                   break;
                case 3: // 左
                   g2.fillRect(x + 2, y + 4, eyeSize, eyeSize);
                   g2.fillRect(x + 2, y + tileSize - eyeSize - 4, eyeSize, eyeSize);
                   break;
            }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, tileSize, tileSize);
    }
}
