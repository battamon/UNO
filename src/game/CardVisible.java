package game;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import base.IHitTestObject;
import base.ImageManager;

/**
 * 自分から見えているカード(手札、捨て場にあるカード)を表現するクラス
 * カードクラスをフィールドに持ち、位置の保持と描画、当たり判定を可能にする。
 * @author ばったもん
 */
public class CardVisible implements IHitTestObject
{
	/** カードの実体(オブジェクト) */
	private Card card;
	/** 座標とサイズ */
	private Rectangle rect;
	/** 当たり判定フラグ(表) */
	private boolean hitSurfaceFlag;
	/** 当たり判定フラグ(裏) */
	private boolean hitBackFlag;

	public CardVisible( Card card )
	{
		this.card = card;
		rect = new Rectangle( 0, 0, Card.SIZE_X, Card.SIZE_Y );

		hitSurfaceFlag = hitBackFlag = false;
	}

	@Override
	public void update()
	{
		//TODO:実装は不必要な気がする？(カード自体が自分から何かを起こすことは無いため)
	}

	@Override
	public void draw( Graphics g )
	{
		ImageManager.draw( g, card.getImageHandle(), rect.x, rect.y );
	}

	public void setPos( Point pos )
	{
		rect.x = pos.x;
		rect.y = pos.y;
	}

	@Override
	public boolean hitTest( Point pos )
	{
		if( rect.x <= pos.x && pos.x <= rect.x + rect.width
				&& rect.y <= pos.y && pos.y <= rect.y + rect.height ){
			return true;
		}
		return false;
	}

	@Override
	public void hitSurface()
	{
		hitSurfaceFlag = true;
	}

	@Override
	public void hitBack()
	{
		hitBackFlag = true;
	}

	@Override
	public void notHit()
	{
		hitSurfaceFlag = hitBackFlag = false;
	}
}
