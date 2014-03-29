package game;

/**
 * リバースカードの効果
 * @author ばったもん
 */
public class EventReverse implements IEvent
{
	@Override
	public boolean hasEvent()
	{
		return true;
	}
	
	/** 順番を反対にする */
	@Override
	public void activate( State state )
	{
		state.switchOrderDirection();
		state.getLogger().setLog( "順番が反対になりました。");
		state.getLogger().setLog( "次のプレイヤーは" + state.getNextPlayer().getName() + "です。" );
	}
}
