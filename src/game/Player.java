package game;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import base.ImageManager;

/**
 * プレイヤークラス<br>
 * 手札やスコア等、プレイヤー固有の情報を保持
 * @author ばったもん
 *
 */
public class Player
{
	//情報画面関連定数
	public static final int INFO_POS_ORIGIN_X = 30;	//基準点X(0番目の情報表示位置)
	public static final int INFO_POS_ORIGIN_Y = 20;	//基準点Y
	public static final int INFO_POS_ALIGN_X = 120;	//隣の情報画面(の左上座標)との距離X
	public static final int INFO_POS_ALIGN_Y = 60;	//隣の情報画面(の左上座標)との距離Y
	public static final int NUM_INFO_COLUMN = 5;	//一列に並ぶ情報画面の数
	public static final int INFO_WIDTH = 100;
	public static final int INFO_HEIGHT = 50;
	//情報画面に出す裏を向いたカード
	private static final int INFO_CARD_HEIGHT = 20;	//スケール基準
	private static final int INFO_CARD_WIDTH = (int)( INFO_CARD_HEIGHT / (double)Card.HEIGHT * Card.WIDTH + 0.5d );
	private static final int hImageCardBack = ImageManager.readImage( "resource/image/card_back.png" );
	/** 名前 */
	private String name;
	/** 手札 */
	protected ArrayList< Card > hands = null;
	/** 持ち点 */
	private int score;
	/** 順番 */
	private int order;
	/** ユーザーかNPCか */
	protected boolean user;

	/** 情報画面位置 */
	private Rectangle infoRect;
	/** 手札表示位置 */
	private List< Point > handPositions;

	public Player( String name )
	{
		this.name = name;
		hands = new ArrayList< Card >();
		score = 0;
		user = false;
		infoRect = new Rectangle();
		infoRect.width = INFO_WIDTH;
		infoRect.height = INFO_HEIGHT;
		handPositions = new ArrayList< Point >();
	}

	public void update()
	{
	}

	/** プレイヤー情報画面の描画 */
	public void draw( Graphics g )
	{
		//手札の表示
		for( int i = hands.size() - 1; i >= 0; --i ){
			Point pos = handPositions.get( i );
			ImageManager.draw( g, hImageCardBack, pos.x, pos.y, INFO_CARD_WIDTH, INFO_CARD_HEIGHT );
		}
		//TODO:サンプル描画
		Color c = g.getColor();
		g.setColor( Color.BLACK );
		g.drawString( toString(), infoRect.x + 2, infoRect.y + 12 );
		g.setColor( c );
	}

	/** 山札からカードを1枚引く */
	public void drawCard( Stack< Card > deck )
	{
		hands.add( deck.pop() );
		Collections.sort( hands, new CardColorComparator() );
		adjustPlayerHandsPosition();
	}

	/** 順番を設定する。ついでに情報画面の位置も計算しとく。 */
	public void setOrder( int order )
	{
		this.order = order;
		infoRect.x = INFO_POS_ORIGIN_X + INFO_POS_ALIGN_X * ( order % NUM_INFO_COLUMN );
		infoRect.y = INFO_POS_ORIGIN_Y + INFO_POS_ALIGN_Y * ( order / NUM_INFO_COLUMN );
		adjustPlayerHandsPosition();
	}

	public boolean isUser()
	{
		return user;
	}

	/**
	 * 手持ちのカードが1枚以上出せるかどうかを返す
	 * @return 出せるならtrue
	 */
	public boolean isPlayable( Card discardTop )
	{
		List< Boolean > removables = isRemovableCards( discardTop );
		for( Boolean b : removables ){
			if( b.booleanValue() ) return true;
		}
		return false;
	}

	/**
	 * 手札のカードが出せるかどうかをそれぞれ調べる
	 * @param discardTop 捨て場に見えているカード
	 * @return 出せるかどうかを示すリスト
	 */
	public List< Boolean > isRemovableCards( Card discardTop )
	{
		List< Boolean > removables = Arrays.asList( new Boolean[ hands.size() ] );
		for( int i = 0; i < hands.size(); ++i ){
			Card card = hands.get( i );
			//出せるパターンならremovablesにtrueをセット
			if( card.color == Card.Color.BLACK				//出すカードが黒
					|| discardTop.color == Card.Color.BLACK	//出ているカードが黒
					|| card.color == discardTop.color		//色が同じ
					|| card.glyph == discardTop.glyph ){	//数字記号が同じ
				removables.set( i, Boolean.TRUE );
				continue;
			}
			//出せないならfalseをセット
			removables.set( i, Boolean.FALSE );
		}
		return removables;
	}

	/**
	 * カードを出す
	 * @param index 手札のインデックス
	 * @return 指定されたカード
	 */
	public Card removeHands( int index )
	{
		//TODO 1枚だけ返す実装だが、ローカルルールによっては複数枚返さないとならない場合も？
		Card card = hands.remove( index );
		adjustPlayerHandsPosition();
		return card;
	}

	/**
	 * カードをすべて出す
	 * @return 手札すべてを返す
	 */
	public List< Card > removeAllHands()
	{
		List< Card > cards = new ArrayList< Card >();
		while( !hands.isEmpty() ){
			cards.add( removeHands( 0 ) );
		}
		return cards;
	}

	public String getName()
	{
		return name;
	}

	public int getNumHands()
	{
		return hands.size();
	}

	private void adjustPlayerHandsPosition()
	{
		final int numHands = hands.size();
		if( numHands == 0 ){
			//手札が0ならオブジェクトだけ作っておしまい
			handPositions = new ArrayList< Point >();
			return;
		}
		
		handPositions = Arrays.asList( new Point[ numHands ] );
		final int fixOverlappedWidth = 2;	//最低限重ねる固定幅
		final int widthMax = numHands * INFO_CARD_WIDTH - ( numHands - 1 ) * fixOverlappedWidth;
		int overlappedWidth = widthMax - INFO_WIDTH;	//単純に並べたときにはみ出す幅
		int x = infoRect.x;
		int y = infoRect.y + ( infoRect.height - INFO_CARD_HEIGHT );
		if( overlappedWidth > 0 ){
			//単純に並べてはみ出す場合
			for( int i = 0; i < numHands - 1; ++i ){
				handPositions.set( i, new Point( x, y ) );
				x = infoRect.x	//カード描画開始の原点
						+ ( INFO_CARD_WIDTH * ( i + 1 )	//i枚目のカードは原点からこれだけ右にずらす
						- ( fixOverlappedWidth * i )	//固定幅で重ねるためにこれだけ左に戻す
						- (int)( ( (double)overlappedWidth / ( numHands - 1 ) ) * ( i + 1 ) ) );	//さらに範囲に収まるように微調整
			}
			//最後の一枚
			x = infoRect.x + infoRect.width - INFO_CARD_WIDTH;
			handPositions.set( numHands - 1, new Point( x, y ) );
		}else{
			//はみ出さない場合
			for( int i = 0; i < numHands; ++i ){
				x = infoRect.x + ( INFO_CARD_WIDTH * i ) - ( fixOverlappedWidth * i );
				handPositions.set( i, new Point( x, y ) );
			}
		}
	}

	//デバッグ用
	@Override
	public String toString()
	{
		return "No." + ( order + 1 ) + " " + name + " " + hands.size() + "枚";
	}
}
