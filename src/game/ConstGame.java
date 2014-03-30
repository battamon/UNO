package game;

public class ConstGame
{
	/** 種類別のカード総数 */
	public static final int NUM_NUMBER_ZERO_CARDS = 1;
	public static final int NUM_NUMBER_WITHOUT_ZERO_CARDS = 2;
	public static final int NUM_SYMBOL_CARDS = 2;
	public static final int NUM_SYMBOL_WILDS_CARDS = 4;

	/** カードの種類を表す文字群。TODO:Cardクラスに定義を移そう */
	public static final char GLYPH_REVERSE = 'r';
	public static final char GLYPH_SKIP = 's';
	public static final char GLYPH_DRAW_TWO = 'd';
	public static final char GLYPH_WILD = 'w';
	public static final char GLYPH_WILD_DRAW_FOUR = 'f';

	/** 初期の手札枚数 */
	public static final int NUM_FIRST_HANDS = 1;
}
