package base;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

/**
 * ImageManagerクラス<br>
 * 画像の読み込み、管理、描画等はすべてこのクラスで行う。<br>
 * 読み込んだ画像はhandle(int型)で紐付けされ,<br>
 * 画像に対するアクションはこのhandleを用いて行う。
 * @author ばったもん
 */
public class ImageManager
{
	/** ハンドルが無効なときの値 */
	public static final int NO_HANDLE = -1;
	/** 次に取得される画像のハンドル値 */
	private static int nextHandle = 0;
	/** 画像バッファの保存場所 */
	private static HashMap< Integer, BufferedImage > images;
	/** ファイルパスとハンドルの紐付け */
	private static HashMap< String, Integer > handleCache;
	/** ファイルパスとハンドルの紐付け(分割ファイル用) */
	private static HashMap< String, Integer[] > handleCacheDiv;

	//スタティックイニシャライザ(静的初期化リスト)
	static
	{
		images = new HashMap< Integer, BufferedImage >();
		handleCache = new HashMap< String, Integer >();
		handleCacheDiv = new HashMap< String, Integer[] >();
	}

	/**
	 * 画像を読み込む
	 * @param filename ファイルパス
	 * @return 画像に紐付けられたハンドル
	 */
	public static int readImage( String filename )
	{
		//既に読み込んで、バッファが保持されたままならそのハンドルを返す
		Integer cache = null;
		if( ( cache = handleCache.get( filename ) ) != null ){
			return cache.intValue();
		}

		int handle = NO_HANDLE;
		BufferedImage bf;
		try{
			bf = ImageIO.read( new File( filename ) );
		}catch( IOException e ){
			e.printStackTrace();
			return handle;
		}
		//返すハンドルを決めつつ、次の読み込み時に使われるハンドルを進めておく。
		handle = nextHandle++;
		images.put( new Integer( handle ), bf );
		handleCache.put( filename, new Integer( handle ) );
		return handle;
	}

	/**
	 * 画像を読み込む。等分割用。<br>
	 * 指定された幅、高さから可能な限り切り出す。余った部分は切り捨て。
	 * @param filename ファイルパス
	 * @param sizeX 1枚の幅
	 * @param sizeY 1枚の高さ
	 * @param numAllImages 全部で何枚切り出すか
	 * @return 画像に紐付けられたハンドルを格納した配列
	 */
	public static int[] readDivImage( String filename, int sizeX, int sizeY, int numAllImages )
	{
		Integer[] cache = null;
		int[] handles = null;
		
		if( ( cache = handleCacheDiv.get( filename ) ) != null ){
			handles = new int[ cache.length ];
			for( int i = 0; i < handles.length; ++i ){
				handles[ i ] = cache[ i ].intValue();
			}
			return handles;
		}
		
		BufferedImage bf;
		try{
			bf = ImageIO.read( new File( filename ) );
		}catch( IOException e ){
			e.printStackTrace();
			return null;
		}
		//分割したい画像のサイズから縦・横の分割数を割り出す
		int divX = bf.getWidth() / sizeX;
		int divY = bf.getHeight() / sizeY;
		//切り出せる枚数が指定した枚数より少ない場合、少ない方(実際に切り出せる枚数)に合わせる
		if( divX * divY <= numAllImages ){
			numAllImages = divX * divY;
		}

		handles = new int[ numAllImages ];
		cache = new Integer[ numAllImages ];
		int countDivided = 0;
		boolean leave = false;
		for( int i = 0; i < divY; ++i ){
			for( int j = 0; j < divX; ++j ){
				int handle = nextHandle++;
				handles[ i * divX + j ] = handle;
				images.put( new Integer( handle ), bf.getSubimage( j * sizeX, i * sizeY, sizeX, sizeY ) );
				cache[ i ] = new Integer( handle );
				++countDivided;
				//切り出し予定数に達したら処理を打ち切る
				if( countDivided >= numAllImages ){
					leave = true;
					break;
				}
			}
			if( leave ) break;
		}
		handleCacheDiv.put( filename, cache );
		return handles;
	}

	/**
	 * 描画する。
	 * @param g 描画用オブジェクト
	 * @param handle 描画したい画像のハンドル
	 * @param x x座標
	 * @param y y座標
	 */
	public static void draw( Graphics g, int handle, int x, int y )
	{
		g.drawImage( images.get( handle ), x, y, NullImageObserver.instance );
	}

	/**
	 * 指定された比率で縮小拡大して描画する。
	 * @param g 描画用オブジェクト
	 * @param handle 描画したい画像のハンドル
	 * @param x x座標
	 * @param y y座標
	 * @param w 幅
	 * @param h 高さ
	 */
	public static void draw( Graphics g, int handle, int x, int y, int w, int h )
	{
		g.drawImage( images.get( handle ), x, y, w, h, NullImageObserver.instance );
	}

	/**
	 * NullImageObserverクラス
	 * Graphicsクラスで画像を描画する際に必要なオブジェクト
	 * 実際は何もしていないただの形だけのクラス
	 * @author ばったもん
	 */
	private static class NullImageObserver implements ImageObserver
	{
		public static NullImageObserver instance;

		static
		{
			instance = new NullImageObserver();
		}
		
		private NullImageObserver()
		{		
		}
		
		public boolean imageUpdate( Image img, int infoflags, int x, int y, int width, int height )
		{
			return false;
		}
	}

}
