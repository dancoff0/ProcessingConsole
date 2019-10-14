// This is the package for the ProcessingConsole library
package processingconsole;

// Import the standard Processing functions
import processing.core.*;

// Standard Java imports
import java.util.ArrayDeque;
import java.util.Deque;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import processing.event.KeyEvent;
import java.awt.Toolkit;

public class Console implements PConstants
{
  // These are the desired width and height
  private int desiredWidth  = 600;
  private int desiredHeight = 500;
  
  // These are the font and related properties
  private       PFont font1;
  private final int   outputColor;    
  private final int   inputColor;     
  private final int   backgroundColor; 
  
  // This is the current line number
  private       int     lineNumber = 1;
  private       float   lineHeight;
  private       float   lineAscender;
  private       float   cursorPosition;
  private       float   linePosition;
  private final float   promptWidth;
  private final int     leading = 3;
  private final int     margin  = 10;
  private final String  prompt  = "> ";
  
  // This is a list of the strings waiting to be displayed
  private Deque<StringDescriptor> pendingStrings;
  
  // This is the Scanner that will be available to user sketches
  private Scanner scanner = null;
  
  // Constructor
  private final PApplet userSketch;
  private final String  userMethodName;
  private Thread        runner;

  // Default constuctor
  public Console( PApplet userSketch, String methodName )
  {
    this.userSketch     = userSketch;
    this.userMethodName = methodName;

    // Set the colors
    backgroundColor = userSketch.color(  87,  88,  87 );
    outputColor     = userSketch.color(  49, 255, 129 );
    inputColor      = userSketch.color( 224, 227, 225 );
    
    // Make up our list of strings
    pendingStrings = new ArrayDeque<StringDescriptor>();
    
    // Set up the terminal window
    userSketch.println( "Setting size" );
    userSketch.getSurface().setResizable( true );
    userSketch.getSurface().setSize( desiredWidth, desiredHeight );
    
    // Create the font get its sizes
    font1 = userSketch.createFont( "Consolas", 14 );
    userSketch.textFont( font1 );

    // Compute the size of the line.
    lineAscender = userSketch.textAscent();
    lineHeight   = lineAscender +  userSketch.textDescent();
    userSketch.println( "line height is " + lineHeight );

    // While we're at it, compute the width of the prompt.
    promptWidth = userSketch.textWidth( prompt );
    userSketch.println( "Prompt width = " + promptWidth );
    
    // Set the color of the terminal
    userSketch.background( backgroundColor );
    
    // Start at the margin
    cursorPosition = margin;
    linePosition   = margin + lineHeight;
    
    userSketch.registerMethod( "draw",     this );
    userSketch.registerMethod( "keyEvent", this );
    
    // Create the scanner for the user to use.
    scanner = new Scanner();
    
    // Queue up the prompt
    synchronized( pendingStrings )
    {
      pendingStrings.add( new StringDescriptor( prompt, outputColor ) );
    }
    
    // Start up the user's method
    runner = new Thread( new Runnable()
    {
      public void run()
      {
        try
        {
          // Go find the user's method and ...
          Method userMethod = userSketch.getClass().getMethod(userMethodName, new Class[] {});
          
          // ... invoke it
          userMethod.invoke( userSketch );
        }
        catch( SecurityException e)
	{
	  userSketch.println( "caught SecurityException " + e);
	}
        catch( NoSuchMethodException e)
	{
	  userSketch.println( "caught NosSuchMethodException: "+ e);
	}
        catch( IllegalAccessException e)
	{
	  userSketch.println( "caught IllegalAccessException: " +  e);
	}
        catch( InvocationTargetException e)
	{
	  userSketch.println( "caught InvocationTargetException: " + e);
	}
      }
    });

    // Start the user's method
    runner.start();
    
  }

