package game;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
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
		GAME_OVER,
		RESULT,
	}
	/** 画面サイズ */
	public static final Point SCREEN_SIZE = new Point( 640, 480 );
	/** 捨て場の表示領域 */
	public static final Rectangle DISCARD_PILE_AREA = new Rectangle( 275, 155, 90, 130 );
	/** 山札の表示領域 */
	public static final Rectangle DECK_AREA = new Rectangle( 440, 155, 90, 130 );
	/** メニューボタンの表示領域 */
	public static final Rectangle BUTTON_MENU_AREA = new Rectangle( 570, 410, 60, 60 );

	//ここからフィールド
	/** 背景画像ハンドル */
	private static final int hFrameImage;
	/** カード表画像ハンドル */
	private static final int hCardFrontImage;

	/** プレイヤー人数 */
	private int numPlayers = 0;
	/** 山札 */
	private Stack< Card > deck = null;
	/** 捨て場 */
	private Stack< Card > discardPile = null;
	/** プレイヤー */
	private ArrayList< Player > players = null;
	/** ゲーム進行フラグ */
	private Phase phase = Phase.START;
	/** 現在の手番のプレイヤー番号 */
	private int whosePlayingIndex = 0;
	/** TODO フレームカウント。最終的に要らないかも */
	private int frameCount = 0;
	/** ログ表示クラス */
	private GameLog logger;

	//ここからメソッド
	static
	{
		hFrameImage = ImageManager.readImage( "resource/image/bg_playing.png" );
		hCardFrontImage = ImageManager.readImage( "resource/image/card_front.png" );
	}
	/**
	 * コンストラクタ
	 */
	public State()
	{
		logger = new GameLog();
	}

	public void update()
	{
		++frameCount;
		//プレイヤー更新
		for( Player p : players ){
			p.update();
		}

		switch( phase ){
			case START: //ゲーム開始前の準備
				//場の初期化
				reset();
				//手札を配る
				dealFirstCards();
				//手番を決める
				decideOrder();
				//場札を決める
				setFirstDiscardPile();
				phase = Phase.PLAYING;
				break;
			case PLAYING:	//ゲーム開始
				Player p = players.get( whosePlayingIndex );
				if( play( p ) ){
					//現在の手番のプレイヤーが行動を終了したら次の手番に回す
					int nextPlayer = ( whosePlayingIndex + 1 ) % players.size();
					if( nextPlayer != whosePlayingIndex ){
						//ゲームオーバー判定
						if( p.getNumHands() == 0 ){
							logger.setLog( p.getName() + "の手札が無くなりました。" );
							logger.setLog( "ゲーム終了。" );
							frameCount = 0;
							phase = Phase.GAME_OVER;
						}
						whosePlayingIndex = nextPlayer;
					}
				}
				break;
			case GAME_OVER:
				//TODO スコア計算に移る。シーケンス遷移？暫定の処理として次のゲームに移行する。
				if( frameCount == 120 ){
					phase = Phase.START;
				}
				break;
			case RESULT:
				//TODO 結果画面。シーケンス遷移？
				break;
		}
	}

	public void draw( Graphics g )
	{
		//背景
		ImageManager.draw( g, hFrameImage, 0, 0 );
		//山札
		ImageManager.draw( g, hCardFrontImage, DECK_AREA.x, DECK_AREA.y );
		//捨て場
		ImageManager.draw( g, discardPile.peek().getImageHandle(), DISCARD_PILE_AREA.x, DISCARD_PILE_AREA.y );
		//ログ
		logger.view( g );

		//プレイヤー情報
		for( Player p : players ){
			p.draw( g );
		}
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
		//プレイヤー領域の確保
		players = new ArrayList< Player >();
		//TODO:とりあえずプレイヤーの名前を適当に決める
		players.add( new User( "user" ) );
		int nameNumber = 0;
		for( int i = 0; i < numPlayers; ++i ){
			players.add( new Player( "NPC_" + nameNumber++ ) );
		}
		//山札領域の確保・山札の構築
		deck = new Stack< Card >();
		createShuffledDeck( deck );
		//捨て場領域の確保
		discardPile = new Stack< Card >();
	}

	private void reset()
	{
		//TODO カードの回収。いったん全部消去して再生成する方法もある。
		while( !discardPile.isEmpty() ){
			deck.add( discardPile.pop() );
		}
		for( Player p : players ){
			List< Card > cards = p.removeAllHands();
			while( !cards.isEmpty() ){
				deck.add( cards.remove( 0 ) );
			}
		}
		Collections.shuffle( deck );
		logger.clear();
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
			createCards( deck, ConstGame.NUM_NUMBER_WITHOUT_ZERO_CARDS, Card.Type.NUMBER, (char)( (int)'0' + i ), Card.FLAGSET_COLORS_NUMBERS, new EventNull() );
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
			for( Player p : players ){
				p.drawCard( deck );
			}
		}
	}

	/**
	 * 手番を決める
	 */
	private void decideOrder()
	{
		//TODO:とりあえずシャッフルで決める。
		Collections.shuffle( players );
		for( int i = 0; i < players.size(); ++i ){
			players.get( i ).setOrder( i );
		}
	}

	/**
	 * 場札を決める
	 */
	private void setFirstDiscardPile()
	{
		Card card;
		while( ( card = deck.pop() ).glyph == ConstGame.GLYPH_WILD_DRAW_FOUR ){
			//ワイルドドローフォーだったらやり直し
			deck.add( card );
			Collections.shuffle( deck );
		}
		discardPile.add( card );
	}

	/**
	 * 現在の手番のプレイヤーを行動を決定する
	 * ユーザーかNPCかでplayUser()、playNPC()に分岐する
	 * @param player 今プレイしているプレイヤーを示すインデックス
	 * @return プレイヤーの行動が完了したらtrueが返る
	 */
	private boolean play( Player player )
	{
		//FIXME ユーザーとNPCの共通処理はこのメソッドでできるだけくくろう
		return ( player.isUser() ) ? playUser( (User)player ) : playNPC( player );
	}

	private boolean playUser( User user )
	{
		boolean end = false;
		List< Boolean > removableHandsList = user.isRemovableCards( discardPile.peek() );
		//TODO: 操作プレイヤーの処理
		//手札を出せるかどうか。出せるなら選択処理へ、出せないなら山札から1枚引く。
		if( user.isPlayable( discardPile.peek() ) ){
			//手札のクリック処理。左クリックで選択状態。右クリックで選択解除。
			//FIXME 一度に1枚しか選択できない状態にしてあるので、ローカルルール実装時は修正が必要。
			int selectedCardIndex = -1;
			MouseHitTestTask visibleHands = user.getVisibleHands();
			for( int i = 0; i < visibleHands.size(); ++i ){
				CardUserHand cuh = (CardUserHand)visibleHands.get( i );
				if( cuh.isLeftClicked() ){	//左クリック
					if( removableHandsList.get( i ).booleanValue() ){	//出せるカードかチェック
						if( cuh.isSelected() ){
							end = true;	//既に選択状態のカードをクリックしたら場に出す
						}else{
							//選択フラグを立てる
							selectedCardIndex = i;
						}
					}
				}
				if( cuh.isRightClicked() ){
					cuh.setSelect( false );
				}
			}
			if( selectedCardIndex != -1 ){
				for( int i = 0; i < visibleHands.size(); ++i ){
					CardUserHand cuh = (CardUserHand)visibleHands.get( i );
					if( selectedCardIndex == i ){
						cuh.setSelect( true );
					}else{
						cuh.setSelect( false );
					}
				}
			}
			if( end ){
				//手札のカードを場に出す
				setCardToDiscardPile( user.removeSelectedCards() );
				if( user.getNumHands() == 1 ){
					logger.setLog( user.getName() + "「UNO!」" );
				}
			}
		}else{
			//山札からカードを引く。 FIXME 標準ルールはカードを引いたら手番は終了するが、ローカルルールでは修正が必要かも。
			drawCard( user );
			logger.setLog( user.getName() + "は山札から1枚引きました。" );
			end = true;
		}

		return end;
	}

	private boolean playNPC( Player player )
	{
		//TODO 手番が回ってきたら少し待つ。アニメーション入れたらこの処理も要らなくなる
		if( frameCount < 5 ) return false;
		frameCount = 0;

		boolean end = true;
		List< Boolean > removableHandsList = player.isRemovableCards( discardPile.peek() );
		if( player.isPlayable( discardPile.peek() ) ){
			//TODO 出せるカードのうち最初に見つかったカードを出す。ちゃんと考えて選んで出せるようにしよう。
			for( int i = 0; i < removableHandsList.size(); ++i ){
				if( removableHandsList.get( i ).booleanValue() ){
					//TODO setCardToDiscardPileメソッドを使おう
					discardPile.add( player.removeHands( i ) );
					if( player.getNumHands() == 1 ){
						logger.setLog( player.getName() + "「UNO!」" );
					}
					end = true;
					break;
				}
			}
		}else{
			drawCard( player );
			logger.setLog( player.getName() + "は山札から1枚引きました。" );
			end = true;
		}
		
		//TODO: NPCプレイヤーの処理
		return end;
	}

	/**
	 * カードを場に出す
	 * @param card プレイヤーの手札から出されたカード
	 */
	private void setCardToDiscardPile( List< Card > cards )
	{
		for( Card card : cards ){
			discardPile.add( card );
		}
	}

	/** プレイヤーに山札からカードを引かせる。山札が無くなったら繰り直す。 */
	public void drawCard( Player player )
	{
		//カードを引かせる
		player.drawCard( deck );

		if( deck.isEmpty() ){	//山札が無くなった
			Card top = discardPile.pop();	//場に見えているカードは山札に戻さない
			while( !discardPile.isEmpty() ){
				deck.add( discardPile.pop() );
			}
			discardPile.add( top );
			Collections.shuffle( deck );	//山札シャッフルして終了
		}
	}
}
