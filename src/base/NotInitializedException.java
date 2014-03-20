package base;

/**
 * 自作例外クラス
 * 初期化されないまま呼び出されたときに投げられる
 */
@SuppressWarnings( "serial" )
public class NotInitializedException extends Exception
{
	public NotInitializedException()
	{
		super( "初期化が行われていません。" );
	}
}
