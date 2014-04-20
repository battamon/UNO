package game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;
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
	/** 出せるカードフラグ */
	private List< Boolean > removableCardFlags = null;
	/** 複数同時出し可能なカードフラグ */
	private List< Boolean > removableMultiFlags = null;

	public User( String name )
	{
		super( name );
		user = true;
		ai = null;
		recreateVisibleHands();
	}

	@Override
	public void update()
	{
		super.update();
		if( isUser() ){
			visibleHands.hitTest();
			visibleHands.update();
		}

	}

	@Override
	public void draw( Graphics g )
	{
		super.draw( g );
		//visibleHands.draw( g );	//汎用の一括描画メソッドはハイライト処理が出来ないので使わない。
		//場に出せないカードは暗く表示する。
		for( int i = visibleHands.size() - 1; i >= 0; --i ){
			CardUserHand c = (CardUserHand)visibleHands.get( i );
			boolean dark = false;
			if( removableCardFlags != null && i < removableCardFlags.size() && !removableCardFlags.get( i ).booleanValue() ){
				dark = true;	//選択できないカードは色を落とす。
				if( removableMultiFlags != null && removableMultiFlags.size() == removableCardFlags.size() ){	//サイズが合わなかったらmulti側のデータが古いので参照しない
					if( removableMultiFlags.get( i ).booleanValue() ){
						dark = false;	//複数同時出しで出せるカードなら色は落とさない。
					}
				}
			}
			c.setDark( dark );
			c.draw( g );
		}
	}

	@Override
	public void drawCard( Stack< Card > deck )
	{
		super.drawCard( deck );
		recreateVisibleHands();
		//手札のカード位置を調整
		adjustUserHandsPosition();
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
	 * 手持ちのカードが1枚以上出せるかどうかを返す
	 * @return 出せるならtrue
	 */
	@Override
	public boolean isPlayable( Card.Color color, char glyph )
	{
		//このメソッドを呼び出してもremovableCardFlagsに影響を与えないようにする。
		List< Boolean > prev = removableCardFlags;
		boolean ret = super.isPlayable( color, glyph );
		removableCardFlags = prev;
		return ret;
	}


	/** 出せるカードフラグを描画用にフィールドとして保持しておく */
	@Override
	public List< Boolean > getRemovableCardList( Card.Color color, char glyph )
	{
		removableCardFlags = super.getRemovableCardList( color, glyph );
		return removableCardFlags;
	}

	/** 同時出し可能カードフラグも描画用にフィールドとして保持しておく */
	@Override
	public List< Boolean > getRemovableCardsMulti( Card sample, RuleBook rb )
	{
		if( sample != null ){
			removableMultiFlags = super.getRemovableCardsMulti( sample, rb );
		}else{
			removableMultiFlags = null;
		}
		return removableMultiFlags;
	}

	/** getRemovableCardListメソッドに同様 */
	@Override
	public List< Boolean > getAvoidableDrawCardList( char glyph )
	{
		removableCardFlags = super.getAvoidableDrawCardList( glyph );
		removableMultiFlags = removableCardFlags;	//multiと同期を取っておく
		return removableCardFlags;
	}

	/** 不必要になった出せるカードフラグ情報を消す */
	public void clearRemovablesCardFlags()
	{
		removableCardFlags = null;
	}

	/**
	 * 選択されたカードを取り除く
	 * @return 取り除かれたカードを格納したArrayList
	 */
	public List< Card > removeSelectedCards()
	{
		//選択されているカードを取り除く
		List< Card > ret = removeSelectedHands();
		//visibleHandsを最新の情報に更新
		recreateVisibleHands();
		//手札のカード位置を調整
		adjustUserHandsPosition();
		
		return ret;
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
			int overlappedWidth = widthMax - USER_HANDS_AREA.width;	//重ねなければならない幅
			//表示領域からはみ出さないようにoverlapWidthピクセル分をnum-1回分の重なりで均等に調整する
			for( int i = 0; i < num - 1; ++i ){
				( (CardVisible)visibleHands.get( i ) ).setPos( x, y );
				x = USER_HANDS_AREA.x + Card.WIDTH * ( i + 1 ) - (int)( overlappedWidth / (double)( num - 1 ) * ( i + 1 ) );	//次のカードのx座標を計算しておく
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
