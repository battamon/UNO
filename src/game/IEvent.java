package game;

/**
 * IEventインターフェース
 * カードの効果を記述する。(ドロー2、スキップなどなど)
 * @author ばったもん
 */
public interface IEvent
{
	/**
	 * 効果を発揮するときこのメソッドを呼び出す。<br>
	 * ゲームの状態を変化させるので、ゲーム本体クラス(State)のオブジェクトを引数にとる。
	 * @param state ゲーム本体クラスのオブジェクト
	 */
	public abstract void event( State state );
}
