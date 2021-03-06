package game;

/**
 * ルールを記録したクラス<br>
 * Setupシーケンスで設定され、GameStateクラスで適用する。
 * @author ばったもん
 */
public class RuleBook
{
	/** プリセット */
	public enum Preset{
		OFFICIAL,
		JAPAN,
		MIX,
	}
	/** 設定群 */
	public enum RuleFlag{
		OFFICIAL,	//採点ルール、採点方式
		JAPAN,		//採点ルール、採点方式
		CUSTOM,		//採点ルール、採点方式
		EVERYTIME,	//山札を引ける条件
		LIMITED,	//山札を引ける条件、複数枚同時出し
		WITH,		//役上がりペナルティ、チャレンジ制度、ドロー系回避
		WITHOUT,	//役上がりペナルティ、チャレンジ制度、ドロー系回避、複数枚同時出し
		ALL,		//複数枚同時出し
		NUMBER,		//同時出しの条件
		NUMBER_AND_COLOR,	//同時出しの条件
		SCORE,		//採点システム
		ROUND,		//採点システム
		MIX,		//採点システム
	}
	/** 採用ルール */
	public RuleFlag rule;
	/** 山札を引ける条件 */
	public RuleFlag deckDraw;
	/** 役上がりペナルティ */
	public RuleFlag penalty;
	/** チャレンジ制度 */
	public RuleFlag challenge;
	/** ドロー系回避 */
	public RuleFlag avoidDraw;
	/** 複数枚同時出し */
	public RuleFlag discardMultiple;
	/** 同時出しの条件 */
	public RuleFlag discardMultipleCondition;
	/** 採点方式 */
	public RuleFlag scoring;
	/** 採点システム */
	public RuleFlag scoringSystem;
	/** プレイヤー人数 */
	public int numPlayers;
	/** 採点方式・スコア制 */
	public int score;
	/** 採点方式・ラウンド制 */
	public int round;
	/** 採点方式・混合(スコア) */
	public int mixScore;
	/** 採点方式・混合(ラウンド) */
	public int mixRound;

	public RuleBook()
	{
		rule = RuleFlag.OFFICIAL;
		deckDraw = RuleFlag.LIMITED;
		penalty = RuleFlag.WITHOUT;
		challenge = RuleFlag.WITH;
		avoidDraw = RuleFlag.WITHOUT;
		discardMultiple = RuleFlag.WITHOUT;
		discardMultipleCondition = RuleFlag.NUMBER;
		scoring = RuleFlag.OFFICIAL;
		scoringSystem = RuleFlag.SCORE;
		numPlayers = 2;
		score = 500;
	}

	public RuleBook( RuleBook rb )
	{
		rule = rb.rule;
		deckDraw = rb.deckDraw;
		penalty = rb.penalty;
		challenge = rb.challenge;
		avoidDraw = rb.avoidDraw;
		discardMultiple = rb.discardMultiple;
		discardMultipleCondition = rb.discardMultipleCondition;
		scoring = rb.scoring;
		scoringSystem = rb.scoringSystem;
		numPlayers = rb.numPlayers;
		score = rb.score;
		round = rb.round;
		mixScore = rb.mixScore;
		mixRound = rb.mixRound;
	}

	public void setPresetRule( Preset set )
	{
		switch( set ){
			case OFFICIAL:
				rule = RuleFlag.OFFICIAL;
				deckDraw = RuleFlag.LIMITED;
				penalty = RuleFlag.WITHOUT;
				challenge = RuleFlag.WITH;
				avoidDraw = RuleFlag.WITHOUT;
				discardMultiple = RuleFlag.WITHOUT;
				discardMultipleCondition = RuleFlag.NUMBER;
				break;
			case JAPAN:
				rule = RuleFlag.JAPAN;
				deckDraw = RuleFlag.EVERYTIME;
				penalty = RuleFlag.WITHOUT;
				challenge = RuleFlag.WITH;
				avoidDraw = RuleFlag.WITH;
				discardMultiple = RuleFlag.LIMITED;
				discardMultipleCondition = RuleFlag.NUMBER;
				break;
		}
	}

	public void setPresetScoring( Preset set )
	{
		switch( set ){
			case OFFICIAL:
				scoring = RuleFlag.OFFICIAL;
				scoringSystem = RuleFlag.SCORE;
				score = 500;
				round = 0;
				mixScore = 0;
				mixRound = 0;
				break;
			case JAPAN:
				scoring = RuleFlag.JAPAN;
				scoringSystem = RuleFlag.ROUND;
				score = 0;
				round = 5;
				mixScore = 0;
				mixRound = 0;
				break;
			case MIX:
				scoring = RuleFlag.MIX;
				scoringSystem = RuleFlag.MIX;
				score = 0;
				round = 0;
				mixScore = 500;
				mixRound = 5;
				break;
		}
	}
}
