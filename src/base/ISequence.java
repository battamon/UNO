package base;

import java.awt.Graphics;

/**
 * シーケンスインターフェース<br>
 * シーケンス系クラスはこれを実装する
 * @author ばったもん
 */
public interface ISequence
{
	/**
	 * updateクラス シーケンス遷移にかかわる処理を記述する
	 * @param parent 親シーケンスクラス
	 * @return 次に移行するシーケンスを示す識別子
	 */
	public abstract int update( ISequence parent );
	/**
	 * 描画する
	 * @param g 描画用オブジェクト
	 */
	public abstract void render( Graphics g );
	/**
	 * シーケンスクラスが解放される際に、必要であればオブジェクトの解放等を行う
	 */
	public abstract void destroy();
}
