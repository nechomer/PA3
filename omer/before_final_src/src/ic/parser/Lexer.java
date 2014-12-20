/* The following code was generated by JFlex 1.6.0 */

package ic.parser;
import ic.parser.LexicalError;
import ic.parser.sym;
/*********** Definitions ***********/

public class Lexer implements java_cup.runtime.Scanner {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;
  public static final int STRING = 2;
  public static final int TRADITIONAL_COMMENT = 4;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0,  0,  1,  1,  2, 2
  };

  /** 
   * Translates characters to character classes
   */
  private static final String ZZ_CMAP_PACKED = 
    "\11\0\1\3\1\2\2\0\1\1\22\0\1\55\1\43\1\73\2\54"+
    "\1\45\1\52\1\54\1\37\1\40\1\44\1\46\1\72\1\42\1\41"+
    "\1\4\1\10\1\56\1\60\1\57\1\62\1\61\1\66\1\64\1\63"+
    "\1\65\1\54\1\71\1\47\1\50\1\51\2\54\32\6\1\35\1\74"+
    "\1\36\1\54\1\5\1\54\1\13\1\25\1\11\1\21\1\15\1\31"+
    "\1\27\1\33\1\22\1\7\1\34\1\12\1\7\1\20\1\24\2\7"+
    "\1\26\1\14\1\17\1\30\1\23\1\32\1\16\2\7\1\67\1\53"+
    "\1\70\1\54\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uffff\0\uff91\0";

  /** 
   * Translates characters to character classes
   */
  private static final char [] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\3\0\1\1\2\2\1\3\1\1\1\4\1\5\1\6"+
    "\14\5\1\7\1\10\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\2\1\3\6"+
    "\1\24\1\25\1\26\1\27\1\30\1\31\1\32\1\1"+
    "\1\2\1\0\1\33\13\5\1\34\6\5\1\35\1\36"+
    "\1\37\1\40\1\41\1\42\2\6\1\43\1\44\1\45"+
    "\1\46\1\47\11\5\1\50\1\5\1\51\6\5\2\6"+
    "\5\5\1\52\1\5\1\53\1\54\1\55\1\56\5\5"+
    "\2\6\1\57\6\5\1\60\1\5\1\61\1\62\2\6"+
    "\1\5\1\63\1\64\1\65\2\5\1\66\2\6\1\5"+
    "\1\67\1\70\2\6\1\71\4\6\1\72";

  private static int [] zzUnpackAction() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\75\0\172\0\267\0\364\0\267\0\u0131\0\u016e"+
    "\0\u01ab\0\u01e8\0\u0225\0\u0262\0\u029f\0\u02dc\0\u0319\0\u0356"+
    "\0\u0393\0\u03d0\0\u040d\0\u044a\0\u0487\0\u04c4\0\u0501\0\267"+
    "\0\267\0\267\0\267\0\267\0\267\0\u053e\0\267\0\267"+
    "\0\267\0\u057b\0\u05b8\0\u05f5\0\u0632\0\u066f\0\u06ac\0\u06e9"+
    "\0\u0726\0\267\0\267\0\267\0\267\0\267\0\u0763\0\267"+
    "\0\u07a0\0\u07dd\0\u081a\0\267\0\u0857\0\u0894\0\u08d1\0\u090e"+
    "\0\u094b\0\u0988\0\u09c5\0\u0a02\0\u0a3f\0\u0a7c\0\u0ab9\0\u01e8"+
    "\0\u0af6\0\u0b33\0\u0b70\0\u0bad\0\u0bea\0\u0c27\0\267\0\267"+
    "\0\267\0\267\0\267\0\267\0\u0c64\0\u0ca1\0\267\0\267"+
    "\0\267\0\267\0\267\0\u0cde\0\u0d1b\0\u0d58\0\u0d95\0\u0dd2"+
    "\0\u0e0f\0\u0e4c\0\u0e89\0\u0ec6\0\u01e8\0\u0f03\0\u01e8\0\u0f40"+
    "\0\u0f7d\0\u0fba\0\u0ff7\0\u1034\0\u1071\0\u10ae\0\u10eb\0\u1128"+
    "\0\u1165\0\u11a2\0\u11df\0\u121c\0\u01e8\0\u1259\0\u01e8\0\u01e8"+
    "\0\u01e8\0\u01e8\0\u1296\0\u12d3\0\u1310\0\u134d\0\u138a\0\u13c7"+
    "\0\u1404\0\u01e8\0\u1441\0\u147e\0\u14bb\0\u14f8\0\u1535\0\u1572"+
    "\0\u01e8\0\u15af\0\u01e8\0\u01e8\0\u15ec\0\u1629\0\u1666\0\u01e8"+
    "\0\u01e8\0\u01e8\0\u16a3\0\u16e0\0\u01e8\0\u171d\0\u175a\0\u1797"+
    "\0\u01e8\0\u01e8\0\u17d4\0\u1811\0\u01e8\0\u184e\0\u188b\0\u18c8"+
    "\0\u1905\0\u18c8";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\4\1\5\2\6\1\7\1\10\1\11\1\12\1\13"+
    "\1\14\1\15\1\12\1\16\1\17\1\12\1\20\1\21"+
    "\1\12\1\22\1\23\1\12\1\24\1\25\2\12\1\26"+
    "\1\27\2\12\1\30\1\31\1\32\1\33\1\34\1\35"+
    "\1\36\1\37\1\40\1\41\1\42\1\43\1\44\1\45"+
    "\1\46\1\4\1\6\1\47\1\50\1\51\6\50\1\52"+
    "\1\53\1\54\1\55\1\56\5\4\67\57\1\60\1\61"+
    "\44\6\1\62\30\6\77\0\1\6\76\0\1\63\37\0"+
    "\1\64\35\0\30\10\21\0\11\10\13\0\30\11\21\0"+
    "\11\11\13\0\30\12\21\0\11\12\16\0\1\13\45\0"+
    "\11\13\13\0\5\12\1\65\11\12\1\66\10\12\21\0"+
    "\11\12\13\0\10\12\1\67\17\12\21\0\11\12\13\0"+
    "\12\12\1\70\15\12\21\0\11\12\13\0\5\12\1\71"+
    "\3\12\1\72\16\12\21\0\11\12\13\0\21\12\1\73"+
    "\4\12\1\74\1\12\21\0\11\12\13\0\10\12\1\75"+
    "\12\12\1\76\4\12\21\0\11\12\13\0\13\12\1\77"+
    "\10\12\1\100\3\12\21\0\11\12\13\0\17\12\1\101"+
    "\10\12\21\0\11\12\13\0\17\12\1\102\1\12\1\103"+
    "\6\12\21\0\11\12\13\0\10\12\1\104\17\12\21\0"+
    "\11\12\13\0\6\12\1\105\21\12\21\0\11\12\13\0"+
    "\26\12\1\106\1\12\21\0\11\12\56\0\1\107\74\0"+
    "\1\110\74\0\1\111\74\0\1\112\76\0\1\113\75\0"+
    "\1\114\31\0\1\50\45\0\11\50\16\0\1\115\45\0"+
    "\11\115\16\0\1\50\45\0\1\116\10\115\12\0\67\57"+
    "\21\0\1\117\1\120\52\0\1\121\1\122\4\0\1\123"+
    "\70\0\1\63\1\5\1\6\72\63\5\0\6\12\1\124"+
    "\21\12\21\0\11\12\13\0\13\12\1\125\14\12\21\0"+
    "\11\12\13\0\13\12\1\126\14\12\21\0\11\12\13\0"+
    "\6\12\1\127\12\12\1\130\6\12\21\0\11\12\13\0"+
    "\7\12\1\131\20\12\21\0\11\12\13\0\12\12\1\132"+
    "\15\12\21\0\11\12\13\0\23\12\1\133\4\12\21\0"+
    "\11\12\13\0\15\12\1\134\12\12\21\0\11\12\13\0"+
    "\25\12\1\135\2\12\21\0\11\12\13\0\5\12\1\136"+
    "\22\12\21\0\11\12\13\0\12\12\1\137\15\12\21\0"+
    "\11\12\13\0\15\12\1\140\12\12\21\0\11\12\13\0"+
    "\17\12\1\141\10\12\21\0\11\12\13\0\10\12\1\142"+
    "\17\12\21\0\11\12\13\0\12\12\1\143\15\12\21\0"+
    "\11\12\13\0\5\12\1\144\22\12\21\0\11\12\13\0"+
    "\15\12\1\145\12\12\21\0\11\12\16\0\1\146\45\0"+
    "\11\146\16\0\1\115\45\0\3\115\1\146\1\147\4\146"+
    "\13\0\7\12\1\150\20\12\21\0\11\12\13\0\12\12"+
    "\1\151\15\12\21\0\11\12\13\0\22\12\1\152\5\12"+
    "\21\0\11\12\13\0\12\12\1\153\15\12\21\0\11\12"+
    "\13\0\15\12\1\154\12\12\21\0\11\12\13\0\10\12"+
    "\1\155\17\12\21\0\11\12\13\0\10\12\1\156\17\12"+
    "\21\0\11\12\13\0\10\12\1\157\17\12\21\0\11\12"+
    "\13\0\7\12\1\160\20\12\21\0\11\12\13\0\5\12"+
    "\1\161\22\12\21\0\11\12\13\0\14\12\1\162\13\12"+
    "\21\0\11\12\13\0\5\12\1\163\22\12\21\0\11\12"+
    "\13\0\6\12\1\164\21\12\21\0\11\12\13\0\23\12"+
    "\1\165\4\12\21\0\11\12\13\0\7\12\1\166\20\12"+
    "\21\0\11\12\13\0\5\12\1\167\22\12\21\0\11\12"+
    "\16\0\1\170\45\0\11\170\16\0\1\146\45\0\5\146"+
    "\1\170\1\171\1\170\1\146\13\0\7\12\1\172\20\12"+
    "\21\0\11\12\13\0\15\12\1\173\12\12\21\0\11\12"+
    "\13\0\12\12\1\174\15\12\21\0\11\12\13\0\15\12"+
    "\1\175\12\12\21\0\11\12\13\0\13\12\1\176\14\12"+
    "\21\0\11\12\13\0\13\12\1\177\14\12\21\0\11\12"+
    "\13\0\10\12\1\200\17\12\21\0\11\12\13\0\27\12"+
    "\1\201\21\0\11\12\13\0\21\12\1\202\6\12\21\0"+
    "\11\12\13\0\10\12\1\203\17\12\21\0\11\12\13\0"+
    "\10\12\1\204\17\12\21\0\11\12\16\0\1\205\45\0"+
    "\11\205\16\0\1\170\45\0\3\170\1\205\1\206\4\205"+
    "\13\0\13\12\1\207\14\12\21\0\11\12\13\0\26\12"+
    "\1\210\1\12\21\0\11\12\13\0\4\12\1\211\23\12"+
    "\21\0\11\12\13\0\22\12\1\212\5\12\21\0\11\12"+
    "\13\0\14\12\1\213\13\12\21\0\11\12\13\0\6\12"+
    "\1\214\21\12\21\0\11\12\13\0\13\12\1\215\14\12"+
    "\21\0\11\12\16\0\1\216\45\0\11\216\16\0\1\205"+
    "\45\0\5\205\1\217\1\205\1\216\1\205\13\0\23\12"+
    "\1\220\4\12\21\0\11\12\13\0\7\12\1\221\20\12"+
    "\21\0\11\12\13\0\13\12\1\222\14\12\21\0\11\12"+
    "\16\0\1\223\45\0\11\223\16\0\1\216\45\0\1\216"+
    "\1\224\1\216\6\223\13\0\10\12\1\225\17\12\21\0"+
    "\11\12\16\0\1\226\45\0\11\226\16\0\1\223\45\0"+
    "\5\223\3\226\1\227\16\0\1\230\45\0\11\230\16\0"+
    "\1\226\45\0\3\226\1\230\1\231\4\230\16\0\1\232"+
    "\45\0\11\232\16\0\1\230\45\0\5\230\1\232\1\230"+
    "\1\232\1\230\6\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[6466];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String ZZ_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\3\0\1\11\1\1\1\11\21\1\6\11\1\1\3\11"+
    "\10\1\5\11\1\1\1\11\2\1\1\0\1\11\22\1"+
    "\6\11\2\1\5\11\107\1";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[154];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn;

  /** 
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int zzFinalHighSurrogate = 0;

  /* user code: */
	StringBuffer string = new StringBuffer();

	// save the last line and column of state
	private int lLine, lCol;

	private int getCurrentLine() {
		return yyline+1;
	}

	private int getCurrentColumn() {
		return yycolumn+1;
	}

	private void lastPos() {
		lLine = getCurrentLine();
		lCol = getCurrentColumn();  
	}

	// if flag == true => then use lastPos else currentPos
	private Token token(int id, String tag, Object value, boolean flag) {
		if(flag)
			return new Token(id,lLine,lCol,tag,value);
		else
			return new Token(id,getCurrentLine(),getCurrentColumn(),tag,value);
	}

	private Token token(int id, String tag, boolean flag) {
		if(flag)
			return new Token(id,lLine,lCol,tag);
		else
			return new Token(id,getCurrentLine(),getCurrentColumn(),tag);
	}

	private void Error(String token, boolean flag) throws LexicalError {
		if(flag)
			throw new LexicalError(lLine,lCol,token);
		else
			throw new LexicalError(getCurrentLine(),getCurrentColumn(),token);
	}



  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    char [] map = new char[0x110000];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < 178) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }


  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (zzStartRead > 0) {
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
      System.arraycopy(zzBuffer, zzStartRead,
                       zzBuffer, 0,
                       zzEndRead-zzStartRead);

      /* translate stored positions */
      zzEndRead-= zzStartRead;
      zzCurrentPos-= zzStartRead;
      zzMarkedPos-= zzStartRead;
      zzStartRead = 0;
    }

    /* is the buffer big enough? */
    if (zzCurrentPos >= zzBuffer.length - zzFinalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[zzBuffer.length*2];
      System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
      zzBuffer = newBuffer;
      zzEndRead += zzFinalHighSurrogate;
      zzFinalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = zzBuffer.length - zzEndRead;           
    int totalRead = 0;
    while (totalRead < requested) {
      int numRead = zzReader.read(zzBuffer, zzEndRead + totalRead, requested - totalRead);
      if (numRead == -1) {
        break;
      }
      totalRead += numRead;
    }

    if (totalRead > 0) {
      zzEndRead += totalRead;
      if (totalRead == requested) { /* possibly more input available */
        if (Character.isHighSurrogate(zzBuffer[zzEndRead - 1])) {
          --zzEndRead;
          zzFinalHighSurrogate = 1;
        }
      }
      return false;
    }

    // totalRead = 0: End of stream
    return true;
  }

    
  /**
   * Closes the input stream.
   */
  public final void yyclose() throws java.io.IOException {
    zzAtEOF = true;            /* indicate end of file */
    zzEndRead = zzStartRead;  /* invalidate buffer    */

    if (zzReader != null)
      zzReader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream 
   */
  public final void yyreset(java.io.Reader reader) {
    zzReader = reader;
    zzAtBOL  = true;
    zzAtEOF  = false;
    zzEOFDone = false;
    zzEndRead = zzStartRead = 0;
    zzCurrentPos = zzMarkedPos = 0;
    zzFinalHighSurrogate = 0;
    yyline = yychar = yycolumn = 0;
    zzLexicalState = YYINITIAL;
    if (zzBuffer.length > ZZ_BUFFERSIZE)
      zzBuffer = new char[ZZ_BUFFERSIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String yytext() {
    return new String( zzBuffer, zzStartRead, zzMarkedPos-zzStartRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer[zzStartRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() throws java.io.IOException {
    if (!zzEOFDone) {
      zzEOFDone = true;
      yyclose();
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public Token next_token() throws java.io.IOException, LexicalError {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    char [] zzBufferL = zzBuffer;
    char [] zzCMapL = ZZ_CMAP;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      boolean zzR = false;
      int zzCh;
      int zzCharCount;
      for (zzCurrentPosL = zzStartRead  ;
           zzCurrentPosL < zzMarkedPosL ;
           zzCurrentPosL += zzCharCount ) {
        zzCh = Character.codePointAt(zzBufferL, zzCurrentPosL, zzMarkedPosL);
        zzCharCount = Character.charCount(zzCh);
        switch (zzCh) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yycolumn = 0;
          zzR = false;
          break;
        case '\r':
          yyline++;
          yycolumn = 0;
          zzR = true;
          break;
        case '\n':
          if (zzR)
            zzR = false;
          else {
            yyline++;
            yycolumn = 0;
          }
          break;
        default:
          zzR = false;
          yycolumn += zzCharCount;
        }
      }

      if (zzR) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (zzMarkedPosL < zzEndReadL)
          zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        else if (zzAtEOF)
          zzPeek = false;
        else {
          boolean eof = zzRefill();
          zzEndReadL = zzEndRead;
          zzMarkedPosL = zzMarkedPos;
          zzBufferL = zzBuffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
        }
        if (zzPeek) yyline--;
      }
      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;
  
      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {
    
          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL, zzEndReadL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMapL[zzInput] ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
        case 1: 
          { Error(yytext(), false);
          }
        case 59: break;
        case 2: 
          { /* ignore */
          }
        case 60: break;
        case 3: 
          { return token(sym.DIV, yytext(), false);
          }
        case 61: break;
        case 4: 
          { return token(sym.CLASS_ID, "CLASS_ID", yytext(), false);
          }
        case 62: break;
        case 5: 
          { return token(sym.IDENTIFIER, "Identifier", yytext(), false);
          }
        case 63: break;
        case 6: 
          { return token(sym.INTEGER_LITERAL, "INTEGER", new Integer(yytext()), false);
          }
        case 64: break;
        case 7: 
          { return token(sym.LBRACKET, yytext(), false);
          }
        case 65: break;
        case 8: 
          { return token(sym.RBRACKET, yytext(), false);
          }
        case 66: break;
        case 9: 
          { return token(sym.LPAREN, yytext(), false);
          }
        case 67: break;
        case 10: 
          { return token(sym.RPAREN, yytext(), false);
          }
        case 68: break;
        case 11: 
          { return token(sym.DOT, yytext(), false);
          }
        case 69: break;
        case 12: 
          { return token(sym.MINUS, yytext(), false);
          }
        case 70: break;
        case 13: 
          { return token(sym.NOT, yytext(), false);
          }
        case 71: break;
        case 14: 
          { return token(sym.MULT, yytext(), false);
          }
        case 72: break;
        case 15: 
          { return token(sym.MODULU, yytext(), false);
          }
        case 73: break;
        case 16: 
          { return token(sym.PLUS, yytext(), false);
          }
        case 74: break;
        case 17: 
          { return token(sym.LT, yytext(), false);
          }
        case 75: break;
        case 18: 
          { return token(sym.EQ, yytext(), false);
          }
        case 76: break;
        case 19: 
          { return token(sym.GT, yytext(), false);
          }
        case 77: break;
        case 20: 
          { return token(sym.LBRACE, yytext(), false);
          }
        case 78: break;
        case 21: 
          { return token(sym.RBRACE, yytext(), false);
          }
        case 79: break;
        case 22: 
          { return token(sym.SEMI, yytext(), false);
          }
        case 80: break;
        case 23: 
          { return token(sym.COMA, yytext(), false);
          }
        case 81: break;
        case 24: 
          { lastPos(); string.setLength(0); yybegin(STRING);
          }
        case 82: break;
        case 25: 
          { string.append(yytext());
          }
        case 83: break;
        case 26: 
          { yybegin(YYINITIAL); return token(sym.STRING_LITERAL, "STRING", string.toString(), true);
          }
        case 84: break;
        case 27: 
          { lastPos(); yybegin(TRADITIONAL_COMMENT);
          }
        case 85: break;
        case 28: 
          { return token(sym.IF, yytext(), false);
          }
        case 86: break;
        case 29: 
          { return token(sym.NEQ, yytext(), false);
          }
        case 87: break;
        case 30: 
          { return token(sym.LTEQ, yytext(), false);
          }
        case 88: break;
        case 31: 
          { return token(sym.EQEQ, yytext(), false);
          }
        case 89: break;
        case 32: 
          { return token(sym.GTEQ, yytext(), false);
          }
        case 90: break;
        case 33: 
          { return token(sym.AND, yytext(), false);
          }
        case 91: break;
        case 34: 
          { return token(sym.OR, yytext(), false);
          }
        case 92: break;
        case 35: 
          { string.append("\t");
          }
        case 93: break;
        case 36: 
          { string.append("\n");
          }
        case 94: break;
        case 37: 
          { string.append("\"");
          }
        case 95: break;
        case 38: 
          { string.append("\\");
          }
        case 96: break;
        case 39: 
          { yybegin(YYINITIAL);
          }
        case 97: break;
        case 40: 
          { return token(sym.NEW, yytext(), false);
          }
        case 98: break;
        case 41: 
          { return token(sym.INTEGER, yytext(), false);
          }
        case 99: break;
        case 42: 
          { return token(sym.ELSE, yytext(), false);
          }
        case 100: break;
        case 43: 
          { return token(sym.TRUE_LITERAL, yytext(), Boolean.valueOf(yytext()), false);
          }
        case 101: break;
        case 44: 
          { return token(sym.THIS, yytext(), false);
          }
        case 102: break;
        case 45: 
          { return token(sym.NULL_LITERAL, yytext(), false);
          }
        case 103: break;
        case 46: 
          { return token(sym.VOID, yytext(), false);
          }
        case 104: break;
        case 47: 
          { return token(sym.CLASS, yytext(), false);
          }
        case 105: break;
        case 48: 
          { return token(sym.BREAK, yytext(), false);
          }
        case 106: break;
        case 49: 
          { return token(sym.FALSE_LITERAL, yytext(), Boolean.valueOf(yytext()), false);
          }
        case 107: break;
        case 50: 
          { return token(sym.WHILE, yytext(), false);
          }
        case 108: break;
        case 51: 
          { return token(sym.LENGTH, yytext(), false);
          }
        case 109: break;
        case 52: 
          { return token(sym.STATIC, yytext(), false);
          }
        case 110: break;
        case 53: 
          { return token(sym.STRING, yytext(), false);
          }
        case 111: break;
        case 54: 
          { return token(sym.RETURN, yytext(), false);
          }
        case 112: break;
        case 55: 
          { return token(sym.EXTENDS, yytext(), false);
          }
        case 113: break;
        case 56: 
          { return token(sym.BOOLEAN, yytext(), false);
          }
        case 114: break;
        case 57: 
          { return token(sym.CONTINUE, yytext(), false);
          }
        case 115: break;
        case 58: 
          { Error("Integer out Of Range: " + yytext(), false);
          }
        case 116: break;
        default: 
          if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
            zzAtEOF = true;
            zzDoEOF();
            switch (zzLexicalState) {
            case STRING: {
              Error(yytext(), true);
            }
            case 155: break;
            case TRADITIONAL_COMMENT: {
              Error(yytext(), true);
            }
            case 156: break;
            default:
              {
                return token(sym.EOF, yytext(), false);
              }
            }
          } 
          else {
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
