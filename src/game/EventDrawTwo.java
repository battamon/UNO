package game;

import java.awt.Graphics;

public class EventDrawTwo implements IEvent
{
	/** 相手に引かせる枚数 */
	private static final int PENALTY_DRAW = 2;
	/** ドロー回避確認ウィンドウ */
	private AvoidDrawWindow ad;

	@Override
	public boolean update( GameState state )
	{
		boolean ret = false;
		Player cp = state.getCurrentPlayer();
		Player np = state.getNextPlayer();
		int stackCount = state.getDiscardCount();

		if( state.getRuleBook().avoidDraw == RuleBook.RuleFlag.WITH ){	//ドロー回避可能ルールの場合
			if( cp.getNumHands() != 0 && np.isAvoidableDraw( state.getCurrentValidGlyph() ) ){	//回避可能手段がある
				if( np.isUser() ){	//カードを引かされるプレイヤーがユーザーだったら
					if( ad == null ){
						ad = new AvoidDrawWindow();
					}
					ad.update();
					AvoidDrawWindow.Pushed pushed = ad.getButtonPushed();
					if( pushed != AvoidDrawWindow.Pushed.NOT_PUSHED ){	//何らかのボタンが押された
						if( pushed == AvoidDrawWindow.Pushed.YES ){
							state.stackPenaltyDrawCount( PENALTY_DRAW * stackCount );	//YESボタンならドロー回避宣言
						}
						if( pushed == AvoidDrawWindow.Pushed.NO ){
							state.resetAvoidDrawFlag();
						}
						ad = null;
						ret = true;
					}
				}else{	//カードを引かされるプレイヤーがNPCだったら
					if( np.getAI().decideAvoidDraw() ){
						//AIがドロー回避すると判断した場合
						state.stackPenaltyDrawCount( PENALTY_DRAW * stackCount );
					}else{
						state.resetAvoidDrawFlag();
					}
					ret = true;
				}
			}else{	//回避手段が無い場合
				ret = true;
				state.resetAvoidDrawFlag();
			}
		}else{	//ドロー回避ルールが採用されていない場合
			ret = true;
		}

		if( ret ){
			String text = cp.getName() + "「ドロー2" + ( stackCount > 1 ? ( "x" + stackCount ) : "" ) + "!!」";
			state.getLogger().setLog( text );
		}

		return ret;
	}

	@Override
	public void draw( Graphics g )
	{
		if( ad != null ){
			ad.draw( g );
		}
	}

	@Override
	public boolean hasEvent()
	{
		return true;
	}
	
	/** 次のプレイヤーにカードを2枚引かせ、手番を終了させる。 */
	@Override
	public void activate( GameState state )
	{
		Player cp = state.getCurrentPlayer();
		Player np = state.getNextPlayer();
		//蓄積されたペナルティ + 今回の分だけ引かせる
		int drawCount = state.getDiscardCount() * PENALTY_DRAW + state.retrievePenaltyDrawCount();
		for( int i = 0; i < drawCount; ++i ){
			state.drawCard( np );
		}
		if( cp.getNumHands() != 0 ){	//ゲーム終了時はターン飛ばしをしない
			state.getLogger().setLog( np.getName() + "は" + drawCount + "枚引いてターンエンド。" );
			state.advanceTurn();
		}
	}
}

