package game;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Stack;

import base.ImageManager;
import base.MainPanel;
import base.MouseHitTestTask;

/**
 * ゲーム本体クラス
 * @author ばったもん
 */
public class GameState
{
	//各種定義
	/** ゲーム進行フラグの定義 */
	public enum Phase{
		START,
		PLAYING,
		EVENT,
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
	private List< Player > players = null;
	/** ゲーム進行フラグ */
	private Phase phase = Phase.START;
	/** 現在の手番のプレイヤー番号 */
	private int whosePlayingIndex = 0;
	/** 順番の進む方向 */
	private boolean clockwise = true;
	/** TODO COM用フレームカウント。最終的に要らないかも */
	private int thinkingCount = 0;
	/** TODO Resultフェイズから抜けるカウント。ボタンクリックで抜けるようにする？ */
	private int waitingCount = 0;
	/** ログ表示クラス */
	private GameLog logger;
	/** ゲーム回数 */
	private int gameCount = 0;
	/** プレイヤーのインデックスをランク順に並べたもの */
	private List< Integer > rankIndices;

	//ここからメソッド
	static
	{
		hFrameImage = ImageManager.readImage( "resource/image/bg_playing.png" );
		hCardFrontImage = ImageManager.readImage( "resource/image/card_back.png" );
	}
	/**
	 * コンストラクタ
	 */
	public GameState()
	{
		logger = new GameLog();
	}

	public Phase update()
	{
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
			{
				Player p = getCurrentPlayer();
				int numBiforeHands = p.getNumHands();
				if( play( p ) ){	//行動が完了した
					int numAfterHands = p.getNumHands();
					//ゲームオーバー判定
					if( numAfterHands == 0 ){
						logger.setLog( p.getName() + "の手札が無くなりました。" );
						logger.setLog( "ゲーム終了。" );
						thinkingCount = 0;
						phase = Phase.GAME_OVER;
					//カード効果有無判定
					}else if( numAfterHands < numBiforeHands && getDiscardPileTop().event.hasEvent() ){
						phase = Phase.EVENT;
					//何も無ければ次の人へ
					}else{
						advanceTurn();
					}
				}
				break;
			}
			case EVENT:
				//TODO とりあえず即時発動
				getDiscardPileTop().event.activate( this );
				advanceTurn();
				phase = Phase.PLAYING;
				break;
			case GAME_OVER: //1ゲーム終了。RESULTフェイズまでのインターバル。
				++waitingCount;
				if( waitingCount == 60 ){
					waitingCount = 0;
					++gameCount;
					phase = Phase.RESULT;
				}
				break;
			case RESULT: //結果画面。シーケンス遷移が伴う。スコア計算と結果表示。
				if( waitingCount == 0 ){
					Player p = getCurrentPlayer();
					for( Player other : players ){
						p.stackScore( other.calcScore() );
					}
					sortRank();	//ランキングの更新
				}
				++waitingCount;
				if( waitingCount == Integer.MAX_VALUE ){
					waitingCount /= 2;	//オーバーフロー対策
				}
				//外部からのフェイズ移行命令でフェーズ移行する
				break;
		}

		return phase;
	}

	public void draw( Graphics g )
	{
		//背景
		ImageManager.draw( g, hFrameImage, 0, 0 );
		//山札
		ImageManager.draw( g, hCardFrontImage, DECK_AREA.x, DECK_AREA.y );
		//捨て場
		if( getDiscardPileTop() != null ){
			ImageManager.draw( g, getDiscardPileTop().getImageHandle(), DISCARD_PILE_AREA.x, DISCARD_PILE_AREA.y );
		}
		//ログ
		logger.view( g );

		//プレイヤー情報
		for( Player p : players ){
			p.draw( g );
		}
	}

