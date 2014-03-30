package game;

import java.awt.Graphics;

/**
 * 何もしない効果<br>
 * 数字カード用
 * @author ばったもん
 */
public class EventNull implements IEvent
{
	@Override
	public boolean updateByUser()
	{
		return true;
	}

	@Override
	public boolean updateByNPC()
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
		return false;
	}
	
	@Override
	public void activate( GameState state )
	{
	}
}
