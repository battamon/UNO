package sequence.game;

import game.GameState;

import java.awt.Graphics;
import java.awt.Rectangle;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;

public class Result implements ISequence
{
	//定数
	public static Rectangle RECT_BUTTON_NEXT = new Rectangle( 570, 410, 60, 60 );

	/** ゲーム本体 */
	GameState gameState = null;
	/** 次へボタン */
	Button buttonNext;
	/** ボタン当たり判定用 */
	MouseHitTestTask hitTests;

	public Result( GameState state )
	{
		gameState = state;
		buttonNext = new Button( RECT_BUTTON_NEXT );
		buttonNext.setImageHandle( ImageManager.readImage( "resource/image/button_right_arrow.png" ) );
		hitTests = new MouseHitTestTask();
		hitTests.add( buttonNext );
	}

	@Override
	public int update( ISequence parent )
	{
		//ボタン
		hitTests.hitTest();
		buttonNext.update();
		
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		if( parent instanceof GameParent ){
			gameState.update();
		}else{
			System.out.println( "予期せぬ親シーケンスから呼び出されました。" );
		}
		if( buttonNext.isClicked() ){
			if( gameState.isEndOfTheMatch() ){
				//ゲーム終了
				next = GameParent.NEXT_SEQUENCE_TITLE;
			}else{
				gameState.advancePhase();
				next = GameParent.NEXT_SEQUENCE_PLAY;
			}
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		gameState.draw( g );
		gameState.drawResult( g );

		buttonNext.draw( g );
	}

	@Override
	public void destroy()
	{
		gameState = null;
		buttonNext = null;
	}
}
