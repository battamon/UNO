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
	//IHitTestObject関連
	/** 直接ヒットした場合のフラグ */
	private boolean hitSurfaceFlag;
	/** 間接ヒットした場合のフラグ。今回はボタンの上に他のオブジェクトが乗っからない想定なので要らない。 */
	//private boolean hitBackFlag;

	/** ボタンの位置、サイズ。 */
	private Rectangle rect;
	/** ボタンの画像 */
	private int hImage;
	/** ハイライトの有無 */
	private boolean highlight;
	/** ボタンがクリックされた瞬間だったらtrue */
	private boolean clicked;
	/** オンオフ */
	private boolean on;

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
		hitSurfaceFlag = false;
		//hitBackFlag = false;
		clicked = false;
		hImage = ImageManager.NO_HANDLE;
		highlight = true;
		on = false;
	}

	@Override
	public void update()
	{
		//フレーム毎にclickedフラグを初期化
		clicked = false;
		//カーソルがボタン上にあり、なおかつクリックされていたならフラグを立てる
		if( hitSurfaceFlag ){
			if( Input.getClicked( Input.MOUSE_BUTTON_LEFT ) ){
				clicked = true;
				on = !on;
			}
		}
	}

	@Override
	public void draw( Graphics g )
	{
		Color prevColor = null;
		//ボタンに画像がセットされてるなら描画
		if( hImage != ImageManager.NO_HANDLE ){
			ImageManager.draw( g, hImage, rect.x, rect.y );
		}
		if( hitSurfaceFlag && highlight ){
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
		hitSurfaceFlag = true;
	}

	/**
	 * MouseHitTestTaskオブジェクトに登録された複数のオブジェクトの中で、２番目以降にhitTestに成功した場合に呼び出される。
	 */
	@Override
	public void hitBack()
	{
		//hitBackFlag = true;
	}

	/** hitTestに失敗した場合に外部(MouseHitTestTask)から呼び出される。 */
	@Override
	public void notHit()
	{
		hitSurfaceFlag = false;
		//hitBackFlag = false;
	}

	/** クリックされたかどうか */
	public boolean isClicked()
	{
		return clicked;
	}

	/** ボタン領域に表示する画像をセットする。 */
	public void setImageHandle( int imageHandle )
	{
		hImage = imageHandle;
	}

	/** ハイライトするかどうかを決める。デフォルトではtrue。 */
	public void setHighlight( boolean l )
	{
		highlight = l;
	}

	/** ボタンがオンかどうかを返す。 */
	public boolean isOn()
	{
		return on;
	}

	/** ボタンをオンにする。 */
	public void on()
	{
		on = true;
	}

	/** ボタンをオフにする。 */
	public void off()
	{
		on = false;
	}
}
