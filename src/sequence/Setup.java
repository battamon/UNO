package sequence;

import game.RuleBook;
import game.RuleBook.RuleFlag;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.Button;
import base.ISequence;
import base.ImageManager;
import base.MouseHitTestTask;
import base.RadioButton;

/**
 * ローカルルールなどを設定するシーケンス
 * @author ばったもん
 */
public class Setup implements ISequence
{
	/** 戻るボタン領域 */
	private static final Rectangle BUTTON_RETURN_AREA = new Rectangle( 430, 420, 180, 40 );
	/** 人数ダウンボタン領域 */
	private static final Rectangle BUTTON_DOWN_AREA = new Rectangle( 472, 367, 21, 18 );
	/** 人数アップボタン領域 */
	private static final Rectangle BUTTON_UP_AREA = new Rectangle( 543, 367, 21, 18 );
	/** 一行の高さ */
	private static final int ROW_HEIGHT = 20;
	/** 行間の高さ */
	private static final int ROW_SPACE = 3;
	/** 横並びのラジオボタンの余裕幅 */
	private static final int COL_SPACE = 4;
	/** ラジオボタンの位置 */
	private static final Rectangle RB_RULE_AREA_OFFICIAL = new Rectangle( 39, 86, 62, ROW_HEIGHT );
	private static final Rectangle RB_RULE_AREA_JAPAN = new Rectangle( RB_RULE_AREA_OFFICIAL.x, RB_RULE_AREA_OFFICIAL.y + RB_RULE_AREA_OFFICIAL.height + ROW_SPACE, 62, ROW_HEIGHT );
	private static final Rectangle RB_RULE_AREA_CUSTOM = new Rectangle( RB_RULE_AREA_OFFICIAL.x, RB_RULE_AREA_JAPAN.y + RB_RULE_AREA_JAPAN.height + ROW_SPACE, 88, ROW_HEIGHT );
	private static final Rectangle RB_RULE_DECK_DRAW_AREA_EVERYTIME = new Rectangle( 166, RB_RULE_AREA_JAPAN.y , 88, ROW_HEIGHT );
	private static final Rectangle RB_RULE_DECK_DRAW_AREA_LIMITED = new Rectangle( RB_RULE_DECK_DRAW_AREA_EVERYTIME.x, RB_RULE_DECK_DRAW_AREA_EVERYTIME.y + RB_RULE_DECK_DRAW_AREA_EVERYTIME.height + ROW_SPACE, 193, ROW_HEIGHT );
	private static final Rectangle RB_RULE_PENALTY_AREA_WITH = new Rectangle( RB_RULE_DECK_DRAW_AREA_LIMITED.x, RB_RULE_DECK_DRAW_AREA_LIMITED.y + RB_RULE_DECK_DRAW_AREA_LIMITED.height + ROW_HEIGHT + ROW_SPACE * 2, 51, ROW_HEIGHT );
	private static final Rectangle RB_RULE_PENALTY_AREA_WITHOUT = new Rectangle( RB_RULE_PENALTY_AREA_WITH.x, RB_RULE_PENALTY_AREA_WITH.y + ROW_HEIGHT + ROW_SPACE, 51, ROW_HEIGHT );
	private static final Rectangle RB_RULE_CHALLENGE_AREA_WITH = new Rectangle( 402, RB_RULE_DECK_DRAW_AREA_EVERYTIME.y, 51, ROW_HEIGHT );
	private static final Rectangle RB_RULE_CHALLENGE_AREA_WITHOUT = new Rectangle( RB_RULE_CHALLENGE_AREA_WITH.x, RB_RULE_DECK_DRAW_AREA_LIMITED.y, 51, ROW_HEIGHT );
	private static final Rectangle RB_RULE_AVOID_DRAW_AREA_WITH = new Rectangle( RB_RULE_CHALLENGE_AREA_WITH.x, RB_RULE_PENALTY_AREA_WITH.y, 51, ROW_HEIGHT );
	private static final Rectangle RB_RULE_AVOID_DRAW_AREA_WITHOUT = new Rectangle( RB_RULE_AVOID_DRAW_AREA_WITH.x, RB_RULE_PENALTY_AREA_WITHOUT.y, 51, 20 );
	private static final Rectangle RB_RULE_DISCARD_MULTIPLE_AREA_WITHOUT = new Rectangle( RB_RULE_PENALTY_AREA_WITHOUT.x, RB_RULE_PENALTY_AREA_WITHOUT.y + RB_RULE_PENALTY_AREA_WITHOUT.height + ROW_HEIGHT + ROW_SPACE * 2, 51, 20 );
	private static final Rectangle RB_RULE_DISCARD_MULTIPLE_AREA_LIMITED = new Rectangle( RB_RULE_DISCARD_MULTIPLE_AREA_WITHOUT.x + RB_RULE_DISCARD_MULTIPLE_AREA_WITHOUT.width + COL_SPACE, RB_RULE_DISCARD_MULTIPLE_AREA_WITHOUT.y, 95, ROW_HEIGHT );
	private static final Rectangle RB_RULE_DISCARD_MULTIPLE_AREA_ALL = new Rectangle( RB_RULE_DISCARD_MULTIPLE_AREA_LIMITED.x + RB_RULE_DISCARD_MULTIPLE_AREA_LIMITED.width + COL_SPACE, RB_RULE_DISCARD_MULTIPLE_AREA_LIMITED.y, 77, ROW_HEIGHT );
	private static final Rectangle RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER = new Rectangle( 182, RB_RULE_DISCARD_MULTIPLE_AREA_ALL.y + RB_RULE_DISCARD_MULTIPLE_AREA_ALL.height + ROW_HEIGHT + ROW_SPACE * 2, 115, ROW_HEIGHT );
	private static final Rectangle RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER_AND_COLOR = new Rectangle( RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER.x + RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER.width + COL_SPACE, RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER.y, 149, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_AREA_OFFICIAL = new Rectangle( RB_RULE_AREA_OFFICIAL.x, 354, 62, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_AREA_JAPAN = new Rectangle( RB_SCORING_AREA_OFFICIAL.x, RB_SCORING_AREA_OFFICIAL.y + RB_SCORING_AREA_OFFICIAL.height + ROW_SPACE, 62, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_AREA_CUSTOM = new Rectangle( RB_SCORING_AREA_JAPAN.x, RB_SCORING_AREA_JAPAN.y + RB_SCORING_AREA_JAPAN.height + ROW_SPACE, 62, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_SYSTEM_AREA_SCORE = new Rectangle( 150, RB_SCORING_AREA_OFFICIAL.y, 89, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_SYSTEM_AREA_ROUND = new Rectangle( RB_SCORING_SYSTEM_AREA_SCORE.x, RB_SCORING_SYSTEM_AREA_SCORE.y + RB_SCORING_SYSTEM_AREA_SCORE.height + ROW_SPACE, 103, ROW_HEIGHT );
	private static final Rectangle RB_SCORING_SYSTEM_AREA_MIX = new Rectangle( RB_SCORING_SYSTEM_AREA_ROUND.x, RB_SCORING_SYSTEM_AREA_ROUND.y + RB_SCORING_SYSTEM_AREA_ROUND.height + ROW_SPACE, 59, ROW_HEIGHT );
	/** ラジオボタンのボタン画像表示位置(相対位置) */
	private static final Point RB_IMAGE_POS = new Point( 2, 2 );
	/** 参加人数の描画位置 */
	private static final Rectangle NUM_PLAYERS_AREA = new Rectangle( 494, 364, 28, ROW_HEIGHT );

	/** 画像ハンドル */
	private static int hSetupImage = ImageManager.NO_HANDLE;

	/** 戻るボタン */
	private Button buttonReturn = null;
	/** 人数ダウンボタン */
	private Button buttonDown = null;
	/** 人数アップボタン */
	private Button buttonUp = null;
	/** 通常ボタン用当たり判定タスク */
	private MouseHitTestTask hitTestTask = null;
	/** ラジオボタン群 */
	private RadioButton rbRule;
	private RadioButton rbRuleDeckDraw;
	private RadioButton rbRulePenalty;
	private RadioButton rbRuleChallenge;
	private RadioButton rbRuleAvoidDraw;
	private RadioButton rbRuleDiscardMultiple;
	private RadioButton rbRuleDiscardMultibleCondition;
	private RadioButton rbScoring;
	private RadioButton rbScoringSystem;
	/** ラジオボタン階層 */
	private Map< RadioButton, List< RadioButton > > rbTree;
	/** 参加人数 */
	private int numPlayers;
	/** ルールブック */
	private RuleBook ruleBook;

	public Setup( RuleBook ruleBook )
	{
		this.ruleBook= ruleBook; 
		//背景
		hSetupImage = ImageManager.readImage( "resource/image/setup.png" );
		//戻る・人数ボタン
		buttonReturn = new Button( BUTTON_RETURN_AREA );
		buttonDown = new Button( BUTTON_DOWN_AREA );
		buttonUp = new Button( BUTTON_UP_AREA );
		hitTestTask = new MouseHitTestTask();
		hitTestTask.add( buttonReturn );
		hitTestTask.add( buttonDown );
		hitTestTask.add( buttonUp );
		//各ラジオボタン
		rbRule = new RadioButton();
		rbRule.addButton( RB_RULE_AREA_OFFICIAL, RB_IMAGE_POS );
		rbRule.addButton( RB_RULE_AREA_JAPAN, RB_IMAGE_POS );
		rbRule.addButton( RB_RULE_AREA_CUSTOM, RB_IMAGE_POS );
		rbRuleDeckDraw = new RadioButton();
		rbRuleDeckDraw.addButton( RB_RULE_DECK_DRAW_AREA_EVERYTIME, RB_IMAGE_POS );
		rbRuleDeckDraw.addButton( RB_RULE_DECK_DRAW_AREA_LIMITED, RB_IMAGE_POS );
		rbRulePenalty = new RadioButton();
		rbRulePenalty.addButton( RB_RULE_PENALTY_AREA_WITH, RB_IMAGE_POS );
		rbRulePenalty.addButton( RB_RULE_PENALTY_AREA_WITHOUT, RB_IMAGE_POS );
		rbRuleChallenge = new RadioButton();
		rbRuleChallenge.addButton( RB_RULE_CHALLENGE_AREA_WITH, RB_IMAGE_POS );
		rbRuleChallenge.addButton( RB_RULE_CHALLENGE_AREA_WITHOUT, RB_IMAGE_POS );
		rbRuleAvoidDraw = new RadioButton();
		rbRuleAvoidDraw.addButton( RB_RULE_AVOID_DRAW_AREA_WITH, RB_IMAGE_POS );
		rbRuleAvoidDraw.addButton( RB_RULE_AVOID_DRAW_AREA_WITHOUT, RB_IMAGE_POS );
		rbRuleDiscardMultiple = new RadioButton();
		rbRuleDiscardMultiple.addButton( RB_RULE_DISCARD_MULTIPLE_AREA_WITHOUT, RB_IMAGE_POS );
		rbRuleDiscardMultiple.addButton( RB_RULE_DISCARD_MULTIPLE_AREA_LIMITED, RB_IMAGE_POS );
		rbRuleDiscardMultiple.addButton( RB_RULE_DISCARD_MULTIPLE_AREA_ALL, RB_IMAGE_POS );
		rbRuleDiscardMultibleCondition = new RadioButton();
		rbRuleDiscardMultibleCondition.addButton( RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER, RB_IMAGE_POS );
		rbRuleDiscardMultibleCondition.addButton( RB_RULE_DISCARD_MULTIPLE_CONDITION_AREA_NUMBER_AND_COLOR, RB_IMAGE_POS );
		rbScoring = new RadioButton();
		rbScoring.addButton( RB_SCORING_AREA_OFFICIAL, RB_IMAGE_POS );
		rbScoring.addButton( RB_SCORING_AREA_JAPAN, RB_IMAGE_POS );
		rbScoring.addButton( RB_SCORING_AREA_CUSTOM, RB_IMAGE_POS);
		rbScoringSystem = new RadioButton();
		rbScoringSystem.addButton( RB_SCORING_SYSTEM_AREA_SCORE, RB_IMAGE_POS );
		rbScoringSystem.addButton( RB_SCORING_SYSTEM_AREA_ROUND, RB_IMAGE_POS );
		rbScoringSystem.addButton( RB_SCORING_SYSTEM_AREA_MIX, RB_IMAGE_POS );
		//階層生成
		rbTree = new HashMap< RadioButton, List< RadioButton > >();
		rbTree.put( rbRule, Arrays.asList( rbRuleDeckDraw, rbRulePenalty, rbRuleChallenge, rbRuleAvoidDraw, rbRuleDiscardMultiple ) );
		rbTree.put( rbRuleDiscardMultiple, Arrays.asList( rbRuleDiscardMultibleCondition ) );
		rbTree.put( rbScoring, Arrays.asList( rbScoringSystem ) );
		readRuleBook();
	}

	@Override
	public int update( ISequence parent )
	{
		//当たり判定
		hitTestTask.hitTest();
		//各オブジェクト更新
		hitTestTask.update();
		if( buttonDown.isClicked() ){
			--numPlayers;
			if( numPlayers < 2 ){
				numPlayers = 2;
			}
		}
		if( buttonUp.isClicked() ){
			++numPlayers;
			if( numPlayers > 10 ){
				numPlayers = 10;
			}
		}
		//ラジオボタン更新 TODO ルール設定によって設定可能な項目を絞ろう
		List< RadioButton > updateList = new ArrayList< RadioButton >();
		updateList.add( rbRule );
		updateList.add( rbScoring );
		while( !updateList.isEmpty() ){
			//リストからひとつ取り出して
			RadioButton rb = updateList.remove( 0 );
			//子が居たらリストに追加
			if( rbTree.containsKey( rb ) ){
				List< RadioButton > childList = rbTree.get( rb );
				for( RadioButton child : childList ){
					updateList.add( child );
				}
			}
			//親を更新
			rb.update();
		}

		//シーケンス遷移
		int next = RootParent.NEXT_SEQUENCE_DEFAULT;
		if( buttonReturn.isClicked() ){
			writeRuleBook();
			next = RootParent.NEXT_SEQUENCE_TITLE;
		}
		return next;
	}

	@Override
	public void render( Graphics g )
	{
		//戻るボタン描画
		ImageManager.draw( g, hSetupImage, 0, 0 );
		hitTestTask.draw( g );
		//ラジオボタン描画
		List< RadioButton > drawList = new ArrayList< RadioButton >();
		drawList.add( rbRule );
		drawList.add( rbScoring );
		while( !drawList.isEmpty() ){
			//リストからひとつ取り出して
			RadioButton rb = drawList.remove( 0 );
			//子が居たらリストに追加
			if( rbTree.containsKey( rb ) ){
				List< RadioButton > childList = rbTree.get( rb );
				for( RadioButton child : childList ){
					drawList.add( child );
				}
			}
			//親を描画
			rb.draw( g );
		}
		//参加人数描画
		Color prevColor = g.getColor();
		Font prevFont = g.getFont();
		g.setColor( Color.BLACK );
		g.setFont( new Font( prevFont.getName(), Font.PLAIN, 14 ) );
		ImageManager.drawString( g, numPlayers + "", NUM_PLAYERS_AREA.x, NUM_PLAYERS_AREA.y, NUM_PLAYERS_AREA.width, NUM_PLAYERS_AREA.height, ImageManager.Align.RIGHT );
		g.setFont( prevFont );
		g.setColor( prevColor );
	}

	@Override
	public void destroy()
	{
		hitTestTask.removeAll();
	}

	private void readRuleBook()
	{
		numPlayers = ruleBook.numPlayers;
		switch( ruleBook.rule ){
			case OFFICIAL: rbRule.on( 0 ); break;
			case JAPAN:    rbRule.on( 1 ); break;
			case CUSTOM:   rbRule.on( 2 ); break;
		}
		switch( ruleBook.deckDraw ){
			case EVERYTIME: rbRuleDeckDraw.on( 0 ); break;
			case LIMITED:   rbRuleDeckDraw.on( 1 ); break;
		}
		switch( ruleBook.penalty ){
			case WITH:    rbRulePenalty.on( 0 ); break;
			case WITHOUT: rbRulePenalty.on( 1 ); break;
		}
		switch( ruleBook.challenge ){
			case WITH:    rbRuleChallenge.on( 0 ); break;
			case WITHOUT: rbRuleChallenge.on( 1 ); break;
		}
		switch( ruleBook.avoidDraw ){
			case WITH:    rbRuleAvoidDraw.on( 0 ); break;
			case WITHOUT: rbRuleAvoidDraw.on( 1 ); break;
		}
		switch( ruleBook.discardMultiple ){
			case WITHOUT: rbRuleDiscardMultiple.on( 0 ); break;
			case LIMITED: rbRuleDiscardMultiple.on( 1 ); break;
			case ALL:     rbRuleDiscardMultiple.on( 2 ); break;
		}
		switch( ruleBook.discardMultipleCondition ){
			case NUMBER:           rbRuleDiscardMultibleCondition.on( 0 ); break;
			case NUMBER_AND_COLOR: rbRuleDiscardMultibleCondition.on( 1 ); break;
		}
		switch( ruleBook.scoring ){
			case OFFICIAL: rbScoring.on( 0 ); break;
			case JAPAN:    rbScoring.on( 1 ); break;
			case CUSTOM:   rbScoring.on( 2 ); break;
		}
		switch( ruleBook.scoringSystem ){
			case SCORE: rbScoringSystem.on( 0 ); break;
			case ROUND: rbScoringSystem.on( 1 ); break;
			case MIX:   rbScoringSystem.on( 2 ); break;
		}
	}

	private void writeRuleBook()
	{
		ruleBook.numPlayers = numPlayers;
		switch( rbRule.getOn() ){
			case 0: ruleBook.rule = RuleFlag.OFFICIAL; break;
			case 1: ruleBook.rule = RuleFlag.JAPAN; break;
			case 2: ruleBook.rule = RuleFlag.CUSTOM; break;
		}
		switch( rbRuleDeckDraw.getOn() ){
			case 0: ruleBook.deckDraw = RuleFlag.EVERYTIME; break;
			case 1: ruleBook.deckDraw = RuleFlag.LIMITED; break;
		}
		switch( rbRulePenalty.getOn() ){
			case 0: ruleBook.penalty =RuleFlag.WITH; break;
			case 1: ruleBook.penalty =RuleFlag.WITHOUT; break;
		}
		switch( rbRuleChallenge.getOn() ){
			case 0: ruleBook.challenge = RuleFlag.WITH; break;
			case 1: ruleBook.challenge = RuleFlag.WITHOUT; break;
		}
		switch( rbRuleAvoidDraw.getOn() ){
			case 0: ruleBook.avoidDraw = RuleFlag.WITH; break;
			case 1: ruleBook.avoidDraw = RuleFlag.WITHOUT; break;
		}
		switch( rbRuleDiscardMultiple.getOn() ){
			case 0: ruleBook.discardMultiple = RuleFlag.WITHOUT; break;
			case 1: ruleBook.discardMultiple = RuleFlag.LIMITED; break;
			case 2: ruleBook.discardMultiple = RuleFlag.ALL; break;
		}
		switch( rbRuleDiscardMultibleCondition.getOn() ){
			case 0: ruleBook.discardMultipleCondition = RuleFlag.NUMBER; break;
			case 1: ruleBook.discardMultipleCondition = RuleFlag.NUMBER_AND_COLOR; break;
		}
		switch( rbScoring.getOn() ){
			case 0: ruleBook.scoring = RuleFlag.OFFICIAL; break;
			case 1: ruleBook.scoring = RuleFlag.JAPAN; break;
			case 2: ruleBook.scoring = RuleFlag.CUSTOM; break;
		}
		switch( rbScoringSystem.getOn() ){
			case 0: ruleBook.scoringSystem = RuleFlag.SCORE; break;
			case 1: ruleBook.scoringSystem = RuleFlag.ROUND; break;
			case 2: ruleBook.scoringSystem = RuleFlag.MIX; break;
		}
	}
}
