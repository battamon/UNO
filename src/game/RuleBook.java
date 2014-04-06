package game;

/**
 * ルールを記録したクラス<br>
 * Setupシーケンスで設定され、GameStateクラスで適用する。
 * @author ばったもん
 */
public class RuleBook
{
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

	public RuleBook()
	{
		rule = RuleFlag.OFFICIAL;
		deckDraw = RuleFlag.EVERYTIME;
		penalty = RuleFlag.WITH;
		challenge = RuleFlag.WITH;
		avoidDraw = RuleFlag.WITH;
		discardMultiple = RuleFlag.WITHOUT;
		discardMultipleCondition = RuleFlag.NUMBER;
		scoring = RuleFlag.OFFICIAL;
		scoringSystem = RuleFlag.SCORE;
		numPlayers = 2;
	}
}
