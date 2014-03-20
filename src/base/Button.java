package base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * ボタンクラス<br>
 * IHitTestObjectを実装してマウスとの当たり判定を可能にする。
 * @author ばったもん
 */
public class Button implements IHitTestObject
{
	/** ボタンの位置、サイズ。 */
	private Rectangle rect;

	//IHitTestObject関連
	/** 直接ヒットした場合のフラグ */
	private boolean hitS;
	/** //間接ヒットした場合のフラグ */
	private boolean hitB;

	/** //ボタンがクリックされたらtrue */
	private boolean clicked;

	/**
	 * コンストラクタ
	 * @param x X座標
	 * @param y Y座標
	 * @param w 幅
	 * @param h 高さ
	 */
	public Button( int x, int y, int w, int h )
	{
		this( new Rectangle( x, y, w, h ) );
	}

	/** 
	 * コンストラクタ
	 * @param rect 位置、サイズ
	 */
	public Button( Rectangle rect )
	{
		this.rect = new Rectangle( rect );
		hitS = hitB = false;
		clicked = false;
	}

	@Override
	public void update()
	{
		//フレーム毎にclickedフラグを初期化
		clicked = false;
		//カーソルがボタン上にあり、なおかつクリックされていたならフラグを立てる
		if( hitS ){
			if( Input.getClicked( Input.MOUSE_BUTTON_LEFT ) ){
				clicked = true;
			}
		}
	}

	@Override
	public void draw( Graphics g )
	{
		Color prevColor = null;
		//TODO:かんたんなハイライト処理 ボタンを画像にすることも要検討
		if( hitS ){
			prevColor = g.getColor();
			g.setColor( new Color( 0, 255, 255, 64 ) );
			g.fillRect( rect.x, rect.y, rect.width, rect.height );
			g.setColor( prevColor );
		}
	}

	/**
	 * hitTestメソッド
	 * 通常はMouseHitTestTaskオブジェクトに登録されて、同オブジェクトのhitTestメソッドから呼び出される。
	 * @param pos マウスカーソルの位置
	 * @return マウスカーソルがこのオブジェクト上にあればtrueを返す
	 */
	@Override
	public boolean hitTest( Point pos )
	{
		if( rect.x <= pos.x && pos.x <= rect.x + rect.width
				&& rect.y <= pos.y && pos.y <= rect.y + rect.height ){
			return true;
		}
		return false;
	}

	/**
	 * MouseHitTestTaskオブジェクトに登録された複数のオブジェクトの中で、最初にhitTestに成功した場合に呼び出される。
	 */
	@Override
	public void hitSurface()
	{
		hitS = true;
	}

	/**
	 * MouseHitTestTaskオブジェクトに登録された複数のオブジェクトの中で、２番目以降にhitTestに成功した場合に呼び出される。
	 */
	@Override
	public void hitBack()
	{
		hitB = true;
	}

	/**
	 * hitTestに失敗した場合に外部(MouseHitTestTask)から呼び出される。
	 */
	@Override
	public void notHit()
	{
		hitS = hitB = false;
	}

	public boolean isClicked()
	{
		return clicked;
	}
}
