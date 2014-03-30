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
	 * イベント発生中の処理(ユーザー用)<br>
	 * イベントの終了を知らせるときはtrueを返す。
	*/
	public abstract boolean updateByUser();
	/** 
	 * イベント発生中の処理(NPC用)<br>
	 * イベントの終了を知らせるときはtrueを返す。
	*/
	public abstract boolean updateByNPC();

	public abstract void draw( Graphics g );

	/**
	 * イベントがあるかどうかを返す。
	 */
	public abstract boolean hasEvent();

	/**
	 * 効果を発揮するときこのメソッドを呼び出す。<br>
	 * ゲームの状態を変化させるので、ゲーム本体クラス(State)のオブジェクトを引数にとる。
	 * @param state ゲーム本体クラスのオブジェクト
	 */
	public abstract void activate( GameState state );
}
