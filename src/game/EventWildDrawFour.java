package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import base.Button;
import base.ImageManager;
import base.MainPanel;
import base.MouseHitTestTask;

/**
 * WildDrawFourカードの効果
 * @author ばったもん
 */
public class EventWildDrawFour implements IEvent
{
	private enum Phase
	{
		CHOOSE_COLOR,
		SELECT_CHALLENGE,
		CHALLENGE,
	}

	/** 色選択画面クラス */
	private ChooseColor cc = null;
	/** チャレンジ決定画面クラス */
	private Challenge ch = null;
	/** 初期化フラグ */
	private boolean init = false;
	/** 現在のフェイズ */
	private Phase phase = Phase.CHOOSE_COLOR;
	/** 選択された色 */
	private Card.Color selectedColor = Card.Color.BLACK;
	/** チャレンジの決定フラグ */
	private boolean challenge = false;
	/** 判定済みフラグ */
	private boolean judged = false;
	/** チャレンジ成功フラグ */
	private boolean success = false;
	/** チャレンジウィンドウが出るまでのインターバルカウント */
	private int count = 0;

	/**
	 * 色選択画面を出してユーザーに選ばせる。<br>
	 * @return 色が選択されたらtrueを返す。
	 */
	@Override
	public boolean update( GameState state )
	{
		if( !init ){
			initialize();
		}
		Player cp = state.getCurrentPlayer();
		Player np = state.getNextPlayer();
		//ゲーム終了時なら処理を飛ばす
		if( cp.getNumHands() == 0 ){
			return true;
		}

		switch( phase ){
			case CHOOSE_COLOR:	//色選択フェイズ
			{
				boolean chosen = false;
				if( cp.isUser() ){	//ユーザーがワイルドドローフォーカードを出したとき
					if( cc != null ){
						cc.update();
						if( ( selectedColor = cc.getSelectedColor() ) != ChooseColor.NOT_SELECTED ){
							chosen = true;
						}
					}
				}else{	//NPCが出したとき
					selectedColor = cp.getAI().chooseColor( state );
					chosen = true;
				}
				if( chosen ){
					cc = null;
					//標準ルール。チャレンジ制度があるならチャレンジ選択フェイズへ。
					if( state.getRuleBook().challenge == RuleBook.RuleFlag.WITH ){
						phase = Phase.SELECT_CHALLENGE;
					}
					state.getLogger().setLog( state.getCurrentPlayer().getName() + "「ワイルドドロー4!![" + selectedColor + "]」" );
					//ローカルルール。チャレンジ制度がないならupdate()の役目を終える。
					if( state.getRuleBook().challenge == RuleBook.RuleFlag.WITHOUT ){
						return true;
					}
				}
				break;
			}
			case SELECT_CHALLENGE:	//チャレンジ決定フェイズ
				if( np.isUser() ){	//ユーザーがチャレンジの権利を持っているとき
					if( ch == null ){
						++count;	//ウィンドウを出すまで少し時間をおく
						if( count == GameState.WAITING_INTERVAL ){
							ch = new Challenge();
							count = 0;
						}
					}else{
						ch.update();
						Challenge.Pushed pushed = ch.getButtonPushed();
						if( pushed != Challenge.Pushed.NOT_PUSHED ){
							challenge = pushed == Challenge.Pushed.YES ? true : false;
							phase = Phase.CHALLENGE;
						}
					}
				}else{	//NPCがチャレンジの権利を持っているとき
					if( np.getAI().think() ){	//NPCの色選択までの猶予時間を取る
						challenge = np.getAI().triesChallenge();
						ch = null;
						phase = Phase.CHALLENGE;
					}
				}
				break;
			case CHALLENGE:
				if( challenge ){
					if( !judged ){
						Card.Color collectColor = state.getCollectColor();
						for( Card card: state.getCurrentPlayer().hands ){
							if( collectColor == card.color ){
								success = true;
								break;
							}
						}
						if( ch != null ){
							ch.setResult( success, collectColor, cp.hands );
							ch.showResult();
						}
						if( success ){
							state.getLogger().setLog( np.getName() + "「チャレンジ!!」　成功!!" );
						}else{
							state.getLogger().setLog( np.getName() + "「チャレンジ!!」　失敗!!" );
						}
						judged = true;
					}
					if( ch != null ){
						ch.update();
					}
					if( ch == null || ch.getButtonPushed() == Challenge.Pushed.OK ){
						ch = null;
						return true;
					}
				}else{
					state.getLogger().setLog( np.getName() +"はチャレンジしませんでした。" );
					return true;
				}
				break;
		}
		return false;
	}

