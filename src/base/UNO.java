package base;

import javax.swing.JFrame;

@SuppressWarnings( "serial" )

/**
 * メインメソッドのあるクラス
 * ウィンドウの生成とパネルの生成しか仕事しない
 */
public class UNO extends JFrame
{
	/** パネルの実体。外部のクラスがコンポーネントを利用するために公開しておく。 */
	public static MainPanel panel;

	public UNO()
	{
		//タイトルバーの設定
		setTitle( "UNO" );
		//パネルの生成
		panel = new MainPanel();
		getContentPane().add( panel );
		//ウィンドウサイズの固定
		setResizable( false );
		//ウィンドウの大きさをパネルの大きさに合わせる
		pack();
		//ゲーム本体スレッドを起動
		panel.doRun();
		//ウィンドウを閉じたときにプロセスも終了させるための設定
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		//ウィンドウの表示
		setVisible( true );
	}
	
	public static void main( String[] args )
	{
		new UNO();
	}
}
