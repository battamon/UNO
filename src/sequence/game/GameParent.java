package sequence.game;

import game.GameState;
import game.RuleBook;

import java.awt.Graphics;
import java.awt.Rectangle;

import sequence.RootParent;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;

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
	public static final int NEXT_SEQUENCE_TITLE = 2;
	public static final int NEXT_SEQUENCE_MENU = 3;
	public static final int NEXT_SEQUENCE_SHOW_RANKING = 4;
	public static final int NEXT_SEQUENCE_SHOW_RULE = 5;

	/** メニューボタン領域 */
	public static final Rectangle BUTTON_MENU_AREA = new Rectangle( 570, 410, 60, 60 );

	/** 子シーケンス */
	private ISequence child = null;
	/** ゲーム本体オブジェクト */
	private GameState state = null;
	/** ルールを記述したオブジェクト */
	private RuleBook ruleBook = null;
	/** 子シーケンスで共有するメニューボタン */
	private Button buttonMenu;
	/** 子シーケンスで共有するヒットテストタスク */
	private MouseHitTestTask task;

	/**
	 * コンストラクタ
	 */
	public GameParent( RuleBook ruleBook )
	{
		state = new GameState( ruleBook );
		state.initialize();
		this.ruleBook = ruleBook;
		buttonMenu = new Button( BUTTON_MENU_AREA );
		buttonMenu.setImageHandle( ImageManager.readImage( "resource/image/button_menu.png" ) );
		task = new MouseHitTestTask();
		task.add( buttonMenu );
		//最初はプレイ画面から
		child = new Play( state, task );
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
				case NEXT_SEQUENCE_PLAY:	//ゲーム進行
					releaseChild();
					child = new Play( state, task );
					break;
				case NEXT_SEQUENCE_RESULT:	//結果表示
					releaseChild();
					child = new Result( state );
					break;
				case NEXT_SEQUENCE_TITLE:	//タイトルへ戻る
					releaseChild();
					ret = RootParent.NEXT_SEQUENCE_TITLE;
					break;
				case NEXT_SEQUENCE_MENU:	//メニューを開く
					releaseChild();
					child = new Menu( state, buttonMenu );
					break;
				case NEXT_SEQUENCE_SHOW_RANKING:	//ランキングを見せる
					releaseChild();
					child = new ShowRanking( state );
					break;
				case NEXT_SEQUENCE_SHOW_RULE:	//ルールを確認
					releaseChild();
					child = new ShowRule( ruleBook );
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
		if( child != null ){
			child.render( g );
		}
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
