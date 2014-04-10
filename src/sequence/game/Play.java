package sequence.game;

import game.GameState;

import java.awt.Graphics;

import base.Button;
import base.ISequence;
import base.MouseHitTestTask;

/**
 * ゲームを進行させるシーケンス<br>
 * @author ばったもん
 */
public class Play implements ISequence
{
	/** 親から渡されたゲーム本体 */
	private GameState gameState = null;
	/** 親から渡されたタスク */
	private MouseHitTestTask task;
	
	public Play( GameState state, MouseHitTestTask task )
	{
		gameState = state;
		this.task = task;
	}

	@Override
	public int update( ISequence parent )
	{
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		if( parent instanceof GameParent ){
			//Resultフェイズに移ったらリザルト画面へ移行
			if( gameState.update() == GameState.Phase.RESULT ){
				next = GameParent.NEXT_SEQUENCE_RESULT;
			}else{
				//メニューボタンが押されたらメニューシーケンスへ移行
				task.hitTest();
				task.update();
				if( ( (Button)task.get( 0 ) ).isClicked() ){
					next = GameParent.NEXT_SEQUENCE_MENU;
				}
			}
		}else{
			System.out.println( "予期せぬ親シーケンスから呼び出されました。" );
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		gameState.draw( g );
		if( task != null ){
			task.draw( g );
		}
	}

	@Override
	public void destroy()
	{
		
	}
}
