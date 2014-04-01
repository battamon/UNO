package game;

import java.awt.Graphics;

public class EventDrawTwo implements IEvent
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
	
	/** 次のプレイヤーにカードを2枚引かせ、手番を終了させる。 */
	@Override
	public void activate( GameState state )
	{
		//TODO 標準ルールのみ実装。後でローカルルールに対応させよう。
		Player nextPlayer = state.getNextPlayer();
		//2枚引かせる
		for( int i = 0; i < 2; ++i ){
			state.drawCard( nextPlayer );
		}
		state.getLogger().setLog( state.getCurrentPlayer().getName() + "「ドロー2!!」");
		state.getLogger().setLog( nextPlayer.getName() + "は2枚引いてターンエンド。" );
		state.advanceTurn();
	}
}
