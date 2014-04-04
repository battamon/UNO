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
	/** 増減点 */
	private int fluctuationPoint;
	/** ユーザーかNPCか */
	protected boolean user;
	/** 順位 */
	private int rank;
	/** 意思決定クラス(AI) */
	protected AI ai;

	/** 情報画面位置 */
	private Rectangle infoRect;
	/** 手札表示位置 */
	private List< Point > handPositions;

	public Player( String name )
	{
		this.name = name;
		hands = new ArrayList< Card >();
		score = 0;
		fluctuationPoint = 0;
		user = false;
		rank = 1;
		infoRect = new Rectangle();
		infoRect.width = INFO_WIDTH;
		infoRect.height = INFO_HEIGHT;
		handPositions = new ArrayList< Point >();
		ai = new AI( this );
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
		Color c = g.getColor();
		g.setColor( Color.BLACK );
		int sideSpace = 2;
		int rowHeight = 12;
		//名前
		int x = infoRect.x + sideSpace;
		int y = infoRect.y - 2;
		ImageManager.drawString( g, name, x, y );
		//順位
		x = infoRect.x + infoRect.width - sideSpace;
		ImageManager.drawString( g, rank + "位", x, y, ImageManager.Align.RIGHT );
		//手札数
		x = infoRect.x + sideSpace;
		y += rowHeight + 2;
		ImageManager.drawString( g, "残り　" + hands.size() + "枚", x, y );
		//点数
		x = infoRect.x + infoRect.width - sideSpace;
		ImageManager.drawString( g, score + "点", x, y, ImageManager.Align.RIGHT );

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
	public boolean isPlayable( Card.Color color, char glyph )
	{
		List< Boolean > removables = isRemovableCards( color, glyph );
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
	public List< Boolean > isRemovableCards( Card.Color color, char glyph )
	{
		List< Boolean > removables = Arrays.asList( new Boolean[ hands.size() ] );
		for( int i = 0; i < hands.size(); ++i ){
			Card card = hands.get( i );
			//出せるパターンならremovablesにtrueをセット
			if( card.color == Card.Color.BLACK	//出すカードが黒
					|| color == Card.Color.BLACK	//TODO: 場の色が黒 ディーラーを決めればこれは要らない
					|| card.color == color		//色が同じ
					|| card.glyph == glyph ){	//数字記号が同じ
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
	public List< Card > removeHands( List< Integer > selectedCardIndecis )
	{
		//TODO 1枚だけ返す実装だが、ローカルルールによっては複数枚返さないとならない場合も？
		List< Card > cards = new ArrayList< Card >();
		for( Integer i : selectedCardIndecis ){
			cards.add( hands.remove( i.intValue() ) );
		}
		adjustPlayerHandsPosition();
		return cards;
	}

	/**
	 * 次のゲームへ移行するために必要な初期化を行う。
	 * @return 持っているカードをすべて返す
	 */
	public List< Card > prepareNextGame()
	{
		//点数を清算して
		addScore();
		//手札をすべて返す
		return hands;
	}

	public String getName()
	{
		return name;
	}

	public int getNumHands()
	{
		return hands.size();
	}

	public int getFluctuationPoint()
	{
		return fluctuationPoint;
	}

	public int getScore()
	{
		return score + fluctuationPoint;
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

	public int calcScore()
	{
		int ret = 0;
		for( Card c : hands ){
			ret += c.getScore();
		}
		return ret;
	}

	public void stackScore( int fluc )
	{
		fluctuationPoint += fluc;
	}

	private void addScore()
	{
		score += fluctuationPoint;
		fluctuationPoint = 0;
	}

	/**
	 * カードを一枚手札に加える
	 * @param card カード
	 */
	public void obtainCard( Card card )
	{
		hands.add( card );
		Collections.sort( hands, new CardColorComparator() );
		adjustPlayerHandsPosition();
	}

	/** 順位を設定する */
	public void setRank( int rank )
	{
		this.rank = rank;
	}

	/** 順位を取得する */
	public int getRank()
	{
		return rank;
	}

	/** AIを取得する */
	public AI getAI()
	{
		return ai;
	}

	//デバッグ用
	@Override
	public String toString()
	{
		return name + " " + hands.size() + "枚" + " " + score + "点";
	}
}
