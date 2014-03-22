package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Stack;

import base.MouseHitTestTask;

/**
 * Playerクラスを継承したユーザークラス<br>
 * 手札表示など、ユーザー固有の部分を実装する
 * @author ばったもん
 */
public class User extends Player
{
	/** ユーザーの手札の表示領域 */
	public static final Rectangle USER_HANDS_AREA = new Rectangle( 80, 320, 480, 130);
	/** ユーザー用の手札表示クラス */
	private MouseHitTestTask visibleHands = null;

	public User( String name )
	{
		super( name );
		user = true;
		recreateVisibleHands();
	}

	@Override
	public void update()
	{
		if( isUser() ){
			visibleHands.hitTest();
			visibleHands.update();
		}
		//手札のカード位置を調整
		adjustUserHandsPosition();
	}

	@Override
	public void draw( Graphics g )
	{
		super.draw( g );
		visibleHands.draw( g );
	}

	@Override
	public void drawCard( Stack< Card > deck )
	{
		super.drawCard( deck );
		recreateVisibleHands();
	}

	public MouseHitTestTask getVisibleHands()
	{
		return visibleHands;
	}

	/**
	 * 手札表示クラスを作り直す<br>
	 * 手札の構成が変わるたびに呼び出びだそう
	 */
	public void recreateVisibleHands()
	{
		visibleHands = new MouseHitTestTask();
		for( Card c : hands ){
			visibleHands.add( new CardUserHand( c ) );
		}
	}

	/**
	 * 手札の表示位置を計算・調整
	 */
	private void adjustUserHandsPosition()
	{
		int num = visibleHands.size();
		int x = USER_HANDS_AREA.x;
		int y = USER_HANDS_AREA.y;
		int widthMax = num * Card.WIDTH;	//カードを重ねずに並べたときの幅
		//カードを重ねずに並べたときに表示領域からはみ出すかどうかを調べる
		if( widthMax > USER_HANDS_AREA.width ){	//はみ出しそうなら・・・
			//はみ出さないようにカードを重ねて表示しないといけないだけの幅を調べる
			int overlapWidth = widthMax - USER_HANDS_AREA.width;	//重ねなければならない幅
			//表示領域からはみ出さないようにoverlapWidthピクセル分をnum-1回分の重なりで均等に調整する
			for( int i = 0; i < num - 1; ++i ){
				( (CardVisible)visibleHands.get( i ) ).setPos( x, y );
				x = USER_HANDS_AREA.x + Card.WIDTH * ( i + 1 ) - (int)( overlapWidth / (double)( num - 1 ) * ( i + 1 ) );	//次のカードのx座標を計算しておく
			}
			//最後の1枚の微調整
			x = USER_HANDS_AREA.x + USER_HANDS_AREA.width - Card.WIDTH;
			( (CardVisible)visibleHands.get( num - 1 ) ).setPos( x, y );
		}else{	//そもそもはみ出さないなら左端から並べるだけ
			for( int i = 0; i < num; ++i ){
				( (CardVisible)visibleHands.get( i ) ).setPos( x, y );
				x += Card.WIDTH;
			}
		}
	}

}
