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
		Player cp = state.getCurrentPlayer();
		int stackCount = state.getDiscardCount();

		//奇数枚のときは通常効果。偶数枚のときは実質効果なし。
		if( stackCount % 2 != 0 ){
			state.switchOrderDirection();
		}
		String text = cp.getName() + "「リバース" + ( stackCount > 1 ? ( "x" + stackCount ) : "" ) + "!!」";

		state.getLogger().setLog( text );
		//2人プレイのときは相手が１回休みになる。ゲーム終了時のときはターンを飛ばさない。
		if( state.getNumPlayers() <= 2 && cp.getNumHands() != 0 ){
			state.advanceTurn();
		}
		if( cp.getNumHands() != 0 ){
			state.getLogger().setLog( "次のプレイヤーは" + state.getNextPlayer().getName() + "です。" );
		}
	}
}
