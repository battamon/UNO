package game;

import java.awt.Graphics;

/**
 * リバースカードの効果
 * @author ばったもん
 */
public class EventReverse implements IEvent
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
	
	/** 順番を反対にする */
	@Override
	public void activate( GameState state )
	{
		state.switchOrderDirection();
		state.getLogger().setLog( state.getCurrentPlayer().getName() + "「リバース!!」" );
		state.getLogger().setLog( "次のプレイヤーは" + state.getNextPlayer().getName() + "です。" );
	}
}
