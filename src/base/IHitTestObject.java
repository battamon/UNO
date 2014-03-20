package base;

import java.awt.Graphics;
import java.awt.Point;

/**
 * 当たり判定インターフェース<br>
 * 当たり判定が必要なクラスはこれを実装する
 * @author ばったもん
 */
public interface IHitTestObject
{
	/**
	 * 当たり判定を行う
	 * @param point 当たり判定を行う対象の座標
	 * @return 当たり判定に成功するとtrue、失敗するとfalseを返す
	 */
	public abstract boolean hitTest( Point pos );
	/** 当たりフラグを立てる(特定の座標から直接当たっている場合) */
	public abstract void hitSurface();
	/** 当たりフラグを立てる(特定の座標から直接当たっていない場合) */
	public abstract void hitBack();
	/** 当たりフラグを消す */
	public abstract void notHit();
	/** タスク系クラスでまとめて更新することもあるので実装してね */
	public abstract void update();
	/** タスク系クラスでまとめて描画すると重なり表現とかもうまくできるので実装してね */
	public abstract void draw( Graphics g );
}
