package base;

import java.text.DecimalFormat;

/**
 * FPSControlクラス<br>
 * FPSの設定、制御を行う<br>
 * ゲームループ内で1度だけcontrol()を呼び出すことで制御する
 * @author ばったもん
 */
public class FPSControl
{
	private static long MAX_STATS_INTERVAL = 1000000000L;
	
	private int fps = 60;
	private long period = (long)( 1.0 / fps * 1000000000L );
	private long calcInterval = 0L;
	private long prevCalcTime;
	private long beforeTime;
	private long overSleepTime = 0L;
	private long frameCount = 0;
	private volatile double actualFPS = 0.0;
	private DecimalFormat df = new DecimalFormat( "0.0" );

	public FPSControl()
	{
		initialize( fps );
	}

	/**
	 * 初期化関数。コンストラクタもしくはsetFPS内で呼ばれるためprivateにしている。
	 * @param fps 1秒間に更新されるフレーム数
	 */
	private void initialize( int fps )
	{
		this.fps = fps;
		period = (long)( 1.0 / fps * 1000000000L );
		beforeTime = System.nanoTime();
		prevCalcTime = beforeTime;
	}

	/**
	 * fpsを設定する。ゲームは大抵60fps
	 * @param fps 1秒間に更新されるフレーム数
	 */
	public void setFPS( int fps )
	{
		if( fps >= 0 ){
			initialize( fps );
		}
	}

	/**
	 * 制御メソッド<br>
	 * 1ループに1回呼び出す
	 */
	public void control()
	{
		long afterTime, timeDiff, sleepTime;
		int noDelays = 0;
		
		afterTime = System.nanoTime();
		timeDiff = afterTime - beforeTime;
		sleepTime = ( period - timeDiff ) - overSleepTime;
		
		if( sleepTime > 0 ){
			try{
				Thread.sleep( sleepTime / 1000000L );	//単位：ミリ秒
			}catch( InterruptedException e ){
				e.printStackTrace();
			}
			overSleepTime = ( System.nanoTime() - afterTime ) - sleepTime;
		}else{
			overSleepTime = 0L;
			if( ++noDelays >= 16 ){
				Thread.yield();
				noDelays = 0;
			}
		}
		beforeTime = System.nanoTime();
		calcFPS();
	}

	/**
	 * 現在のFPSを計算
	 */
	public void calcFPS()
	{
		++frameCount;
		calcInterval += period;
		
		if( calcInterval >= MAX_STATS_INTERVAL ){
			long timeNow = System.nanoTime();	//単位：ナノ秒
			long realElapsedTime = timeNow - prevCalcTime;
			
			actualFPS = ( (double)frameCount / realElapsedTime ) * 1000000000L;
			//デバッグ出力にFPSを表示。別に必要ない。
			System.out.println( df.format( actualFPS ) );
			
			frameCount = 0L;
			calcInterval = 0L;
			prevCalcTime = timeNow;
		}
	}

	/**
	 * 現在のfpsを「00.0]の形式で返す
	 */
	@Override
	public String toString()
	{
		return df.format( actualFPS );
	}
}
