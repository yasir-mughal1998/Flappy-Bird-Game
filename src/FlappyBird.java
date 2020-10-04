import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author yasir
 */

public class FlappyBird implements ActionListener, MouseListener, KeyListener
{
    private static FlappyBird flappyBird = null;
    public final int WIDTH = 800, HEIGHT = 600;
    public GameColor gc;
    public ArrayList<Rectangle> columns;
    public int temp, birdYMotion, score;
    public boolean gameOver, started;
    public Random rand;
    public Timer timer;
    public Rectangle rectBird;
    
    private FlappyBird() throws IOException
    {
	JFrame jframe = new JFrame();
	timer = new Timer(20,this);
        
	gc = new GameColor();
        rand = new Random();

	jframe.add(gc);
	jframe.setTitle("Flappy Bird");
	jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	jframe.setSize(WIDTH, HEIGHT);
	jframe.addMouseListener(this);
	jframe.addKeyListener(this);
	jframe.setResizable(false);
        jframe.setLocation(280,90);
	jframe.setVisible(true);

	columns = new ArrayList<Rectangle>();
   
	addColumn(true);
	addColumn(true);
	addColumn(true);
	addColumn(true);
        
        
	timer.start();
    }

    public static FlappyBird getInstance() throws IOException
    {
        if(flappyBird == null)
        {
            flappyBird = new FlappyBird();
        }
        return flappyBird;
    }
    
    public void addColumn(boolean start)
    {       
	int space = 300;
	int width = 100;
	int height = 50 + rand.nextInt(230);
                
	if (start)
	{       
            columns.add(new Rectangle(WIDTH + width + columns.size() * 200, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 200, 0, width, HEIGHT - height - space));
        }
  	else
	{             
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 400, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }
        
    public void paintColumn(Graphics g, Rectangle column)
    {
	g.setColor(Color.green.darker());
	g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void jump()
    {
	if (gameOver)
	{
            timer.start();
            gc.birdX = 380;
            gc.birdY = 290;
            columns.clear();
            birdYMotion = 0;
            score = 0;
            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;
	}
        
	if (!started)
	{
            started = true;
	}
	else if (!gameOver)
	{
            if (birdYMotion > 0)
            {
		birdYMotion = 0;
            }
		
            birdYMotion -= 10;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
	int speed = 10;
        temp++;
        rectBird = gc.getBoundsOfBird();
                
	if (started)
        {
            for (int i = 0; i < columns.size(); i++)
            {
		Rectangle column = columns.get(i);
                column.x -= speed;
            }
            if (temp % 2 == 0 && birdYMotion < 15)
            {
        	birdYMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++)
            {
		Rectangle column = columns.get(i);
		if (column.x + column.width < 0)
                    {
			columns.remove(column);
	             	addColumn(false);
                    }
            }

            gc.birdY += birdYMotion;

            for (Rectangle column : columns)
            {
		if (column.y == 0 && gc.birdX + gc.image.getWidth() / 2 > column.x + column.width / 2 - 5 && gc.birdX + gc.image.getWidth() / 2 < column.x + column.width / 2 + 5)
		{
                    score++;
                    playAudio("point.wav");
		}
                
		if (column.intersects(rectBird))
		{
                    playAudio("hit.wav");
                    gameOver = true;
                    timer.stop();
                }
            }

            if (gc.birdY < 0)
            {
                playAudio("hit.wav");
                gameOver = true;
                timer.stop();
            }

            if (gc.birdY + birdYMotion >= HEIGHT - 120)
            {
		gc.birdY = HEIGHT - 120 - gc.image.getHeight();
		playAudio("hit.wav");
                gameOver = true;
                timer.stop();
            }
        }

	gc.repaint();
    }

    public void paint(Graphics g)
    {
	g.setColor(Color.cyan);
	g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.orange);
	g.fillRect(0, HEIGHT - 120, WIDTH, 120);

	g.setColor(Color.green);
	g.fillRect(0, HEIGHT - 120, WIDTH, 20);


	for (Rectangle column : columns)
	{
            paintColumn(g, column);
	}

	g.setColor(Color.white);
	g.setFont(new Font("Arial", 1, 100));

	if (!started)
	{
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
	}

	if (gameOver)
	{
            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
            
            g.setColor(Color.MAGENTA);
            g.setFont(new Font("Arial", 1, 50));              
            g.drawString("Total Score = " + score,200 ,HEIGHT / 2 - 200);
            
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", 1, 35));
            g.drawString("Press Space To Restart game!",150 ,HEIGHT / 2 + 80);
	}

	if (!gameOver && started)
	{
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
	}
    }
    
    public void playAudio(String filePath)
    {
        InputStream music;
        try {
            music = new FileInputStream(new File(filePath));
            AudioStream audio = new AudioStream(music);
            AudioPlayer.player.start(audio);
        } catch (Exception e) {
            System.out.println("Error");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
	jump();
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
	if (e.getKeyCode() == KeyEvent.VK_SPACE)
	{
        	jump();
	}
    }
	
    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }
    
    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {	
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
    }
}
