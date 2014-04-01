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
		return true;
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
		Card.Color selectedColor;
		if( cc != null ){
			selectedColor = cc.getSelectedColor();
			cc = null;
		}else{
			//TODO NPCが色を選択する。AIはどこかでまとめたほうがよさそう。
			Card.Color[] colors = { Card.Color.RED, Card.Color.BLUE, Card.Color.GREEN, Card.Color.YELLOW };
			selectedColor = colors[ (int)( Math.random() * colors.length ) ];
		}
		state.setValidColor( selectedColor );
		state.getLogger().setLog( state.getCurrentPlayer().getName() + "「ワイルド!![" + selectedColor + "]」" );
	}
}
