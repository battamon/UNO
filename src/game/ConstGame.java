package game;

public class ConstGame
{
	/** 色を表すビット群 */
	public static final int CARD_COLOR_RED = 1 << 0;
	public static final int CARD_COLOR_BLUE = 1 << 1;
	public static final int CARD_COLOR_GREEN = 1 << 2;
	public static final int CARD_COLOR_YELLOW = 1 << 3;
	public static final int CARD_COLOR_BLACK = 1 << 4;
	/** 色の総数 */
	public static final int NUM_COLORS = 5;

	/** カードタイプ */
	public static final int CARD_TYPE_NUMBER = 0;
	public static final int CARD_TYPE_SYMBOL = CARD_TYPE_NUMBER + 1;

	/** 種類別のカード総数 */
	public static final int NUM_NUMBER_ZERO_CARDS = 1;
	public static final int NUM_NUMBER_WITHOUT_ZERO_CARDS = 2;
	public static final int NUM_SYMBOL_CARDS = 2;
	public static final int NUM_SYMBOL_WILDS_CARDS = 4;

	/** 種類別の色組み合わせ */
	public static final int FLAGSET_COLORS_NUMBERS =
			CARD_COLOR_RED | CARD_COLOR_BLUE | CARD_COLOR_GREEN | CARD_COLOR_YELLOW;
	public static final int FLAGSET_COLORS_SYMBOLS =
			CARD_COLOR_RED | CARD_COLOR_BLUE | CARD_COLOR_GREEN | CARD_COLOR_YELLOW;
	public static final int FLAGSET_COLORS_WILDS =
			CARD_COLOR_BLACK;

	/** カードの種類を表す文字群 */
	public static final char GLYPH_REVERSE = 'r';
	public static final char GLYPH_SKIP = 's';
	public static final char GLYPH_DRAW_TWO = 'd';
	public static final char GLYPH_WILD = 'w';
	public static final char GLYPH_WILD_DRAW_FOUR = 'f';

	/** 初期の手札枚数 */
	public static final int NUM_FIRST_HANDS = 7;
}
