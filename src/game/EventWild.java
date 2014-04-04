package game;

import java.awt.Graphics;

/**
 * Wildカードの効果
 * @author ばったもん
 */
public class EventWild implements IEvent
{
	/** 色選択画面クラス */
	private ChooseColor cc = null;

	/**
	 * 色選択画面を出してユーザーに選ばせる。<br>
	 * @return 色が選択されたらtrueを返す。
	 */
	@Override
	public boolean update( GameState state )
	{
		Player p = state.getCurrentPlayer();
		if( p.isUser() ){
			if( cc == null ){
				cc = new ChooseColor();
			}
			if( cc != null ){
				cc.update();
				if( cc.getSelectedColor() != ChooseColor.NOT_SELECTED ){
					return true;
				}
			}
			return false;
		}

		return p.getAI().think();	//NPCなら思考時間だけ待ってから進める。
	}

	@Override
	public void draw( Graphics g )
	{
		if( cc != null ){
			cc.draw( g );
		}
	}

	@Override
	public boolean hasEvent()
	{
		return true;
	}

	@Override
	public void activate( GameState state )
	{
		Player p = state.getCurrentPlayer();
		Card.Color selectedColor;
		if( cc != null ){
			selectedColor = cc.getSelectedColor();
			cc = null;
		}else{
			selectedColor = p.getAI().chooseColor( state );
		}
		state.setValidColor( selectedColor );
		state.getLogger().setLog( p.getName() + "「ワイルド!![" + selectedColor + "]」" );
	}
}
