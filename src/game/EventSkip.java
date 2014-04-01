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
		state.getLogger().setLog( state.getCurrentPlayer().getName() + "「スキップ!!」" );
		state.getLogger().setLog( state.getNextPlayer().getName() + "の手番が飛ばされます。");
		state.advanceTurn();
		state.getLogger().setLog( "次は" + state.getNextPlayer().getName() + "です。" );
	}
}