  // Draw to the console.
  public void draw()
  {
    synchronized( pendingStrings )
    {
      while( !pendingStrings.isEmpty() )
      {
	// Get the descriptor and from it ...
	StringDescriptor descriptor = pendingStrings.poll();
	
	// ... get the string to display and the color to use.
	String newString = descriptor.getString();
	userSketch.fill( descriptor.getColor() );
	
	userSketch.println( "Printing string " + newString + " at " + cursorPosition + "/" + linePosition );
        userSketch.text( newString, cursorPosition,  linePosition );

	// If the string ends with "\n", then advance to the start of the next line.
        if( newString.endsWith( "\n" ) )
        {
          cursorPosition = margin;
          lineNumber++;
          linePosition += lineHeight + leading;
        }
        else
        {
	  // Overwise, just move the cursor over by the width of the string just displayed.
          cursorPosition += userSketch.textWidth( newString );
        }
      }
    }
  }

  // Print a string to the console. This will appear as "output text".
  public void termprint( String str )
  {
    synchronized( pendingStrings )
    {
	pendingStrings.add( new StringDescriptor(str, outputColor ) );
    }
  }

  // Print a string to the console, following it with a "\n". This will appear as "output text".
  public void termprintln( String str )
  {
    synchronized( pendingStrings )
    {
	pendingStrings.add( new StringDescriptor( str + "\n", outputColor ) );
	pendingStrings.add( new StringDescriptor( prompt, outputColor  ) );
    }
  }


  // Print a string to the console. The text will appear as "input text".
  private void echo( String str )
  {
    synchronized( pendingStrings )
    {
	pendingStrings.add( new StringDescriptor(str, inputColor) );
    }
  }

  // Handle a key event.
  StringBuffer enteredStringBuffer = null;
  public void keyEvent( KeyEvent event )
  {

    // Check that this a key press event.
    if( event.getAction() != KeyEvent.PRESS )
    {
      return;
    }

    // Create a string buffer if needed.
    if( enteredStringBuffer == null )
    {
      enteredStringBuffer = new StringBuffer(); 
    }
    
    // Get the keystroke
    int keyCode = event.getKeyCode();
    if( keyCode == RETURN || keyCode == ENTER )
    {
      // Get the entered string --- largely for debugging purposes.
      String enteredString = enteredStringBuffer.toString();
      enteredStringBuffer.delete( 0, enteredStringBuffer.length() );
      userSketch.println( "String is " + enteredString );
      scanner.enqueue( '\n' );
      termprintln( "" );
    }
    else if( keyCode == BACKSPACE )
    {
      // Check that we are not at the beginning of the line.  We don't want to delete the prompt!
      if( cursorPosition > margin + promptWidth )
      {
	int bufferLength = enteredStringBuffer.length();
	String previousCharacter = enteredStringBuffer.substring( bufferLength - 1, bufferLength );
	userSketch.println( "The previous character was " + previousCharacter );
	enteredStringBuffer.delete( bufferLength - 1, bufferLength );

	// We can't just remove the previous character, we need to cover it over with a rectangle of
	// appropriate size, filled with the background color.  We also need to make sure the stroke
	// is turned off.
	userSketch.rectMode( CORNER );
	float previousCharacterWidth = userSketch.textWidth( previousCharacter );
	float previousCursorPosition = cursorPosition - previousCharacterWidth;
	userSketch.fill( backgroundColor );
	userSketch.noStroke();
	userSketch.rect( previousCursorPosition, linePosition - lineAscender, previousCharacterWidth, lineHeight );

	// Now move the cursor position back to what it was after the previous character.
	cursorPosition = previousCursorPosition;

	// Now tell the scanner to remove the previous character from its buffer.
	scanner.dequeue();
      }
      else
      {
	// if we are already at the beginning of the line, just beep.
	Toolkit.getDefaultToolkit().beep();
      }
    }
    else
    {
      // This is just any other keyboard character.  If it is 'coded', that is not a modifier or arrow key,
      // add it the display and scanner buffer.
      char key = event.getKey();
      if( key != CODED )
      {
        enteredStringBuffer.append( key );
        scanner.enqueue( key );
        echo( Character.toString(key)  );
      }
    }
      
  }

  // This descriptor class allows us to enter a String and specify the color to be used
  // to display it.
  private class StringDescriptor
  {
    private final String str;
    private final int    color;
    
    public StringDescriptor( String str, int color )
    {
      this.str   = str;
      this.color = color;
    }
    
    public String getString()
    {
      return str;
    }
    
    public int getColor()
    {
      return color;
    }
  }
  
  public Scanner getScanner()
  {
    return scanner;
  }
  
}
