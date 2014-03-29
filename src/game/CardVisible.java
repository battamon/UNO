package game;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import base.IHitTestObject;
import base.ImageManager;
import base.Input;

/**
 * 自分から見えているカード(手札、捨て場にあるカードなど)を表現するクラス<br>
 * カードクラスをフィールドに持ち、位置の保持と描画、当たり判定を可能にする。
 * @author ばったもん
 */
public class CardVisible implements IHitTestObject
{
	/** カードの実体(オブジェクト) */
	protected Card card;
	/** 座標とサイズ */
	protected Rectangle rect;
	/** 当たり判定フラグ(表) */
	private boolean hitSurfaceFlag;
	/** 当たり判定フラグ(裏) */
	private boolean hitBackFlag;
	/** 左クリックフラグ */
	private boolean leftClicked;
	/** 右クリックフラグ */
	private boolean rightClicked;

	public CardVisible( Card card )
	{
		this.card = card;
		rect = new Rectangle( 0, 0, Card.WIDTH, Card.HEIGHT );
		hitSurfaceFlag = hitBackFlag = false;
	}

	@Override
	public void update()
	{
		//クリックフラグのリセット
		leftClicked = rightClicked = false;
		//当たり判定に成功かつクリックされていたらフラグを立てる
		if( hitSurfaceFlag ){
			if( Input.getClicked( Input.MOUSE_BUTTON_LEFT ) ){
				leftClicked = true;
			}
			if( Input.getClicked( Input.MOUSE_BUTTON_RIGHT ) ){
				rightClicked = true;
			}
		}
	}

	@Override
	public void draw( Graphics g )
	{
		ImageManager.draw( g, card.getImageHandle(), rect.x, rect.y );
	}

	/** 座標を設定する */
	public void setPos( int x, int y )
	{
		setPosX( x );
		setPosY( y );
	}

	/** X座標を設定する */
	public void setPosX( int x )
	{
		rect.x = x;
	}

	/** Y座標を設定する */
	public void setPosY( int y )
	{
		rect.y = y;
	}

	/** 左クリックされたかどうか　*/
	public boolean isLeftClicked()
	{
		return leftClicked;
	}

	/** 右クリックされたかどうか */
	public boolean isRightClicked()
	{
		return rightClicked;
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
		hitBackFlag = false;
		hitSurfaceFlag = true;
	}

	@Override
	public void hitBack()
	{
		hitSurfaceFlag = false;
		hitBackFlag = true;
	}

	@Override
	public void notHit()
	{
		hitSurfaceFlag = hitBackFlag = false;
	}
}
