package game;

import java.util.EnumSet;

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
		RED,
		BLUE,
		GREEN,
		YELLOW,
		BLACK,
		NUM_COLORS,
	}
	/** タイプの定義 */
	public enum Type{
		NUMBER,
		SYMBOL,
	}
	/** 数字と記号の並び順 */
	private enum Order{
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
	}
	private enum OrderW{
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
	public static final int SIZE_X = 90;
	public static final int SIZE_Y = 130;
	/** 各色に含まれる数字、記号カードの種類数(黒を除く ) */
	private static final int NUM_KINDS_PER_COLOR = 13;	//0123456789rsdの13種
	/** カードの種類の総数 */
	private static final int NUM_KINDS = NUM_KINDS_PER_COLOR * Color.NUM_COLORS.ordinal() + 2;

	//static変数
	/** カードの画像ハンドル */
	private static final int[] hImage;

	//ここからフィールド
	/** 色 */
	public final Color color;
	/** 種類(数字or記号) */
	public final Type type;
	/** カードの種類を表す文字('0'～'9','r','s','d','w','f') */
	public final char glyph;
	/** カード効果を記述したオブジェクト */
	private final IEvent event;

	//ここからメソッド
	static
	{
		hImage = ImageManager.readDivImage( "resource/image/cards.png", SIZE_X, SIZE_Y, NUM_KINDS );
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
		//シンボルカード系
		if( type == Type.SYMBOL ){
			switch( glyph ){
				case 'r':	//リバース
					handleIndex += Order.REVERSE.ordinal();
					break;
				case 's':	//スキップ
					handleIndex +=  Order.SKIP.ordinal();
					break;
				case 'd':	//ドロー2
					handleIndex += Order.DRAW_TWO.ordinal();
					break;
				case 'w':	//ワイルド
					handleIndex += OrderW.WILD.ordinal();
					break;
				case 'f':	//ワイルドドローフォー
					handleIndex += OrderW.WILD_DRAW_FOUR.ordinal();
					break;
			}
		}else{
			//数字
			handleIndex += (int)( glyph - '0' );
		}
		return hImage[ handleIndex ];
	}

	//TODO:デバッグ用？
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