	@Override
	public void draw( Graphics g )
	{
		if( cc != null ){
			cc.draw( g );
		}
		if( ch != null ){
			ch.draw( g );
		}
	}

	@Override
	public boolean hasEvent()
	{
		return true;
	}

	@Override
	public void activate( GameState state )
	{
		Player cp = state.getCurrentPlayer();
		Player np = state.getNextPlayer();

		if( cp.getNumHands() != 0 ){
			if( challenge ){	//チャレンジした時の処理
				if( success ){	//チャレンジ成功
					cp.obtainCard( state.takeBackCard() );	//WildDrawFourカードを手札に戻して
					for( int i = 0; i < 4; ++i ){
						state.drawCard( cp );	//4枚引ひかせる
					}
						state.getLogger().setLog( cp.getName() + "は出したカードを" );
						state.getLogger().setLog( "手札に戻し、更に4枚引きます。" );
				}else{	//チャレンジ失敗
					for( int i = 0; i < 6; ++i ){
						state.drawCard( np );	//6枚引かせる
					}
					state.setValidColor( selectedColor );
					state.advanceTurn();
					state.getLogger().setLog( np.getName() + "はペナルティとして6枚ひきます。" );
				}
			}else{
				for( int i = 0; i < 4; ++i ){
					state.drawCard( np );	//4枚引かせる
				}
				state.setValidColor( selectedColor );
				state.advanceTurn();
				state.getLogger().setLog( np.getName() + "は4枚引いてターン終了。" );
			}
			init = false;
		}else{
			//ゲーム終了時は４枚引かせて終了
			for( int i = 0; i < 4; ++i ){
				state.drawCard( np );
			}
		}
	}

	private void initialize()
	{
		cc = new ChooseColor();
		ch = null;
		init = true;
		phase = Phase.CHOOSE_COLOR;
		selectedColor = ChooseColor.NOT_SELECTED;
		challenge = false;
		judged = false;
		success = false;
		count = 0;
	}
}

/**
 * チャレンジするかどうかを決めるクラス<br>
 * EventWildDrawFourクラスでしか使わない(予定)なので同ファイル内にアクセス属性なしクラスで記述
 * @author ばったもん
 */
class Challenge
{
	/** 表示指示フラグ */
	public enum Showing{
		SELECT,
		RESULT,
	}
	/** どのボタンが押されたかのフラグ */
	public enum Pushed{
		YES,
		NO,
		OK,
		NOT_PUSHED,
	}
	private static final Dimension VIEW_SIZE = new Dimension( 400, 300 );
	private static final Point VIEW_POS = new Point( ( MainPanel.WIDTH - VIEW_SIZE.width ) / 2, 150 );
	private static final Rectangle CAPTION_AREA = new Rectangle( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height / 5 * 2 );
	private static final Rectangle SHOW_CARD_AREA = new Rectangle( VIEW_POS.x, CAPTION_AREA.y + CAPTION_AREA.height, VIEW_SIZE.width, VIEW_SIZE.height / 5 * 1 );
	private static final Dimension BUTTON_SIZE = new Dimension( 80, 50 );
	private static final Rectangle BUTTON_YES_AREA = new Rectangle( VIEW_POS.x + ( VIEW_SIZE.width / 2 - BUTTON_SIZE.width ) / 2, SHOW_CARD_AREA.y + SHOW_CARD_AREA.height + ( VIEW_SIZE.height - CAPTION_AREA.height - SHOW_CARD_AREA.height - BUTTON_SIZE.height ) / 2, BUTTON_SIZE.width, BUTTON_SIZE.height );
	private static final Rectangle BUTTON_NO_AREA = new Rectangle( VIEW_POS.x + VIEW_SIZE.width / 2 + ( VIEW_SIZE.width / 2 - BUTTON_SIZE.width ) / 2, BUTTON_YES_AREA.y, BUTTON_SIZE.width, BUTTON_SIZE.height );
	private static final Rectangle BUTTON_OK_AREA = new Rectangle( VIEW_POS.x + ( VIEW_SIZE.width - BUTTON_SIZE.width ) / 2, BUTTON_YES_AREA.y, BUTTON_SIZE.width, BUTTON_SIZE.height );
	/** Yesボタン */
	private Button buttonYes;
	/** Noボタン */
	private Button buttonNo;
	/** OKボタン */
	private Button buttonOK;
	/** 当たり判定 */
	private MouseHitTestTask task;
	/** 表示切り替えフラグ */
	private Showing show;
	/** チャレンジ成功フラグ */
	private boolean success;
	/** 比較対象色 */
	private Card.Color collectColor;
	/** 公開手札 */
	private List< Card > challengedHands;

