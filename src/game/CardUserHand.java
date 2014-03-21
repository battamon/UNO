package game;

import java.awt.Graphics;

import base.ImageManager;

/**
 * 手札専用カードクラス<br>
 * CardVisibleクラスの機能に加えて、選択状態を保持でき、それに伴う描画等の変化にも対応する。
 * @author ばったもん
 */
public class CardUserHand extends CardVisible
{
	/** 選択状態の描画位置調整 */
	private static final int SELECTED_MOVE_Y = -20;
	/** 選択状態フラグ */
	private boolean selected = false;

	public CardUserHand( Card card )
	{
		//継承元のクラスのコンストラクタ呼び出しはsuperを使う
		super( card );
	}

	/** 選択状態なら20px上にずれて描画される */
	@Override
	public void draw( Graphics g )
	{
		int x = rect.x;
		int y = rect.y + ( selected ? SELECTED_MOVE_Y : 0 );
		ImageManager.draw( g, card.getImageHandle(), x, y );
	}

	public void setSelect( boolean b )
	{
		selected = b;
	}
}
