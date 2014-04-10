package game;


import java.util.EnumSet;
import java.util.HashMap;

import base.ImageManager;

/**
 * カードクラス
 * Cの構造体的な利用を考えているためフィールドはpublic設定
 */
public class Card
{
	//各種定義
	/** 色の定義 */
	public enum Color{
		RED( "赤" ),
		BLUE( "青" ),
		GREEN( "緑" ),
		YELLOW( "黄" ),
		BLACK( "黒" ),
		NUM_COLORS( "" );
		String textColor;
		private Color( String s ){ textColor = s; }
		@Override public String toString(){ return textColor; }
	}
	/** タイプの定義 */
	public enum Type{
		NUMBER,
		SYMBOL,
	}
	/** カードの種類を表す文字群。 */
	public static final char GLYPH_REVERSE = 'r';
	public static final char GLYPH_SKIP = 's';
	public static final char GLYPH_DRAW_TWO = 'd';
	public static final char GLYPH_WILD = 'w';
	public static final char GLYPH_WILD_DRAW_FOUR = 'f';

	/** 数字と記号の並び順 */
	public enum Order{
		ZERO,
		ONE,
		TWO,
		THREE,
		FOUR,
		FIVE,
		SIX,
		SEVEN,
		EIGHT,
		NINE,
		REVERSE,
		SKIP,
		DRAW_TWO,
		WILD,
		WILD_DRAW_FOUR,
	}
	/** 種類別の色組み合わせ */
	public static final EnumSet< Color > FLAGSET_COLORS_NUMBERS =
			EnumSet.of( Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW );
	public static final EnumSet< Color > FLAGSET_COLORS_SYMBOLS_WITHOUT_WILDS =
			EnumSet.of( Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW );
	public static final EnumSet< Color > FLAGSET_COLORS_WILDS =
			EnumSet.of( Color.BLACK );
	/** カードの寸法 */
	public static final int WIDTH = 90;
	public static final int HEIGHT = 130;
	/** 各色に含まれる数字、記号カードの種類数(黒を除く ) */
	private static final int NUM_KINDS_PER_COLOR = 13;	//0123456789rsdの13種
	/** カードの種類の総数 */
	private static final int NUM_KINDS = NUM_KINDS_PER_COLOR * Color.NUM_COLORS.ordinal() + 2;
	/** 種類別のカード総数 */
	public static final int NUM_NUMBER_ZERO_CARDS = 1;
	public static final int NUM_NUMBER_WITHOUT_ZERO_CARDS = 2;
	public static final int NUM_SYMBOL_CARDS = 2;
	public static final int NUM_SYMBOL_WILDS_CARDS = 4;

	//static変数
	/** カードの画像ハンドル */
	private static final int[] hImages;
	/** カード裏画像ハンドル */
	public static final int hCardBackImage;
	/** glyph→Oder対応表 */
	public static final HashMap< Character, Order > orderTable;

	//ここからフィールド
	/** 色 */
	public final Color color;
	/** 種類(数字or記号) */
	public final Type type;
	/** カードの種類を表す文字('0'～'9','r','s','d','w','f') */
	public final char glyph;
	/** カード効果を記述したオブジェクト */
	public final IEvent event;

	//ここからメソッド
	static
	{
		//画像読み込み
		hImages = ImageManager.readDivImage( "resource/image/cards.png", WIDTH, HEIGHT, NUM_KINDS );
		hCardBackImage = ImageManager.readImage( "resource/image/card_back.png" );
		//対応表作成
		orderTable = new HashMap< Character, Order >();
		orderTable.put( new Character( (char)( '0' ) ), Order.ZERO );
		orderTable.put( new Character( (char)( '1' ) ), Order.ONE );
		orderTable.put( new Character( (char)( '2' ) ), Order.TWO );
		orderTable.put( new Character( (char)( '3' ) ), Order.THREE );
		orderTable.put( new Character( (char)( '4' ) ), Order.FOUR );
		orderTable.put( new Character( (char)( '5' ) ), Order.FIVE );
		orderTable.put( new Character( (char)( '6' ) ), Order.SIX );
		orderTable.put( new Character( (char)( '7' ) ), Order.SEVEN );
		orderTable.put( new Character( (char)( '8' ) ), Order.EIGHT );
		orderTable.put( new Character( (char)( '9' ) ), Order.NINE );
		orderTable.put( new Character( (char)( 'r' ) ), Order.REVERSE );
		orderTable.put( new Character( (char)( 's' ) ), Order.SKIP );
		orderTable.put( new Character( (char)( 'd' ) ), Order.DRAW_TWO );
		orderTable.put( new Character( (char)( 'w' ) ), Order.WILD );
		orderTable.put( new Character( (char)( 'f' ) ), Order.WILD_DRAW_FOUR );
	}
	/**
	 * コンストラクタ。各属性を最初に与える。
	 * @param color 色
	 * @param type 種類
	 * @param glyph 種類を表す文字
	 * @param event カード効果
	 */
	public Card( Color color, Type type, char glyph, IEvent event )
	{
		this.color = color;
		this.type = type;
		this.glyph = glyph;
		this.event = event;
	}

	/**
	 * コピーコンストラクタ
	 */
	public Card( Card card )
	{
		color = card.color;
		type = card.type;
		glyph = card.glyph;
		event = card.event;
	}

	/**
	 * カード情報から適切なイメージハンドルを返す
	 * @return ImageManagerで管理されているイメージハンドル値
	 */
	public int getImageHandle()
	{
		int handleIndex = NUM_KINDS_PER_COLOR * color.ordinal();
		handleIndex += orderTable.get( new Character( glyph ) ).ordinal();
		if( color == Color.BLACK ){	//黒系カード
			handleIndex -= NUM_KINDS_PER_COLOR;
		}
		return hImages[ handleIndex ];
	}

	/**
	 * 表裏を指定して画像ハンドルを取得する。
	 * @param surface カードの向き
	 * @return カード画像のイメージハンドル。
	 */
	public static int getCardBackImageHandle()
	{
		return hCardBackImage;
	}

	/**
	 * カードの点数を返す
	 */
	public int getScore()
	{
		switch( type ){
			case NUMBER:
				return glyph - '0';
			case SYMBOL:
				switch( glyph ){
					case 'r': case 's': case 'd':
						return 20;
					case 'w': case 'f':
						return 50;
				}
		}
		return 0;
	}

	//デバッグ用？
	@Override
	public String toString()
	{
		String strColor = null;
		switch( color ){
			case RED: strColor = "Red"; break;
			case BLUE: strColor = "Blue"; break;
			case GREEN: strColor = "Green"; break;
			case YELLOW: strColor = "Yellow"; break;
			case BLACK: strColor = "Black"; break;
		}
		return strColor + " " + glyph;
	}
}