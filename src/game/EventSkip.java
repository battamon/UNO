package game;

public class EventSkip implements IEvent
{
	@Override
	public boolean hasEvent()
	{
		return true;
	}

	@Override
	public void activate( GameState state )
	{
		state.getLogger().setLog( state.getNextPlayer().getName() + "の手番が飛ばされます。");
		state.advanceTurn();
		state.getLogger().setLog( "次のプレイヤーは" + state.getNextPlayer().getName() + "です。" );
	}
}
