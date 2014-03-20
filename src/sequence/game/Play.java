package sequence.game;

import game.State;

import java.awt.Graphics;

import base.ISequence;

/**
 * ゲームを進行させるシーケンス<br>
 * @author ばったもん
 */
public class Play implements ISequence
{
	State gameState = null;
	
	public Play()
	{
	}

	@Override
	public int update( ISequence parent )
	{
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		if( parent instanceof GameParent ){
			gameState = ( (GameParent)parent ).getGameState();
			gameState.update();
		}else{
			System.out.println( "予期せぬ親シーケンスから呼び出されました。" );
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		if( gameState != null ){
			gameState.draw( g );
		}
	}

	@Override
	public void destroy()
	{
		
	}
}
