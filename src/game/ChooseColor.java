package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import base.Button;
import base.ImageManager;
import base.MainPanel;
import base.MouseHitTestTask;

/**
 * Wildカード、WildDrawFourカードで色選択する際に使う。
 * @author ばったもん
 */
public class ChooseColor
{
	//実際のカードの色
	public static final Color RED = new Color( 255, 0, 0 );
	public static final Color BLUE = new Color( 0, 71, 235 );
	public static final Color GREEN = new Color( 77, 139, 0 );
	public static final Color YELLOW = new Color( 255, 169, 0 );
	//選択されていない場合の色
	public static final Card.Color NOT_SELECTED = null;
	//位置・領域
	public static final Dimension VIEW_SIZE = new Dimension( 360, 400 );
	public static final Point VIEW_POS = new Point( ( MainPanel.WIDTH - VIEW_SIZE.width ) / 2, ( MainPanel.HEIGHT - VIEW_SIZE.height ) / 2 );
	public static final Rectangle CAPTION_AREA = new Rectangle( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, 40 );
	public static final Dimension COLOR_SIZE = new Dimension( 160, 160 );	//どの色も同じ矩形
	public static final Rectangle RED_AREA = new Rectangle( VIEW_POS.x + ( VIEW_SIZE.width - COLOR_SIZE.width * 2 ) / 2, VIEW_POS.y + CAPTION_AREA.height + ( VIEW_SIZE.height - CAPTION_AREA.height - COLOR_SIZE.height * 2 ) / 2, COLOR_SIZE.width, COLOR_SIZE.height );
	public static final Rectangle BLUE_AREA = new Rectangle( RED_AREA.x + COLOR_SIZE.width, RED_AREA.y, COLOR_SIZE.width, COLOR_SIZE.height );
	public static final Rectangle GREEN_AREA = new Rectangle( RED_AREA.x, RED_AREA.y + COLOR_SIZE.height, COLOR_SIZE.width, COLOR_SIZE.height );
	public static final Rectangle YELLOW_AREA = new Rectangle( BLUE_AREA.x, GREEN_AREA.y, COLOR_SIZE.width, COLOR_SIZE.height );

	/** 色ボタン */
	private Button buttonRed = null;
	private Button buttonBlue = null;
	private Button buttonGreen = null;
	private Button buttonYellow = null;
	/** 当たり判定 */
	private MouseHitTestTask task = null;

	public ChooseColor()
	{
		buttonRed = new Button( RED_AREA );
		buttonBlue = new Button( BLUE_AREA );
		buttonGreen = new Button( GREEN_AREA );
		buttonYellow = new Button( YELLOW_AREA );
		task = new MouseHitTestTask();
		task.add( buttonRed );
		task.add( buttonBlue );
		task.add( buttonGreen );
		task.add( buttonYellow );
	}

	public void update()
	{
		task.hitTest();
		task.update();
	}

	public void draw( Graphics g )
	{
		//背景暗転処理
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		g.setColor( new Color( 0, 0, 0, 64 ) );
		g.fillRect( 0, 0, MainPanel.WIDTH, MainPanel.HEIGHT );	//画面全体を少し暗くする
		//表示画面枠
		g.setColor( Color.WHITE );
		g.fillRect( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height );
		//キャプション
		g.setColor( Color.BLACK );
		g.setFont( new Font( prevFont.getName(), Font.PLAIN, 24 ) );
		ImageManager.drawString( g, "色を選択してください", CAPTION_AREA.x, CAPTION_AREA.y, CAPTION_AREA.width, CAPTION_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
		//色見本
		Color[] colors = { RED, BLUE, GREEN, YELLOW };
		Rectangle[] rects = { RED_AREA, BLUE_AREA, GREEN_AREA, YELLOW_AREA };
		for( int i = 0; i < colors.length; ++i ){
			g.setColor( colors[ i ] );
			Rectangle r = rects[ i ];
			g.fillRect( r.x, r.y, r.width, r.height );
		}

		g.setFont( prevFont );
		g.setColor( prevColor );
	}

	public Card.Color getSelectedColor()
	{
		if( buttonRed.isClicked() ) return Card.Color.RED;
		if( buttonBlue.isClicked() ) return Card.Color.BLUE;
		if( buttonGreen.isClicked() ) return Card.Color.GREEN;
		if( buttonYellow.isClicked() ) return Card.Color.YELLOW;
		return NOT_SELECTED;
	}

	/** カードの色から実際の色データを取得 */
	public static Color getColor( Card.Color color )
	{
		switch( color ){
			case RED: return RED;
			case BLUE: return BLUE;
			case GREEN: return GREEN;
			case YELLOW: return YELLOW;
		}
		return Color.BLACK;
	}
}
