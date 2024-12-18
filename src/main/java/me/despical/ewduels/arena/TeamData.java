package me.despical.ewduels.arena;

import me.despical.ewduels.user.User;

/**
 * @author Despical
 * <p>
 * Created at 17.12.2024
 */
public class TeamData {

    private int score;
    private boolean quit;

    private User user;
    private final Team team;

    public TeamData(Team team) {
        this.team = team;
    }

    public int getScore() {
        return score;
    }

    public void addScore() {
        score++;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    public boolean hasQuit() {
        return quit;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.quit = false;
    }

    public Team getTeam() {
        return team;
    }

    public boolean isInTeam(User target) {
        return user != null && user.equals(target);
    }

    public boolean isUserOnline() {
        return user != null && user.getPlayer() != null;
    }
}
