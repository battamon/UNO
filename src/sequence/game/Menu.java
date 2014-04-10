package sequence.game;

import game.GameState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MainPanel;
import base.MouseHitTestTask;

/**
 * メニューウィンドウを呼び出す
 * @author ばったもん
 */
public class Menu implements ISequence
{
	/** メニューウィンドウの大きさ */
	private static final Dimension VIEW_SIZE = new Dimension( 250, 300 );
	/** メニューウィンドウの位置 */
	private static final Point VIEW_POS = new Point( ( MainPanel.WIDTH - VIEW_SIZE.width ) / 2, ( MainPanel.HEIGHT - VIEW_SIZE.height ) / 2 );
	/** メニューウィンドウに設置するボタンの数 */
	private static final int NUM_BUTTONS = 4;
	/** ボタン共通の大きさ */
	private static final Dimension BUTTON_SIZE = new Dimension( 200, 50 );
	/** ボタンと周辺の感覚 */
	private static final Dimension BUTTON_SPACE = new Dimension( ( VIEW_SIZE.width - BUTTON_SIZE.width) / 2, ( VIEW_SIZE.height - BUTTON_SIZE.height * NUM_BUTTONS ) / ( NUM_BUTTONS + 1 ) );
	/** ゲームに戻るボタン領域 */
	private static final Rectangle BUTTON_RETURN_AREA = new Rectangle( VIEW_POS.x + BUTTON_SPACE.width, VIEW_POS.y + BUTTON_SPACE.height, BUTTON_SIZE.width, BUTTON_SIZE.height );
	/** ランキングを見るボタン領域 */
	private static final Rectangle BUTTON_SHOW_RANKING_AREA = new Rectangle( BUTTON_RETURN_AREA.x, BUTTON_RETURN_AREA.y + BUTTON_RETURN_AREA.height + BUTTON_SPACE.height, BUTTON_SIZE.width, BUTTON_SIZE.height );
	/** ルールを確認するボタン領域 */
	private static final Rectangle BUTTON_SHOW_RULE_AREA = new Rectangle( BUTTON_SHOW_RANKING_AREA.x, BUTTON_SHOW_RANKING_AREA.y + BUTTON_SHOW_RANKING_AREA.height + BUTTON_SPACE.height, BUTTON_SIZE.width, BUTTON_SIZE.height );
	/** ゲームをやめるボタン領域 */
	private static final Rectangle BUTTON_EXIT_AREA = new Rectangle( BUTTON_SHOW_RULE_AREA.x, BUTTON_SHOW_RULE_AREA.y + BUTTON_SHOW_RULE_AREA.height + BUTTON_SPACE.height, BUTTON_SIZE.width, BUTTON_SIZE.height );

	/** ゲーム本体 */
	private GameState gameState;
	/** 親から渡された共有のメニューボタン */
	private Button buttonMenu;
	/** ゲームに戻るボタン */
	private Button buttonReturn;
	/** ランキングを見るボタン */
	private Button buttonShowRanking;
	/** ルールを確認するボタン */
	private Button buttonShowRule;
	/** ゲームをやめるボタン */
	private Button buttonExit;
	/** ウィンドウに設置するボタンのタスク */
	private MouseHitTestTask task;

	public Menu( GameState state, Button buttonMenu )
	{
		gameState = state;
		this.buttonMenu = buttonMenu;
		//ボタン生成
		buttonReturn = new Button( BUTTON_RETURN_AREA );
		buttonShowRanking = new Button( BUTTON_SHOW_RANKING_AREA );
		buttonShowRule = new Button( BUTTON_SHOW_RULE_AREA );
		buttonExit = new Button( BUTTON_EXIT_AREA );
		//タスク生成
		task = new MouseHitTestTask();
		task.add( buttonReturn );
		task.add( buttonShowRanking );
		task.add( buttonShowRule );
		task.add( buttonExit );
	}

	@Override
	public int update( ISequence parent )
	{
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		//ボタン
		task.hitTest();
		task.update();

		//シーケンス遷移
		if( parent instanceof GameParent ){
			if( buttonReturn.isClicked() ){
				next = GameParent.NEXT_SEQUENCE_PLAY;
			}
			if( buttonShowRanking.isClicked() ){
				next = GameParent.NEXT_SEQUENCE_SHOW_RANKING;
			}
			if( buttonShowRule.isClicked() ){
				next = GameParent.NEXT_SEQUENCE_SHOW_RULE;
			}
			if( buttonExit.isClicked() ){
				next = GameParent.NEXT_SEQUENCE_TITLE;
			}
		}else{
			System.out.println( "予期せぬ親シーケンスから呼び出されました。" );
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		//ゲーム画面
		gameState.draw( g );
		//メニューボタン
		if( buttonMenu != null ){
			buttonMenu.draw( g );
		}

		//前準備
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		//背景暗転処理
		g.setColor( new Color( 0, 0, 0, 128 ) );
		g.fillRect( 0, 0, MainPanel.WIDTH, MainPanel.HEIGHT );	//画面全体を少し暗くする
		//ウィンドウの表示
		g.setColor( Color.WHITE );
		g.fillRect( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height );
		g.setColor( Color.BLACK );
		g.drawRect( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height );
		//ボタンの表示
		task.draw( g );
		g.setFont( new Font( prevFont.getName(), Font.PLAIN, 16 ) );
		String buttonTexts[] = { "ゲームに戻る", "ランキングを見る", "ルールを確認する", "ゲームをやめる" };
		Rectangle buttonAreas[] = { BUTTON_RETURN_AREA, BUTTON_SHOW_RANKING_AREA, BUTTON_SHOW_RULE_AREA, BUTTON_EXIT_AREA };
		for( int i = 0; i < NUM_BUTTONS; ++i ){
			Rectangle area = buttonAreas[ i ];
			g.drawRect( area.x, area.y, area.width, area.height );
			ImageManager.drawString( g, buttonTexts[ i ], area, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
		}
		
		//後処理
		g.setColor( prevColor );
		g.setFont( prevFont );
	}

	@Override
	public void destroy()
	{
	}
	
}
