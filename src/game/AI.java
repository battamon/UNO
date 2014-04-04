package game;

import java.util.ArrayList;
import java.util.List;

/**
 * NPCの挙動を記述する<br>
 * このクラスを既定としてさまざまな強さのAIを作ることになるが、今回はこれひとつだけ
 * @author ばったもん
 */
public class AI
{
	/** 意思決定に要する時間 */
	private static final int DICISION_TIME = 30;	//フレーム単位
	/** このAIを使うプレイヤー */
	private Player player;
	/** 思考カウント */
	private int thinkingCount;

	/**
	 * コンストラクタ
	 * @param p このAIを使うプレイヤー
	 */
	public AI( Player p )
	{
		player = p;
	}

	/**
	 * 思考カウントを進める。
	 * @return 意思決定したならtrue。考え中ならfalse。
	 */
	public boolean think()
	{
		++thinkingCount;
		if( thinkingCount >= DICISION_TIME ){
			thinkingCount = 0;
			return true;
		}
		return false;
	}

	/**
	 * 場に出すカードを選択する。
	 * @param s ゲーム本体オブジェクト
	 * @return 選択された(複数の)カードのインデックスを返す。カード自体は手札から取り除かれない。
	 */
	public List< Integer > chooseCardIndecis( GameState s )
	{
		//TODO カードを1枚選んで返す。複数出せるローカルルールに対応させよう。
		List< Boolean > removableHandsList = player.isRemovableCards( s.getCurrentValidColor(), s.getCurrentValidGlyph() );
		List< Integer > validIndices = new ArrayList< Integer >();
		for( int i = 0; i < removableHandsList.size(); ++i ){
			if( removableHandsList.get( i ).booleanValue() ){
				validIndices.add( Integer.valueOf( i ) );
			}
		}
		if( validIndices.isEmpty() ){
			return new ArrayList< Integer >();	//選択できなかったら空のリストを返す
		}
		int selectedIndex = (int)( Math.random() * validIndices.size() );
		List< Integer > selectedCardIndecis = new ArrayList< Integer >();
		selectedCardIndecis.add( validIndices.get( Integer.valueOf( selectedIndex ) ) );
		return selectedCardIndecis;
	}

	/**
	 * 色を選択する。
	 * @param s ゲーム本体オブジェクト
	 * @return 選択された色を返す
	 */
	public Card.Color chooseColor( GameState s )
	{
		Card.Color[] colors = { Card.Color.RED, Card.Color.BLUE, Card.Color.GREEN, Card.Color.YELLOW };
		return colors[ (int)( Math.random() * colors.length ) ];
	}

	/**
	 * ワイルドドローフォーイベントのチャレンジをするかどうかを決める。
	 * @return チャレンジするつもりならtrue。
	 */
	public boolean triesChallenge()
	{
		return (int)( Math.random() * 2 ) == 0 ? false : true;
	}
}
