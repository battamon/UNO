package sequence;

import java.awt.Graphics;
import java.awt.Rectangle;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;

/**
 * タイトル画面シーケンス<br>
 * ゲームを始める、設定画面、プログラムの終了に分岐する。
 * @author ばったもん
 *
 */
public class Title implements ISequence
{
	//定数
	private static final Rectangle RECT_BUTTON_START = new Rectangle( 210, 220, 220, 50 );
	private static final Rectangle RECT_BUTTON_SETUP = new Rectangle( 210, 290, 220, 50 );
	private static final Rectangle RECT_BUTTON_EXIT  = new Rectangle( 210, 360, 220, 50 );
	//static変数
	private static int backGroundImageHandle = ImageManager.NO_HANDLE;

	private Button buttonStart;
	private Button buttonSetup;
	private Button buttonExit;
	private MouseHitTestTask hitTestTask;

	//コンストラクタ
	Title()
	{
		if( backGroundImageHandle == ImageManager.NO_HANDLE ){
			backGroundImageHandle = ImageManager.readImage( "resource/image/bg_title.png" );
		}
		buttonStart = new Button( RECT_BUTTON_START );
		buttonSetup = new Button( RECT_BUTTON_SETUP );
		buttonExit = new Button( RECT_BUTTON_EXIT );
		hitTestTask = new MouseHitTestTask();
		hitTestTask.add( buttonStart );
		hitTestTask.add( buttonSetup );
		hitTestTask.add( buttonExit );
	}

	@Override
	public int update( ISequence parent )
	{
		//当たり判定
		hitTestTask.hitTest();

		//各オブジェクト更新
		hitTestTask.update();

		//シーケンス遷移
		int next = RootParent.NEXT_SEQUENCE_DEFAULT;
		if( buttonStart.isClicked() ){
			next = RootParent.NEXT_SEQUENCE_GAME_PARENT;
		}else if( buttonSetup.isClicked() ){
			next = RootParent.NEXT_SEQUENCE_SETUP;
		}else if( buttonExit.isClicked() ){
			next = RootParent.NEXT_SEQUENCE_EXIT;
		}

		return next;
	}

	@Override
	public void render( Graphics g )
	{
		//描画
		ImageManager.draw( g, backGroundImageHandle, 0, 0 );
		hitTestTask.draw( g );
	}

	@Override
	public void destroy()
	{
		hitTestTask.removeAll();
	}
}
