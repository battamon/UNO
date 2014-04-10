package sequence.game;

import game.GameState;

import java.awt.Graphics;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;

/**
 * ゲームの途中でランキングを表示する
 * @author ばったもん
 */
public class ShowRanking implements ISequence
{
	/** ゲーム本体 */
	private GameState gameState;
	/** 戻るボタン */
	private Button buttonReturn;
	/** タスク */
	private MouseHitTestTask task;

	public ShowRanking( GameState state )
	{
		gameState = state;
		buttonReturn = new Button( GameParent.BUTTON_MENU_AREA );	//メニューボタンの位置に設置
		buttonReturn.setImageHandle( ImageManager.readImage( "resource/image/button_right_arrow.png" ) );
		task = new MouseHitTestTask();
		task.add( buttonReturn );
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
				next = GameParent.NEXT_SEQUENCE_MENU;
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
		//ランキングウィンドウ
		gameState.drawRanking( g );
		//戻るボタン
		task.draw( g );
	}

	@Override
	public void destroy()
	{
	}

}
