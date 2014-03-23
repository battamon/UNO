package game;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * ゲームの進行をテキストで表示する
 * @author ばったもん
 */
public class GameLog
{
	/** ログ表示領域 */
	public static final Rectangle VIEW_AREA = new Rectangle( 35, 140, 210, 170 );

	/** テキスト保存 */
	public List< String > logs;

	public GameLog()
	{
		logs = new ArrayList< String >();
	}

	/**
	 * ログ表示
	 */
	public void view( Graphics g )
	{
		//TODO 改行やフォントサイズの変更などさまざまな問題点の修正が必要
		Font font = g.getFont();
		Color prevColor = g.getColor();
		g.setColor( Color.BLACK );
		int space = 1;
		int rowSize = font.getSize() + space;
		int numViewRow = VIEW_AREA.height / rowSize;
		int x = VIEW_AREA.x + space;
		int y = VIEW_AREA.y + space + font.getSize();	//フォントサイズを足すのはベースラインにあわせるため
		for( int i = logs.size() < numViewRow ? 0 : ( logs.size() - numViewRow ); i < logs.size(); ++i ){
			g.drawString( logs.get( i ), x, y );
			y += rowSize;
		}
		g.setColor( prevColor );
	}

	public void setLog( String text )
	{
		logs.add( text );
	}

	public void clear()
	{
		logs.clear();
	}
}
