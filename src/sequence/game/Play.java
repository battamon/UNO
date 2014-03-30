package sequence.game;

import game.GameState;

import java.awt.Graphics;

import base.ISequence;

/**
 * ゲームを進行させるシーケンス<br>
 * @author ばったもん
 */
public class Play implements ISequence
{
	GameState gameState = null;
	
	public Play( GameState state )
	{
		gameState = state;
	}

	@Override
	public int update( ISequence parent )
	{
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		if( parent instanceof GameParent ){
			//Resultフェイズに移ったらリザルト画面へ移行
			if( gameState.update() == GameState.Phase.RESULT ){
				next = GameParent.NEXT_SEQUENCE_RESULT;
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
	}

	@Override
	public void destroy()
	{
		
	}
}
