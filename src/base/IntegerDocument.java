package base;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

@SuppressWarnings( "serial" )

/**
 * TextBoxクラスにおける入力制限機能を付け加えるクラス
 * @author ばったもん
 */
public class IntegerDocument extends PlainDocument
{
	int currentValue = 0;
	int digitLimit = 0;

	public IntegerDocument( int digitLimit )
	{
		super();
		this.digitLimit = digitLimit;
	}

	public int getValue()
	{
		return currentValue;
	}

	@Override
	public void insertString( int offset, String str, AttributeSet attributes ) throws BadLocationException
	{
		if( str == null ){
			return;
		}else{
			String newValue;
			int length = getLength();
			if( length == 0 ){
				newValue = str;
			}else{
				String currentContent = getText( 0, length );
				StringBuffer currentBuffer = new StringBuffer( currentContent );
				currentBuffer.insert( offset, str );
				newValue = currentBuffer.toString();
			}
			checkLength( newValue.length() );
			currentValue = checkInput( newValue, offset );
			super.insertString( offset, str, attributes );
		}
	}

	@Override
	public void remove( int offset, int length ) throws BadLocationException
	{
		int currentLength = getLength();
		String currentContent = getText( 0, currentLength );
		String before = currentContent.substring( 0, offset );
		String after = currentContent.substring( length + offset, currentLength );
		String newValue = before + after;
		currentValue = checkInput( newValue, offset );
		super.remove( offset, length );
	}

	private int checkInput( String proposedValue, int offset ) throws BadLocationException
	{
		if( proposedValue.length() > 0 ){
			try{
				int newValue = Integer.parseInt( proposedValue );
				return newValue;
			}catch( NumberFormatException e ){
				throw new BadLocationException( proposedValue, offset );
			}
		}else{
			return 0;
		}
	}

	private boolean checkLength( int length ) throws BadLocationException
	{
		if( length > 0 ){
			if( length > digitLimit ){
				throw new BadLocationException( null, 0 );
			}
		}
		return true;
	}
}
