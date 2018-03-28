package nl.saxion.EHI1VSB4.Attributes;

import java.util.ArrayList;
import java.util.Iterator;

public class TeamCommunication {
    public ArrayList teammates = new ArrayList();
    private BasicsBot teammate;
    private BasicsBot[][] teams;

    public TeamCommunication() {
    }

    public BasicsBot getTeammate() {
        return this.teammate;
    }

    public void createTeam() {
        BasicsBot[] leaders = this.getleaders();
        BasicsBot[] slaves = this.getslaves(leaders);
        this.teams = this.teamup(leaders, slaves);
        BasicsBot[][] var3 = this.teams;
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            BasicsBot[] team = var3[var5];
            System.out.println("Team: ");
            BasicsBot[] var7 = team;
            int var8 = team.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                BasicsBot BasicsBot = var7[var9];
                System.out.println(BasicsBot.getName());
            }
        }

        this.setTeammate();
        System.out.println("Teammate: " + this.teammate.getName());
    }

    private BasicsBot[] getleaders() {
        BasicsBot[] furthest = new BasicsBot[]{new BasicsBot((String)null, 0.0D, 0.0D), new BasicsBot((String)null, 0.0D, 0.0D)};
        double furthestdistance = 0.0D;
        Iterator var4 = this.teammates.iterator();

        while(var4.hasNext()) {
            Object teammate1 = var4.next();
            BasicsBot bot = (BasicsBot)teammate1;
            Iterator var7 = this.teammates.iterator();

            while(var7.hasNext()) {
                Object teammate2 = var7.next();
                BasicsBot otherbot = (BasicsBot)teammate2;
                if (!bot.getName().equals(otherbot.getName())) {
                    double distance = bot.getDistance(otherbot.getX(), otherbot.getY());
                    if (distance > furthestdistance) {
                        furthest[0] = bot;
                        furthest[1] = otherbot;
                        furthestdistance = distance;
                    }
                }
            }
        }

        return furthest;
    }

    private BasicsBot[] getslaves(BasicsBot[] leaders) {
        BasicsBot[] slaves = new BasicsBot[]{new BasicsBot((String)null, 0.0D, 0.0D), new BasicsBot((String)null, 0.0D, 0.0D)};
        int slave = 0;
        Iterator var4 = this.teammates.iterator();

        while(var4.hasNext()) {
            Object teammate1 = var4.next();
            BasicsBot mate = (BasicsBot)teammate1;
            if (!mate.getName().equals(leaders[0].getName()) && !mate.getName().equals(leaders[1].getName())) {
                slaves[slave] = mate;
                ++slave;
            }
        }

        return slaves;
    }

    private BasicsBot[][] teamup(BasicsBot[] leaders, BasicsBot[] slaves) {
        double[] distances = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
        int distance = 0;
        BasicsBot[] var5 = leaders;
        int var6 = leaders.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            BasicsBot leader = var5[var7];
            BasicsBot[] var9 = slaves;
            int var10 = slaves.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                BasicsBot slave = var9[var11];
                distances[distance] = leader.getDistance(slave.getX(), slave.getY());
                ++distance;
            }
        }

        double var23 = distances[0] + distances[3];
        double var24 = distances[1] + distances[2];
        BasicsBot[][] var25 = new BasicsBot[][]{{new BasicsBot((String)null, 0.0D, 0.0D), new BasicsBot((String)null, 0.0D, 0.0D)}, {new BasicsBot((String)null, 0.0D, 0.0D), new BasicsBot((String)null, 0.0D, 0.0D)}};
        if (var23 < var24) {
            var25[0][0] = leaders[0];
            var25[0][1] = slaves[0];
            var25[1][0] = leaders[1];
            var25[1][1] = slaves[1];
        } else {
            var25[0][0] = leaders[1];
            var25[0][1] = slaves[0];
            var25[1][0] = leaders[0];
            var25[1][1] = slaves[1];
        }

        return var25;
    }

    public void setTeammate() {
        int teamcode = 0;
        BasicsBot[][] var2 = this.teams;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            BasicsBot[] team = var2[var4];
            int teammatecode = 0;
            BasicsBot[] var7 = team;
            int var8 = team.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                BasicsBot bot = var7[var9];
                if (bot.getName().equals(((BasicsBot)this.teammates.get(0)).getName())) {
                    if (teamcode == 0 && teammatecode == 0) {
                        this.teammate = this.teams[0][1];
                    } else if (teamcode == 0 && teammatecode == 1) {
                        this.teammate = this.teams[0][0];
                    } else if (teamcode == 1 && teammatecode == 0) {
                        this.teammate = this.teams[1][1];
                    } else if (teamcode == 1 && teammatecode == 1) {
                        this.teammate = this.teams[1][0];
                    }
                }

                ++teammatecode;
            }

            ++teamcode;
        }

    }

    public void updateBots(String RobotScannedName, double x, double y) {
        Iterator var6 = this.teammates.iterator();

        while(var6.hasNext()) {
            Object teammate1 = var6.next();
            BasicsBot item = (BasicsBot)teammate1;
            if (item.getName().equalsIgnoreCase(RobotScannedName)) {
                item.setX(x);
                item.setY(y);
            }
        }

    }

    public BasicsBot getThis() {
        return (BasicsBot)this.teammates.get(0);
    }

    BasicsBot[] getTeam(String robotname) {
        for(int team = 0; team < this.teams.length; ++team) {
            for(int mate = 0; mate < this.teams[team].length; ++mate) {
                if (robotname.equals(this.teams[team][mate].getName())) {
                    return this.teams[team];
                }
            }
        }

        return null;
    }

    BasicsBot[] getOtherTeam(String robotname) {
        if (this.teams.length == 2) {
            for(int team = 0; team < this.teams.length; ++team) {
                for(int mate = 0; mate < this.teams[team].length; ++mate) {
                    if (robotname.equals(this.teams[team][mate].getName())) {
                        if (team == 0) {
                            return this.teams[1];
                        }

                        return this.teams[0];
                    }
                }
            }
        }

        return null;
    }

    private ArrayList<ArrayList<BasicsBot>> arrayToList() {
        ArrayList<ArrayList<BasicsBot>> tempTeams = new ArrayList();

        for(int teamIndex = 0; teamIndex < this.teams.length; ++teamIndex) {
            tempTeams.add(new ArrayList());

            for(int index = 0; index < this.teams[teamIndex].length; ++index) {
                ((ArrayList)tempTeams.get(teamIndex)).add(this.teams[teamIndex][index]);
            }
        }

        return tempTeams;
    }

    public void update(String robotName) {
        ArrayList<ArrayList<BasicsBot>> tempTeams = this.arrayToList();
        tempTeams.forEach((BasicsBotsx) -> {
            BasicsBotsx.removeIf((BasicsBot) -> {
                return BasicsBot.getName().equals(robotName);
            });
        });
        if (((ArrayList)tempTeams.get(0)).size() == 1 && tempTeams.get(1) != null && ((ArrayList)tempTeams.get(1)).size() == 1) {
            ArrayList<ArrayList<BasicsBot>> newResult = new ArrayList();
            newResult.add(new ArrayList());
            ((ArrayList)newResult.get(0)).add(((ArrayList)tempTeams.get(0)).get(0));
            ((ArrayList)newResult.get(0)).add(((ArrayList)tempTeams.get(1)).get(0));
            this.teams = (BasicsBot[][])newResult.stream().map((BasicsBotsx) -> {
                return (BasicsBot[])BasicsBotsx.toArray(new BasicsBot[0]);
            }).toArray((x$0) -> {
                return new BasicsBot[x$0][];
            });
        } else {
            this.teams = (BasicsBot[][])tempTeams.stream().map((BasicsBotsx) -> {
                return (BasicsBot[])BasicsBotsx.toArray(new BasicsBot[0]);
            }).toArray((x$0) -> {
                return new BasicsBot[x$0][];
            });
        }

        BasicsBot[][] var11 = this.teams;
        int var4 = var11.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            BasicsBot[] BasicsBots = var11[var5];
            System.out.println("new team");
            BasicsBot[] var7 = BasicsBots;
            int var8 = BasicsBots.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                BasicsBot BasicsBot = var7[var9];
                System.out.println(BasicsBot.getName());
            }
        }

    }
}
