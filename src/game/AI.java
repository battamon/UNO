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
	public boolean chooseCardsIndices( GameState s )
	{
		List< Boolean > removableHandsList;
		if( !s.getAvoidDrawFlag() ){	//通常時とドロー回避中で取得するリストを変える。
			removableHandsList = player.getRemovableCardList( s.getCurrentValidColor(), s.getCurrentValidGlyph() );
		}else{
			removableHandsList = player.getAvoidableDrawCardList( s.getCurrentValidGlyph() );
		}
		List< Integer > validIndices = new ArrayList< Integer >();
		for( int i = 0; i < removableHandsList.size(); ++i ){
			if( removableHandsList.get( i ).booleanValue() ){
				validIndices.add( Integer.valueOf( i ) );
			}
		}
		if( validIndices.isEmpty() ){
			return false;	//選択可能なカードが無い。
		}
		//カードを１枚選んで、それが複数枚出せるなら出せるだけ出す。
		int selectedValidIndex = validIndices.get( (int)( Math.random() * validIndices.size() ) );
		Card selectedCard = player.hands.get( selectedValidIndex );
		List< Integer > selects = new ArrayList< Integer >();
		List< Boolean > multi = player.getRemovableCardsMulti( selectedCard, s.getRuleBook() );
		for( int i = 0; i < multi.size(); ++i ){
			if( multi.get( i ).booleanValue() ){
				selects.add( Integer.valueOf( i ) );
			}
		}
		for( int i = 0; i < selects.size(); ++i ){
			player.selectHand( selects.get( i ).intValue() );	//カードを選ぶ。
		}
		return true;
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

	/**
	 * ドロー回避行動に出るかどうか判断する。
	 * @return ドロー回避するならtrueが返る。
	 */
	public boolean decideAvoidDraw()
	{
		//基本的に回避行動に出ることにする。
		return true;
	}
}
