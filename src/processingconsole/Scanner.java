// proceessingconsole library
package processingconsole;

// Standard Java imports
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import java.util.ArrayDeque;

public class Scanner
{
  // Member fields
  private ArrayDeque<Character> bytes;
  private ArrayDeque<Character> stream;

  // Construtor
  Scanner()
  {
    bytes  = new ArrayDeque<Character>();
    stream = new ArrayDeque<Character>();
  }

  // Remove the last character from the bytes buffer.
  protected void dequeue()
  {
    synchronized( bytes )
    {
      if( !bytes.isEmpty() )
      {
	bytes.removeLast();
      }
    }
  }
    
  // Add a new character to the end of the bytes buffer.
  protected void enqueue( char b )
  {
    synchronized( bytes )
    {
      if ( b != '\n' && b != '\r' )
      {
        bytes.add( b );
      } 
      else
      {
        synchronized( stream )
        {
          stream.addAll( bytes );
          stream.add( '\n' );
          bytes.clear();
        }
      }
    }
  }

  public boolean hasNext()
  {
    synchronized( stream )
    {
      return stream.size() > 0;
    }
  }

  public String next()
  {
    StringBuffer nextBuffer = new StringBuffer();
    synchronized( stream )
    {
      while ( stream.size() > 0 )
      {
        char c = stream.poll();
        if ( Character.isWhitespace( c ) )
        {
          break;
        } 
        else
        {
          nextBuffer.append( c );
        }
      }

      // Now skip over any remaining leading white space
      while ( stream.size() > 0 )
      {
        char c = stream.peek();
        if ( !Character.isWhitespace( c ) )
        {
          break;
        }

        stream.poll();
      }
    }

    // That's it.
    String nextString = nextBuffer.toString();
    return nextString;
  }

  public boolean hasNextInt()
  {
    String firstWord = null;
    synchronized( stream )
    {
      if ( stream.size() == 0 )
      {
        return false;
      }

      // Non-destructively get the characters currently in the stream
      Character[] currentChars = new Character[ stream.size() ];
      StringBuffer wordBuffer = new StringBuffer();
      stream.toArray( currentChars );
      for ( int i = 0; i < currentChars.length; i++ )
      {
        if ( Character.isWhitespace( currentChars[i] ) )
        {
          break;
        } else
        {
          wordBuffer.append( currentChars[i] );
        }
      }
      firstWord = wordBuffer.toString();
    }

    // Now check if this can be parsed as an int
    if ( firstWord == null )
    {
      return false;
    }
    try
    {
      Integer.parseInt( firstWord );
    }
    catch( NumberFormatException nfe )
    {
      return false;
    }
    return true;
  }

  public int nextInt()
  {
    String firstWord = null;
    synchronized( stream )
    {
      if ( stream.size() == 0 )
      {
        throw new NoSuchElementException();
      }

      // Destructively get the characters currently in the stream
      StringBuffer wordBuffer = new StringBuffer();
      while ( stream.size() > 0 )
      {
        char c = stream.poll();

        if ( Character.isWhitespace( c ) )
        {
          break;
        } else
        {
          wordBuffer.append( c );
        }
      }
      firstWord = wordBuffer.toString();
    }

    // Now check if this can be parsed as an int
    if ( firstWord == null )
    {
      throw new NoSuchElementException();
    }

    int newValue;
    try
    {
      newValue = Integer.parseInt( firstWord );
    }
    catch( NumberFormatException nfe )
    {
      throw new InputMismatchException();
    }
    return newValue;
  }
  
    public boolean hasNextFloat()
  {
    String firstWord = null;
    synchronized( stream )
    {
      if ( stream.size() == 0 )
      {
        return false;
      }

      // Non-destructively get the characters currently in the stream
      Character[] currentChars = new Character[ stream.size() ];
      StringBuffer wordBuffer = new StringBuffer();
      stream.toArray( currentChars );
      for ( int i = 0; i < currentChars.length; i++ )
      {
        if ( Character.isWhitespace( currentChars[i] ) )
        {
          break;
        } else
        {
          wordBuffer.append( currentChars[i] );
        }
      }
      firstWord = wordBuffer.toString();
    }

    // Now check if this can be parsed as an int
    if ( firstWord == null )
    {
      return false;
    }
    try
    {
      Float.parseFloat( firstWord );
    }
    catch( NumberFormatException nfe )
    {
      return false;
    }
    return true;
  }

  public float nextFloat()
  {
    String firstWord = null;
    synchronized( stream )
    {
      if ( stream.size() == 0 )
      {
        throw new NoSuchElementException();
      }

      // Destructively get the characters currently in the stream
      StringBuffer wordBuffer = new StringBuffer();
      while ( stream.size() > 0 )
      {
        char c = stream.poll();

        if ( Character.isWhitespace( c ) )
        {
          break;
        } else
        {
          wordBuffer.append( c );
        }
      }
      firstWord = wordBuffer.toString();
    }

    // Now check if this can be parsed as an int
    if ( firstWord == null )
    {
      throw new NoSuchElementException();
    }

    float newValue;
    try
    {
      newValue = Float.parseFloat( firstWord );
    }
    catch( NumberFormatException nfe )
    {
      throw new InputMismatchException();
    }
    return newValue;
  }
}
