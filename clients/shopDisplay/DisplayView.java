package clients.shopDisplay;

import middle.MiddleFactory;
import middle.OrderException;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * The visual display seen by customers (Change to graphical version)
 * Change to a graphical display
 * @author  Mike Smith University of Brighton
 * @version 1.0
 */
public class DisplayView extends Canvas implements Observer
{
  private static final long serialVersionUID = 1L;
  private Font font = new Font("Monospaced",Font.BOLD,24);
  private int H = 300;         // Height of window 
  private int W = 400;         // Width  of window 
  private String textToDisplay = "";
  private String displayTitle = "";
  private String[] displayCategories = {"Waiting", "Being Picked Up", "To Be Collected"};
  private String[] displayOrders = {"", "", ""};
  private DisplayController cont= null;
  
  /**
   * Construct the view
   * @param rpc   Window in which to construct
   * @param mf    Factor to deliver order and stock objects
   * @param x     x-coordinate of position of window on screen 
   * @param y     y-coordinate of position of window on screen  
   */
  
  public DisplayView(  RootPaneContainer rpc, MiddleFactory mf, int x, int y )
  {
    Container cp         = rpc.getContentPane();    // Content Pane
    Container rootWindow = (Container) rpc;         // Root Window
    cp.setLayout( new BorderLayout() );             // Border N E S W CENTER 
    rootWindow.setSize( W, H );                     // Size of Window  
    rootWindow.setLocation( x, y );                 // Position on screen
    rootWindow.add( this, BorderLayout.CENTER );    //  Add to rootwindow
    
    rootWindow.setVisible( true );                  // Make visible
  }
  
  
  public void setController( DisplayController c )
  {
    cont = c;
  }
  
  /**
   * Called to update the display in the shop
   */
  @Override
  public void update( Observable aModelOfDisplay, Object arg )
  {
    // Code to update the graphical display with the current
    //  state of the system
    //  Orders awaiting processing
    //  Orders being picked in the 'warehouse. 
    //  Orders awaiting collection
    
    try
    {
      Map<String, List<Integer> > res =
      ( (DisplayModel) aModelOfDisplay ).getOrderState();

      displayTitle = "Orders in system";
      displayOrders[0] = listOfOrders( res, "Waiting" );
      displayOrders[1] = listOfOrders( res, "BeingPicked" );
      displayOrders[2] = listOfOrders( res, "ToBeCollected" );

    }
    catch ( OrderException err )
    {
      textToDisplay = "\n" + "** Communication Failure **";
    }
    repaint();                            // Draw graphically    
  }
  
  @Override
  public void update( Graphics g )        // Called by repaint
  {                                       // 
    drawScreen( (Graphics2D) g );         // Draw information on screen
  }

    /**
     * Redraw the screen double buffered
     * @param g Graphics context
     */
  @Override 
  public void paint( Graphics g )         // When 'Window' is first 
  {                                       //  shown or damaged 
    drawScreen( (Graphics2D) g );         // Draw information on screen
  }

  private Dimension     theAD;           // Alternate Dimension
  private BufferedImage theAI;           // Alternate Image
  private Graphics2D    theAG;           // Alternate Graphics
  
  public void drawScreen( Graphics2D g )  // Re draw contents 
  {                                         //  allow resize
    Dimension d    = getSize();             // Size of image

    if (  ( theAG == null )           ||
          ( d.width  != theAD.width ) ||
          ( d.height != theAD.height )   )
    {                                       // New size
      theAD = d;
      theAI = (BufferedImage) createImage( d.width, d.height );
      theAG = theAI.createGraphics();
    }
    drawActualScreen( theAG );            // draw
    g.drawImage( theAI, 0, 0, this );     // Now on screen
  }
  
  /**
   * Redraw the screen
   * @param g Graphics context
   */
 
  public void drawActualScreen( Graphics2D g )  // Re draw contents
  {
    g.setPaint( Color.getHSBColor(0.49f,0.79f,0.74f) );            // Paint Colour
    W = getWidth(); H = getHeight();      // Current size
    
    g.setFont( font );
    g.fill( new Rectangle2D.Double( 0, 0, W, H ) );

    // Dimensions and positions for the rectangles
    int squareWidth = W / 3; // Size of each square
    int squareHeight = H / 2;
    int yPos = H / 2; // Y position (centered)

    // Set font for text
    Font categoryFont = new Font("Arial", Font.BOLD, 16);
    Font titleFont = new Font("Arial", Font.BOLD, 40);
    Font orderFont = new Font("Arial", Font.BOLD, 20);

    g.setFont(titleFont);

    String text = displayTitle;
    FontMetrics fm = g.getFontMetrics();
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();

    int textX = W / 2 - textWidth/2;
    int textY = H/4 + textHeight/3;

    g.setPaint(Color.WHITE); // Text color
    g.drawString(text, textX, textY); // Draw text

    // Draw three squares side by side with text
    for (int i = 0; i < 3; i++) {
      // Draw square
      g.setPaint(Color.white); // Square fill color
      g.fillRect(i * squareWidth, yPos , squareWidth, squareHeight); // Fill square
      g.setPaint(Color.black); // Square border color
      g.drawRect(i * squareWidth, yPos, squareWidth, squareHeight); // Draw square

      // Draw top-centered category text in each square
      g.setFont(categoryFont);
      text = displayCategories[i];
      fm = g.getFontMetrics();
      textWidth = fm.stringWidth(text);
      textHeight = fm.getHeight();

      // Calculate position for category text
      textX = i * squareWidth + (squareWidth - textWidth) / 2;
      textY = yPos + textHeight;

      g.setPaint(Color.black); // Text color
      g.drawString(text, textX, textY); // Draw text

      // Draw centered order numbers in each square
      g.setFont(orderFont);
      text = displayOrders[i];
      textWidth = fm.stringWidth(text);

      // Calculate position for order numbers
      textX =  i * squareWidth + (squareWidth - textWidth) / 2;
      textY = yPos + squareHeight/2 + textHeight/2;

      g.setPaint(Color.black); // Text color
      g.drawString(text, textX, textY); // Draw text
    }
    
  }

  /**
   * Return a string of order numbers
   * @param map Contains the current state of the system
   * @param key The key of the list requested
   * @return As a string a list of order numbers.
   */
  private String listOfOrders( Map<String, List<Integer> > map, String key )
  {
    String res = "";
    if ( map.containsKey( key ))
    {
      List<Integer> orders = map.get(key);
      for ( Integer i : orders )
      {
        res += " " + i;
      }
    } else {
      res = "-No key-";
    }
    return res;
  }
}
