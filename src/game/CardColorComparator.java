package game;



import java.util.Comparator;

/**
 * Cardクラスをソートする際の条件を記述したクラス<br>
 * Javaのコレクションクラスで標準のソートで用いる。<br>
 * 色を優先してソートする。
 * @author ばったもん
 *
 */
public class CardColorComparator implements Comparator< Card >
{
	public int compare( Card a, Card b )
	{
		int ac = a.color.ordinal();
		int bc = b.color.ordinal();
		if( ac < bc ){
			return -1;
		}else if( ac > bc ){
			return 1;
		}
		int ag = Card.orderTable.get( new Character( a.glyph ) ).ordinal();
		int bg = Card.orderTable.get( new Character( b.glyph ) ).ordinal();
		if( ag < bg ){
			return -1;
		}else if( ag > bg ){
			return 1;
		}
		return 0;
	}
}
