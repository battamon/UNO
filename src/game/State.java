package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Stack;

import base.ImageManager;
import base.MouseHitTestTask;

/**
 * ゲーム本体クラス
 * @author ばったもん
 */
public class State
{
	//各種定義
	/** ゲーム進行フラグの定義 */
	private enum Phase{
		START,
		PLAYING,
		GAMEOVER,
		RESULT,
	}

	//ここからフィールド
	/** 背景画像ハンドル */
	private static final int hFrameImage;

	/** プレイヤー人数 */
	private int numPlayers = 0;
	/** 山札 */
	private Stack< Card > deck = null;
	/** 捨て場 */
	private Stack< Card > discardPile = null;
	/** 手札 */
	private ArrayList< ArrayList< Card > > playerHands = null;

	/** ゲーム進行フラグ */
	private Phase phase = Phase.START;
	/** 操作プレイヤー(ユーザー)番号 */
	private int userID = 0;
	/** ユーザーの手札 */
	private MouseHitTestTask userHands;
	/** 現在の手番のプレイヤー番号 */
	private int playingPlayer = 0;

	//ここからメソッド
	static
	{
		hFrameImage = ImageManager.readImage( "resource/image/bg_playing.png" );
	}
	/**
	 * コンストラクタ
	 */
	public State()
	{
		userHands = new MouseHitTestTask(); 
	}

	public void update()
	{
		switch( phase ){
			case START: //ゲーム開始前の準備
				//手札を配る
				dealFirstCards();
				//TODO:手番を決める。後でちゃんと実装しましょう。
				decideOrder();
				phase = Phase.PLAYING;
				break;
			case PLAYING:	//ゲーム開始
				playingPlayer = play( playingPlayer );
				//TODO:以降の処理を書く
				break;
		}
	}

	public void draw( Graphics g )
	{
		//背景
		ImageManager.draw( g, hFrameImage, 0, 0 );

		//TODO: とりあえずカード描画して動作テスト
		userHands.draw( g );
	}

	/**
	 * ルールを設定する。(Setup画面でローカルルールを実装したりする予定)
	 * @param numPlayers プレイヤー人数
	 */
	public void setRule( int numPlayers )
	{
		this.numPlayers = numPlayers;
	}

	/**
	 * ゲーム開始前の初期化。
	 * 先にsetRuleを呼び出してゲームルールを決めておく。
	 */
	public void initialize()
	{
		//プレイヤーの手札領域の確保
		playerHands = new ArrayList< ArrayList< Card > >();
		for( int i = 0; i < numPlayers; ++i ){
			playerHands.add( new ArrayList< Card >() );
		}
		//山札領域の確保・山札の構築
		deck = new Stack< Card >();
		createShuffledDeck( deck );
		//捨て場領域の確保
		discardPile = new Stack< Card >();
	}

