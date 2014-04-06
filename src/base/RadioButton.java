package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class RadioButton
{
	/** ラジオボタンの大きさ */
	private static final Dimension RADIO_BUTTON_SIZE = new Dimension( 16, 16 );
	/** ラジオボタンのオン時のデフォルト表現図形の相対位置 */
	private static final Point RADIO_BUTTON_ON_POS = new Point( 2, 2 );
	/** ラジオボタンの音字のデフォルト表現図形のサイズ */
	private static final Dimension RADIO_BUTTON_ON_SIZE = new Dimension( RADIO_BUTTON_SIZE.width - RADIO_BUTTON_ON_POS.x * 2, RADIO_BUTTON_SIZE.height - RADIO_BUTTON_ON_POS.y * 2 );
	/** ボタン確保領域 */
	private MouseHitTestTask buttons;
	/** ラジオボタンの画像位置 */
	private List< Point > imagePosList;
	/** オンになっているボタンのインデックス */
	private int onIndex;

	public RadioButton()
	{
		buttons = new MouseHitTestTask();
		imagePosList = new ArrayList< Point >();
		onIndex = 0;
	}

	public void update()
	{
		buttons.hitTest();
		buttons.update();
		//ひとつだけ常にオン状態にしておく。
		int currentOnIndex = -1;
		for( int i = 0; i < buttons.size(); ++i ){
			Button b = (Button)buttons.get( i );
			if( b.isOn() && i != onIndex ){
				currentOnIndex = i;
				break;
			}
		}
		if( currentOnIndex == -1 ){
			currentOnIndex = onIndex;
		}
		on( currentOnIndex );
	}

	public void draw( Graphics g )
	{
		//ボタン自体の描画は何もないがとりあえず呼んどく
		buttons.draw( g );

		//ボタン画像
		Color prevColor = g.getColor();
		g.setColor( Color.BLACK );
		for( int i = 0; i < buttons.size(); ++i ){
			Button b = (Button)buttons.get( i );
			Point p = imagePosList.get( i );
			g.drawRect( p.x, p.y, RADIO_BUTTON_SIZE.width - 1, RADIO_BUTTON_SIZE.height - 1 );
			if( b.isOn() ){
				g.fillRect( p.x + RADIO_BUTTON_ON_POS.x, p.y + RADIO_BUTTON_ON_POS.y, RADIO_BUTTON_ON_SIZE.width, RADIO_BUTTON_ON_SIZE.height );
			}
		}
		g.setColor( prevColor );
	}

	/**
	 * ボタンを追加する。
	 * @param hitArea ボタンの有効範囲(位置、サイズ)。
	 * @param buttonPos ボタンのオンオフで切り替わる画像の位置(hitAreaの位置情報からの相対位置)。
	 */
	public void addButton( Rectangle hitArea, Point buttonPos )
	{
		Button b = new Button( hitArea );
		b.setHighlight( false );
		buttons.add( b );
		//初めてのボタン追加ならオンにしておく。
		if( buttons.size() == 1 ){
			b.on();
		}
		Point p = new Point( hitArea.x + buttonPos.x, hitArea.y + buttonPos.y );
		imagePosList.add( p );
	}

	/**
	 * buttonsに含まれるボタンはひとつだけオンで、そのほかはオフの状態を保たねばならない。
	 * @param index オンにするボタンのインデックス
	 */
	public void on( int index )
	{
		for( int i = 0; i < buttons.size(); ++i ){
			Button b = (Button)buttons.get( i );
			if( i == index ){
				b.on();
			}else{
				b.off();
			}
		}
		onIndex = index;
	}

	/**
	 * 今オンになっているボタンのインデックスを返す。
	 * @return ボタンのインデックス
	 */
	public int getOn()
	{
		int index = -1;
		for( int i = 0; i < buttons.size(); ++i ){
			if( ( (Button)buttons.get( i ) ).isOn() ){
				index = i;
				break;
			}
		}
		return index;
	}
}