	public Challenge()
	{
		buttonYes = new Button( BUTTON_YES_AREA );
		buttonNo = new Button( BUTTON_NO_AREA );
		buttonOK = null;
		task = new MouseHitTestTask();
		task.add( buttonYes );
		task.add( buttonNo );
		show = Showing.SELECT;
		success = false;
		challengedHands = null;
	}

	public void update()
	{
		task.hitTest();
		task.update();
	}

	public void draw( Graphics g )
	{
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		//背景を暗くする
		g.setColor( new Color( 0, 0, 0, 64 ) );
		g.fillRect( 0, 0, MainPanel.WIDTH, MainPanel.HEIGHT );	//画面全体を少し暗くする
		//表示画面枠
		g.setColor( Color.WHITE );
		g.fillRect( VIEW_POS.x, VIEW_POS.y, VIEW_SIZE.width, VIEW_SIZE.height );

		switch( show ){
			case SELECT:
				//キャプション
				g.setColor( Color.BLACK );
				g.setFont( new Font( prevFont.getName(), Font.PLAIN, 24 ) );
				ImageManager.drawString( g, "チャレンジしますか？", CAPTION_AREA.x, CAPTION_AREA.y, CAPTION_AREA.width, CAPTION_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
				//ボタン
				g.drawRect( BUTTON_YES_AREA.x, BUTTON_YES_AREA.y, BUTTON_YES_AREA.width, BUTTON_YES_AREA.height );
				g.drawRect( BUTTON_NO_AREA.x, BUTTON_NO_AREA.y, BUTTON_NO_AREA.width, BUTTON_NO_AREA.height );
				ImageManager.drawString( g, "はい", BUTTON_YES_AREA.x, BUTTON_YES_AREA.y, BUTTON_YES_AREA.width, BUTTON_YES_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
				ImageManager.drawString( g, "いいえ", BUTTON_NO_AREA.x, BUTTON_NO_AREA.y, BUTTON_NO_AREA.width, BUTTON_NO_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
				task.draw( g );
				break;
			case RESULT:
				//キャプション
				g.setColor( Color.BLACK );
				g.setFont( new Font( prevFont.getName(), Font.PLAIN, 24 ) );
				String textCaption = success ? "チャレンジ成功！！" : "チャレンジ失敗・・・";
				ImageManager.drawString( g, textCaption, CAPTION_AREA.x, CAPTION_AREA.y, CAPTION_AREA.width, CAPTION_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
				//カード
				final int colorFrameThickness = 10;	//カードを囲む枠の太さ
				final int sideSpace = 20;
				final int limitedWidth = VIEW_SIZE.width - sideSpace * 2 - colorFrameThickness * 2;
				final int showCardHeight = SHOW_CARD_AREA.height - colorFrameThickness * 2;
				final int showCardWidth = (int)( (double)showCardHeight / Card.HEIGHT * Card.WIDTH + 0.5 );
				final int numHands = challengedHands.size();
				int overlappedWidth = numHands * showCardWidth - limitedWidth;
				g.setColor( ChooseColor.getColor( collectColor ) );
				if( overlappedWidth > 0 ){	//重ねて表示
					//色背景
					g.fillRect( SHOW_CARD_AREA.x + sideSpace, SHOW_CARD_AREA.y, limitedWidth + colorFrameThickness * 2, SHOW_CARD_AREA.height );
					g.setColor( new Color( 0, 0, 0, 64 ) );	//色違いのカードを暗くするため
					int ox = SHOW_CARD_AREA.x + sideSpace + colorFrameThickness;
					int x = ox;
					int y = SHOW_CARD_AREA.y + colorFrameThickness; 
					for( int i = 0; i < numHands - 1; ++i ){
						Card c = challengedHands.get( i );
						ImageManager.draw( g, c.getImageHandle(), x, y, showCardWidth, showCardHeight );
						if( c.color != collectColor ){
							g.fillRect( x, y, showCardWidth, showCardHeight );
						}
						x = ox + showCardWidth * ( i + 1 ) - (int)( overlappedWidth / (double)( numHands - 1 ) * ( i + 1 ) );	//次のカードのx座標を計算しておく
					}
					//最後の1枚の微調整
					x = ox + limitedWidth - showCardWidth;
					Card lastCard = challengedHands.get( numHands - 1 );
					ImageManager.draw( g, lastCard.getImageHandle(), x, y, showCardWidth, showCardHeight );
					if( lastCard.color != collectColor ){
						g.fillRect( x, y, showCardWidth, showCardHeight );
					}
				}else{	//並べて表示
					//色背景
					g.fillRect( SHOW_CARD_AREA.x + ( SHOW_CARD_AREA.width - numHands * showCardWidth ) /2 - colorFrameThickness, SHOW_CARD_AREA.y, numHands * showCardWidth + colorFrameThickness * 2, SHOW_CARD_AREA.height );
					g.setColor( new Color( 0, 0, 0, 64 ) );	//色違いのカードを暗くするため
					int x = SHOW_CARD_AREA.x + ( SHOW_CARD_AREA.width - numHands * showCardWidth ) / 2;
					int y = SHOW_CARD_AREA.y + colorFrameThickness;
					for( Card c : challengedHands ){
						ImageManager.draw( g, c.getImageHandle(), x, y, showCardWidth, showCardHeight );
						if( c.color != collectColor ){
							g.fillRect( x, y, showCardWidth, showCardHeight );
						}
						x += showCardWidth;
					}
				}
				//ボタン
				g.setColor( Color.BLACK );
				g.drawRect( BUTTON_OK_AREA.x, BUTTON_OK_AREA.y, BUTTON_OK_AREA.width, BUTTON_OK_AREA.height );
				ImageManager.drawString( g, "OK", BUTTON_OK_AREA.x, BUTTON_OK_AREA.y, BUTTON_OK_AREA.width, BUTTON_OK_AREA.height, ImageManager.Align.CENTER, ImageManager.Align.CENTER );
				task.draw( g );
				break;
		}

		g.setColor( prevColor );
		g.setFont( prevFont );
	}

	public Pushed getButtonPushed()
	{
		if( buttonYes != null && buttonYes.isClicked() ){
			return Pushed.YES;
		}
		if( buttonNo != null && buttonNo.isClicked() ){
			return Pushed.NO;
		}
		if( buttonOK != null && buttonOK.isClicked() ){
			return Pushed.OK;
		}
		return Pushed.NOT_PUSHED;
	}

	public Showing getShowing()
	{
		return show;
	}

	public void setResult( boolean success, Card.Color color, List< Card > hands )
	{
		this.success = success;
		collectColor = color;
		challengedHands = hands;
	}

	public void showResult()
	{
		show = Showing.RESULT;
		task.removeAll();
		buttonYes = null;
		buttonNo = null;
		buttonOK = new Button( BUTTON_OK_AREA );
		task.add( buttonOK );
	}
}
