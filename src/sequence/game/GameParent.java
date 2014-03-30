package sequence.game;

import game.GameState;

import java.awt.Graphics;

import sequence.RootParent;

import base.ISequence;

/**
 * ゲーム本体部分の親シーケンス<br>
 * ゲーム本体はここで保持し、各子シーケンスでゲームを進行させたり止めたりを制御する。
 * @author ばったもん
 */
public class GameParent implements ISequence
{
	//定数
	public static final int NEXT_SEQUENCE_DEFAULT = -1;
	public static final int NEXT_SEQUENCE_PLAY = 0;
	public static final int NEXT_SEQUENCE_RESULT = 1;

	/** 子シーケンス */
	private ISequence child = null;
	/** ゲーム本体オブジェクト */
	private GameState state = null;

	/**
	 * コンストラクタ
	 */
	public GameParent()
	{
		state = new GameState();
		//TODO:人数は設定なりで決めよう
		state.setRule( 2 );
		state.initialize();
		//最初はプレイ画面から
		child = new Play( state );
	}

	@Override
	public int update( ISequence parent )
	{
		int ret = RootParent.NEXT_SEQUENCE_DEFAULT;
		if( child != null ){
			//ここで親シーケンスのオブジェクト(this)を渡す理由は、
			//子シーケンスが実際のゲームの進行を制御するために、
			//親シーケンス(のStateオブジェクト)を呼び出す必要があるからである。
			int next = child.update( this );
			//シーケンス遷移
			switch( next ){
				case NEXT_SEQUENCE_PLAY:
					releaseChild();
					child = new Play( state );
					break;
				case NEXT_SEQUENCE_RESULT:
					releaseChild();
					child = new Result( state );
					break;
				default:
					break;
			}
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

	private void releaseChild()
	{
		child.destroy();
		child = null;
	}
}
