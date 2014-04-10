package sequence.game;

import base.ISequence;
import game.RuleBook;
import sequence.Setup;

/**
 * ゲームの途中でルールを確認する画面に移る
 * @author ばったもん
 */
public class ShowRule extends Setup
{
	public ShowRule( RuleBook ruleBook )
	{
		super( ruleBook );
		changeable = false;
	}

	/**
	 * 継承元のupdateをオーバーライドすることで、設定変更に関わる処理を無視した実装に変える。
	 * ちなみにrenderメソッドは継承元のものをそのまま使う。changeableフラグで表示自体は変わっている。
	 */
	@Override
	public int update( ISequence parent )
	{
		int next = GameParent.NEXT_SEQUENCE_DEFAULT;
		//ボタンの更新
		taskReturn.hitTest();
		taskReturn.update();
		//シーケンス遷移
		if( buttonReturn.isClicked() ){
			next = GameParent.NEXT_SEQUENCE_MENU;	//メニューに戻る
		}
		return next;
	}
}
