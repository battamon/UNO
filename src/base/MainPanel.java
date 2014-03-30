package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import base.Input;

import sequence.RootParent;

@SuppressWarnings( "serial" )

/**
 * 描画元となるパネルの生成と、ゲームを動かすマルチスレッドの生成、起動を行う。
 */
public class MainPanel extends JPanel implements Runnable
{
	//ループフラグ定数
	public static final int LOOP_END = 0;
	public static final int LOOP_CONTINUE = 1;
	//画面サイズ
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	/** ゲームを動かすスレッド */
	private Thread gameMain;
	/** ループフラグ */
	private volatile boolean running = false;

	/** 描画用オブジェクト */
	private Graphics g = null;
	/** 描画用オブジェクトで描画される対象となるバッファ */
	private Image image = null;
	/** FPS調整クラスオブジェクト */
	private FPSControl fpsControl;

	/**
	 * コンストラクタ
	 */
	public MainPanel()
	{
		//パネル(画面サイズ)の大きさを設定
		Dimension d = new Dimension( WIDTH, HEIGHT );
		setPreferredSize( d );

		//入力の準備
		setFocusable( true );
		addKeyListener( Input.getInstance() );
		addMouseListener( Input.getInstance() );
		addMouseMotionListener( Input.getInstance() );

		//FPS調整クラスのインスタンス化
		fpsControl = new FPSControl();
		fpsControl.setFPS( 60 );
	}

	/**
	 * マルチスレッド(今回はゲームのメインループ)の起動
	 */
	public void doRun()
	{
		if( !running && gameMain == null ){
			gameMain = new Thread( this );
			gameMain.start();
		}
	}

	/** 
	 * マルチスレッドのエントリポイント
	 */
	public void run()
	{
		running = true;

		RootParent parent = new RootParent();
		
		Input.initialize( getLocationOnScreen() );

		//ゲームのメインループ
		while( running ){
			//入力
			try{
				Input.update();
			}catch( NotInitializedException e ){
				System.out.println( e );
				Input.initialize( getLocationOnScreen() );
				continue;
			}
			//更新
			if( gameUpdate( parent ) == LOOP_END ){
				running = false;
				continue;
			}
			//描画
			gameRender( parent );
			//表示
			paintScreen();
			//FPS制御
			fpsControl.control();
		}
		System.exit( 0 );
	}

	/**
	 * ゲームの更新
	 * @param parent ルートシーケンスのオブジェクト
	 * @return プログラムを継続するならLOOP_CONTINUE、終了ならLOOP_ENDが返る
	 */
	private int gameUpdate( RootParent parent )
	{
		return parent.update( null );
	}
	
	/** 
	 * バッファへの描画
	 * @param parent ルートシーケンスのオブジェクト
	 */
	private void gameRender( RootParent parent )
	{
		//描画対象となる画面イメージの作成
		if( image == null ){
			image = createImage( WIDTH, HEIGHT );
			if( image == null ){
				System.out.println( "imageが作成できません。" );
				return;
			}else{
				g = image.getGraphics();
			}
		}
		
		//最初に画面を真っ白に塗りつぶす
		g.setColor( Color.WHITE );
		g.fillRect( 0, 0, WIDTH, HEIGHT );
		
		//描画
		parent.render( g );
	}

	/**
	 * スクリーンに投影(実際に画面に表示する)
	 */
	private void paintScreen()
	{
		Graphics g;
		try{
			g = this.getGraphics();
			if( ( g != null ) && ( image != null ) ){
				g.drawImage( image, 0, 0, null );
				//FPS表示(別に表示しなくてもいい)
				//g.setColor( Color.BLUE );
				//g.drawString( "FPS: " + fpsControl.toString(), 4, 16 );
			}
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
		}catch( Exception e ){
			e.printStackTrace();
		}
	}
}
