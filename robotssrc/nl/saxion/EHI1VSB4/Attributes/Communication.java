package nl.saxion.EHI1VSB4.Attributes;

import robocode.MessageEvent;

import java.io.Serializable;

public class Communication {
    private int messages = 0;
    private BasicsBot otherTeamEnemy;
    private BasicsBot enemy;

    public Communication() {
    }

    public void processmessage(MessageEvent event, TeamCommunication team) {
        String message = event.getMessage().toString();
        String[] messagesplit = message.split(";");
        ++this.messages;
        double x;
        double y;
        if (this.messages <= 3) {
            x = Double.parseDouble(messagesplit[0]);
            y = Double.parseDouble(messagesplit[1]);
            team.teammates.add(new BasicsBot(event.getSender(), x, y));
        } else if (this.messages >= 4) {
            if (messagesplit.length == 2) {
                x = Double.parseDouble(messagesplit[0]);
                y = Double.parseDouble(messagesplit[1]);
                team.updateBots(event.getSender(), x, y);
            } else if (messagesplit.length == 3) {
                System.out.println("Message from: " + event.getSender() + "Message: " + message);
                String name = messagesplit[0];
                double xx = Double.parseDouble(messagesplit[1]);
                Double yy = Double.parseDouble(messagesplit[2]);
                BasicsBot newEnemy = new BasicsBot(name, xx, yy.doubleValue());
                if (event.getSender().equals(team.getTeammate().getName())) {
                    this.setEnemy(team, newEnemy);
                } else {
                    this.setOtherTeamEnemy(team, newEnemy);
                }
            }
        }

    }

    public int getmessageNumber() {
        return this.messages;
    }

    private boolean checkEnemy(BasicsBot[] team, BasicsBot[] otherTeam, BasicsBot newEnemy, BasicsBot oldEnemy, BasicsBot otherEnemy) {
        return this.checkOtherteammateEnemy(team, otherTeam, newEnemy, otherEnemy) && this.checkOldEnemy(team, newEnemy, oldEnemy);
    }

    private boolean checkOtherteammateEnemy(BasicsBot[] team, BasicsBot[] otherTeam, BasicsBot enemy, BasicsBot otherEnemy) {
        if (otherTeam != null && otherEnemy != null && team != null) {
            if (enemy.getName().equals(otherEnemy.getName())) {
                double thisTeamDistance;
                if (team.length == 1) {
                    thisTeamDistance = team[0].getDistance(enemy.getX(), enemy.getY());
                } else {
                    thisTeamDistance = team[0].getTeamDistance(team[1], enemy);
                }

                double otherTeamDistance;
                if (otherTeam.length == 1) {
                    otherTeamDistance = otherTeam[0].getDistance(enemy.getX(), enemy.getY());
                } else {
                    otherTeamDistance = otherTeam[0].getTeamDistance(otherTeam[1], enemy);
                }

                return thisTeamDistance < otherTeamDistance;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private boolean checkOldEnemy(BasicsBot[] team, BasicsBot newEnemy, BasicsBot oldEnemy) {
        if (oldEnemy == null) {
            return true;
        } else if (newEnemy.getName().equals(oldEnemy.getName())) {
            return false;
        } else {
            if (team != null) {
                if (team.length == 1) {
                    if (team[0].getDistance(newEnemy.getX(), newEnemy.getY()) < team[0].getDistance(oldEnemy.getX(), oldEnemy.getY())) {
                        return true;
                    }

                    return false;
                }

                if (team[0].getTeamDistance(team[1], newEnemy) < team[0].getTeamDistance(team[1], oldEnemy)) {
                    return true;
                }
            }

            return false;
        }
    }

    public void resetEnemy() {
        this.enemy = null;
    }

    public Serializable Message(double x, double y) {
        return x + ";" + y;
    }

    public Serializable Message(String enemyName, double x, double y) {
        return enemyName + ";" + x + ";" + y;
    }

    public BasicsBot getEnemy() {
        return this.enemy;
    }

    public boolean checkScannedEnemy(TeamCommunication team, BasicsBot scannedBot) {
        return this.setEnemy(team, scannedBot);
    }

    public BasicsBot getOtherTeamEnemy() {
        return this.otherTeamEnemy;
    }

    public void resetOtherTeamEnemy() {
        this.otherTeamEnemy = null;
    }

    private boolean setEnemy(TeamCommunication team, BasicsBot teammateEnemynew) {
        if (this.checkEnemy(team.getTeam(team.getThis().getName()), team.getOtherTeam(team.getThis().getName()), teammateEnemynew, this.enemy, this.otherTeamEnemy)) {
            this.enemy = teammateEnemynew;
            if (this.otherTeamEnemy != null && this.enemy.getName().equals(this.otherTeamEnemy.getName()) && this.checkOtherteammateEnemy(team.getTeam(team.getThis().getName()), team.getOtherTeam(team.getThis().getName()), this.enemy, this.otherTeamEnemy)) {
                this.otherTeamEnemy = null;
                System.out.println("This team taking over " + this.enemy.getName());
            }

            System.out.println("Enemy: " + this.enemy.getName());
            if (this.otherTeamEnemy != null) {
                System.out.println("OtherEnemy: " + this.otherTeamEnemy.getName());
            } else {
                System.out.println("OtherEnemy is not set");
            }

            return true;
        } else {
            return false;
        }
    }

    private void setOtherTeamEnemy(TeamCommunication team, BasicsBot otherTeamEnemyNew) {
        if (this.checkEnemy(team.getOtherTeam(team.getThis().getName()), team.getTeam(team.getThis().getName()), otherTeamEnemyNew, this.otherTeamEnemy, this.enemy)) {
            this.otherTeamEnemy = otherTeamEnemyNew;
            if (this.enemy != null && this.otherTeamEnemy.getName().equals(this.enemy.getName()) && this.checkOtherteammateEnemy(team.getOtherTeam(team.getThis().getName()), team.getTeam(team.getThis().getName()), this.otherTeamEnemy, this.enemy)) {
                this.enemy = null;
                System.out.println("Other team taking over " + this.otherTeamEnemy.getName());
            }

            if (this.enemy != null) {
                System.out.println("Enemy: " + this.enemy.getName());
            } else {
                System.out.println("Enemy is not set");
            }

            System.out.println("OtherteamEnemy: " + this.otherTeamEnemy.getName());
        }

    }
}
