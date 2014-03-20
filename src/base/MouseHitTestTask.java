package base;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 * マウスと当たり判定が必要なオブジェクトはこのタスククラスに登録して当たり判定を行ってもらう<br>
 * 当たり判定はオブジェクトの登録順に依存するので、登録する順番に気をつけること
 * @author ばったもん
 */
public class MouseHitTestTask
{
	/** 登録されるオブジェクトを格納する */
	private ArrayList< IHitTestObject > objList;

	/**
	 * コンストラクタ
	 */
	public MouseHitTestTask()
	{
		objList = new ArrayList< IHitTestObject >();
	}

	/**
	 * 当たり判定を行うべきオブジェクトを登録する
	 * @param obj IHitTestObjectインターフェースを実装したオブジェクト
	 */
	public void add( IHitTestObject obj )
	{
		objList.add( obj );
	}

	/**
	 * 当たり判定メソッド<br>
	 * 登録した順に当たり判定を行っていき、<br>
	 * 一番最初に判定に成功したオブジェクトはhitSurface()が呼び出され、<br>
	 * それ以降に判定に成功したオブジェクトはhitBack()が呼び出される。
	 */
	public void hitTest()
	{
		Point mousePos = Input.getMousePosition();
		boolean hitFirst = true;
		for( IHitTestObject obj : objList ){	//拡張for文
			if( obj.hitTest( mousePos ) ){
				if( hitFirst ){
					obj.hitSurface();
				}else{
					obj.hitBack();
				}
			}else{
				obj.notHit();
			}
		}
	}

	/**
	 * タスクに登録したオブジェクトのupdate()を順番に呼び出す
	 */
	public void update()
	{
		for( IHitTestObject obj : objList ){
			obj.update();
		}
	}

	/**
	 * タスクに登録したオブジェクトのdraw()を順番に呼び出す
	 * 登録した順とは逆に描画していくため、先に登録したオブジェクトが上に描画される
	 * @param g
	 */
	public void draw( Graphics g )
	{
		for( int i = objList.size() - 1; i >= 0; --i ){
			objList.get( i ).draw( g );
		}
	}

	/**
	 * 登録したオブジェクトをすべて取り除く
	 */
	public void removeAll()
	{
		objList.clear();
	}
}
