package sequence;

import java.awt.Graphics;
import java.awt.Rectangle;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;

/**
 * ローカルルールなどを設定するシーケンス
 * @author ばったもん
 */
public class Setup implements ISequence
{
	//定数
	private static final Rectangle RECT_BUTTON_RETURN = new Rectangle( 400, 410, 220, 50 );
	//static変数
	private static int backGroundImageHandle = ImageManager.NO_HANDLE;

	private Button buttonReturn = null;
	private MouseHitTestTask hitTestTask = null;

	public Setup()
	{
		backGroundImageHandle = ImageManager.readImage( "resource/image/bg_setup.png" );
		buttonReturn = new Button( RECT_BUTTON_RETURN );
		hitTestTask = new MouseHitTestTask();
		hitTestTask.add( buttonReturn );
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
		if( buttonReturn.isClicked() ){
			next = RootParent.NEXT_SEQUENCE_TITLE;
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		ImageManager.draw( g, backGroundImageHandle, 0, 0 );
		hitTestTask.draw( g );
	}

	@Override
	public void destroy()
	{
		hitTestTask.removeAll();
	}
}
