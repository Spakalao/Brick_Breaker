import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    public final String name;
    public final int score;
    public final long timestamp;

    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return score == player.score &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    @Override
    public String toString() {
        return name + " : " + score;
    }
}
