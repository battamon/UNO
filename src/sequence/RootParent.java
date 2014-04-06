package sequence;

import game.RuleBook;

import java.awt.Graphics;

import sequence.game.GameParent;

import base.ISequence;
import base.MainPanel;

/**
 * シーケンス遷移の出発点となるクラス
 * @author ばったもん
 */
public class RootParent implements ISequence
{
	//シーケンス遷移定数
	public static final int NEXT_SEQUENCE_DEFAULT = -1;
	public static final int NEXT_SEQUENCE_TITLE = 0;
	public static final int NEXT_SEQUENCE_GAME_PARENT = 1;
	public static final int NEXT_SEQUENCE_SETUP = 2;
	public static final int NEXT_SEQUENCE_EXIT = 3;

	/** 子シーケンス */
	private ISequence child = null;
	/** ルール設定オブジェクト */
	private RuleBook ruleBook;
	
	public RootParent()
	{
		//最初はタイトル画面から
		child = new Title();
		ruleBook = new RuleBook();
	}

	/**
	 * 子シーケンスのupdateを呼び出し、戻り値を見て次のシーケンスに移行する。<br>
	 * すべての親シーケンスは基本的にこの構造を取る。
	 */
	@Override
	public int update( ISequence parent )
	{
		int ret = MainPanel.LOOP_CONTINUE;

		if( child != null ){
			int next = child.update( this );
			//シーケンス遷移
			switch( next ){
			case NEXT_SEQUENCE_TITLE:
				releaseChild();
				child = new Title();
				break;
			case NEXT_SEQUENCE_GAME_PARENT:
				releaseChild();
				child = new GameParent( ruleBook );
				break;
			case NEXT_SEQUENCE_SETUP:
				releaseChild();
				child = new Setup( ruleBook );
				break;
			case NEXT_SEQUENCE_EXIT:
				ret = MainPanel.LOOP_END;
				break;
			default:
				break;
			}
		}else{
			//seqがnullになることはありえないのでそうなったら強制終了
			ret = MainPanel.LOOP_END;
		}
		return ret;
	}

	@Override
	public void render( Graphics g )
	{
		child.render( g );
	}

	@Override
	public void destroy()
	{
	}

	/**
	 * 子シーケンスを解放する前の後片付け<br>
	 * 親シーケンスはこのメソッドを実装すべきなので、<br>
	 * ISequenceクラスを実装し、childをフィールドに持つParentBase抽象クラスなるものを間に挟む予定。
	 */
	private void releaseChild()
	{
		child.destroy();
		child = null;
	}
}
