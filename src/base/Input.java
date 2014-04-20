package base;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Inputクラス　シングルトンクラス<br>
 * キーボード入力の検知、フラグ等はこのクラスで一元管理<br>
 * フレーム毎の入力の整合性を持たせる実装をしている<br>
 * ゲームループ内で一度だけupdate()を呼び出すことで入力情報を更新する<br>
 * @author ばったもん
 */
public class Input implements KeyListener, MouseListener, MouseMotionListener
{
	/** 唯一のインスタンス */
	private static final Input instance = new Input();
	/** 初期化されたかどうかのフラグ */
	private static boolean initialized = false;

	//入力情報保持クラス関連
	private static final int STATE_CURRENT = 0;
	private static final int STATE_LAST    = 1;
	private static final int STATE_PREV    = 2;
	private static final int NUM_STATES    = 3;

	//キーボード入力関連
	public static final int UP    = 0;
	public static final int RIGHT = 1;
	public static final int DOWN  = 2;
	public static final int LEFT  = 3;
	public static final int Z     = 4;
	public static final int X     = 5;
	public static final int ESC   = 6;
	public static final int SPACE = 7;
	public static final int NUM_KEYS = 8;

	//マウス入力関連
	public static final int MOUSE_BUTTON_LEFT   = 0;
	public static final int MOUSE_BUTTON_MIDDLE = 1;
	public static final int MOUSE_BUTTON_RIGHT  = 2;
	public static final int NUM_MOUSE_BUTTONS   = 3;

	private static volatile int[] keyCount;	//入力しっぱなしの情報を保存
	private static volatile int[] mouseDragCount;
	private static volatile InputState[] states;

	private Input()
	{
	}

	/**
	 * 初期化メソッド<br>
	 * Inputクラスを運用する前に1度だけ呼び出す必要がある。
	 * @param componentPos マウスの現在の位置
	 */
	public static void initialize( Point componentPos )
	{
		initialized = true;

		states = new InputState[ NUM_STATES ];
		for( int i = 0; i < NUM_STATES; ++i ){
			states[ i ] = new InputState();
		}
		keyCount = new int[ NUM_KEYS ];
		mouseDragCount = new int[ NUM_MOUSE_BUTTONS ];
		PointerInfo pi = MouseInfo.getPointerInfo();
		states[ STATE_CURRENT ].mousePos.x = pi.getLocation().x - componentPos.x;
		states[ STATE_CURRENT ].mousePos.y = pi.getLocation().y - componentPos.y;
	}

	public static final Input getInstance()
	{
		return instance;
	}

	public static void update() throws NotInitializedException
	{
		//初期化されてないのにupdateが呼び出されたら例外を投げる
		if( !initialized ) throw new NotInitializedException();

		InputState prev = states[ STATE_PREV ];
		InputState last = states[ STATE_LAST ];
		InputState current = states[ STATE_CURRENT ];
		//入力情報を新しく更新する
		prev.copy( last );
		last.copy( current );

		//連続入力カウント
		for( int i = 0; i < NUM_KEYS; ++i ){
			if( last.keys[ i ] ){
				++keyCount[ i ];
			}else{
				keyCount[ i ] = 0;
			}
		}
		for( int i = 0; i < NUM_MOUSE_BUTTONS; ++i ){
			if( last.mouseClicks[ i ] ){
				++mouseDragCount[ i ];
			}else{
				mouseDragCount[ i ] = 0;
			}
		}
	}

	//現在の(フレーム単位での)有効なキーの状態を返す
	public static boolean getKeyState( int code )
	{
		return states[ STATE_LAST ].keys[ code ];
	}

	//入力した瞬間を検出する
	public static boolean getKeyTriggered( int code )
	{
		return !states[ STATE_PREV ].keys[ code ] && states[ STATE_LAST ].keys[ code ];
	}

	public static int getKeyCount( int code )
	{
		return keyCount[ code ];
	}