	/**
	 * 結果表示する
	 * @param g 描画用オブジェクト
	 */
	public void drawResult( Graphics g )
	{
		//前準備
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		//リザルト画面のサイズ
		final Dimension viewSize = new Dimension( 440, 400 );
		final Point tablePos = new Point( 10, 60 );
		final int rowHeight = 30;	//一行の縦幅
		final int[] colWidths = { 30, 60, 180, 50, 50, 50 };	//一列の横幅

		//背景暗転処理
		g.setColor( new Color( 0, 0, 0, 128 ) );
		g.fillRect( 0, 0, MainPanel.WIDTH, MainPanel.HEIGHT );	//画面全体を少し暗くする
		g.setColor( prevColor );

		//リザルト画面の枠描画
		final Point viewPos = new Point( ( MainPanel.WIDTH - viewSize.width ) / 2, ( MainPanel.HEIGHT - viewSize.height ) / 2 );
		g.setColor( Color.WHITE );
		g.fillRect( viewPos.x, viewPos.y, viewSize.width, viewSize.height );
		g.setColor( Color.BLACK );
		g.drawRect( viewPos.x, viewPos.y, viewSize.width, viewSize.height );

		int x, y;
		//見出し
		String headline = new String( "第" + gameCount + "ゲーム終了" );
		g.setFont( new Font( prevFont.getFontName(), Font.PLAIN, 30 ) );
		Dimension headlineSize = ImageManager.getStringPixelSize( g, headline );
		x = viewPos.x + ( viewSize.width - headlineSize.width ) / 2;	//見出しが真ん中になるように
		y = viewPos.y;	//適当
		g.setColor( Color.ORANGE );
		ImageManager.drawString( g, headline, x, y );

		//プレイヤー情報
		List< String > captions = Arrays.asList( new String[]{ "順位", "名前", "終了時の手札", "換算点", "増減", "持ち点" } );
		g.setColor( Color.BLACK );
		g.setFont( new Font( prevFont.getFontName(), Font.PLAIN, 14 ) );
		x = viewPos.x + tablePos.x;
		y = viewPos.y + tablePos.y;
		for( int i = 0; i < captions.size(); ++i ){
			ImageManager.drawString( g, captions.get( i ), x, y, colWidths[ i ], rowHeight, ImageManager.Align.CENTER );
			x += colWidths[ i ];
		}
		y += rowHeight;
		//FIXME 順位が設定されてないので改善しましょう
		for( int i = 0; i < players.size(); ++i ){
			Player p = players.get( rankIndices.get( i ) );
			int colIndex = 0;
			String text = ( i + 1 ) + "";
			//順位
			g.setFont( new Font( prevFont.getFontName(), Font.PLAIN, 18 ) );
			x = viewPos.x + tablePos.x;
			ImageManager.drawString( g, text, x, y, colWidths[ colIndex ], rowHeight, ImageManager.Align.CENTER );
			//名前
			g.setFont( new Font( prevFont.getFontName(), Font.PLAIN, 14 ) );
			x += colWidths[ colIndex++ ];
			ImageManager.drawString( g, p.getName(), x, y, colWidths[ colIndex ], rowHeight, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
			//手札
			x += colWidths[ colIndex++ ];
			final int viewCardHeight = rowHeight - 2;
			final int viewCardWidth = (int)( (double)viewCardHeight / Card.HEIGHT * Card.WIDTH + 0.5 );
			//FIXME カードを並べる。このままだとカード枚数が多いとはみ出す。
			int cx = x + 10;
			for( Card c : p.hands ){
				ImageManager.draw( g, c.getImageHandle(), cx, y + 2, viewCardWidth, viewCardHeight );
				cx += viewCardWidth;
			}
			//換算点
			x += colWidths[ colIndex++ ];
			ImageManager.drawString( g, p.calcScore() + "", x, y, colWidths[ colIndex ], rowHeight, ImageManager.Align.RIGHT, ImageManager.Align.CENTER );
			//増減
			x += colWidths[ colIndex++ ];
			if( p.getFluctuationPoint() != 0 ){
				char operation = p.getFluctuationPoint() > 0 ? '+' : '-';
				ImageManager.drawString( g, "" + operation + p.getFluctuationPoint(), x, y, colWidths[ colIndex ], rowHeight, ImageManager.Align.RIGHT, ImageManager.Align.CENTER );
			}
			//持ち点
			x += colWidths[ colIndex++ ];
			ImageManager.drawString( g, "" + p.getScore(), x, y, colWidths[ colIndex ], rowHeight, ImageManager.Align.RIGHT, ImageManager.Align.CENTER );

			y += rowHeight;
		}

		g.setColor( prevColor );
		g.setFont( prevFont );
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
		for( int i = 1; i < numPlayers; ++i ){
			players.add( new Player( "NPC_" + nameNumber++ ) );
		}
		//山札領域の確保・山札の構築
		deck = new Stack< Card >();
		createShuffledDeck( deck );
		//捨て場領域の確保
		discardPile = new Stack< Card >();
		//ランク保存領域確保
		rankIndices = new ArrayList< Integer >();
		for( int i = 0; i < players.size(); ++i ){
			rankIndices.add( new Integer( i ) );
		}
	}

	private void reset()
	{
		//TODO カードの回収。いったん全部消去して再生成する方法もある。
		while( !discardPile.isEmpty() ){
			deck.add( discardPile.pop() );
		}
		for( Player p : players ){
			List< Card > cards = p.prepareNextGame();
			while( !cards.isEmpty() ){
				deck.add( cards.remove( 0 ) );
			}
		}
		clockwise = true;
		Collections.shuffle( deck );
		logger.clear();
		thinkingCount = 0;
		waitingCount = 0;
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
		IEvent[] symbolEvents = { new EventReverse(), new EventSkip(), new EventDrawTwo() };
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
		List< Boolean > removableHandsList = user.isRemovableCards( getDiscardPileTop() );
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

		if( end ){
			user.clearRemovablesCardFlags();	//手番が終わったらフラグ情報は消しておく。
		}

		return end;
	}

	private boolean playNPC( Player player )
	{
		//TODO 手番が回ってきたら少し待つ。アニメーション入れたらこの処理も要らなくなる
		++thinkingCount;
		if( thinkingCount < 30 ) return false;
		thinkingCount = 0;

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

	/** 次のプレイヤーを示すインデックスを返す。 */
	private int getNextPlayerIndex(){
		return clockwise ? ( whosePlayingIndex + 1 ) % players.size() : ( whosePlayingIndex - 1 + players.size() ) % players.size();
	}

	/** 順番の方向を切り替える */
	public void switchOrderDirection()
	{
		clockwise = !clockwise;
	}

	/** ログオブジェクトを取得する */
	public GameLog getLogger()
	{
		return logger;
	}

	/** 現在の手番のプレイヤーオブジェクトを取得する */
	public Player getCurrentPlayer()
	{
		return players.get( whosePlayingIndex );
	}

	/** 次の手番のプレイヤーオブジェクトを取得する */
	public Player getNextPlayer(){
		return players.get( getNextPlayerIndex() );
	}

	/** 手番を次へ回す */
	public void advanceTurn()
	{
		whosePlayingIndex = getNextPlayerIndex();
	}

	/** 捨て場の一番上にあるカードを返す */
	public Card getDiscardPileTop()
	{
		if( discardPile.isEmpty() ){
			return null;
		}
		return discardPile.peek();
	}

	/** シーケンス系クラスなどの外部からフェイズを進める */
	public void advancePhase()
	{
		switch( phase ){
			case RESULT:
				phase = Phase.START;
		}
	}

	/** ランキングを並べなおす */
	private void sortRank()
	{
		List< Point > pairs = new ArrayList< Point >();
		for( int i = 0; i < players.size(); ++i ){
			//プレイヤーのインデックスとスコアの数値ペアをPointクラスで表現
			pairs.add( new Point( players.get( i ).getScore(), i ) );
		}
		//ソート
		for( int i = 0; i < pairs.size() - 1; ++i ){
			for( int j = i + 1; j < pairs.size(); ++j ){
				Point p0 = pairs.get( i );
				Point p1 = pairs.get( j );
				if( p0.x < p1.x ){	//点が低いほうが後ろ
					pairs.set( i, p1 );
					pairs.set( j, p0 );
				}else if( p0.x == p1.x ){	//同点なら
					if( p0.y > p1.y ){	//プレイヤーインデックスの若い方が前
						pairs.set( i, p1 );
						pairs.set( j, p0 );
					}
				}
			}
		}
		for( int i = 0; i < rankIndices.size(); ++i ){
			rankIndices.set( i, pairs.get( i ).y );
		}
	}
}
