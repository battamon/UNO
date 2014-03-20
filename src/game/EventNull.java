package game;

/**
 * 何もしない効果<br>
 * 数字カード用
 * @author ばったもん
 */
public class EventNull implements IEvent
{
	@Override
	public void event( State state )
	{
		return;
	}
}
