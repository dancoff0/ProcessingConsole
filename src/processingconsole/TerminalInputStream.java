    /*
  
  public InputStream getInputStream()
  { 

    return terminalInputStream;
  }
  
  public class TerminalInputStream extends InputStream
  {
    private ArrayDeque<Byte> bytes = null;
    
    TerminalInputStream()
    {
      bytes = new ArrayDeque<Byte>();
    }
    
    private void enqueue( byte b )
    {
      bytes.add( b );
    }
    
    public int available()
    {
      println( "Available called" );
      return bytes.size();
    }
    
    public int read()
    {
      if( !bytes.isEmpty() )
      {
        return bytes.poll();
      }
      else
      {
        return -1;
      }
    }
  }

    */

