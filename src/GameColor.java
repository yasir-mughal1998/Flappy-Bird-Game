import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author yasir
 */

public class GameColor extends JPanel
{
    public FlappyBird flappyBird = null;
    public BufferedImage image; 
    public int currentImage = 1;
    public int birdX = 380;
    public int birdY = 290;
            
    public String image1 = "yellowbird-midflap.png";
    public String image2 = "yellowbird-downflap.png";
    public String image3 = "yellowbird-upflap.png";
     
    public GameColor(){       
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        if(flappyBird == null)
        {
            try {
                flappyBird = FlappyBird.getInstance();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try {
                if(currentImage == 1 )
                {
                    image = ImageIO.read(new File(image1));
                    currentImage = 2;
                }
                else if(currentImage == 2 )
                {
                    image = ImageIO.read(new File(image2));
                    currentImage = 3;
                }
                else if(currentImage == 3 )
                {
                    image = ImageIO.read(new File(image2));
                    currentImage = 1;
                }
        } catch (Exception e) {
            e.printStackTrace();
            }
        
    	super.paintComponent(g);
	flappyBird.paint(g);
        g.drawImage(image,birdX,birdY,this);
    }
    
    public Rectangle getBoundsOfBird() {
        return new Rectangle(birdX, birdY, image.getWidth(), image.getHeight());
    }
}
