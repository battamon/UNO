package game;

public class EventDrawTwo implements IEvent
{
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
		state.getLogger().setLog( "ドロー2の効果で");
		state.getLogger().setLog( nextPlayer.getName() + "はカードを2枚引きます。" );
		//2枚引かせる
		for( int i = 0; i < 2; ++i ){
			state.drawCard( nextPlayer );
		}
		state.advanceTurn();
	}
}
