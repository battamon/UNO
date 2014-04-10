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
		//TODO 標準ルールのみ実装。ローカルルールのドロー系回避に対応させよう。
		Player cp = state.getCurrentPlayer();
		Player np = state.getNextPlayer();
		//2枚引かせる
		for( int i = 0; i < 2; ++i ){
			state.drawCard( np );
		}
		state.getLogger().setLog( cp.getName() + "「ドロー2!!」");
		if( cp.getNumHands() != 0 ){	//ゲーム終了時はターン飛ばしをしない
			state.getLogger().setLog( np.getName() + "は2枚引いてターンエンド。" );
			state.advanceTurn();
		}
	}
}
