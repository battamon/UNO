package game;

import java.awt.Graphics;

/**
 * IEventインターフェース<br>
 * カードの効果を記述する。(ドロー2、スキップなどなど)
 * @author ばったもん
 */
public interface IEvent
{
	/** 
	 * イベント発生中の処理<br>
	 * イベントの終了を知らせるときはtrueを返す。
	*/
	public abstract boolean update( GameState state );

	public abstract void draw( Graphics g );

	/**
	 * イベントがあるかどうかを返す。
	 */
	public abstract boolean hasEvent();

	/**
	 * 効果を発揮するときこのメソッドを呼び出す。<br>
	 * ゲームの状態を変化させるので、ゲーム本体クラス(State)のオブジェクトを引数にとる。
	 * @param state ゲーム本体クラスのオブジェクト
	 * @param stackCount 効果が発動するときの場に出されたカード枚数
	 */
	public abstract void activate( GameState state );
}
