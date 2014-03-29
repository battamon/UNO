package game;

/**
 * 何もしない効果<br>
 * 数字カード用
 * @author ばったもん
 */
public class EventNull implements IEvent
{
	@Override
	public boolean hasEvent()
	{
		return false;
	}
	
	@Override
	public void activate( State state )
	{
		return;
	}
}
