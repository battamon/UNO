package game;

/**
 * カードクラス
 * Cの構造体的な利用を考えているためフィールドはpublic設定
 */
public class Card
{
	/** 色 */
	public final int color;
	/** 種類(数字or記号) */
	public final int type;
	/** カードの種類を表す文字('0'～'9','r','s','d','w','f') */
	public final char glyph;
	/** カード効果を記述したオブジェクト */
	private final IEvent event;

	/**
	 * コンストラクタ。各属性を最初に与える。
	 * @param color 色
	 * @param type 種類
	 * @param glyph 種類を表す文字
	 * @param event カード効果
	 */
	public Card( int color, int type, char glyph, IEvent event )
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
		type = card.color;
		glyph = card.glyph;
		event = card.event;
	}

	//TODO:デバッグ用？
	@Override
	public String toString()
	{
		String strColor = null;
		switch( color ){
			case ConstGame.CARD_COLOR_RED: strColor = "Red"; break;
			case ConstGame.CARD_COLOR_BLUE: strColor = "Blue"; break;
			case ConstGame.CARD_COLOR_GREEN: strColor = "Green"; break;
			case ConstGame.CARD_COLOR_YELLOW: strColor = "Yellow"; break;
			case ConstGame.CARD_COLOR_BLACK: strColor = "Black"; break;
		}
		return strColor + " " + glyph;
	}
}
