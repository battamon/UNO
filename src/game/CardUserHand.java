package game;


import java.awt.Color;
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
	/** 暗く表示するためのフラグ */
	private boolean dark;

	public CardUserHand( Card card )
	{
		//継承元のクラスのコンストラクタ呼び出しはsuperを使う
		super( card );
		dark = false;
	}

	/** 選択状態なら20px上にずれて描画される */
	@Override
	public void draw( Graphics g )
	{
		//描画だけずらしているが、当たり判定はそのままなので直すなら直そう。
		int x = rect.x;
		int y = rect.y + ( selected ? SELECTED_MOVE_Y : 0 );
		ImageManager.draw( g, card.getImageHandle(), x, y );
		if( dark ){
			Color prevColor = g.getColor();
			g.setColor( new Color( 0, 0, 0, 64 ) );
			g.fillRect( rect.x, rect.y, rect.width, rect.height );
			g.setColor( prevColor );
		}
	}

	public void setSelect( boolean b )
	{
		selected = b;
	}

	public boolean isSelected()
	{
		return selected;
	}

	/** 暗く描画するかどうかを設定する */
	public void setDark( boolean b )
	{
		dark = b;
	}
}