	/**
	 * シャッフル済みの山札の生成
	 * @param deck 生成した山札を保持する変数(ここではメンバ変数が渡される)
	 */
	private void createShuffledDeck( Stack< Card > deck )
	{
		//全カードの生成 全108枚
		//カード[0] 赤青緑黄 各1枚 計4枚
		createCards( deck, ConstGame.NUM_NUMBER_ZERO_CARDS, Card.Type.NUMBER, '0', Card.FLAGSET_COLORS_NUMBERS, new EventNull() );
		//カード[1]～[9] 赤青緑黄 各2枚 計72枚
		for( int i = 1; i < 10; ++i ){
			createCards( deck, ConstGame.NUM_NUMBER_WITHOUT_ZERO_CARDS, Card.Type.NUMBER, (char)( (int)'1' + i ), Card.FLAGSET_COLORS_NUMBERS, new EventNull() );
		}
		//カード[Reverse]、[Skip]、[DrawTwo] 赤青緑黄 各2枚 計24枚
		char[] symbols = { ConstGame.GLYPH_REVERSE, ConstGame.GLYPH_SKIP, ConstGame.GLYPH_DRAW_TWO };
		IEvent[] symbolEvents = { new EventNull()/* TODO:EventReverse */, new EventNull()/* TODO:EventSkip */, new EventNull()/* TODO:EventDrawTwo */ };
		for( int i = 0; i < symbols.length; ++i ){
			createCards( deck, ConstGame.NUM_SYMBOL_CARDS, Card.Type.SYMBOL, symbols[ i ], Card.FLAGSET_COLORS_SYMBOLS_WITHOUT_WILDS, symbolEvents[ i ] );
		}
		//カード[Wild]、[WildDrawFour] 黒 各4枚 計8枚
		char[] wilds = { ConstGame.GLYPH_WILD, ConstGame.GLYPH_WILD_DRAW_FOUR };
		IEvent[] wildEvents = { new EventNull()/* TODO:EventWild */, new EventNull()/* TODO:EventWildDrawFour*/ };
		for( int i = 0; i < wilds.length; ++i ){
			createCards( deck, ConstGame.NUM_SYMBOL_WILDS_CARDS, Card.Type.SYMBOL, wilds[ i ], Card.FLAGSET_COLORS_WILDS, wildEvents[ i ] );
		}
		//山札をシャッフルする
		Collections.shuffle( deck );
	}

	/**
	 * 各種カード情報を引数に取り、それを基にカードを生成する
	 * @param deck 山札
	 * @param numCards 第3引数以降で設定される属性を持ったカードを生成する枚数
	 * @param type 生成するカードのタイプ(数字or記号)
	 * @param glyph 生成するカードの種類を表す文字( '0'～'9','r','s','d','w','f' )
	 * @param colorFlagSet 生成するカードの種類に関する色情報の組み合わせ
	 * @param event 生成するカードの効果を記述したオブジェクト
	 */
	private void createCards( Stack< Card > deck, int numCards, Card.Type type, char glyph, EnumSet< Card.Color > colorFlagSet, IEvent event )
	{
		for( int i = 0; i < numCards; ++i ){	//作りたい枚数分ループ
			for( Card.Color color : Card.Color.values() ){	//色の数分ループ(5色分)
				if( colorFlagSet.contains( color ) ){	//色フラグが立ってたら、その色のカードを生成
					deck.add( new Card( color, type, glyph, event ) );
				}
			}
		}
	}

	/**
	 * 手札を配る
	 */
	private void dealFirstCards()
	{
		//各プレイヤーにNUM_FIRST_HANDS枚ずつ配る
		for( int i = 0; i < ConstGame.NUM_FIRST_HANDS; ++i ){
			for( int j = 0; j < numPlayers; ++j ){
				playerHands.get( j ).add( deck.pop() );
			}
		}
		//自分の手札はCardVisibleクラスでラップして保持
		int i = 0;
		for( Card card : playerHands.get( userID ) ){
			//TODO:とりあえず座標適当に放り込む
			CardVisible cv = new CardVisible( card );
			cv.setPos( new Point( 80 + i++ * 60, 320 ) );
			userHands.add( cv );
		}
	}

	/**
	 * 手番を決める
	 */
	private void decideOrder()
	{
		//:TODOプレイヤーの番号で手番を決める。(適当な実装なのでちゃんと作り直そう。)
		userID = (int)( Math.random() * numPlayers );
	}

	/**
	 * 現在の手番のプレイヤーを行動を決定する
	 * ユーザーかNPCかでplayUser()、playNPC()に分岐する
	 * @param player 
	 * @return 次の手番のプレイヤー番号か、手番が回らない場合現在の手番のプレイヤー番号が返る。
	 */
	private int play( int player )
	{
		return ( player == userID ) ? playUser( player ) : playNPC( player );
	}

	private int playUser( int player )
	{
		int nextPlayer = player;
		//TODO: 操作プレイヤーの処理
		return nextPlayer;
	}

	private int playNPC( int player )
	{
		int nextPlayer = player;
		//TODO: NPCプレイヤーの処理
		return nextPlayer;
	}
}
