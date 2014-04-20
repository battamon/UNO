package base;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

@SuppressWarnings( "serial" )

/**
 * テキストフィールドをラップしたクラス。<br>
 * オブジェクトが不必要になった際にはdestroyメソッドを必ず呼び出すこと。
 * @author ばったもん
 */
public class TextBox extends JTextField implements IHitTestObject, KeyListener
{
	/** 表示領域 */
	private Rectangle area;
	/** 直接ヒットした場合のフラグ */
	private boolean hitSurfaceFlag;
	/** カレット点滅フレームカウンタ */
	private int frameCount;
	/** キャレット移動入力用カウンタ */
	private int arrowLeft;
	private int arrowRight;
	/** キャレット位置 */
	private int caretPos;
	/** アクティブ状態 */
	private boolean active;

	public TextBox( Rectangle area )
	{
		super();
		setBounds( area );
		this.area = area;
		setDocument( new IntegerDocument( 4 ) );	//入力の桁数制限
		UNO.panel.add( this );
		frameCount = 0;
		addKeyListener( this );
		setEditable( false );
	}

	@Override
	public void update()
	{
		if( Input.getClicked( Input.MOUSE_BUTTON_LEFT ) ){
			if( hitSurfaceFlag ){
			}else{
				//外側をクリックされたらフォーカスを外す
				transferFocus();
			}
		}
		if( active && isFocusOwner() ){
			//キャレット点滅表現のカウンタ更新
			++frameCount;
			//キャレット位置補正
			if( !Input.getClicked( Input.MOUSE_BUTTON_LEFT ) ){
				if( arrowLeft == 1 && caretPos > 0 ){
					setCaretPosition( --caretPos );
				}else if( arrowRight == 1 && caretPos < getText().length() ){
					setCaretPosition( ++caretPos );
				}
			}
			caretPos = getCaretPosition();
		}else{
			frameCount = 0;
		}
	}

	@Override
	public void draw( Graphics g )
	{
		//前準備
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		final int offset = 2;
		//入力
		g.setColor( Color.BLACK );
		g.setFont( new Font( prevFont.getName(), Font.PLAIN, 14 ) );
		ImageManager.drawString( g, getText(), area.x + offset, area.y, area.width, area.height, ImageManager.Align.DEFAULT, ImageManager.Align.CENTER );
		//キャレット
		if( active && isFocusOwner() && ( frameCount / 40 % 2 == 0 ) ){	//40フレーム毎に点いたり消えたりする計算式
			int width = 0;
			if( getText().length() > 0 ){
				FontMetrics fm = g.getFontMetrics();
				int widths[] = fm.getWidths();
				for( int i = 1; i < widths.length; ++i ){
					widths[ i ] += widths[ i - 1 ] + 1;
				}
				width = widths[ caretPos ];
			}
			int x = area.x + offset + width;
			int y = area.y;
			g.drawLine( x, y, x, y + area.height - 1 );
		}
		//後処理
		g.setColor( prevColor );
		g.setFont( prevFont );
	}

	@Override
	public boolean hitTest( Point pos )
	{
		if( area.x <= pos.x && pos.x <= area.x + area.width
				&& area.y <= pos.y && pos.y <= area.y + area.height ){
			return true;
		}
		return false;
	}

	@Override
	public void hitSurface()
	{
		hitSurfaceFlag = true;
	}

	@Override
	public void hitBack()
	{
	}

	@Override
	public void notHit()
	{
		hitSurfaceFlag = false;
	}

	public void destroy()
	{
		UNO.panel.remove( this );
	}

	@Override
	public void paintImmediately( int x, int y, int w, int h ){
		//内部で呼び出される描画関数はオーバーライドして殺しておく
	}

	@Override
	public void keyPressed( KeyEvent e )
	{
		switch( e.getKeyCode() ){
			case KeyEvent.VK_LEFT: ++arrowLeft; break;
			case KeyEvent.VK_RIGHT: ++arrowRight; break;
		}
	}

	@Override
	public void keyReleased( KeyEvent e )
	{
		switch( e.getKeyCode() ){
			case KeyEvent.VK_LEFT: arrowLeft = 0; break;
			case KeyEvent.VK_RIGHT: arrowRight = 0; break;
		}
	}

	@Override
	public void keyTyped( KeyEvent arg0 )
	{
	}

	@Override
	public void setEditable( boolean b )
	{
		active = b;
		super.setEditable( b );
	}
}