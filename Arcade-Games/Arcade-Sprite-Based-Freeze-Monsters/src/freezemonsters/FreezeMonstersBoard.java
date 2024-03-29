package freezemonsters;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Random;
import javax.swing.ImageIcon;

import spriteframework.AbstractBoard;
import spriteframework.sprite.BadSprite;
import spriteframework.sprite.Player;

import freezemonsters.sprite.*;

public class FreezeMonstersBoard extends AbstractBoard{
    private FreezingShot shot;

    private int direction = -1;
    private int freezes = 0;

    private String explImg = "images/explosion.png";

    public FreezeMonstersBoard () {

    }

    protected void setBoardColor(Graphics g){
        g.setColor(new java.awt.Color(0,255,255));
        g.fillRect(0, 0, d.width, d.height);
    }

    protected void createBadSprites(){
        int m = 1;
        for(int i=0; i < 3; i++){
            for(int j=0; j < 3; j++){
                Monster monster = new Monster(Commons.MONSTER_INIT_X,
                        Commons.MONSTER_INIT_Y);
                badSprites.add(monster);
                m++;
            }
        }
    }

    protected void createOtherSprites(){
       shot = new FreezingShot();
    }

    private void drawShot(Graphics g) {
        if (shot.isVisible()) {
            g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
        }
    }

    protected void drawOtherSprites(Graphics g) {
        drawShot(g);
    }

//    TODO:
//    @Override
//    protected void update() {
//    }

    protected void update() {

        if (freezes == Commons.NUMBER_OF_MONSTER_TO_FREEZE) {

            inGame = false;
            timer.stop();
            message = "Game won!";
        }

        // player
        for (Player player: players)
            player.act();

        // shot
        if (shot.isVisible()) {

            int shotX = shot.getX();
            int shotY = shot.getY();

            for (BadSprite monster : badSprites) {

                int alienX = monster.getX();
                int alienY = monster.getY();

                if (monster.isVisible() && shot.isVisible()) {
                    if (shotX >= (alienX)
                            && shotX <= (alienX + Commons.MONSTER_WIDTH)
                            && shotY >= (alienY)
                            && shotY <= (alienY + Commons.MONSTER_HEIGTH)) {

                        ImageIcon ii = new ImageIcon(explImg);
                        monster.setImage(ii.getImage());
                        monster.setDying(true);
                        freezes++;
                        shot.die();
                    }
                }
            }

            int y = shot.getY();
            y -= 4;

            if (y < 0) {
                shot.die();
            } else {
                shot.setY(y);
            }
        }

        // monsters

        for (BadSprite monster : badSprites) {

            int x = monster.getX();

            if (x >= Commons.BOARD_WIDTH - Commons.BORDER_RIGHT && direction != -1) {

                direction = -1;

                Iterator<BadSprite> i1 = badSprites.iterator();

                while (i1.hasNext()) {
                    BadSprite a2 = i1.next();
                    a2.setY(a2.getY() + Commons.GO_DOWN);
                }
            }

            if (x <= Commons.BORDER_LEFT && direction != 1) {

                direction = 1;

                Iterator<BadSprite> i2 = badSprites.iterator();

                while (i2.hasNext()) {
                    BadSprite a = i2.next();
                    a.setY(a.getY() + Commons.GO_DOWN);
                }
            }
        }

        Iterator<BadSprite> it = badSprites.iterator();

        while (it.hasNext()) {

            BadSprite monster = it.next();

            if (monster.isVisible()) {

                int y = monster.getY();

                if (y > Commons.GROUND - Commons.MONSTER_HEIGTH) {
                    inGame = false;
                    message = "Invasion!";
                }

                monster.moveX(direction);
            }
        }

        // goops

        updateOtherSprites();
    }

    protected void updateOtherSprites() {
        Random generator = new Random();

        for (BadSprite monster : badSprites) {

            int shot = generator.nextInt(15);
            Goop goop = ((Monster)monster).getGoop();

            if (shot == Commons.CHANCE && monster.isVisible() && monster.isDestroyed()) {

                goop.setFrozen(false);
                goop.setX(monster.getX());
                goop.setY(monster.getY());
            }

            int bombX = goop.getX();
            int bombY = goop.getY();
            int playerX = players.get(0).getX();
            int playerY = players.get(0).getY();

            if (players.get(0).isVisible() && !goop.isDestroyed()) {

                if (bombX >= (playerX)
                        && bombX <= (playerX + Commons.WOODY_WIDTH)
                        && bombY >= (playerY)
                        && bombY <= (playerY + Commons.WOODY_HEIGHT)) {

                    ImageIcon ii = new ImageIcon(explImg);
                    players.get(0).setImage(ii.getImage());
                    players.get(0).setDying(true);
                    goop.setFrozen(true);
                }
            }

            if (!goop.isDestroyed()) {

                goop.setY(goop.getY() + 1);

                if (goop.getY() >= Commons.GROUND - Commons.GOOP_HEIGHT) {

                    goop.setFrozen(true);
                }
            }
        }
    }

    protected void processOtherSprites(Player player, KeyEvent e) {
        int x = player.getX();
        int y = player.getY();

        int key = e.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {

            if (inGame) {

                if (!shot.isVisible()) {

                    shot = new FreezingShot(x, y);
                }
            }
        }
    }
}