	//マウスの位置を返す
	public static Point getMousePosition()
	{
		//まったく同じ値を持つオブジェクトを生成して、それを返す。
		//直接mousePosを返すと、呼び出し元でデータの書き換えが行われ、整合性が取れなくなる可能性があるため。
		return new Point( states[ STATE_LAST ].mousePos );
	}

	//クリックした瞬間(マウスのボタンを押して、離した瞬間)を検出する
	public static boolean getClicked( int code )
	{
		return states[ STATE_PREV ].mouseClicks[ code ] && !states[ STATE_LAST ].mouseClicks[ code ];
	}

	//ドラッグしている時間(フレーム数)を返す
	public static int getDragCount( int code )
	{
		return mouseDragCount[ code ];
	}

	private void setKeyState( int code, boolean flag )
	{
		switch( code ){
			case KeyEvent.VK_UP:    states[ STATE_CURRENT ].keys[ UP ]    = flag; break;
			case KeyEvent.VK_RIGHT: states[ STATE_CURRENT ].keys[ RIGHT ] = flag; break;
			case KeyEvent.VK_DOWN:  states[ STATE_CURRENT ].keys[ DOWN ]  = flag; break;
			case KeyEvent.VK_LEFT:  states[ STATE_CURRENT ].keys[ LEFT ]  = flag; break;
			case KeyEvent.VK_X:     states[ STATE_CURRENT ].keys[ X ]     = flag; break;
			case KeyEvent.VK_Z:     states[ STATE_CURRENT ].keys[ Z ]     = flag; break;
			case KeyEvent.VK_ESCAPE: states[ STATE_CURRENT ].keys[ ESC ]  = flag; break;
			case KeyEvent.VK_SPACE: states[ STATE_CURRENT ].keys[ SPACE ] = flag; break;
		}
	}

	private void setMouseState( int code, boolean flag )
	{
 		switch( code ){
			case MouseEvent.BUTTON1: states[ STATE_CURRENT ].mouseClicks[ MOUSE_BUTTON_LEFT ] = flag; break;	//左クリック
			case MouseEvent.BUTTON2: states[ STATE_CURRENT ].mouseClicks[ MOUSE_BUTTON_MIDDLE ] = flag; break;	//中クリック
			case MouseEvent.BUTTON3: states[ STATE_CURRENT ].mouseClicks[ MOUSE_BUTTON_RIGHT ] = flag; break;	//右クリック
		}
 	}

	@Override
	public void keyPressed( KeyEvent e )
	{
		setKeyState( e.getKeyCode(), true );
	}

	@Override
	public void keyReleased( KeyEvent e )
	{
		setKeyState( e.getKeyCode(), false );
	}

	@Override
	public void keyTyped( KeyEvent e )
	{
	}

	@Override
	public void mouseClicked( MouseEvent e )
	{
	}

	@Override
	public void mouseEntered( MouseEvent e )
	{
	}
	
	@Override
	public void mouseExited( MouseEvent e )
	{
	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		setMouseState( e.getButton(), true );
	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		setMouseState( e.getButton(), false );
	}

	@Override
	public void mouseDragged( MouseEvent e )
	{
	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		if( !initialized ) return;
		states[ STATE_CURRENT ].mousePos = e.getPoint();
	}
}

class InputState
{
	public boolean[] keys;
	public boolean[] mouseClicks;
	public Point mousePos;

	public InputState()
	{
		keys = new boolean[ Input.NUM_KEYS ];
		mouseClicks = new boolean[ Input.NUM_MOUSE_BUTTONS ];
		mousePos = new Point();
	}

	public void copy( InputState src )
	{
		System.arraycopy( src.keys, 0, keys, 0, Input.NUM_KEYS );
		System.arraycopy( src.mouseClicks, 0, mouseClicks, 0, Input.NUM_MOUSE_BUTTONS );
		mousePos = (Point)src.mousePos.clone();
	}
}