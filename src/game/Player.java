package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

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

	public Player( String name )
	{
		this.name = name;
		hands = new ArrayList< Card >();
		score = 0;
		user = false;
		infoRect = new Rectangle();
		infoRect.width = INFO_WIDTH;
		infoRect.height = INFO_HEIGHT;
	}

	//何もしないように見えて、サブクラス用に必要なのだ。
	public void update()
	{
	}

	/** プレイヤー情報画面の描画 */
	public void draw( Graphics g )
	{
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
	}

	/** 順番を設定する。ついでに情報画面の位置も計算しとく。 */
	public void setOrder( int order )
	{
		this.order = order;
		infoRect.x = INFO_POS_ORIGIN_X + INFO_POS_ALIGN_X * ( order % NUM_INFO_COLUMN );
		infoRect.y = INFO_POS_ORIGIN_Y + INFO_POS_ALIGN_Y * ( order / NUM_INFO_COLUMN );
	}

	public boolean isUser()
	{
		return user;
	}

	//デバッグ用
	@Override
	public String toString()
	{
		return ( order + 1 ) + " " + name + " ";
	}
}
