import java.io.Serializable;

public class ClientInput implements Serializable {
    public boolean up, down, left, right;
    public boolean isShooting; // 撃っているかどうかも送る
}