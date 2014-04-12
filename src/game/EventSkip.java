package game;

import java.awt.Graphics;

public class EventSkip implements IEvent
{
	@Override
	public boolean update( GameState state )
	{
		return true;
	}

	@Override
	public void draw( Graphics g )
	{
	}

	@Override
	public boolean hasEvent()
	{
		return true;
	}

	@Override
	public void activate( GameState state )
	{
		Player cp = state.getCurrentPlayer();
		int stackCount = state.getEventStackCount();
		String text = cp.getName() + "「スキップ" + ( stackCount > 1 ? ( "x" + stackCount ) : "" ) + "!!」";
		state.getLogger().setLog( text );
		if( cp.getNumHands() != 0 ){
			for( int i = 0; i < stackCount * 2 - 1; ++i ){
				state.advanceTurn();	//(n枚 * 2 - 1 )回ターンを進める
			}
			state.getLogger().setLog( "次は" + state.getNextPlayer().getName() + "です。" );
		}
	}
}
