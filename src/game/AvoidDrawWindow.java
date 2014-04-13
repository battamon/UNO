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
 * ドロー回避するかを問うウィンドウを生成するクラス
 * @author ばったもん
 */
public class AvoidDrawWindow
{
	/** ボタン識別子 */
	public enum Pushed{
		YES,
		NO,
		NOT_PUSHED,
	}
	//各領域
	private static final Dimension VIEW_SIZE = new Dimension( 400, 300 );
	private static final Point VIEW_POS = new Point( ( MainPanel.WIDTH - VIEW_SIZE.width ) / 2, ( MainPanel.HEIGHT - VIEW_SIZE.height ) / 2 );
	private static final Rectangle CAPTION_AREA = new Rectangle( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height / 5 * 3 );
	private static final Dimension BUTTON_SIZE = new Dimension( 80, 50 );
	private static final Rectangle BUTTON_YES_AREA = new Rectangle( VIEW_POS.x + ( VIEW_SIZE.width / 2 - BUTTON_SIZE.width ) / 2, CAPTION_AREA.y + CAPTION_AREA.height , BUTTON_SIZE.width, BUTTON_SIZE.height );
	private static final Rectangle BUTTON_NO_AREA = new Rectangle( VIEW_POS.x + VIEW_SIZE.width / 2 + ( VIEW_SIZE.width / 2 - BUTTON_SIZE.width ) / 2, BUTTON_YES_AREA.y, BUTTON_SIZE.width, BUTTON_SIZE.height );

	/** Yesボタン */
	private Button buttonYes;
	/** Noボタン */
	private Button buttonNo;
	/** 当たり判定タスク */
	private MouseHitTestTask task;

	public AvoidDrawWindow()
	{
		buttonYes = new Button( BUTTON_YES_AREA );
		buttonNo = new Button( BUTTON_NO_AREA );
		task = new MouseHitTestTask();
		task.add( buttonYes );
		task.add( buttonNo );
	}

	public void update()
	{
		task.hitTest();
		task.update();
	}

	public void draw( Graphics g )
	{
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		//背景を暗くする
		g.setColor( new Color( 0, 0, 0, 64 ) );
		g.fillRect( 0, 0, MainPanel.WIDTH, MainPanel.HEIGHT );	//画面全体を少し暗くする
		//表示画面枠
		g.setColor( Color.WHITE );
		g.fillRect( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height );
		//キャプション
		g.setColor( Color.BLACK );
		g.setFont( new Font( prevFont.getName(), Font.PLAIN, 24 ) );
		ImageManager.drawString( g, "ドロー回避しますか？", CAPTION_AREA.x, CAPTION_AREA.y, CAPTION_AREA.width, CAPTION_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
		//ボタン
		g.drawRect( BUTTON_YES_AREA.x, BUTTON_YES_AREA.y, BUTTON_YES_AREA.width, BUTTON_YES_AREA.height );
		g.drawRect( BUTTON_NO_AREA.x, BUTTON_NO_AREA.y, BUTTON_NO_AREA.width, BUTTON_NO_AREA.height );
		ImageManager.drawString( g, "はい", BUTTON_YES_AREA.x, BUTTON_YES_AREA.y, BUTTON_YES_AREA.width, BUTTON_YES_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
		ImageManager.drawString( g, "いいえ", BUTTON_NO_AREA.x, BUTTON_NO_AREA.y, BUTTON_NO_AREA.width, BUTTON_NO_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
		task.draw( g );

		g.setColor( prevColor );
		g.setFont( prevFont );
	}

	/**
	 * どのボタンが押されたかを返す。
	 * @return 押されたボタンの識別子が返る。何も押されてなければNOT_PUSHEDが返る。
	 */
	public Pushed getButtonPushed()
	{
		if( buttonYes.isClicked() ){
			return Pushed.YES;
		}
		if( buttonNo.isClicked() ){
			return Pushed.NO;
		}
		return Pushed.NOT_PUSHED;
	}
}